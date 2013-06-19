package edu.knowitall.chunkedextractor.confidence

import org.slf4j.LoggerFactory
import edu.knowitall.tool.conf.impl.LogisticRegression
import edu.knowitall.tool.conf.FeatureSet
import java.net.URL
import edu.knowitall.chunkedextractor.BinaryExtractionInstance
import edu.knowitall.tool.chunk.ChunkedToken

object SrlConfidenceFunction {
  val logger = LoggerFactory.getLogger(classOf[SrlConfidenceFunction])

  type SrlConfidenceFunction = LogisticRegression[BinaryExtractionInstance[ChunkedToken]]

  val defaultModelUrl = Option(this.getClass.getResource("default-classifier.txt")).getOrElse {
    throw new IllegalArgumentException("Could not load confidence function resource.")
  }

  def loadDefaultClassifier(): SrlConfidenceFunction = {
    fromUrl(ChunkedExtractorFeatureSet, defaultModelUrl)
  }

  def fromUrl(featureSet: FeatureSet[BinaryExtractionInstance[ChunkedToken], Double], url: URL): SrlConfidenceFunction = {
    LogisticRegression.fromUrl(featureSet, url)
  }
}