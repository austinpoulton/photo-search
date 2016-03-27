package co.semanticiris.model

import org.scalatest.{BeforeAndAfter, FlatSpec}
import scala.io.Source

/**
  * Created by austin on 04/03/2016.
  */
class CaptionSpec extends FlatSpec with BeforeAndAfter {

  /** test fixtures */
  final val validCaption1WithTab     = "1000268201_693b08cb0e.jpg#0\tA child in a pink dress is climbing up a set of stairs in an entry way ."
  final val validCaption1WithTabs    = "1000268201_693b08cb0e.jpg#0\t\tA child in a pink dress is climbing up a set of stairs in an entry way ."
  final val validCaption2WithTab     = "101654506_8eb26cfb60.jpg#4\tA dog is running in the snow"
  final val corruptCaption1         = "3231211#@ \t"
  final val corruptCaption2         = "\t#\t"
  final val caption1                = "1007320043_627395c3d8.jpg#0\tA child playing on a rope net "



  "A Caption " should "parse caption text with spaces" in {
    val capOpt = Caption(validCaption1WithTab)
    assert(capOpt != None)
    val c = capOpt.get
    println(c)
    assert(c.photoId == "1000268201_693b08cb0e.jpg")
    assert(c.captionId == 0)
    assert(c.words.length == 8)
  }

  it should "parse caption text with tabs and spaces" in {
    val capOpt = Caption(validCaption1WithTabs)
    assert(capOpt != None)
    val c = capOpt.get
    println(c)
    assert(c.photoId == "1000268201_693b08cb0e.jpg")
    assert(c.captionId == 0)
    assert(c.words.length == 8)
  }

  it should "fail to parse corrupted caption text" in {
    val capOpt = Caption(corruptCaption1)
    assert(capOpt == None)
  }

  it should "filter out simple stopwords" in {
    val capOpt = Caption(validCaption2WithTab)
    val c = capOpt.get.filterStopWords()
    println(c)
    assert(c.words.length == 3)
    assert(c.words == List("dog","running", "snow"))
  }

}
