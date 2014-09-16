package edu.knowitall.chunkedextractor

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.allenai.nlpstack.tokenize.FactorieTokenizer
import org.allenai.nlpstack.postag.FactoriePostagger
import org.allenai.nlpstack.chunk.OpenNlpChunker

@RunWith(classOf[JUnitRunner])
object R2A2SpecTest extends Specification {
  def extract(sentence: String) = {
    val tokenizer = new FactorieTokenizer()
    val postagger = new FactoriePostagger()
    val chunker = new OpenNlpChunker
    val r2a2 = new R2A2
    val chunked = chunker.chunk(tokenizer, postagger)(sentence)
    r2a2(chunked)
  }

  "r2a2" should {
    val extrs = extract("Michael ate at the best restaurant in London")
    "have a single extraction" in {
      extrs.size must_== 1
    }
    "have the correct extraction" in {
      extrs.head.extr.toString must_== "(Michael; ate at; the best restaurant in London)"
    }
  }
}
