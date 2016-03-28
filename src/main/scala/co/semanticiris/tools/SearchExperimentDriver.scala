package co.semanticiris.tools

import java.io.{OutputStreamWriter, PrintWriter}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Path, Paths}
import java.util

import co.semanticiris.model.ImageCollection
import org.apache.lucene.benchmark.quality._
import org.apache.lucene.benchmark.quality.trec.{TrecJudge, TrecTopicsReader}
import org.apache.lucene.benchmark.quality.utils.{SimpleQQParser, SubmissionReport}
import org.apache.lucene.index.{DirectoryReader, FieldInvertState, IndexReader, LeafReaderContext}
import org.apache.lucene.search.{CollectionStatistics, IndexSearcher, TermStatistics}
import org.apache.lucene.search.similarities._
import org.apache.lucene.search.similarities.Similarity.{SimScorer, SimWeight}

/**
  * Created by austin on 27/03/2016.
  */
object SearchExperimentDriver extends App {
  val logger: PrintWriter = new PrintWriter(new OutputStreamWriter(System.out, Charset.defaultCharset), true)

  val topicsFile = Paths.get("/var/irdata/benchmark/input/trecTopics.txt")
  val qrelsFile = Paths.get("/var/irdata/benchmark/input/trecQRels.txt")
  val submissionFile: Path = Paths.get("/var/irdata/benchmark/output/out.txt")

  val maxResults = 100
  val fieldSpec = "T"
  val submitLog: SubmissionReport = new SubmissionReport(new PrintWriter(Files.newBufferedWriter(submissionFile, StandardCharsets.UTF_8)), "lucene")

  val collection = ImageCollection.load("/var/irdata/flickrImageColl.ser")
  val basicDir = collection.directory
  val stemmedDir = collection.stemmedDirectory
  val reader: IndexReader = DirectoryReader.open(basicDir)
  val searcher: IndexSearcher = new IndexSearcher(reader)
  val docNameField: String = "docname"


  val qReader : TrecTopicsReader = new TrecTopicsReader
  val queries : Array[QualityQuery] = qReader.readQueries(Files.newBufferedReader(topicsFile, StandardCharsets.UTF_8))
  val judgements : Judge = new TrecJudge(Files.newBufferedReader(qrelsFile, StandardCharsets.UTF_8))
  // prepare judge, with trec utilities that read from a QRels file
  val judge: Judge = new TrecJudge(Files.newBufferedReader(qrelsFile, StandardCharsets.UTF_8))
  judge.validateData(queries, logger)

  val fieldSet: util.HashSet[String] = new util.HashSet[String]()
  if (fieldSpec.indexOf('T') >= 0) fieldSet.add("title")
  if (fieldSpec.indexOf('D') >= 0) fieldSet.add("description")
  if (fieldSpec.indexOf('N') >= 0) fieldSet.add("narrative")
  // set the parsing of quality queries into Lucene queries.
  val qqParser: QualityQueryParser = new SimpleQQParser(fieldSet.toArray(new Array[String](0)), "body")
  // run the benchmark
  val qrun: QualityBenchmark = new QualityBenchmark(queries, qqParser, searcher, docNameField)
  qrun.setMaxResults(maxResults)
  val stats: Array[QualityStats] = qrun.execute(judge, submitLog, logger)
  // print an avarage sum of the results
  val avg: QualityStats = QualityStats.average(stats)
  avg.log("SUMMARY", 2, logger, "  ")
  reader.close
  //dir.close


  def similaritiesModels() : Map[String, Similarity] = {
    val similarities = Map("Standard BM25 " -> new BM25Similarity(),
      "BM25 k1 =1.0, b =0.5" -> new BM25Similarity(1.0f, 0.5f),
      "DFR Model: AfterEffect:L, Norm:H1 "
        -> new DFRSimilarity((new BasicModelD()).asInstanceOf[BasicModel], new AfterEffectL(), new NormalizationH1()))
    similarities
  }

}