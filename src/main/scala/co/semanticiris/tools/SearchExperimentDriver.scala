package co.semanticiris.tools

import co.semanticiris.model.ImageCollection

/**
  * Created by austin on 27/03/2016.
  */
object SearchExperimentDriver extends App {


  val imgCollection = ImageCollection.load("/var/irdata/flickrImageColl.ser")
  println("read in image collcation data!!")
  println(" #terms: "+imgCollection.termCount())
  println(" #docs: "+imgCollection.documentCount())

}
