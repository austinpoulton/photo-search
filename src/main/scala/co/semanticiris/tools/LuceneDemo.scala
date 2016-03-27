package co.semanticiris.tools

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{TextField, Field, Document}
import org.apache.lucene.index.{IndexWriterConfig, IndexWriter, DirectoryReader}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{ScoreDoc, Query, IndexSearcher}
import org.apache.lucene.store.{RAMDirectory, Directory}

/**
  * Created by austin on 10/03/2016.
  */
object LuceneDemo extends App {

  // standard token stream analyzer
  val analyzer : Analyzer = new StandardAnalyzer()
  // RAM (in-memory) directory
  val directory : Directory  = new RAMDirectory()
  val config : IndexWriterConfig = new IndexWriterConfig(analyzer)
  // index readers
  val iwriter : IndexWriter  = new IndexWriter(directory,config)
  val doc = new Document()
  val text = "This is the text to be indexed."
  doc.add(new Field("fieldname", text, TextField.TYPE_STORED))
  iwriter.addDocument(doc)
  iwriter.close()

  val ireader : DirectoryReader = DirectoryReader.open(directory)
  val isearcher  = new IndexSearcher(ireader)
  // Parse a simple query that searches for "text":
  val parser = new QueryParser("fieldname", analyzer)
  val query : Query = parser.parse("text")
  val  hits : Array[ScoreDoc] = isearcher.search(query, null, 1000).scoreDocs
  assert(hits.length ==1)
  println("found doc")
  // Iterate through the results:
  for ( h <-  hits) {
    val hitDoc = isearcher.doc(h.doc)
    assert("This is the text to be indexed." == hitDoc.get("fieldname"))
    println("Hit document: " + hitDoc.get("fieldname"))
  }

  ireader.close()
  directory.close()
}