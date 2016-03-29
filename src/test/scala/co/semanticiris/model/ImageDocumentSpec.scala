package co.semanticiris.model

import org.scalatest.FlatSpec

/**
  * Created by austin on 09/03/2016.
  */
class ImageDocumentSpec extends FlatSpec {

  val caption1 = Caption("img1#0\tMad Furry Dog").get
  val caption2 = Caption("img1#1\tAngry Furry Hound").get
  val caption3 = Caption("img1#2\tMad Hairy Dog").get
  val caption4 = Caption("img1#3\tMad hairy light brown dog").get

  val captionList : List[Caption] = List (caption1, caption2, caption3, caption4)


  "A ImageDocument " should "have at least one caption" in {
    val imageDoc = ImageDocument("img1", caption1)
    println(imageDoc)
    assert(imageDoc.captions.size == 1)
    assert(imageDoc.rawTerms().size == 3)
  }

  it should "be created from a list of captions" in {
    val imageDoc = ImageDocument("img1", captionList)
    println(imageDoc)
    println(imageDoc.captions(0))
    assert(imageDoc.captions.size == 4)
    assert(imageDoc.rawTerms().size == 8)
  }

  it should "allow captions to be added to it" in {
    val imageDoc = ImageDocument("img1", caption1)
    val imageDoc2 = imageDoc + caption2
    println(imageDoc2)
    assert(imageDoc2.captions.size == 2)
    assert(imageDoc2.rawTerms().size == 5)
  }

}