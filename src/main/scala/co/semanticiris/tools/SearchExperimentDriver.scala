package co.semanticiris.tools

import java.io.{OutputStreamWriter, PrintWriter}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Paths}
import java.util

import co.semanticiris.model.{ImageCollection, ImageDocument}
import org.apache.lucene.benchmark.quality._
import org.apache.lucene.benchmark.quality.trec.{TrecJudge, TrecTopicsReader}
import org.apache.lucene.benchmark.quality.utils.{SimpleQQParser, SubmissionReport}
import org.apache.lucene.index.{DirectoryReader, IndexReader}
import org.apache.lucene.search.{IndexSearcher}
import org.apache.lucene.search.similarities.Similarity.{SimScorer, SimWeight}

/**
  * Created by austin on 27/03/2016.
  */
object SearchExperimentDriver extends App {

  def loadQueries(qTopicsFileLocation : String = "/var/irdata/benchmark/input/trecTopics.txt"): Array[QualityQuery] = {
    val qReader : TrecTopicsReader = new TrecTopicsReader
    qReader.readQueries(Files.newBufferedReader( Paths.get(qTopicsFileLocation), StandardCharsets.UTF_8))
  }

  def loadRelevanceJudgments(qRelevanceFileLocation : String = "/var/irdata/benchmark/input/trecQRels.txt" ):Judge = {
    new TrecJudge(Files.newBufferedReader(Paths.get(qRelevanceFileLocation), StandardCharsets.UTF_8))
  }

  def imageCollection(imgCollFileLocation : String = "/var/irdata/flickrImageColl.ser"):ImageCollection = ImageCollection.load(imgCollFileLocation)

  def queryParser(fieldSpec : String = "T"): QualityQueryParser = {
    val fieldSet: util.HashSet[String] = new util.HashSet[String]()
    if (fieldSpec.indexOf('T') >= 0) fieldSet.add("title")
    if (fieldSpec.indexOf('D') >= 0) fieldSet.add("description")
    if (fieldSpec.indexOf('N') >= 0) fieldSet.add("narrative")
    new SimpleQQParser(fieldSet.toArray(new Array[String](0)), ImageDocument.BENCHMARK)
  }

  def submitLogger(logFileLocation: String = "/var/irdata/benchmark/output/out.txt"):SubmissionReport =  {
    new SubmissionReport(new PrintWriter(Files.newBufferedWriter(Paths.get(logFileLocation), StandardCharsets.UTF_8)), "lucene")
  }

  val logger: PrintWriter = new PrintWriter(new OutputStreamWriter(System.out, Charset.defaultCharset), true)
  val submitLog: SubmissionReport = submitLogger()
  val maxResults = 100


  val collection =  ImageCollection.load("/var/irdata/flickrImageColl.ser")

  val basicDir = collection.indexDocuments()
  val stemmedDir = collection.indexDocuments(true)
  val reader: IndexReader = DirectoryReader.open(basicDir)
  val searcher: IndexSearcher = new IndexSearcher(reader)


  val queries = loadQueries()
  val judgements = loadRelevanceJudgments()
  // validate the queries for the judgements
  judgements.validateData(queries, logger)
  // set the parsing of quality queries into Lucene queries.
  val qqParser: QualityQueryParser =queryParser()

  // run the benchmark
  val qrun: QualityBenchmark = new QualityBenchmark(queries, qqParser, searcher, "docname")
  qrun.setMaxResults(maxResults)
  val stats: Array[QualityStats] = qrun.execute(judgements, submitLog, logger)

  // print an avarage sum of the results
  val avg: QualityStats = QualityStats.average(stats)
  avg.log("SUMMARY", 2, logger, "  ")
  reader.close
  //dir.close
}