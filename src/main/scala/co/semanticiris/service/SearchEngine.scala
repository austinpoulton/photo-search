package co.semanticiris.service

import co.semanticiris.model.ImageDocument
import org.apache.lucene.document.Document
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{IndexSearcher, Query}
import org.apache.lucene.search.similarities.Similarity
import org.apache.lucene.store.RAMDirectory

import scala.collection.immutable.HashMap

/**
  * Created by austin on 26/03/2016.
  */
class SearchEngine(val directory : RAMDirectory, val similarity : Similarity, val analyzer: Analyzer, val maxResults : Int) {


  def search(searchStrs : Array[String]) : Unit = {
    val analyzer : Analyzer = new StandardAnalyzer()
    val qp = new QueryParser(ImageDocument.IMAGE_CAPTIONS, analyzer)
    val queries : Array[Query] = searchStrs.map(s => qp.parse(s))
    search(queries)
  }

  def search(queries : Array[Query]) : Map[Query, ResultEntry] = {
    val ireader : DirectoryReader = DirectoryReader.open(directory)
    val isearcher  = new IndexSearcher(ireader)
    // set the specific similarity for this retrieval model
    isearcher.setSimilarity(similarity)
    val qHitsTuples = queries.toList.map(q => (q,isearcher.search(q, maxResults)))
    // todo - check this !!!!
    val results = qHitsTuples.foldLeft(new HashMap[Query,ResultEntry]) {
      (hm,qHitTuple) => hm + (qHitTuple._1 -> (ResultEntry(qHitTuple._2.totalHits, qHitTuple._2.scoreDocs.map(sd => SearchResult(sd.score, isearcher.doc(sd.doc))).toList)))
    }
    ireader.close()
    results
  }

  def benchmarkSearch():Unit = ???

}

class ResultEntry(val totalHits : Int, results : List[SearchResult])

object ResultEntry {
  def apply(totalHits : Int, results : List[SearchResult]):ResultEntry = new ResultEntry(totalHits, results.sortWith(_.score > _.score))
}

case class SearchResult(val score : Double,val doc : Document)

//  extends Ordered[SearchResult] {
//
//  override def compare(that: SearchResult): Int = {
//    if (this.score == that.score)
//      0
//    else if (this.score > that.score)
//      1
//    else
//      -1
//  }
//}

object SearchEngine {



}
