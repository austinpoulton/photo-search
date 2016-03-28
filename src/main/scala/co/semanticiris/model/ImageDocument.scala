package co.semanticiris.model

import java.io.StringReader
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import scala.collection.immutable.HashMap
import org.apache.lucene.analysis.en.PorterStemmer

/**
  * This class encapsulates a image document and wraps a Lucene Document
  * @param photoId
  * @param captions
  */

class ImageDocument (val photoId : String, val  captions : Map[Int,Caption]) extends Serializable {

  require(captions.values.map(c => c.photoId  == photoId).reduce((x,y) => x && y ))

  /**
    * @return a map of term frequencies
    */
  def rawTerms(): Map[String,Int] = {

//    val words = (captions.values.flatMap(c => c.text.split("\\p+").toList)).map(s => s.trim)
//    val termMap = words.foldLeft(new HashMap[String,Int]) { (tm, w) => if (tm.get(w) == None) tm + (w->1) else tm + (w-> (tm(w)+1)) }
    val allWords = captions.values.toList.flatMap(c => c.words)
    val wordMap = allWords.foldLeft(new HashMap[String,Int]) { (tm, w) => if (tm.get(w) == None) tm + (w->1) else tm + (w-> (tm(w)+1)) }
    wordMap
  }

  /**
    * @return term frequency map after stopword and porter stemming has been applied
    */
  def stemmedTerms(): Map[String, Int] = ???
    //val docText = captions.values.map(c => c.text).mkString("\n")
    //val tReader = new StringReader(docText


  def uniqueTerms():Int = rawTerms().size

  def length():Int = rawTerms().values.sum

  def rawLength():Int = captions.values.foldLeft(0)((s,c)=> s+ c.length() )

  /**
    * @return a Lucene document
    */
  def document(): Document = {
    //val docText = (for (c <- captions.values) yield c.text).mkString("\n")
    val captionTexts = for (c <- captions.values) yield c.words.mkString(" ")
    val doc : Document = new Document()

    val capsField : Field = new Field(ImageDocument.IMAGE_CAPTIONS, captionTexts.mkString(" ") ,TextField.TYPE_STORED )
    // Todo - frequency map
    val uniqueTermsField : Field = new Field(ImageDocument.UNIQUE_TERMS, rawTerms().keys.toList.mkString(" "), TextField.TYPE_STORED)
    val imageIdField : Field = new Field(ImageDocument.IMAGE_ID, photoId, TextField.TYPE_STORED)
    val docNameField : Field = new Field(ImageDocument.DOC_NAME, photoId, TextField.TYPE_STORED)
    doc.add(capsField)
    doc.add(uniqueTermsField)
    doc.add(imageIdField)
    doc.add(docNameField)
    doc
  }

  /**
    * Add a caption to an ImageDocument
 *
    * @param caption
    * @return a new ImageDocument
    */

  def + (caption : Caption) : ImageDocument  = {
    require(caption.photoId == photoId)
    ImageDocument(photoId, caption :: captions.values.toList)
  }

  override def toString(): String = "Photo Id: "+photoId + " terms:\n" + rawTerms
}

object ImageDocument extends Serializable{


  final val IMAGE_CAPTIONS : String = "body"
  final val CAPTION2 : String = "CAPTION2"
  final val UNIQUE_TERMS : String = "UNIQUE_TERMS"
  final val IMAGE_ID : String = "IMAGE_ID"
  final val DOC_NAME : String = "docname"


  def apply(photoId: String, captions : List[Caption]):ImageDocument = {
    new ImageDocument(photoId,captions.map(c=> (c.captionId, c)).toMap)
  }

  def apply(photoId: String, caption : Caption):ImageDocument = {
    new ImageDocument(photoId, Map(caption.captionId ->caption))
  }
}