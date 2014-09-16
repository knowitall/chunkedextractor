package edu.knowitall.chunkedextractor

import org.allenai.nlpstack.core.ChunkedToken

trait JavaChunkedExtractor {
  def apply(tokens: Seq[ChunkedToken]): Seq[BinaryExtractionInstance[ChunkedToken]]
  def extractWithConfidence(tokens: Seq[ChunkedToken]): Seq[(Double, BinaryExtractionInstance[ChunkedToken])]
}
