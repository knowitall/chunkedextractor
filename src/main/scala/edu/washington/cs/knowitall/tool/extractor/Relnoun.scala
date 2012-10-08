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
import Relnoun._

class Relnoun(val encloseInferredWords: Boolean = true) {
  val subextractors: Seq[BinaryPatternExtractor[BinaryExtraction]] = Seq(
      new AppositiveExtractor(this.encloseInferredWords, Relnoun.nouns),
      new AdjectiveDescriptorExtractor(this.encloseInferredWords, Relnoun.nouns),
      new PossessiveExtractor(this.encloseInferredWords, Relnoun.nouns),
      new PossessiveAppositiveExtractor(this.encloseInferredWords, Relnoun.nouns),
      new OfIsExtractor(this.encloseInferredWords, Relnoun.nouns),
      new PossessiveReverseExtractor(this.encloseInferredWords, Relnoun.nouns),
      new ProperNounAdjectiveExtractor(this.encloseInferredWords, Relnoun.nouns))
  
  def apply(tokens: Seq[Lemmatized[ChunkedToken]]): Seq[BinaryExtraction] = {
    for (
      sub <- subextractors;
      extr <- sub(tokens)
    ) yield extr
  }
}

object Relnoun {
    type Token = Lemmatized[ChunkedToken]


    val properNounChunk = "(?:<chunk=\"B-NP\" & pos=\"NNPS?\"> <chunk=\"I-NP\">*) | (?:<chunk=\"B-NP\"> <chunk=\"I-NP\">* <chunk=\"I-NP\" & pos=\"NNPS?\"> <chunk=\"I-NP\">*)";

