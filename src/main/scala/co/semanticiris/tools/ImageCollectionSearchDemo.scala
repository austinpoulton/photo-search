package co.semanticiris.tools

import co.semanticiris.model.{ImageCollection, ImageDocument}
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{IndexSearcher, Query, ScoreDoc}
import org.apache.lucene.store.RAMDirectory

/**
  * Created by austin on 26/03/2016.
  */
object ImageCollectionSearchDemo extends App {

  val analyzer : Analyzer = new StandardAnalyzer()
  val imgCollection = ImageCollection.load("/var/irdata/flickrImageColl.ser")
  println("read in image collcation data!!")
  println(" #terms: "+imgCollection.termCount())
  println(" #docs: "+imgCollection.documentCount())
  val directory =  imgCollection.indexDocuments()

 // val persistentDir = imgCollection.directory(false, true, "/var/irdata/directories/testdir")
 //  persistentDir.close()

  println("RAM Directory size: "+directory.asInstanceOf[RAMDirectory].ramBytesUsed())

  val ireader : DirectoryReader = DirectoryReader.open(directory)
  val isearcher  = new IndexSearcher(ireader)
  // Parse a simple query that searches for "text":
  val parser = new QueryParser(ImageDocument.IMAGE_CAPTIONS, analyzer)

  val query : Query = parser.parse("person")
  val query1 :Query = parser.parse("watches")
  val query2 :Query = parser.parse("sleeping in lecture")
  val  hits : Array[ScoreDoc] = isearcher.search(query, null, 1000).scoreDocs
  val  hits1 : Array[ScoreDoc] = isearcher.search(query1, null, 1000).scoreDocs
  val  hits2 : Array[ScoreDoc] = isearcher.search(query2, null, 1000).scoreDocs


  println("found "+hits.length + " matching docs for query: "+query.toString)
  println("found "+hits1.length + " matching docs for query: "+query1.toString)
  println("found "+hits2.length + " matching docs for query: "+query2.toString)
  // Iterate through the results:
//  for ( h <-  hits) {
//    val hitDoc = isearcher.doc(h.doc)
//    assert("This is the text to be indexed." == hitDoc.get("fieldname"))
//    println("Hit document: " + hitDoc.get("fieldname"))
//  }

  ireader.close()
  directory.close()
}
