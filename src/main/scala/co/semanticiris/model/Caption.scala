package co.semanticiris.model

import scala.collection.immutable.HashMap

/**
  * Created by austin on 04/03/2016.
  */

/**
  * Convenience model class to represent caption parsed from Flickr 8k dataset.
  * The caption text is parsed into a list of words with punctuation removed
  *
  * @param photoId
  * @param captionId 0..4
  * @param words List of caption words, raw no stop word/entity/stemming  filtering
  */
class Caption(val photoId: String, val captionId : Int, val words: List[String]) extends Serializable {

  /**
    * Returns the term frequency for this caption
    *
    * @return List[String, Int] eg: List(("sky",2),("river",1))
    */
  def termFreq(): List[(String,Int)] = {
    // use foldLeft to traverse list adding/updating a hash map of word frequencies
    val wordMap = words.foldLeft(new HashMap[String,Int]) { (tm, w) => if (tm.get(w)==None) tm + (w->1) else tm + (w-> (tm(w)+1)) }
    wordMap.toList
  }

  def length():Int = words.length

  override def toString():String = "\nCaption for photoID: "+photoId+" caption id: "+captionId+" with tokens: "+words.mkString(",")
}


object Caption extends Serializable {

  def apply(captionStr: String): Option[Caption] = {
    val components = captionStr.split("\\s+") // split on all whitespace
    if (components.length <= 1)
      return None
    val photoAndCaptionId = components(0).split("#")
    if (photoAndCaptionId.length <= 1)
      return None
    val words = (for (i <- 1 to components.length - 1) yield (components(i).toLowerCase())).toList
    // remove punctuation in the caption words and any empty strings
    val wordsNoPunct = words.map(s => s.replaceAll("""[\p{Punct}]""", "")).filter(s => s != "")
    // (implicit tf: TermFilter = TermFilter())
    // val filteredWords = tf.filter(wordsNoPunct)
    val capIdOpt = toInt(photoAndCaptionId(1))
    if (capIdOpt == None)
      return None
    val c = new Caption(photoAndCaptionId(0), capIdOpt.get, wordsNoPunct)
    Some(c)
  }

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


  implicit class TermFilter(c: Caption) {

    val stopWords: Set[String] = Set("a", "the", "into", "is", "in", "it", "its", "an", "on", "of", "with", "he", "she", "her","hers", "his", "there", "at",
                                     "and", "are","to", "by", "up", "for","as", "or", "no")

    def filterStopWords() = {
      new Caption(c.photoId, c.captionId, c.words.filter(w => !stopWords(w)))
    }

    def filterNumbers() = {
      new Caption(c.photoId,c.captionId, c.words.filter(w => !isNumeric(w)))
    }

    def isNumeric(s : String) : Boolean = {
      toInt(s) != None || toDouble(s) != None
    }
  }
}