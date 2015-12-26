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
    val relnoun = new Relnoun(true, true, true)
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
  
  test("VerbBasedExtractor",
      "Barack Obama is the president of the United States.",
      ("Barack Obama", "is the president of", "the United States"))
      

  test("AppositiveExtractor",
      "Barack Obama, the President of the U.S.",
      ("Barack Obama", "[is] the President of", "the U.S."))
      
      
  test("AppositiveExtractor2",
      "Lauren Faust, a cartoonist,",
      ("Lauren Faust", "[is]", "a cartoonist"))

  test("AdjectiveDescriptorExtractor_[of]",
      "United States President Barack Obama gave a speech today.",
      ("Barack Obama", "[is] President [of]", "United States"))
      
  test("AdjectiveDescriptorExtractor__[from]",
      "Indian player Sachin Tendulkar received the Arjuna Award in 1994.",
      ("Sachin Tendulkar", "[is] player [from]", "India"))
      
  test("AdjectiveDescriptorExtractor_title",
      "President Barack Obama gave a speech today.",
      ("Barack Obama", "[is] President [of]", "[UNKNOWN]"))
      
  test("AdjectiveDescriptorExtractor_more_1",
      "Foreign Ministry spokesman Qin Gang.",
      ("Qin Gang", "[is] spokesman [of]", "Foreign Ministry"))
      
  test("AdjectiveDescriptorExtractor_more_2",
      "New Yorker's best staff writer Adam.",
      ("Adam", "[is] best staff writer [of]", "New York"))
      
  test("AdjectiveDescriptorExtractor_more_3",
      "General Motors interim chief executive Ed Whitacre.",
      ("Ed Whitacre", "[is] interim chief executive [of]", "General Motors"))
      
  test("AdjectiveDescriptorExtractor_demonym",
      "Indian President Pranab Mukherjee gave a speech today.",
      ("Pranab Mukherjee", "[is] President [of]", "India"))
      

  test("PossessiveExtractor_[of]",
       "United States' President Barack Obama was in a debate on Wednesday.",
       ("Barack Obama", "[is] President [of]", "United States"))

  test("PossessiveExtractor_[from]",
       "India's player Tendulkar received the Arjuna Award in 1994.",
       ("Tendulkar", "[is] player [from]", "India"))
       
       
  test("PossessiveAppositiveExtractor_[of]",
       "United States' President, Barack Obama, was in a debate on Wednesday.",
       ("Barack Obama", "[is] President [of]", "United States"))
       
  test("PossessiveAppositiveExtractor_[from]",
       "India's player, Tendulkar, received the Arjuna Award in 1994.",
       ("Tendulkar", "[is] player [from]", "India"))

       
  test("PossessiveIsExtractor_[of]",
       "America's President is Barack Obama.",
       ("Barack Obama", "is President [of]", "America"))
       
  test("PossessiveIsExtractor_[from]",
       "India's Player is Sachin.",
       ("Sachin", "is Player [from]", "India"))
       

  test("IsPossessiveExtractor_[of]",
       "Barack Obama is America's President.",
       ("Barack Obama", "is President [of]", "America"))
       
  test("IsPossessiveExtractor_[from]",
       "Tendulkar is India's player.",
       ("Tendulkar", "is player [from]", "India"))
       

  test("OfIsExtractor",
       "The President of the United States is Barack Obama.",
       ("Barack Obama", "is The President of", "the United States"))
       
  
  test("OfCommaExtractor",
       "The Chairperson of the Commission of the African Union, Jean Ping, on Tuesday...",
       ("Jean Ping", "[is] The Chairperson of", "the Commission of the African Union"))
       

  test("PossessiveReverseExtractor_[of]",
       "Barack Obama, America's President, gave a debate on Wednesday.",
       ("Barack Obama", "[is] President [of]", "America"))
       
  test("PossessiveReverseExtractor_[from]",
       "Tendulkar, India's player, received the Arjuna Award in 1994.",
       ("Tendulkar", "[is] player [from]", "India"))
       

  test("ProperNounAdjectiveExtractor_[of]",
       "Barack Obama, the US President, gave a debate on Wednesday.",
       ("Barack Obama", "[is] the President [of]", "United States"))
       
  test("ProperNounAdjectiveExtractor_[from]",
       "Tendulkar, the Indian player, received the Arjuna Award in 1994.",
       ("Tendulkar", "[is] the player [from]", "India"))
}
