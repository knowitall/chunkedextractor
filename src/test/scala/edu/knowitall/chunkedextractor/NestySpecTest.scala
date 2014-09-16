package edu.knowitall.chunkedextractor

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.allenai.nlpstack.tokenize.FactorieTokenizer
import org.allenai.nlpstack.postag.FactoriePostagger
import org.allenai.nlpstack.chunk.OpenNlpChunker
import org.allenai.nlpstack.lemmatize.MorphaStemmer

@RunWith(classOf[JUnitRunner])
object NestySpecTest extends Specification {
  def extract(sentence: String) = {
    val tokenizer = new FactorieTokenizer()
    val postagger = new FactoriePostagger()
    val chunker = new OpenNlpChunker
    val nesty = new Nesty
    val chunked = chunker.chunk(tokenizer, postagger)(sentence)
    val lemmatized = chunked.map(MorphaStemmer.lemmatizeToken)
    nesty(lemmatized)
  }

  "nesty" should {
    val extrs = extract("Michael said that nesty extends reverb.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.extr.toString must_== "(Michael; said that; nesty extends reverb)"
    }
  }

  "nesty without that" should {
    val extrs = extract("Michael said nesty extends reverb.")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.extr.toString must_== "(Michael; said; nesty extends reverb)"
    }
  }
}
