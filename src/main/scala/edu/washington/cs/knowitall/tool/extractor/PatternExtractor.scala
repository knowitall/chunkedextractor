package edu.washington.cs.knowitall
package tool.extractor

import java.util.regex.Pattern

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.seqAsJavaListConverter

import com.google.common.base.{Function => GuavaFunction}

import edu.washington.cs.knowitall.collection.immutable.Interval
import edu.washington.cs.knowitall.logic.{Expression => LExpression}
import edu.washington.cs.knowitall.logic.LogicExpression
import edu.washington.cs.knowitall.regex.Expression
import edu.washington.cs.knowitall.regex.Match
import edu.washington.cs.knowitall.regex.RegularExpression
import edu.washington.cs.knowitall.tool.chunk.ChunkedToken
import edu.washington.cs.knowitall.tool.stem.Lemmatized

object PatternExtractor {
  type Token = Lemmatized[ChunkedToken]

  implicit def guavaFromFunction[A, B](f: A => B) = new GuavaFunction[A, B] {
    override def apply(a: A) = f(a)
  }

  implicit def logicArgFromFunction[T](f: T => Boolean) = new LExpression.Arg[T] {
    override def apply(token: T) = f(token)
  }

  def compile(pattern: String) =
    RegularExpression.compile(pattern, (expression: String) => {
      val valuePattern = Pattern.compile("([\"'])(.*)\\1")

      val baseExpr = new Expression.BaseExpression[Token](expression) {
        val deserializeToken: String => (Token => Boolean) = (argument: String) => {
          val Array(base, value) = argument.split("=")

          val matcher = valuePattern.matcher(value)
          if (!matcher.matches()) {
            throw new IllegalArgumentException("Value not enclosed in quote (\") or ('): " + argument)
          }

          val string = matcher.group(2)

          base match {
            case "string" => new Expressions.StringExpression(string)
            case "lemma" => new Expressions.LemmaExpression(string)
            case "pos" => new Expressions.PostagExpression(string)
            case "chunk" => new Expressions.ChunkExpression(string)
          }
        }

        val logic: LogicExpression[Token] =
          LogicExpression.compile(expression, deserializeToken andThen logicArgFromFunction[Token])

        override def apply(token: Token): Boolean = logic.apply(token)
      }

      baseExpr: Expression.BaseExpression[Token]
    })

  def intervalFromGroup(group: Match.Group[_]): Interval = {
    val startIndex = group.startIndex()
    val endIndex = group.endIndex() + 1

    if (startIndex == -1 || endIndex == -1) {
      Interval.empty
    } else {
      Interval.open(startIndex, endIndex)
    }
  }
}

abstract class BinaryPatternExtractor[B](val expression: RegularExpression[PatternExtractor.Token]) extends Extractor[Seq[PatternExtractor.Token], B] {
  def this(pattern: String) = this(PatternExtractor.compile(pattern))

  def apply(tokens: Seq[PatternExtractor.Token]): Iterable[B] = {
    val matches = expression.findAll(tokens.asJava);

    for (
      m <- matches.asScala;
      val extraction = buildExtraction(tokens, m);
      if !filterExtraction(extraction)
    ) yield extraction
  }

  protected def filterExtraction(extraction: B): Boolean = false

  protected def buildExtraction(tokens: Seq[PatternExtractor.Token], m: Match[PatternExtractor.Token]): B
}