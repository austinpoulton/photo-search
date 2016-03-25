package co.semanticiris.model

//import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.collection.mutable.HashMap

/**
  * Created by austin on 23/03/2016.
  */
class ImageCollection {
  var termDocMat : mutable.Map[String,TermEntry] = new HashMap[String, TermEntry]()
  var docs : mutable.Map[String, ImageDocument] = new HashMap[String, ImageDocument]()

  // add a doc to the collection
  def + (idoc : ImageDocument): ImageCollection = {
    docs = docs + (idoc.photoId->idoc)
    val termEntries = TermEntry(idoc)
    for (te <- termEntries) {
      if (termDocMat.get(te.term) == None) termDocMat + (te.term -> te)
      else
        termDocMat + (te.term -> (termDocMat(te.term) + te))
    }
    this
  }
}



object ImageCollection {


  def apply():ImageCollection = ???
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