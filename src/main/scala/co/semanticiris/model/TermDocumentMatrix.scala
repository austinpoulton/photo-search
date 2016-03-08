package co.semanticiris.model

/**
  * Created by austin on 04/03/2016.
  */

sealed class TermEntry(val entry: Map[String, Int]) {


  def documentFrequency(): Int = entry.toList.foldLeft(0)( (s,t)=> s + t._2)

  def documentList(): List[String] = entry.keys.toList

  def documentListWithCounts(): List[(String, Int)] = entry.toList

}


class TermDocumentMatrix {

  var table : Map[String, TermDocumentMatrix] = ???

  def + (termEntry : TermEntry): TermDocumentMatrix = ???

  def addImageDoc(idoc : ImageDocument) : TermDocumentMatrix = ???





}