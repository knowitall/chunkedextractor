package edu.knowitall.chunkedextractor

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import edu.knowitall.tool.chunk.OpenNlpChunker
import edu.knowitall.tool.stem.MorphaStemmer

@RunWith(classOf[JUnitRunner])
object RelnounSpecTest extends Specification {
  def extract(sentence: String) = {
    val chunker = new OpenNlpChunker
    val relnoun = new Relnoun
    val chunked = chunker.chunk(sentence)
    val lemmatized = chunked.map(MorphaStemmer.lemmatizeToken)
    relnoun(lemmatized)
  }

  def test(name: String, sentence: String, extraction: (String, String, String)) = {
    name should {
      val extrs = extract(sentence)
      "have a single extraction" in {
        extrs.size must_== 1
      }
      "have the correct extraction" in {
        extrs.head.extr.rel.toString must_== extraction._2
        extrs.head.extr.arg1.toString must_== extraction._1
        extrs.head.extr.arg2.toString must_== extraction._3
      }
    }
  }

  test("appositive",
      "Barack Obama, the president of the U.S.",
      ("Barack Obama", "[is] the president of", "the U.S."))

  test("adjective descriptor",
      "United States president Barack Obama gave a speech today.",
      ("Barack Obama", "[is] president [of]", "United States"))

  test("adjective descriptor",
      "U.S. president Barack Obama",
      ("Barack Obama", "[is] president [of]", "U.S."))

  test("possessive",
       "United States' president Barack Obama was in a debate on Wednesday.",
       ("Barack Obama", "[is] president [of]", "United States"))

  test("possessive appositive",
       "United States' president, Barack Obama, was in a debate on Wednesday.",
       ("Barack Obama", "[is] president [of]", "United States"))

  test("possessive is",
       "America's president is Barack Obama",
       ("Barack Obama", "is president [of]", "America"))

  test("is possessive",
       "Barack Obama is America's president.",
       ("Barack Obama", "is president [of]", "America"))

  test("of is",
       "The president of the United States is Barack Obama.",
       ("Barack Obama", "is The president of", "the United States"))

  test("possessive reverse",
       "Barack Obama, America's president, gave a debate on Wednesday.",
       ("Barack Obama", "[is] president [of]", "America"))

  test("proper noun adjective",
       "Barack Obama, the US president, gave a debate on Wednesday.",
       ("Barack Obama", "[is] the president [of]", "US"))
}
