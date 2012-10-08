package edu.washington.cs.knowitall.tool.extractor

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import edu.washington.cs.knowitall.tool.chunk.OpenNlpChunker
import edu.washington.cs.knowitall.tool.stem.MorphaStemmer

@RunWith(classOf[JUnitRunner])
class NestySpec extends Specification {
  def extract(sentence: String) = {
    val chunker = new OpenNlpChunker
    val nesty = new Nesty
    val chunked = chunker.chunk(sentence)
    val lemmatized = chunked.map(MorphaStemmer.instance.lemmatizeToken)
    nesty(lemmatized)
  }

  "nesty" should {
    val extrs = extract("Michael said that nesty extends reverb.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.toString must_== "(Michael; said that; nesty extends reverb)"
    }
  }

  "nesty without that" should {
    val extrs = extract("Michael said nesty extends reverb.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.toString must_== "(Michael; said; nesty extends reverb)"
    }
  }
}