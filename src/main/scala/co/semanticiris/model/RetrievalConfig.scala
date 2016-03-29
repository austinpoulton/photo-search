package co.semanticiris.model

import java.io.{FileOutputStream, OutputStreamWriter, PrintWriter}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Paths}
import java.util

import org.apache.lucene.benchmark.quality._
import org.apache.lucene.benchmark.quality.utils.{SimpleQQParser, SubmissionReport}
import org.apache.lucene.index.{DirectoryReader, IndexReader}
import org.apache.lucene.search.{IndexSearcher, Query, ScoreDoc}
import org.apache.lucene.search.similarities.{AfterEffectL, NormalizationH1, _}
import org.apache.lucene.store.{Directory, RAMDirectory}

/**
  * Created by austin on 26/03/2016.
  */
class RetrievalConfig(val id : String, val desc : String, val similarity : Similarity) {

//  def searcher(): IndexSearcher = {
//    val reader: IndexReader = DirectoryReader.open(directory)
//    val isearcher: IndexSearcher = new IndexSearcher(reader)
//    // set the specific similarity for this retrieval model
//    isearcher.setSimilarity(similarity)
//    isearcher
//  }

  override def toString():String = "Retrieval Config:\n" + desc +"\t"+similarity
}

object RetrievalConfig {

  def apply():RetrievalConfig = new RetrievalConfig("Classic", "Lucene ClassicSimilarity", new ClassicSimilarity())

  def apply(id: String, desc : String, similarity : Similarity ):RetrievalConfig = new RetrievalConfig(id, desc, similarity)

  // TODO - Add retrieval configs with similarity models
  def standardConfigSuite(): Map[String,RetrievalConfig] = {
      val classic = new RetrievalConfig("Classic", "Lucene ClassicSimilarity", new ClassicSimilarity())
      val bm25Standard = new RetrievalConfig("bm25Standard", "BM25 Standard", new BM25Similarity())
      val bm25_k1_10_b_05 = new RetrievalConfig(",bm25_k1_10_b_05","BM25 k1 = 1.0 b = 0.5", new BM25Similarity(1.0f, 0.5f))
      val dfr_D_L_H1 = new RetrievalConfig("dfr_D_L_H1","DFR BM = D, AE = L, Norm = H1",
        new DFRSimilarity((new BasicModelD()).asInstanceOf[BasicModel], new AfterEffectL(), new NormalizationH1()))
     val dfr_P_B_H2 = new RetrievalConfig("dfr_P_B_H2","DFR BM = P, AE = B, Norm = H2",
       new DFRSimilarity((new BasicModelP()).asInstanceOf[BasicModel], new AfterEffectB(), new NormalizationH2()))

      //return the map of configs
      Map(classic.id -> classic,
          bm25Standard.id ->  bm25Standard,
          bm25_k1_10_b_05.id -> bm25_k1_10_b_05,
          dfr_D_L_H1.id -> dfr_D_L_H1,
          dfr_P_B_H2.id -> dfr_P_B_H2)
  }
}

class RetrievalExperiment(val name: String, val directory : RAMDirectory, val config: RetrievalConfig, val judgements : Judge, val queries : Array[QualityQuery], val maxResults: Int) {

  def searcher(): IndexSearcher = {
    val reader: IndexReader = DirectoryReader.open(directory)
    val isearcher: IndexSearcher = new IndexSearcher(reader)
    // set the specific similarity for this retrieval model
    isearcher.setSimilarity(config.similarity)
    isearcher
  }

