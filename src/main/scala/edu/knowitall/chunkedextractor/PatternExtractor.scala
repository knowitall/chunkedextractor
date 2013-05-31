package edu.knowitall.chunkedextractor

import java.util.regex.Pattern
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.seqAsJavaListConverter
import com.google.common.base.{Function => GuavaFunction}
import edu.knowitall.collection.immutable.Interval
import edu.knowitall.tool.chunk.ChunkedToken
import edu.knowitall.tool.stem.Lemmatized
import edu.knowitall.openregex
import edu.washington.cs.knowitall.logic.{Expression => LExpression}
import edu.washington.cs.knowitall.logic.LogicExpression
import edu.washington.cs.knowitall.regex.Expression
import edu.washington.cs.knowitall.regex.Match
import edu.washington.cs.knowitall.regex.RegularExpression

object PatternExtractor {
  type Token = Lemmatized[ChunkedToken]
  object Token {
    implicit def patternTokenAsToken(lemmatized: PatternExtractor.Token): edu.knowitall.tool.tokenize.Token = lemmatized.token
  }

  implicit def guavaFromFunction[A, B](f: A => B) = new GuavaFunction[A, B] {
    override def apply(a: A) = f(a)
  }

  implicit def logicArgFromFunction[T](f: T => Boolean) = new LExpression.Arg[T] {
    override def apply(token: T) = f(token)
  }

  def compile[T <: ChunkedToken](pattern: String) =
    openregex.Pattern.compile(pattern, (expression: String) => {
      val valuePattern = Pattern.compile("([\"'])(.*)\\1")

      val baseExpr = new Expression.BaseExpression[Lemmatized[T]](expression) {
        val deserializeToken: String => (Lemmatized[T] => Boolean) = (argument: String) => {
          val Array(base, value) = argument.split("=")

          val matcher = valuePattern.matcher(value)
          if (!matcher.matches()) {
            throw new IllegalArgumentException("Value not enclosed in quote (\") or ('): " + argument)
          }

          val string = matcher.group(2)

          base match {
            case "string" => new Expressions.StringExpression[T](string)
            case "lemma" => new Expressions.LemmaExpression[T](string)
            case "pos" => new Expressions.PostagExpression[T](string)
            case "chunk" => new Expressions.ChunkExpression[T](string)
          }
        }

        val logic: LogicExpression[Lemmatized[T]] =
          LogicExpression.compile(expression, deserializeToken andThen logicArgFromFunction[Lemmatized[T]])

        override def apply(token: Lemmatized[T]): Boolean = logic.apply(token)
      }

      baseExpr: Expression.BaseExpression[Lemmatized[T]]
    })

  def intervalFromGroup(group: openregex.Pattern.Group[_]): Interval = {
    val interval = group.interval

    if (interval.start == -1 || interval.end == -1) {
      Interval.empty
    } else {
      interval
    }
  }
}

abstract class BinaryPatternExtractor[T <: ChunkedToken, B](val expression: openregex.Pattern[Lemmatized[T]])
extends Extractor[Seq[Lemmatized[T]], B] {
  def this(pattern: String) = this(PatternExtractor.compile[T](pattern))

  def apply(tokens: Seq[Lemmatized[T]]): Iterable[B] = {
    val matches = expression.findAll(tokens.toList);

    for (
      m <- matches;
      extraction = buildExtraction(tokens, m);
      if !filterExtraction(extraction)
    ) yield extraction
  }

  protected def filterExtraction(extraction: B): Boolean = false

  protected def buildExtraction(tokens: Seq[Lemmatized[T]], m: openregex.Pattern.Match[Lemmatized[T]]): B
}
