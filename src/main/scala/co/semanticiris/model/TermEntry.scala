package co.semanticiris.model

import scala.collection.immutable.HashMap

/**
  * A term entry is a term with a corresponding map of documents in which the term
  * appears and an occurance count of the term within each document
  * @param term the word
  * @param entry a map of documents -> term occurances
  */
class TermEntry(val term : String, val entry: Map[String, Int]) extends Serializable {

  /**
    * @return the number of documents this term appears in
    */
  def documentFrequency(): Int = entry.keys.size

  /**
    * @return the total number of occurances of this term in the corpus/collection
    */
  def termFrequency(): Int = entry.values.toList.sum

  /**
    * @return the list of documents in which this term appears
    */
  def documentList(): List[String] = entry.keys.toList

  /**
    * @return a list of documents and counts for this term
    */
  def documentListWithCounts(): List[(String, Int)] = entry.toList


  override def toString():String = term+", " + termFrequency()+", "+ documentFrequency()+", "+ (entry.keys.toList.map(doc=> doc+" : "+entry(doc))).mkString(" ; ")

  def statsString():String = term + ", " + termFrequency()+", "+ documentFrequency()


  def + (te : TermEntry) : TermEntry = {
    require(te.term == term)
    // ensure the entries are disjoint
    val docSet = entry.keys.toSet
    require(te.entry.keys.filter(doc => docSet(doc)).size ==0)
    val newDocFreqMap = te.entry.foldLeft(entry) { (m, docFreqTuple) =>
      m + (docFreqTuple._1 -> docFreqTuple._2)
    }
    TermEntry(term, newDocFreqMap)
  }
}


object TermEntry extends Serializable {

  def apply(term: String, docId : String, freq: Int):TermEntry = new TermEntry(term, Map(docId -> freq))
  def apply(term: String, docFreq : Map[String, Int]) = new TermEntry(term, docFreq)
  def apply(idoc : ImageDocument) : List[TermEntry] = {
    val termsMap = idoc.rawTerms()
    termsMap.toList.map(tt => TermEntry(tt._1, idoc.photoId,tt._2) )
  }
}