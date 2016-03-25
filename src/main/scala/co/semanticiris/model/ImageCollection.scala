package co.semanticiris.model

//import scala.collection.immutable.HashMap
import scala.collection.immutable.HashMap
import scala.collection.mutable


/**
  * Created by austin on 23/03/2016.
  */
class ImageCollection {
  var termDocMat : mutable.Map[String,TermEntry] = new mutable.HashMap[String, TermEntry]()
  var docs : mutable.Map[String, ImageDocument] = new mutable.HashMap[String, ImageDocument]()

  // add a doc to the collection
  def + (idoc : ImageDocument): ImageCollection = {
    docs = docs + (idoc.photoId->idoc)
    val termEntries = TermEntry(idoc)
    termDocMat = termEntries.foldLeft(termDocMat) {(tm, te) => if (tm.get(te.term) == None) tm + (te.term ->te) else tm + (te.term -> (tm(te.term) + te))}
    this
  }

  def termCount():Int = termDocMat.keys.size

  def documentCount(): Int = docs.keys.size

  def meanDocumentLength() :Int = docs.values.map(d => d.length()).sum/docs.values.size

  //def get(term : String):Option[TermEntry] = termDocMat(term)

  // convenient
  override def toString():String = "Term, Term Count, Document Count, Documents\n"+ (termDocMat.values.toList.map(t => t.toString())).mkString("\n")

  def minString():String = "Term, Term Count, Document Count\n"+ (termDocMat.values.toList.map(t => t.statsString())).mkString("\n")

  def documentString():String = "PhotoId, Doc Length, Unique Terms\n" + (docs.values.toList.map(d => d.photoId + ","+d.length()+","+d.uniqueTerms())).mkString("\n")
}

object ImageCollection {

  def apply(imgMap: Map[String, ImageDocument]):ImageCollection = {
    var imgColl = new ImageCollection()
    for (img <- imgMap.values) {
      imgColl = imgColl + img
    }
    imgColl
  }

  def apply(captionStrs : List[String]): ImageCollection = {
    val captionTupleList = captionStrs.map(s => Caption(s)).filter(copt => copt != None).map(co => co.get.filterStopWords().filterNumbers()).map(c => (c.photoId, c.captionId, c))

    // now things get interesting, map each tuple into a HashMap[PhotoId, ImageDoc]
    val photoDocMap = captionTupleList.foldLeft(new HashMap[String, ImageDocument]) { (imgDocMap, capTuple) =>
      if (imgDocMap.get(capTuple._1) == None) imgDocMap + (capTuple._1 -> ImageDocument(capTuple._1, capTuple._3))
      else imgDocMap + (capTuple._1 -> (imgDocMap(capTuple._1) + capTuple._3))
    }
    println("image document map: "+photoDocMap.size)
    apply(photoDocMap)
  }
}


//class TermDocumentMatrix(val matrix: Map[String,TermEntry]) {
//
//  //var table : Map[String, TermDocumentMatrix] = ???
//
//  def size():Int = matrix.size
//
//  def + (termEntry : TermEntry): TermDocumentMatrix = ???
//
//  //  def + (imgDoc : ImageDocument): TermDocumentMatrix = {
//  //
//  //    val x = imgDoc.termFrequencyMap.foldLeft(matrix) { (matrix, t) =>
//  //      if (matrix.get(t._1) == None)
//  //        matrix + (t._1 -> TermEntry(t._1, imgDoc.photoId, t._2))
//  //      else
//  //
//  //
//  //
//  //    }
//  //  }
//
//  def addImageDoc(idoc : ImageDocument) : TermDocumentMatrix = ???
//
//
//}
//
//object TermDocumentMatrix {
//
//
//  def pivot(termDocMat : HashMap[String, TermEntry], docTuple :(String, ImageDocument)): HashMap[String, TermEntry] = {
//    val termMap = docTuple._2.termFrequencyMap()
//    termMap.toList.foldLeft(termDocMat) = { (tdMap, termFreqTuple) =>
//
//    }
//  }
//
//
//  def apply(imgDocMap : Map[String, ImageDocument]): TermDocumentMatrix = {
//
//    val tdMat = imgDocMap.foldLeft(new HashMap[String, TermEntry]) { (termMap, docTuple) =>
//      val termMap = docTuple._2.termFrequencyMap()
//
//    }
//  }
//}