package edu.washington.cs.knowitall.chunkedextractor

import edu.washington.cs.knowitall.extractor.ReVerbExtractor
import edu.washington.cs.knowitall.tool.chunk.ChunkedToken
import edu.washington.cs.knowitall.nlp.ChunkedSentence
import edu.washington.cs.knowitall.commonlib.Range
import edu.washington.cs.knowitall.collection.immutable.Interval
import edu.washington.cs.knowitall.nlp.extraction.ChunkedExtraction
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunction
import edu.washington.cs.knowitall.util.DefaultObjects
import edu.washington.cs.knowitall.extractor.conf.ReVerbOpenNlpConfFunction
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction

class ReVerb(val reverb: ReVerbExtractor, val conf: Option[ConfidenceFunction] = None) extends Extractor[Seq[ChunkedToken], BinaryExtractionInstance[ChunkedToken]] {
  def this() = this(new ReVerbExtractor, Some(new ReVerbOpenNlpConfFunction))

  private def confidence(extr: ChunkedBinaryExtraction): Option[Double] =
    conf map (_ getConf extr)

  private def reverbExtract(tokens: Seq[ChunkedToken]) = {
    import collection.JavaConverters._

    val chunkedSentence = new ChunkedSentence(
        tokens.map(token => Range.fromInterval(token.offset, token.offset + token.string.length)).toArray,
            tokens.map(_.string).toArray,
            tokens.map(_.postag).toArray,
            tokens.map(_.chunk).toArray)

    val extrs = reverb.extract(chunkedSentence)
    extrs.asScala
  }

  private def convertExtraction(tokens: Seq[ChunkedToken])(extr: ChunkedBinaryExtraction) = {
    def convertPart(ce: ChunkedExtraction) = {
      val interval = Interval.open(ce.getRange.getStart, ce.getRange.getEnd)
      new ExtractionPart(ce.getText, tokens.view(interval.start, interval.end), interval)
    }

     new BinaryExtraction(convertPart(extr.getArgument1), convertPart(extr.getRelation), convertPart(extr.getArgument2))
  }

  def apply(tokens: Seq[ChunkedToken]) = {
    reverbExtract(tokens) map convertExtraction(tokens) map (extr => BinaryExtractionInstance(extr, tokens))
  }

  def extractWithConf(tokens: Seq[ChunkedToken]): Seq[(Option[Double], BinaryExtractionInstance[ChunkedToken])] = {
    val extrs = reverbExtract(tokens)
    val confs = extrs map this.confidence

    val converted = extrs map (extr => BinaryExtractionInstance(convertExtraction(tokens)(extr), tokens))
    (confs.iterator zip converted.iterator).toList
  }
}
