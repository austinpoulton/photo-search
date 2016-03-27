package co.semanticiris.model

import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{IndexSearcher, Query, ScoreDoc}
import org.apache.lucene.search.similarities.Similarity
import org.apache.lucene.store.RAMDirectory

/**
  * Created by austin on 26/03/2016.
  */
class SearchExperiment(val directory : RAMDirectory, val queries : Array[Query], val similarity : Similarity) {



  def run(): Unit = {

    val ireader : DirectoryReader = DirectoryReader.open(directory)
    val isearcher  = new IndexSearcher(ireader)
    // set the specific similarity for this retrieval model
    isearcher.setSimilarity(similarity)

    val hits = queries.toList.map(q => isearcher.search(q, null, 1000).scoreDocs)


  }
}