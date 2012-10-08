package edu.washington.cs.knowitall
package tool.extractor

import edu.washington.cs.knowitall.tool.stem.Lemmatized
import scala.collection.JavaConverters._
import edu.washington.cs.knowitall.tool.chunk.ChunkedToken
import edu.washington.cs.knowitall.collection.immutable.Interval
import edu.washington.cs.knowitall.regex.Match
import edu.washington.cs.knowitall.regex.RegularExpression
import edu.washington.cs.knowitall.tool.chunk.OpenNlpChunker
import edu.washington.cs.knowitall.tool.stem.MorphaStemmer

class Nesty
  extends BinaryPatternExtractor[BinaryExtraction](Nesty.pattern) {
  
  lazy val reverb = new ReVerb
  
  override def apply(tokens: Seq[PatternExtractor.Token]): Iterable[BinaryExtraction] = {
    val reverbExtractions = reverb.extract(tokens.map(_.token))
    apply(tokens, reverbExtractions)
  }

  def apply(tokens: Seq[PatternExtractor.Token], reverbExtractions: Iterable[BinaryExtraction]): Iterable[BinaryExtraction] = {
    val transformed = 
      tokens.iterator.zipWithIndex.map { case (t, i) =>
        val ext = reverbExtractions.flatMap {
          case extr if (extr.arg1.interval.start == i) =>
            Some("B-ARG1")
          case extr if (extr.arg1.interval superset Interval.singleton(i)) =>
            Some("I-ARG1")
          case extr if (extr.rel.interval.start == i) =>
            Some("B-REL")
          case extr if (extr.rel.interval superset Interval.singleton(i)) =>
            Some("I-REL")
          case extr if (extr.arg2.interval.start == i) =>
            Some("B-ARG2")
          case extr if (extr.arg2.interval superset Interval.singleton(i)) =>
            Some("I-ARG2")
          case _ => None
        }.mkString(":")
        
        t.copy(token= new ChunkedToken(t.token.chunk + ":" + ext, t.token.postag, t.token.string, t.token.offset))
    }.toSeq
    
    super.apply(transformed)
  }

  override def buildExtraction(tokens: Seq[PatternExtractor.Token], m: Match[PatternExtractor.Token]) = {
    val relation = new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.group("baseRelation")))
    
    new Nesty.NestedExtraction(
      new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.group("arg1"))),
      new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.group("nestedRelation"))),
      new BinaryExtraction(
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.group("baseArg1"))),
        relation,
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.group("baseArg2")))))
  }
}

object Nesty {
  class NestedExtraction(arg1: ExtractionPart, rel: ExtractionPart, nested: BinaryExtraction)
    extends BinaryExtraction(arg1, rel, new ExtractionPart(nested.interval, nested.text))