    private final val nouns = Array("abbot",
            "abomination", "accessory", "accompanist", "accomplice",
            "accountant", "accuser", "ace", "acquaintance", "active",
            "activist", "actor", "actress", "adherent", "adjunct",
            "administrator", "admiral", "admirer", "adopter", "adult",
            "adversary", "advertiser", "adviser", "advisor", "advocate",
            "affiliate", "aficionado", "agent", "aggressor", "agonist", "aide",
            "alien", "ally", "alternate", "alum", "alumna", "alumnus",
            "amateur", "ambassador", "anachronism", "analyst", "anathema",
            "ancestor", "anchor", "ancient", "angel", "announcer", "annoyance",
            "anomaly", "antagonist", "apologist", "apostle", "apotheosis",
            "applicant", "appointment", "apprentice", "arbiter", "arbitrator",
            "archbishop", "architect", "arrival", "artist", "ass", "asshole",
            "assignee", "assistant", "associate", "atheist", "athlete",
            "attendant", "attendee", "attorney", "attraction", "auditor",
            "aunt", "author", "authority", "avatar", "babe", "baby",
            "bachelor", "back", "backer", "backup", "bag", "banker", "barber",
            "barrister", "bartender", "bassist", "batsman", "bear", "bearer",
            "beast", "beat", "beauty", "beginner", "believer", "belle",
            "bellwether", "beloved", "benefactor", "beneficiary", "best",
            "better", "bidder", "bird", "birth", "bishop", "bitch",
            "blacksmith", "blade", "blogger", "blonde", "blood", "bomber",
            "bomb-expert", "bomb-maker", "bookkeeper", "booster", "bore",
            "borrower", "boss", "bouncer", "bowler", "boxer", "boy",
            "boyfriend", "brain", "breadwinner", "breaker", "breeder", "bride",
            "bridesmaid", "broadcaster", "broker", "brother", "brother-in-law",
            "browser", "brunette", "buddy", "buff", "builder", "bull", "bully",
            "businessman", "butcher", "butt", "buyer", "cadet", "calculator",
            "camper", "canary", "candidate", "canon", "captain", "captive",
            "card", "caregiver", "caretaker", "carpenter", "carrier",
            "cartoonist", "case", "cashier", "casualty", "cat", "catch",
            "catcher", "caterer", "celebrity", "center", "CEO", "CFO", "chair",
            "chairman", "chairperson", "chairwoman", "champ", "champion",
            "chancellor", "chaplain", "character", "charge", "cheerleader",
            "chef", "chemist", "chick", "chicken", "chief", "chieftain",
            "child", "chiropractor", "choreographer", "chorister", "christ",
            "cinematographer", "cipher", "citizen", "classic", "classmate",
            "cleaner", "cleric", "clerk", "client", "clone", "closer", "clown",
            "coach", "coaster", "coauthor", "co-conspirator", "cofounder",
            "co-founder", "cog", "collaborator", "colleague", "collector",
            "colonel", "columnist", "comedian", "comer", "commandant",
            "commander", "commentator", "commissioner", "communicant",
            "communicator", "commuter", "companion", "company", "competition",
            "competitor", "compiler", "complainant", "composer", "computer",
            "conductor", "confidant", "congressman", "connection",
            "connoisseur", "conservative", "consort", "conspirator",
            "constituent", "constructor", "consultant", "consumer", "contact",
            "contemporary", "contender", "contestant", "contractor",
            "contributor", "controller", "convener", "convert", "convict",
            "cook", "coordinator", "cop", "corporal", "correspondent",
            "cosmopolitan", "councillor", "councilman", "counsel", "counselor",
            "count", "counter", "cousin", "cow", "coward", "cowboy",
            "co-worker", "cracker", "crazy", "creator", "creature",
            "creditor", "critic", "crossover", "crusader", "culprit",
            "cultist", "curator", "custodian", "customer", "czar", "dad",
            "daddy", "dame", "dancer", "darling", "date", "daughter",
            "daughter-in-law", "deacon", "dealer", "dean", "dearest", "debtor",
            "defendant", "defender", "delegate", "democrat", "demon",
            "denizen", "dentist", "dependent", "deputy", "descendant",
            "descendent", "designer", "destroyer", "detective", "developer",
            "deviant", "devil", "devotee", "dick", "dictator",
            "differentiator", "diplomat", "diplomate", "director", "disciple",
            "discoverer", "dish", "dissenter", "distributor", "diver", "dj",
            "doctor", "doer", "dog", "donor", "double", "doyen", "dragon",
            "draw", "driver", "drummer", "dry", "duchess", "dud", "dude",
            "duke", "earl", "economist", "editor", "educator", "elder",
            "eldest", "elector", "electrician", "embodiment", "emcee",
            "emeritus", "emperor", "employee", "employer", "end", "enemy",
            "engineer", "enthusiast", "entrant", "entrepreneur", "envoy",
            "equal", "escapee", "evangelist", "examiner", "executive",
            "executor", "exhibitor", "expert", "explorer", "exponent",
            "exporter", "extra", "extremist", "ex-wife", "eyewitness", "face",
            "facilitator", "factor", "failure", "faller", "familiar", "family",
            "fan", "farmer", "father", "father-in-law", "favorite",
            "favourite", "fellow", "female", "fighter", "figure", "figurehead",
            "filmmaker", "finalist", "finder", "finisher", "firefighter",
            "fireman", "firstborn", "fisherman", "fixture", "flop", "florist",
            "flyer", "fodder", "follower", "fool", "foot", "footballer",
            "forefather", "foreigner", "foreman", "forerunner", "forward",
            "founder", "fraud", "freak", "freshman", "friend", "front",
            "front-runner", "fugitive", "fundamentalist", "fundraiser",
            "gainer", "gatekeeper", "geek", "gem", "general", "generator",
            "genius", "gentleman", "geologist", "ghost", "giant", "girl",
            "girlfriend", "giver", "glutton", "goalie", "goalkeeper", "god",
            "godfather", "godmother", "golfer", "governor", "grader",
            "graduate", "granddaddy", "granddaughter", "grandfather",
            "grandmother", "grandson", "great", "grind", "groomsman", "grower",
            "guarantor", "guard", "guardian", "guest", "guide", "guitarist",
            "gunman", "gunner", "guru", "guy", "gymnast", "half-brother",
            "half-sister", "hand", "handler", "handmaid", "handmaiden",
            "hangover", "head", "headliner", "headmaster", "healer",
            "heartbreaker", "heavy", "heel", "heir", "heiress", "help",
            "herald", "hero", "heroine", "hijacker", "hire", "historian",
            "hitter", "holder", "holdover", "homemaker", "homeowner", "hope",
            "host", "hostage", "housewife", "hunk", "hunter", "husband",
            "hypocrite", "ideal", "ideologist", "idiot", "idol", "illustrator",
            "image", "imam", "immigrant", "import", "importer", "incarnation",
            "indexer", "individual", "inducer", "inductee", "industrialist",
            "infant", "informant", "inhabitant", "inheritor", "initiate",
            "initiator", "inmate", "innovator", "inpatient", "insider",
            "inspector", "instigator", "instructor", "instrument", "insurgent",
            "intermediary", "intern", "interpreter", "intimate", "inventor",
            "investigator", "investor", "issue", "...*ist", "jack", "janitor",
            "jerk", "jewel", "jihadist", "joker", "journalist", "judge",
            "junior", "justice", "keeper", "keyboardist", "kicker", "kid",
            "killer", "king", "kingpin", "knight", "knower", "lad", "lady",
            "lamb", "landlord", "landowner", "latecomer", "laughingstock",
            "laureate", "lawmaker", "lawyer", "lead", "leader", "learner",
            "lecturer", "lender", "lesbian", "lessee", "lessor", "letter",
            "letterman", "liar", "liberal", "librarian", "licensee",
            "lieutenant", "life", "lifeguard", "lifesaver", "light",
            "linebacker", "lion", "lobbyist", "locator", "loner", "longer",
            "lord", "loser", "love", "lover", "loyalist", "lump", "machine",
            "machinist", "magician", "maid", "mainstay", "maintainer", "major",
            "maker", "male", "man", "manager", "manufacturer", "marine",
            "mark", "marketer", "marshal", "martyr", "mason", "master",
            "mastermind", "match", "mate", "mater", "material", "matriarch",
            "matron", "mayor", "md", "mechanic", "medalist", "mediator",
            "medium", "member", "mentor", "merchant", "messenger", "messiah",
            "middleman", "midwife", "militant", "millionaire", "mind",
            "minister", "minor", "miss", "missionary", "mistress", "mod",
            "model", "moderator", "modern", "mole", "mom", "monarch",
            "monitor", "monk", "monster", "moron", "mother", "mouse", "mouth",
            "mouthpiece", "mover", "mp", "murderer", "muscle", "musician",
            "mvp", "name", "namesake", "nanny", "narrator", "national",
            "nationalist", "native", "natural", "neighbor", "neighbour",
            "nephew", "nerd", "newbie", "newcomer", "niece", "nigger",
            "nobody", "nominee", "nonresident", "no-show", "notable", "novice",
            "nuisance", "nurse", "nut", "observer", "occupant", "offender",
            "officer", "official", "offspring", "ombudsman", "opener",
            "operative", "operator", "opponent", "opposite", "opposition",
            "oracle", "ordinary", "organiser", "organist", "organizer",
            "originator", "outcast", "outfielder", "outsider", "overseer",
            "owner", "page", "pain", "painter", "pallbearer", "panelist",
            "paragon", "paralegal", "paranoid", "parasite", "paratrooper",
            "parent", "pariah", "parishioner", "parliamentarian",
            "participant", "partner", "part-owner", "party", "passenger",
            "passer", "pastor", "patient", "patriarch", "patriot", "patron",
            "patroness", "pawn", "payer", "paymaster", "pediatrician", "peer",
            "perfectionist", "performer", "perpetrator", "person",
            "personality", "personification", "pest", "pet", "petitioner",
            "pharmacist", "philosopher", "photographer", "physician",
            "physicist", "pianist", "pig", "pill", "pillar", "pilot", "pimp",
            "pioneer", "pirate", "pitcher", "pivot", "placeholder",
            "plaintiff", "planet", "planner", "plant", "player", "pledge",
            "poet", "policeman", "politician", "pop", "pope", "possessor",
            "postdoc", "poster", "pow", "power", "powerhouse", "practitioner",
            "prayer", "preacher", "precursor", "predator", "predecessor",
            "predictor", "premier", "presenter", "president", "prey", "priest",
            "priestess", "primitive", "prince", "princess", "principal",
            "prior", "prisoner", "private", "processor", "producer",
            "professional", "professor", "progenitor", "progeny", "programmer",
            "progressive", "promoter", "proofreader", "prophet", "proponent",
            "proprietor", "prosecutor", "prospect", "prostitute",
            "protagonist", "protector", "protege", "provider", "proxy",
            "psychiatrist", "psychologist", "psychotherapist", "publisher",
            "punk", "pupil", "puppet", "purchaser", "purveyor", "qualifier",
            "quarter", "quarterback", "queen", "rabbi", "racist", "radical",
            "raiser", "rapper", "rat", "reader", "rebel", "receiver",
            "receptionist", "recipient", "recruiter", "rector", "redeemer",
            "referee", "referral", "refugee", "registrant", "registrar",
            "regular", "regulator", "reincarnation", "relation", "relative",
            "relief", "religious", "reminder", "remover", "rep", "replacement",
            "reporter", "repository", "representative", "republican",
            "researcher", "reserve", "reservist", "resident", "respondent",
            "retailer", "revenue", "reviewer", "rider", "ringer", "ringleader",
            "rip", "rival", "rn", "rock", "romantic", "rookie", "roommate",
            "root", "ruler", "runner", "runner-up", "runt", "sage", "sailor",
            "saint", "salesman", "sampler", "satellite", "saver", "savior",
            "saviour", "scanner", "scapegoat", "scholar", "schoolteacher",
            "scientist", "scion", "scorer", "scourge", "scout", "scratch",
            "screenwriter", "screw", "second", "secretary", "seed", "seeker",
            "self", "self-starter", "seller", "semifinalist", "senator",
            "sender", "senior", "sensation", "sensitive", "seperatist",
            "sergeant", "servant", "server", "settler", "shadow", "sham",
            "shareholder", "sharper", "sheep", "shepherd", "sheriff",
            "shill", "shit", "shocker", "shoemaker", "shooter", "shortstop",
            "sibling", "signatory", "signer", "silly", "simple", "singer",
            "sinner", "sire", "sister", "sister-in-law", "skater", "skipper",
            "slave", "slayer", "sleeper", "slip", "smoker", "snake", "sneak",
            "sniper", "soldier", "solicitor", "soloist", "someone", "son",
            "songwriter", "son-in-law", "sophisticate", "sophomore", "sort",
            "soul", "source", "sovereign", "speaker", "spearhead",
            "specialist", "spectator", "speechwriter", "spoiler", "spokesman",
            "spokesperson", "spokeswoman", "sponsor", "sport", "spouse", "spy",
            "square", "staffer", "stakeholder", "stalwart", "standard-bearer",
            "stand-in", "star", "starter", "stepdaughter", "stepfather",
            "stepson", "steward", "stickler", "stiff", "stockholder",
            "straight", "stranger", "strategist", "striker", "stripper",
            "stroke", "strongman", "stud", "student", "study", "subcontractor",
            "subject", "subscriber", "subsidiary", "substitute", "success",
            "successor", "sucker", "sufferer", "suit", "sultan", "sun",
            "superintendent", "superior", "superstar", "supervisor",
            "supplier", "supporter", "suppressor", "supremacist", "surgeon",
            "surrogate", "survivor", "suspect", "sustainer", "sweep",
            "sweetheart", "swell", "swimmer", "tail", "tailor", "talent",
            "target", "taxpayer", "teacher", "teammate", "teaser",
            "technician", "technologist", "teen", "teenager", "televangelist",
            "tenant", "tender", "terror", "terrorist", "tester", "theorist",
            "therapist", "thief", "threat", "tiger", "tiller", "timekeeper",
            "titan", "toast", "tool", "tough", "tourist", "trader",
            "trailblazer", "trailer", "trainer", "traitor", "transfer",
            "translator", "treasurer", "trick", "trier", "triggerman",
            "trooper", "trustee", "tutor", "twin", "type", "uncle", "underdog",
            "undergrad", "undergraduate", "understudy", "underwriter", "user",
            "usher", "vagabond", "valedictorian", "vassal", "vendor",
            "veteran", "veterinarian", "vicar", "victim", "victor", "viewer",
            "villain", "violinist", "virgin", "virtuoso", "visitor",
            "vocalist", "voice", "volunteer", "voter", "waiter", "waitress",
            "ward", "warden", "warlord", "warrior", "watch", "watchdog",
            "webmaster", "whale", "whiz", "wholesaler", "whore", "widow",
            "widower", "wife", "winemaker", "wing", "winner", "witch",
            "witness", "wizard", "wolf", "woman", "worker", "worm",
            "worshipper", "worthy", "wrestler", "writer", "youngster", "youth")