  def run(outputDir: String = "/var/irdata/benchmark/output/") : ExperimentResult = {
    val qqParser = RetrievalExperiment.queryParser()
    val isearcher = searcher()
    val submitLog = RetrievalExperiment.submitLogger(outputDir+name+"-log.txt")
    val logger = RetrievalExperiment.logger(false, outputDir+name+"-sum.txt")
    // run the benchmark
    val qrun: QualityBenchmark = new QualityBenchmark(queries, qqParser, isearcher, ImageDocument.DOC_NAME)
    qrun.setMaxResults(maxResults)
    val stats: Array[QualityStats] = qrun.execute(judgements, submitLog, logger)

    val avg: QualityStats = QualityStats.average(stats)
    avg.log("SUMMARY", 2, logger, "  ")
    isearcher.getIndexReader.close()
    new ExperimentResult(name,queries,stats)
  }
}

object RetrievalExperiment {

//  def apply(config : RetrievalConfig ,judgements : Judge,queries : Array[QualityQuery],maxResults: Int =100): RetrievalExperiment =
//    RetrievalExperiment(config.id, config,judgements,queries,maxResults)

  def apply(name : String, dir: RAMDirectory, config : RetrievalConfig ,judgements : Judge,queries : Array[QualityQuery],maxResults: Int =100): RetrievalExperiment = {
      new RetrievalExperiment(name, dir, config,judgements,queries,maxResults)
  }

  def suite(suitName : String, dir: RAMDirectory, judgements : Judge,queries : Array[QualityQuery], configs : List[RetrievalConfig],maxResults: Int):List[RetrievalExperiment] = {
    configs.map(c => RetrievalExperiment(suitName+c.id,dir, c, judgements, queries, maxResults))
  }

  def queryParser(fieldSpec : String = "T"): QualityQueryParser = {
    val fieldSet: util.HashSet[String] = new util.HashSet[String]()
    if (fieldSpec.indexOf('T') >= 0) fieldSet.add("title")
    if (fieldSpec.indexOf('D') >= 0) fieldSet.add("description")
    if (fieldSpec.indexOf('N') >= 0) fieldSet.add("narrative")
    new SimpleQQParser(fieldSet.toArray(new Array[String](0)), ImageDocument.BENCHMARK)
  }

  def submitLogger(logFileLocation: String = "/var/irdata/benchmark/output/out.txt"):SubmissionReport =
    new SubmissionReport(new PrintWriter(Files.newBufferedWriter(Paths.get(logFileLocation), StandardCharsets.UTF_8)), "lucene")

  def logger(toStdOut: Boolean = true, summaryFileLocation : String): PrintWriter = {
    if (toStdOut) new PrintWriter(new OutputStreamWriter(System.out, Charset.defaultCharset), true)
    else new PrintWriter(new OutputStreamWriter(new FileOutputStream(summaryFileLocation), Charset.defaultCharset), true)
  }
}


class ExperimentResult (val expName : String, val queries: Array[QualityQuery], val stats : Array[QualityStats]) {

  final val header = "Query ID, Search Terms, Search ms, Doc Extract ms, Num Points, Num Good Points, Max Good Points, Average Precision, MRR, Recall,P@1,P@2,P@3,P@4,P@5,P@6,P@7,P@8,P@9,P@10,P@11,P@12,P@13,P@14,P@15,P@16,P@17,P@18,P@19,P@20\n"

  def toCSV():String = {
    val qqParser = RetrievalExperiment.queryParser()
    val rows = for (i<- 0 to (queries.length-1)) yield qStatsToCSVRow(queries(i).getQueryID, qqParser.parse(queries(i)).toString, stats(i))
    val avg = QualityStats.average(stats)
    rows.mkString("\n") + "\n"+qStatsToCSVRow(expName,"Averages", avg)
  }

  def qStatsToCSVRow(qqId: String, qStr: String, st : QualityStats):String = {
      qqId + "," + qStr + "," + st.getSearchTime+","+ st.getDocNamesExtractTime + "," + st.getNumPoints + ","+st.getNumGoodPoints +
      ","+st.getMaxGoodPoints + "," +st.getAvp + "," + st.getMRR + "," + st.getRecall + "," +
      (for (i <- 1 to 20) yield st.getPrecisionAt(i)).mkString(",") + st.getRecallPoints
  }
}