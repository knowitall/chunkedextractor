package edu.washington.cs.knowitall.tool.extractor

import edu.washington.cs.knowitall.extractor.ReVerbExtractor
import edu.washington.cs.knowitall.tool.chunk.ChunkedToken
import edu.washington.cs.knowitall.nlp.ChunkedSentence
import edu.washington.cs.knowitall.commonlib.Range
import edu.washington.cs.knowitall.collection.immutable.Interval
import edu.washington.cs.knowitall.nlp.extraction.ChunkedExtraction

class ReVerb(val reverb: ReVerbExtractor) extends Extractor[Seq[ChunkedToken], BinaryExtraction] {
  def this() = this(new ReVerbExtractor)
  
  def apply(tokens: Seq[ChunkedToken]) = {
    import collection.JavaConverters._
    
    val chunkedSentence = new ChunkedSentence(
        tokens.map(token => Range.fromInterval(token.offset, token.offset + token.string.length)).toArray, 
            tokens.map(_.string).toArray, 
            tokens.map(_.postag).toArray, 
            tokens.map(_.chunk).toArray)
    val extrs = reverb.extract(chunkedSentence)
    
    extrs.asScala.map { extr =>
      def convert(ce: ChunkedExtraction) = {
        new ExtractionPart(Interval.open(ce.getRange.getStart, ce.getRange.getEnd), ce.getText)
      }
      new BinaryExtraction(convert(extr.getArgument1), convert(extr.getRelation), convert(extr.getArgument2))
    }
  }
}