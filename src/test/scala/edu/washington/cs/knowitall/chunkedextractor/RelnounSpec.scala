package edu.washington.cs.knowitall.chunkedextractor

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import edu.washington.cs.knowitall.tool.chunk.OpenNlpChunker
import edu.washington.cs.knowitall.tool.stem.MorphaStemmer

@RunWith(classOf[JUnitRunner])
object RelnounSpecTest extends Specification {
  def extract(sentence: String) = {
    val chunker = new OpenNlpChunker
    val relnoun = new Relnoun
    val chunked = chunker.chunk(sentence)
    val lemmatized = chunked.map(MorphaStemmer.instance.lemmatizeToken)
    relnoun(lemmatized)
  }

  "appositive" should {
    val extrs = extract("Barack Obama, the president of the U.S.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.rel.toString must_== "[is] the president of"
      extrs.head.arg1.toString must_== "Barack Obama"
      extrs.head.arg2.toString must_== "the U.S."
    }
  }

  "adjective descriptor" should {
    val extrs = extract("United States president Barack Obama gave a speech today.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.toString must_== "(Barack Obama; [is] president [of]; United States)"
    }
  }

  "adjective descriptor" should {
    val extrs = extract("U.S. president Barack Obama.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.rel.toString must_== "[is] president [of]"
      extrs.head.arg1.toString must_== "Barack Obama"
      extrs.head.arg2.toString must_== "U.S."
    }
  }

  "possessive" should {
    val extrs = extract("United States' president Barack Obama was in a debate on Wednesday.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.rel.toString must_== "[is] president [of]"
      extrs.head.arg1.toString must_== "Barack Obama"
      extrs.head.arg2.toString must_== "United States"
    }
  }

  "possessive appositive" should {
    val extrs = extract("United States' president, Barack Obama, was in a debate on Wednesday.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.rel.toString must_== "[is] president [of]"
      extrs.head.arg1.toString must_== "Barack Obama"
      extrs.head.arg2.toString must_== "United States"
    }
  }

  "possessive is" should {
    val extrs = extract("America's president is Barack Obama.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.rel.toString must_== "is president [of]"
      extrs.head.arg1.toString must_== "Barack Obama"
      extrs.head.arg2.toString must_== "America"
    }
  }

  "is possessive" should {
    val extrs = extract("Barack Obama is America's president.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.rel.toString must_== "is president [of]"
      extrs.head.arg1.toString must_== "Barack Obama"
      extrs.head.arg2.toString must_== "America"
    }
  }

  "of is" should {
    val extrs = extract("The president of the United States is Barack Obama")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.rel.toString must_== "is The president of"
      extrs.head.arg1.toString must_== "Barack Obama"
      extrs.head.arg2.toString must_== "the United States"
    }
  }

  "possessive reverse" should {
    val extrs = extract("Barack Obama, America's president, gave a debate on Wednesday.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.rel.toString must_== "[is] president [of]"
      extrs.head.arg1.toString must_== "Barack Obama"
      extrs.head.arg2.toString must_== "America"
    }
  }

  "proper noun adjective" should {
    val extrs = extract("Barack Obama, the US president, gave a debate on Wednesday")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.rel.toString must_== "[is] the president [of]"
      extrs.head.arg1.toString must_== "Barack Obama"
      extrs.head.arg2.toString must_== "US"
    }
  }
}
