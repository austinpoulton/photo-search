package co.semanticiris.service

import co.semanticiris.model.{Caption, ImageDocument}

import scala.collection.immutable.HashMap
import scala.io.Source

/**
  * Created by austin on 06/03/2016.
  */

class CaptionLoader(val captionFileName: String = "/Flickr8k.token.txt") {


  def loadCaptions():Map[String, ImageDocument] = {
    // read the caption file into a list of Strings for each line
    val captionStrs = Source.fromURL(getClass.getResource(captionFileName)).getLines().toList
    // parse each string into a caption: Caption(s)
    // then map each caption into a tuple (imageId, captionId, caption): map(c => (c.photoId, c.captionId, c))
    val captionTupleList = captionStrs.map(s => Caption(s)).filter(copt => copt != None).map(c => (c.get.photoId, c.get.captionId, c.get))

    // now things get interesting, map each tuple into a HashMap[PhotoId, ImageDoc]
    val photoDocMap = captionTupleList.foldLeft(new HashMap[String, ImageDocument]) { (imgDocMap, capTuple) =>
      if (imgDocMap.get(capTuple._1) == None) imgDocMap + (capTuple._1 -> ImageDocument(capTuple._1, capTuple._3))
      else imgDocMap + (capTuple._1 -> (imgDocMap(capTuple._1) + capTuple._3))
    }
    photoDocMap
  }
}