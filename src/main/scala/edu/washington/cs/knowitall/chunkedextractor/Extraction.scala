package edu.washington.cs.knowitall
package chunkedextractor

import edu.washington.cs.knowitall.collection.immutable.Interval
import tool.stem.Lemmatized
import tool.chunk.ChunkedToken
import edu.washington.cs.knowitall.tool.tokenize.Token

case class ExtractionPart[+T <% Token](text: String, tokens: Seq[T], interval: Interval) {
  override def toString = text
}

object ExtractionPart {
  def fromSentenceTokens[T <% Token](sentenceTokens: Seq[T], interval: Interval, text: String) =
    new ExtractionPart[T](text, sentenceTokens.view(interval.start, interval.end), interval)

  def fromSentenceTokens[T <% Token](sentenceTokens: Seq[T], interval: Interval) =
    new ExtractionPart(sentenceTokens.view(interval.start, interval.end).iterator.map(_.string).mkString(" "), sentenceTokens.view(interval.start, interval.end), interval)
}

case class BinaryExtraction[+T <% Token](arg1: ExtractionPart[T], rel: ExtractionPart[T], arg2: ExtractionPart[T]) {
  override def toString = Iterable(arg1, rel, arg2).mkString("(", "; ", ")")

  def text = Iterable(arg1.text, rel.text, arg2.text).mkString(" ")
  def interval = Interval.span(Iterable(arg1.interval, rel.interval, arg2.interval))
  def tokens = arg1.tokens ++ rel.tokens ++ arg2.tokens
}

class BinaryExtractionInstance[+T <% Token](val extr: BinaryExtraction[T], val sent: Seq[T])