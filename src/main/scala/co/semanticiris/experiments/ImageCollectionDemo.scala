package co.semanticiris.experiments

import co.semanticiris.model.ImageCollection
import java.io.PrintWriter
import scala.io.Source

/**
  * Created by austin on 25/03/2016.
  */
object ImageCollectionDemo extends  App {

  lazy val flickrCaptionStrs = Source.fromURL(getClass.getResource("/Flickr8k.token.txt")).getLines().toList

  val imgCollection = ImageCollection(flickrCaptionStrs)

  println("image collcation properties:")
  println(" #terms: "+imgCollection.termCount())
  println(" #docs: "+imgCollection.documentCount())

  println(" avg docs length: "+imgCollection.meanDocumentLength())

  Some(new PrintWriter("/Users/austin/DATA/flickrTermsDocTable.csv")).foreach{p => p.write(imgCollection.toString()); p.close}

  Some(new PrintWriter("/Users/austin/DATA/flickrTerms.csv")).foreach{p => p.write(imgCollection.minString()); p.close}

  Some(new PrintWriter("/Users/austin/DATA/flickrDocs.csv")).foreach{p => p.write(imgCollection.documentString()); p.close}


  val ramDir = imgCollection.directory()
  val docs =  ramDir.listAll()
  println("# indexed docs: "+docs.length)
  println("indexed = "+docs.mkString(", "))

  ImageCollection.save(imgCollection,"/Users/austin/DATA/flickrImageColl.ser")

}