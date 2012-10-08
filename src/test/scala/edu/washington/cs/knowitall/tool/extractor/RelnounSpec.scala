package edu.washington.cs.knowitall.tool.extractor

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import edu.washington.cs.knowitall.tool.chunk.OpenNlpChunker
import edu.washington.cs.knowitall.tool.stem.MorphaStemmer

@RunWith(classOf[JUnitRunner])
class RelnounSpec extends Specification {
  def extract(sentence: String) = {
    val chunker = new OpenNlpChunker
    val relnoun = new Relnoun
    val chunked = chunker.chunk(sentence)
    val lemmatized = chunked.map(MorphaStemmer.instance.lemmatizeToken)
    relnoun(lemmatized)
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
      extrs.size == 1
    }
    "have the correct extraction" in {
      extrs.head.rel.toString must_== "[is] president [of]"
      extrs.head.arg1.toString must_== "Barack Obama"
      extrs.head.arg2.toString must_== "U.S."
    }
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
}