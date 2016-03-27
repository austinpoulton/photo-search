import sbt._

object Version {
  val mockito    = "1.10.19"
  val scala      = "2.10.5"
  val scalaTest  = "2.2.4"
  val lucence    = "5.5.0"
  val es         = "2.1.1"
  val nlp        = "3.6.0"
  val flickr     = "2.15"
  val solr       = ""
  //val sparkTest  = "1.3.0_0.2.0"
}

object Library {


  val mockitoAll      = "org.mockito"       %  "mockito-all"     % Version.mockito
  val scalaTest       = "org.scalatest"     %% "scalatest"       % Version.scalaTest
  val luceneCore      = "org.apache.lucene"  % "lucene-core"  % Version.lucence
  val luceneAnalyzer  = "org.apache.lucene"  % "lucene-analyzers-common"  % Version.lucence
  val luceneQuery     = "org.apache.lucene"  % "lucene-queryparser"  % Version.lucence
  val luceneBenchmark = "org.apache.lucene"  % "lucene-benchmark" % Version.lucence
  val esCore          = "org.elasticsearch" % "elasticsearch" %  Version.es
  val nlpCore         = "edu.stanford.nlp" % "stanford-corenlp" % Version.nlp
  val flickr4j        = "com.flickr4java" % "flickr4java" % Version.flickr
}

object Dependencies {

  import Library._

  val projectDeps = Seq(
    luceneCore,
    luceneAnalyzer,
    luceneQuery,
    luceneBenchmark,
    // esCore,
    nlpCore,
    scalaTest      % "test",
    mockitoAll     % "test"
  )
}