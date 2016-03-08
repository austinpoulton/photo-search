package co.semanticiris.service

import org.scalatest.{BeforeAndAfter, FlatSpec}

import scala.io.Source

/**
  * Created by austin on 08/03/2016.
  */
class CaptionLoaderSpec extends FlatSpec with BeforeAndAfter {

  lazy val flickrCaptionStrs = Source.fromURL(getClass.getResource("/Flickr8k.token.txt")).getLines().toList
  lazy val testCaptionStrs = Source.fromURL(getClass.getResource("/Test.Captions.txt")).getLines().toList

  before {
    println("Read captions from file "+ testCaptionStrs.length)
  }

  after {
    println("****")
  }


}