  val verbs = List("be", "say", "have", "believe",
    "tell", "suggest", "argue", "indicate", "claim", "note", "know",
    "show", "state", "find", "conclude", "report", "means", "announce",
    "think", "warn", "write", "add", "demonstrate", "appear", "reveal",
    "agree", "assert", "acknowledge", "hope", "realize", "fear",
    "suspect", "mean", "feel", "see", "explain", "confirm", "mention",
    "ask", "seem", "observe", "estimate", "admit", "recognize",
    "allege", "insist", "require", "discover", "declare", "imply",
    "give", "deny", "understand", "express", "recommend", "worry",
    "point", "maintain", "contend", "stress", "prove", "demand",
    "learn", "hear", "assume", "predict", "inform", "complain",
    "reflect", "provide", "hold", "testify", "request", "notice",
    "assess", "remind", "wish", "speculate", "raise", "forget",
    "decide", "promise", "pray", "expect", "ensure", "challenge",
    "recall", "present", "determine", "doubt", "concede", "reply",
    "reject", "propose", "make", "include", "emphasize", "concern",
    "charge", "take", "remains", "receive", "exist", "assure", "teach",
    "rule", "respond", "remember", "reinforce", "happen", "do",
    "caution", "underscore", "turn", "threaten", "signal", "repeat",
    "release", "relate", "presume", "discuss", "confess", "bear",
    "advise", "trust", "reiterate", "disclose", "dictate", "convince",
    "consider", "anticipate", "answer", "accept", "will", "swear",
    "regret", "order", "issue", "increase", "illustrate", "ignore",
    "establish", "continue", "certify", "bemoan", "affirm", "surface",
    "support", "seize", "sa", "refute", "prompt", "posit", "offer",
    "live", "leave", "lack", "invite", "imagine", "highlight",
    "follow", "fail", "examine", "build", "boast", "begin", "urge",
    "theorize", "represent", "remain", "reason", "proclaim", "pretend",
    "postulate", "perceive", "outline", "mark", "list", "judge",
    "guarantee", "grasp", "go", "face", "emerge", "echo", "dispute",
    "deserve", "describe", "decree", "control", "contain", "cite",
    "cause", "brag", "bolster", "aver", "alert", "wwa", "vow", "voice",
    "use", "underline", "thank", "survey", "strengthen", "spread",
    "specify", "speak", "signify", "sense", "send", "rebuff", "read",
    "reach", "put", "protest", "prescribe", "postpone", "pledge",
    "pick", "persuade", "operate", "object", "need", "nag", "muslim",
    "mirror", "lose", "lessen", "keep", "get", "generate", "form",
    "explore", "eply", "embody", "dismiss", "disagree", "detonate",
    "denounce", "come", "claus", "clarify", "circulate", "call",
    "brush", "bring", "alarm")

  final val nestedRelationPatternString =
    "(?:(?:(?:<pos='RB'>? <pos='MD'>? (?:<chunk='.-VP.*' & pos='VB.?' & lemma='" + verbs.mkString("|") + "'>) <pos='RP|TO'>? <pos='RB'>?)+" +
      "(?:(?:<chunk='B-NP.*'> <chunk='I-NP.*'>*)*)) |" +
      "(?:(?:<pos='RB'>? <pos='MD'>? (?:<chunk='.-VP.*' & pos='VB.?'>) <pos='RP|TO'>? <pos='RB'>?)+" +
      "(?:(?:<chunk='B-NP.*'> <chunk='I-NP.*'>*)* <string='that'>)))"

  // The pattern for a nested relation is defined as...
  final val pattern =
    // A noun phrase, with optional PP attachment (don't allow because)
    "(<arg1>:<chunk='B-NP.*' & !string='who|which'> <chunk='I-NP.*'>* (?: <pos='IN|TO' & !string='so|because'> <chunk='B-NP.*'> <chunk='I-NP.*'>*)* <string='who|which'>?)" +
      // Followed by a nested relation phrase (with negative lookahead to
      // prevent the next noun phrase from "absorbing" part of the nested
      // relation)
      "(<nestedRelation>:" + nestedRelationPatternString + "+)" +
      // Followed by another noun phrase
      // Sometimes OpenNLP classifies "that" as the start of the chunk so we
      // allow the chunk to start with I-NP.
      "(<baseArg1>:<chunk='.*:B-ARG1.*'> <chunk='.*:I-ARG1.*'>*)" +
      // Followed by a base relation phrase (again with negative lookahead)
      "(<baseRelation>:<chunk='.*:B-REL.*'> <chunk='.*:I-REL.*'>*)" +
      // Followed by another noun phrase (possibly starting from inside)
      "(<baseArg2>:<chunk='.*:B-ARG2.*'> <chunk='.*:I-ARG2.*'>*)"

  def main(args: Array[String]) {
    System.out.println("Creating the nested relation extractor... ")
    val nesty = new Nesty()

    if (args.length > 0 && (args(0) equals "--pattern")) {
      System.out.println(Nesty.pattern)
    } else {
      System.out.println("Creating the sentence chunker... ")
      val chunker = new OpenNlpChunker()
      val stemmer = new MorphaStemmer()
      System.out.println("Please enter a sentence:")

      try {
        for (line <- scala.io.Source.stdin.getLines) {
          val chunked = chunker.chunk(line)
          val tokens = chunked map stemmer.lemmatizeToken

          for (extraction <- nesty(tokens)) {
            println(extraction)
          }

          System.out.println()
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          System.exit(2)
      }
    }
  }
}
