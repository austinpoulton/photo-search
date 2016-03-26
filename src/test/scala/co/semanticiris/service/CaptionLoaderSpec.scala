package co.semanticiris.service

import co.semanticiris.model.Caption
import org.scalatest.{BeforeAndAfter, FlatSpec}

import scala.io.Source

/**
  * Created by austin on 08/03/2016.
  */
class CaptionLoaderSpec extends FlatSpec  {

  lazy val flickrCaptionStrs = Source.fromURL(getClass.getResource("/Flickr8k.token.txt")).getLines().toList
  lazy val testCaptionStrs = Source.fromURL(getClass.getResource("/Test.Captions.txt")).getLines().toList

  "A CaptionLoader" should "load all captions into a image document map" in {
    val captionLoader = new CaptionLoader()
    val imgDocMap = captionLoader.loadCaptions()
    println("image document map: "+imgDocMap.size)
    // assert we have got the full 8k img documents in the map
    assert(imgDocMap.size == 8093)

  }
}
