package co.semanticiris.model

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.file.{Path, Paths}

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.search.similarities.{ClassicSimilarity, Similarity}
import org.apache.lucene.store._

import scala.collection.immutable.{HashMap, HashSet}
import scala.collection.mutable


/**
  * Created by austin on 23/03/2016.
  *
  */
class ImageCollection extends Serializable with TermOperations {

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


  /**
    * find all matching term entries - Assumes conjunction logic only
    * @param query
    * @return
    */
  def findTerms(query : String):Option[Set[TermEntry]] = {
    // split into tokens and filter out punctation and stopwords and numbers
    val words = tokenize(query)
//      query.split("\\s").toList.map(s => s.replaceAll("""[\p{Punct}]""", "")).filter(s => s != "").
//      filter(s => !isStopWord(s)).filter(s => !isNumeric(s))
    if (words.length > 0) Some(words.foldLeft(new HashSet[TermEntry]()) { (hs, w) => val te = termDocMat.get(w); if (te != None) (hs + te.get) else hs })
    else None
  }

  def findDocuments(query : String): Option[Set[ImageDocument]] = {
    val termOpt = findTerms(query)
    if (termOpt.get != None)
      Some(termOpt.get.toList.foldLeft(new HashSet[ImageDocument]()) {(hs, te) => te.entry.keys.toList.foldLeft(hs) ((hs, imgId) => hs + (docs.get(imgId).get))})
    else None
  }

  def inverseIndexString():String = "Term, Term Count, Document Count, Documents\n"+ (termDocMat.values.toList.map(t => t.toString())).mkString("\n")

  def minString():String = "Term, Term Count, Document Count\n"+ (termDocMat.values.toList.map(t => t.statsString())).mkString("\n")

  def documentString():String = "PhotoId, Doc Length, Unique Terms\n" + (docs.values.toList.map(d => d.photoId + ","+d.length()+","+d.uniqueTerms())).mkString("\n")


  def indexDocuments():RAMDirectory = indexDocuments(new StandardAnalyzer())

  def indexDocuments(stemmed : Boolean):RAMDirectory = if (stemmed) indexDocuments(new EnglishAnalyzer()) else indexDocuments(new StandardAnalyzer())

  def indexDocuments(analyzer: Analyzer):RAMDirectory = indexDocuments(analyzer, new ClassicSimilarity())

  def indexDocuments(analyzer: Analyzer, similarity: Similarity, fieldToIndex : IndexField.Value = IndexField.AllCaptions):RAMDirectory = {
    val directory =  new RAMDirectory()
    val config : IndexWriterConfig = new IndexWriterConfig(analyzer)
    config.setSimilarity(similarity)
    // index readers
    val iwriter : IndexWriter  = new IndexWriter(directory,config)
    for (idoc <- docs.values.toList) {
      iwriter.addDocument(idoc.document(fieldToIndex))
    }
    iwriter.close()
    directory
  }
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

  def save(iColl: ImageCollection, location : String):Unit = {
    val oos = new ObjectOutputStream(new FileOutputStream(location))
    oos.writeObject(iColl)
    oos.close
  }

  def load(location: String):ImageCollection = {
    val ois = new ObjectInputStream(new FileInputStream(location))
    val iColl = ois.readObject.asInstanceOf[ImageCollection]
    ois.close
    iColl
  }
}