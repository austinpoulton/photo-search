package co.semanticiris.tools

import java.io.{OutputStreamWriter, PrintWriter}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Paths}

import co.semanticiris.model.{ImageCollection, RetrievalConfig, RetrievalExperiment}
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.benchmark.quality.{Judge, QualityQuery}
import org.apache.lucene.benchmark.quality.trec.{TrecJudge, TrecTopicsReader}
import org.apache.lucene.store.RAMDirectory

/**
  * Created by austin on 28/03/2016.
  */
object RetrievalExperimentDriver extends  App {

  def loadQueries(qTopicsFileLocation : String = "/var/irdata/benchmark/input/trecTopics-2.txt"): Array[QualityQuery] = {
    val qReader : TrecTopicsReader = new TrecTopicsReader
    qReader.readQueries(Files.newBufferedReader( Paths.get(qTopicsFileLocation), StandardCharsets.UTF_8))
  }

  def loadRelevanceJudgments(qRelevanceFileLocation : String = "/var/irdata/benchmark/input/trecQRels-2.txt" ):Judge = {
    new TrecJudge(Files.newBufferedReader(Paths.get(qRelevanceFileLocation), StandardCharsets.UTF_8))
  }

  val logger: PrintWriter = new PrintWriter(new OutputStreamWriter(System.out, Charset.defaultCharset), true)

  val defaultOutputLoc = "/var/irdata/benchmark/output/"
  val collection =  ImageCollection.load("/var/irdata/flickrImageColl.ser")

  val directory  = collection.indexDocuments()  //OR use: EnglishAnalyzer() for stemmed indexing

  // load queirs and judegments
  val queries = loadQueries()
  val judgements = loadRelevanceJudgments()
  // validate the queries for the judgements
  judgements.validateData(queries, logger)

//  val classicConfig = RetrievalConfig(directory)
//  val experiment = RetrievalExperiment("test1",classicConfig,judgements,queries)
//  val results = experiment.run(defaultOutputLoc)
//
//  Some(new PrintWriter(defaultOutputLoc+ experiment.name +"-stats.csv")).foreach{p => p.write(results.header+results.toCSV()); p.close}
  val configs = RetrievalConfig.standardConfigSuite()
  val experiments = RetrievalExperiment.suite("2T-", directory, judgements,queries,configs.values.toList,100)
  //val results = experiments.map(e => e.run(defaultOutputLoc))
  for (e <- experiments) {
    val r = e.run(defaultOutputLoc)
    Some(new PrintWriter(defaultOutputLoc+ e.name +"-stats.csv")).foreach{p => p.write(r.header+r.toCSV()); p.close}
  }
}
