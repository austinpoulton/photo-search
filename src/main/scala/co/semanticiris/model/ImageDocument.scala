package co.semanticiris.model

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import scala.collection.immutable.HashMap

/**
  * This class encapsulates a image document and wraps a Lucene Document
  * @param photoId
  * @param captions
  */

class ImageDocument (val photoId : String, val  captions : Map[Int,Caption]) {

  require(captions.values.map(c => c.photoId  == photoId).reduce((x,y) => x && y ))

  final val IMAGE_DESC : String = "IMAGE_DESC"

  /**
    * @return a map of term frequencies
    */
  def termFrequencyMap():Map[String,Int] = {
    val allWords = captions.values.toList.flatMap(c => c.words)
    val wordMap = allWords.foldLeft(new HashMap[String,Int]) { (tm, w) => if (tm.get(w) == None) tm + (w->1) else tm + (w-> (tm(w)+1)) }
    wordMap
  }


  /**
    * @return a Lucene document
    */
  def document(): Document = {
    val textList = for (c <- captions.values) yield c.words.mkString(" ")
    val doc : Document = new Document()
    val field : Field = new Field(IMAGE_DESC, textList.mkString(" "),TextField.TYPE_STORED )
    doc.add(field)
    doc
  }

  /**
    * Add a caption to an ImageDocument
    * @param caption
    * @return a new ImageDocument
    */

  def + (caption : Caption) : ImageDocument  = {
    require(caption.photoId == photoId)
    ImageDocument(photoId, caption :: captions.values.toList)
  }

  override def toString(): String = "Photo Id: "+photoId + " terms:\n" + termFrequencyMap
}

object ImageDocument {

  def apply(photoId: String, captions : List[Caption]):ImageDocument = {
    new ImageDocument(photoId,captions.map(c=> (c.captionId, c)).toMap)
  }

  def apply(photoId: String, caption : Caption):ImageDocument = {
    new ImageDocument(photoId, Map(caption.captionId ->caption))
  }
}