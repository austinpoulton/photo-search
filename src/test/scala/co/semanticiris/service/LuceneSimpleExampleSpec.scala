package co.semanticiris.service

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{ScoreDoc, Query, IndexSearcher}
import org.apache.lucene.store.{Directory, RAMDirectory}
import org.scalatest.{BeforeAndAfter, FlatSpec}

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.document.{TextField, Field, Document}

/**
  * Created by austin on 09/03/2016.
  */
class LuceneSimpleExampleSpec extends FlatSpec with BeforeAndAfter {

  // standard token stream analyzer
  val analyzer : Analyzer = new StandardAnalyzer()
  // RAM (in-memory) directory
  val directory : Directory  = new RAMDirectory();
  // index readers
  val ireader : DirectoryReader = DirectoryReader.open(directory);
  var iwriter : IndexWriter = _

  before {
    // create token stream analyser/parser, filterer

    //
    val config : IndexWriterConfig = new IndexWriterConfig(analyzer)
    iwriter = new IndexWriter(directory,config)
    println("Configured Lucene")
  }

  after {
    ireader.close()
    directory.close()
    println("Lucene resources closed")
  }

  "The Lucence API " should "index documents for retrieval" in {
    val doc = new Document()
    val text = "This is the text to be indexed."
    doc.add(new Field("fieldname", text, TextField.TYPE_STORED))
    iwriter.addDocument(doc)
    assert(directory.listAll().length==1)
    iwriter.close()
  }

  it should "search" in {
    val isearcher  = new IndexSearcher(ireader)
    // Parse a simple query that searches for "text":
    val parser = new QueryParser("fieldname", analyzer)
    val query : Query = parser.parse("text")
    val  hits : Array[ScoreDoc] = isearcher.search(query, null, 1000).scoreDocs
    assert(hits.length ==1)
    // Iterate through the results:
    for ( i <- 0 to hits.length) {
      val hitDoc = isearcher.doc(hits(i).doc)
      assert("This is the text to be indexed." == hitDoc.get("fieldname"))
    }
  }


  def indexDocs():Unit = {


  }






}