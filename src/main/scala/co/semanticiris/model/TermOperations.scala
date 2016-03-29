package co.semanticiris.model

import org.tartarus.snowball.ext.PorterStemmer


/**
  * Created by austin on 26/03/2016.
  */
trait TermOperations {

  val stopWords: Set[String] = Set("a", "the", "into", "is", "in", "it", "its", "an", "on", "of", "with", "he", "she", "her","hers", "his", "there", "at",
    "and", "are","to", "by", "up", "for","as", "or", "no")

  def isStopWord(word : String) :Boolean = {
    stopWords(word)
  }

  def isNumeric(s : String) : Boolean = {
    toInt(s) != None || toDouble(s) != None
  }

  def tokenize(data : String): List[String] = data.split("\\s").toList.map(s => s.replaceAll("""[\p{Punct}]""", ""))
    .filter(s => s != "").filter(s => !isStopWord(s)).filter(s => !isNumeric(s))


  def toInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case e: NumberFormatException => None
    }
  }

  def toDouble(s: String): Option[Double] = {
    try {
      Some(s.toDouble)
    } catch {
      case e: NumberFormatException => None
    }
  }
}