  /**
   * *
   * Extracts relations from phrases such as:
   *  "Chris Curran, a lawyer for Al-Rajhi Banking"
   *  (Chris Curran, (is) a lawyer (for), Al-Rajhi Banking)
   *
   * @author schmmd
   *
   */
  class AppositiveExtractor(private val encloseInferredWords: Boolean, private val nouns: Array[String])
    extends BinaryPatternExtractor[BinaryExtraction](
      AppositiveExtractor.pattern.replace("${relnoun}", nouns.mkString("|"))) {
    val inferredIs = if (encloseInferredWords) "[is]" else "is"

    override def buildExtraction(tokens: Seq[Token], m: Match[Token]) = {
      new BinaryExtraction(
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups().get(1))),
        new ExtractionPart(PatternExtractor.intervalFromGroup(m.groups().get(2)), this.inferredIs + " " + m.groups().get(2).tokens().asScala.map(_.token.string).mkString(" ")),
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups().get(3))))
    }
  }

    object AppositiveExtractor {
        private final val pattern: String =
            // {proper noun}
            "(" + properNounChunk + ")" +
            // {comma}
            "<string=\",\">" +
            // {article}
            "(<string=\"a|an|the\">*" +
            // {adjective or noun}
            "<pos=\"JJ|VBD|VBN|NN|NNP\">*" +
            // {relnoun} {preposition}
            "<string=\"${relnoun}\" & pos=\"NN\"> <pos=\"IN\">)" +
            // {chunk}, the relnoun may have been incorrectly identified
            // as the beginning of the chunk
            "(<chunk=\".-NP\"> <chunk=\"I-NP\">*)"
    }

    /***
     * Extracts relatios from phrases such as:
     *  "Jihad leader Abu Musab Al-Zarqawi"
     *  (Abu Musab Al-Zarqawi, (is) leader (of), Jihad)
     *
     * @author schmmd
     *
     */
    class AdjectiveDescriptorExtractor(private val encloseInferredWords: Boolean, private val nouns: Array[String]) 
    extends BinaryPatternExtractor[BinaryExtraction](
     AdjectiveDescriptorExtractor.pattern.replace("${relnoun}", nouns.mkString("|"))) {
      
     private val inferredIs = if (encloseInferredWords) "[is]" else "is"
     private val inferredOf = if (encloseInferredWords) "[of]" else "of"

    override def buildExtraction(tokens: Seq[Token], m: Match[Token]) = {
      val adjectiveGroup = m.group("adj") match {
        case g if g.text.isEmpty => None
        case g => Some(g)
      }

      val adjective = adjectiveGroup map { adj =>
        adj.tokens.iterator.asScala.map(_.token.string).mkString(" ")
      }
      
      val relation = new ExtractionPart(PatternExtractor.intervalFromGroup(m.group("pred")), inferredIs +
        adjective.map(" " + _ + " ").getOrElse(" ") +
        m.group("pred").tokens.iterator.asScala.map(_.token.string).mkString(" ") + " " + inferredOf)

      val arg2Group = m.group("arg2");
      new BinaryExtraction(
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.group("arg1"))),
        relation,
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(arg2Group)))
    }
  }

  object AdjectiveDescriptorExtractor {
    val pattern =
      // {adjective}
      "(<adj>: <pos=\"JJ|VBD|VBN\">*)" +
        // {proper noun} (don't allow prepositions)
        "(<arg2>: <pos=\"NNS?|NNPS?\">+)" +
        // {relnoun}
        "(<pred>: <string=\"${relnoun}\" & pos=\"nn\">)" +
        // {proper noun} (don't allow prepositions)
        "(<arg1>: <pos=\"nn\">* <pos=\"nnp\">+ <pos=\"nn|nnp\">*)";
  }

  /**
   * *
   * Extracts relatios from phrases such as:
   *  "Hakani's nephew Batsha"
   *  (Batsha, (is) nephew (of), Hakani)
   * @author schmmd
   *
   */
  class PossessiveExtractor(private val encloseInferredWords: Boolean, private val nouns: Array[String])
    extends BinaryPatternExtractor[BinaryExtraction](PossessiveExtractor.pattern.replace("${relnoun}", nouns.mkString("|"))) {

    private val inferredIs = if (encloseInferredWords) "[is]" else "is"
    private val inferredOf = if (encloseInferredWords) "[of]" else "of"

    override def buildExtraction(tokens: Seq[Token], m: Match[Token]) = {
      val relation = new ExtractionPart(PatternExtractor.intervalFromGroup(m.groups().get(2)), inferredIs + " " + m.groups().get(2).tokens.iterator.asScala.map(_.token.string).mkString(" ") + " " + inferredOf)
      
      new BinaryExtraction(
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups.get(3))),
        relation,
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups.get(1))))
    }
  }

  object PossessiveExtractor {
    val pattern =
      // {proper noun} (no preposition)
      "(<pos=\"NNS?\">* <pos=\"NNPS?\">+ <pos=\"NNS?|NNPS?\">*)" +
        // {possessive}
        "<pos=\"POS\">" +
        // {adverb} {adjective} {relnoun}
        "(<pos=\"RB\">* <pos=\"JJ|VBD|VBN\">* <string=\"${relnoun}\" & pos=\"NN\">)" +
        // {proper noun} (no preposition)
        // the proper noun is required to distinguish this noun from the previous
        // consider: Baghdad's deputy governor (deputy is a relnoun)
        "(<pos=\"NN\">* <pos=\"NNP\">+ <pos=\"NN|NNP\">*)";
  }

  /**
   * Extracts relations from phrases such as:
   *  "AUC's leader, Carlos Castano"
   *  (Carlos Castano, (is) leader (of), AUC)
   * @author schmmd
   *
   */
  class PossessiveAppositiveExtractor(private val encloseInferredWords: Boolean, private val nouns: Array[String])
    extends BinaryPatternExtractor[BinaryExtraction](PossessiveAdjectiveExtractor.pattern.replace("${relnoun}", nouns.mkString("|"))) {
    private val inferredIs = if (encloseInferredWords) "[is]" else "is"
    private val inferredOf = if (encloseInferredWords) "[of]" else "of"

    override def buildExtraction(tokens: Seq[Token], m: Match[Token]) = {
      val relation = new ExtractionPart(PatternExtractor.intervalFromGroup(m.groups.get(2)), inferredIs + " " + m.groups.get(2).tokens.iterator.asScala.map(_.token.string).mkString(" ") + " " + inferredOf)
      new BinaryExtraction(
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups.get(3))),
        relation,
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups.get(1))))
    }
  }

  object PossessiveAdjectiveExtractor {
    val pattern: String =
      // {nouns} (no preposition)
      "(<pos=\"NNS?|NNPS?\">+)" +
        // {possessive}
        "<pos=\"POS\">" +
        // {adverb} {adjective} {relnoun}
        "(<pos=\"RB\">* <pos=\"JJ|VBD|VBN\">* <string=\"${relnoun}\" & pos=\"NN\">)" +
        // {comma}
        "<string=\",\">" +
        // {proper np chunk}
        "(" + properNounChunk + ")";
  }

  /**
   * Extracts relations from phrases such as:
   *  "AUC's leader is Carlos Castano"
   *  (Carlos Castano, (is) leader (of), AUC)
   * @author schmmd
   */
  class PossessiveIsExtractor(private val encloseInferredWords: Boolean, private val nouns: Array[String])
    extends BinaryPatternExtractor[BinaryExtraction](PossessiveIsExtractor.pattern.replace("${relnoun}", nouns.mkString(" "))) {

    private val inferredOf = if (encloseInferredWords) "[of]" else "of"

    override def buildExtraction(tokens: Seq[Token], m: Match[Token]) = {
      val relation = new ExtractionPart(PatternExtractor.intervalFromGroup(m.groups().get(2)), m.groups().get(3).tokens.iterator.asScala.mkString(" ") + " " + m.groups().get(2).tokens.iterator.asScala.mkString(" ") + " " + inferredOf)
      new BinaryExtraction(
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups.get(4))),
        relation,
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups.get(1))));
    }
  }

  object PossessiveIsExtractor {
    val pattern =
      // {nouns} (no preposition)
      "(<pos=\"NNS?|NNPS?\">+)" +
        // {possessive}
        "<pos=\"POS\">" +
        // {adverb} {adjective} {relnoun}
        "(<pos=\"RB\">* <pos=\"JJ|VBD|VBN\">* <string=\"${relnoun}\" & pos=\"NN\">)" +
        // {comma}
        "(<lemma=\"be\">)" +
        // {proper np chunk}
        "(" + properNounChunk + ")";
  }

  /**
   * Extracts relations from phrases such as:
   *  "Michael is Don's son"
   *  (Carlos Castano, (is) leader (of), AUC)
   * @author schmmd
   */
  class IsPossessiveExtractor(private val encloseInferredWords: Boolean, private val nouns: Array[String])
  extends BinaryPatternExtractor[BinaryExtraction](IsPossessiveExtractor.pattern.replace("${relnoun}", nouns.mkString("|"))) {

    private val inferredOf = if (encloseInferredWords) "[of]" else "of"

    override def buildExtraction(tokens: Seq[Token], m: Match[Token]) = {
      val relation = new ExtractionPart(PatternExtractor.intervalFromGroup(m.groups().get(4)), m.groups().get(2).tokens.iterator.asScala.mkString(" ") + " " + m.groups().get(4).tokens.iterator.asScala.mkString(" ") + " " + inferredOf)
      new BinaryExtraction(
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups().get(1))),
        relation,
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups().get(3))))
    }
  }

  object IsPossessiveExtractor {
    val pattern =
      // {nouns} (no preposition)
      "(" + properNounChunk + ")" +
        "(<lemma=\"be\">)" +
        "(<pos='NNS?|NNPS?'>+)" +
        "<pos='POS'>" +
        "(<pos=\"RB\">* <pos=\"JJ|VBD|VBN\">* <string=\"${relnoun}\" & pos=\"NN\">)";
  }

  /**
   * Extracts relations from phrases such as:
   *  "the father of Michael is Don"
   *  
   * @author schmmd
   */
  class OfIsExtractor(private val encloseInferredWords: Boolean, private val nouns: Array[String])
    extends BinaryPatternExtractor[BinaryExtraction](OfIsExtractor.pattern.replace("${relnoun}", nouns.mkString("|"))) {

    override def buildExtraction(tokens: Seq[Token], m: Match[Token]) = {
      val relation = new ExtractionPart(PatternExtractor.intervalFromGroup(m.groups().get(1)), m.groups().get(3).tokens.iterator.asScala.map(_.token.string).mkString(" ") + " " + m.groups().get(1).tokens.iterator.asScala.map(_.token.string).mkString(" "))
      new BinaryExtraction(
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups().get(4))),
        relation,
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups().get(2))))
    }
  }

  object OfIsExtractor {
    val pattern =
      "(<chunk='B-NP'> <chunk='I-NP'>* <string='${relnoun}' & pos='NN' & chunk='I-NP'> " +
        "<string='of'>) " +
        "(<chunk='.-NP'> <chunk='I-NP'>*) " +
        "(<lemma='be'>) " +
        "(<chunk='B-NP'> <chunk='I-NP'>*)";
  }

  /**
   * Extracts relations from phrases such as:
   *  Mohammed Jamal, bin Laden's brother
   *  (Mohammed Jamal, (is) brother (of), bin Laden)
   *
   * @author schmmd
   */
  class PossessiveReverseExtractor(private val encloseInferredWords: Boolean, private val nouns: Array[String])
    extends BinaryPatternExtractor[BinaryExtraction](IsPossessiveExtractor.pattern.replace("${relnoun}", nouns.mkString("|"))) {

    private val inferredIs = if (encloseInferredWords) "[is]" else "is"
    private val inferredOf = if (encloseInferredWords) "[of]" else "of"

    override def buildExtraction(tokens: Seq[Token], m: Match[Token]) = {
      val relation = new ExtractionPart(PatternExtractor.intervalFromGroup(m.groups().get(3)), inferredIs + " " + m.groups().get(3).tokens.iterator.asScala.mkString(" ") + " " + inferredOf);
      new BinaryExtraction(
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups().get(1))),
        relation,
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups().get(2))))
    }
  }

  object PossessiveReverseExtractor {
    val pattern =
      // {proper noun} (no preposition)
      "(" + properNounChunk + ")" +
        // comma
        "<string=\",\">" +
        // {np chunk}
        "(<chunk=\"B-NP\"> <chunk=\"I-NP\">*)" +
        // {possessive}
        "<pos=\"POS\">" +
        // {adverb} {adjective} {relnoun}
        "(<pos=\"RB\">* <pos=\"JJ|VBD|VBN\">* <string=\"${relnoun}\" & pos=\"NN\">)" +
        // make sure the relnoun isn't part of a larger np-chunk
        // consider: "...spokesman Suleiman Abu Ghaith , Al-Qaeda 's military chief Saif al-Adel , and two of Osama bin Laden 's sons..."
        "(?:<!chunk=\"I-NP\">|$)";
  }

  /**
   * Extracts relations from phrases such as:
   *  Obama, the US president.
   *  (Obama, (is) president (of), U.S.)
   *
   * @author schmmd
   */
  class ProperNounAdjectiveExtractor(private val encloseInferredWords: Boolean, private val nouns: Array[String])
    extends BinaryPatternExtractor[BinaryExtraction](ProperNounAdjectiveExtractor.pattern.replace("${relnoun}", nouns.mkString("|"))) {
    private val inferredIs = if (encloseInferredWords) "[is]" else "is"
    private val inferredOf = if (encloseInferredWords) "[of]" else "of"

    override def buildExtraction(tokens: Seq[Token], m: Match[Token]) = {
      val relation = new ExtractionPart(PatternExtractor.intervalFromGroup(m.groups().get(4)), inferredIs + " " + (m.groups().get(2).tokens.iterator.asScala.map(_.token.string) ++ m.groups().get(4).tokens.iterator.asScala.map(_.token.string)).mkString(" ") + " " + inferredOf)
      new BinaryExtraction(
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups().get(1))),
        relation,
        new ExtractionPart(tokens, PatternExtractor.intervalFromGroup(m.groups().get(3))))
    }
  }

  object ProperNounAdjectiveExtractor {
    val pattern =
      "(" + properNounChunk + ")" +
        "<string=\",\">" +
        "(<string=\"a|an|the\"> <pos=\"JJ|VBD|VBN\">*)" +
        "(<pos=\"NNP|NN\">* <pos=\"NNP\">+)" +
        "(<pos=\"NN\">* <string=\"${relnoun}\" & pos=\"NN\">)"
  }

  def main(args: Array[String]) {
    System.out.println("Creating the relational noun extractor... ")
    val relnoun = new Relnoun()

    if (args.length > 0 && (args(0) equals "--pattern")) {
      for (extractor <- relnoun.subextractors) {
        System.out.println(extractor.expression);
      }
    } else {

      System.out.println("Creating the sentence chunker... ")
      val chunker = new OpenNlpChunker()
      val stemmer = new MorphaStemmer()
      System.out.println("Please enter a sentence:")

      try {
        for (line <- scala.io.Source.stdin.getLines) {
          val chunked = chunker.chunk(line);
          val tokens = chunked map stemmer.lemmatizeToken

          for (extraction <- relnoun(tokens)) {
            println(extraction);
          }

          System.out.println();
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          System.exit(2)
      }
    }
  }
}
