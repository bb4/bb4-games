/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.persistence

import com.barrybecker4.ca.dj.jigo.sgf.{SGFException, SGFLoader}
import com.barrybecker4.ca.dj.jigo.sgf.tokens._
import java.io.{IOException, StreamTokenizer}

/**
 * Created on May 27, 2007, 5:35 AM
 * @author Barry Becker
 */
class GoSGFLoader extends SGFLoader {

  /**
   * Reads an SGF token, provided a StreamTokenizer to help with parsing the
   * text into SGFTokens.
   *
   * @param st - The StreamTokenizer from which to read an SGF token.
   * @return An SGFToken representing a piece of information about the game.
   */
  @throws[IOException]
  @throws[SGFException]
  override def readToken(st: StreamTokenizer): SGFToken = {
    val tokenName = st.sval.toUpperCase

    val token: SGFToken = tokenName match {
      case "B" | "BLACK" => new BlackMoveToken()
      case "W" | "WHITE" => new WhiteMoveToken()
      case "C" | "COMMENT" => new CommentToken()
      case "N" | "NAME" => new NodeNameToken()
      case "FG" => new FigureToken()
      case "AR" => new ArrowToken()
      case "CR" => new CircleToken()
      case "SQ" => new SquareToken()
      case "LB" => new LabelToken()
      case "MN" => new MoveNumberToken()
      case "LN" => new LineToken()
      case "DD" => new DimToken()
      case "SL" => new SelectedListToken()
      case "VW" => new ViewToken()
      case "AB" | "ADDBLACK" => new AddBlackToken()
      case "AW" | "ADDWHITE" => new AddWhiteToken()
      case "TE" => new TesujiToken()
      case "BM" => new BadMoveToken()
      case "GB" => new GoodBlackMoveToken()
      case "GW" => new GoodWhiteMoveToken()
      case "DM" => new EvenPositionToken()
      case "UC" => new UnclearPositionToken()
      case "IT" => new InterestingMoveToken()
      case "DO" => new DoubtfulMoveToken()
      case "KO" => new KoToken()
      case "FF" => new FileFormatToken()
      case "GM" | "GAME" => new GameTypeToken()
      case "SZ" | "SIZE" => new SizeToken()
      case "KM" | "KOMI" => new KomiToken()
      case "PW" | "PLAYERWHITE" => new WhiteNameToken()
      case "PB" | "PLAYERBLACK" => new BlackNameToken()
      case "WR" | "WHITERANK" => new WhiteRankToken()
      case "BR" => new BlackRankToken()
      case "DT" | "DATE" => new DateToken()
      case "RE" | "RESULT" => new ResultToken()
      case "TM" | "TIME" => new TimeLimitToken()
      case "BL" => new BlackTimeToken()
      case "WL" => new WhiteTimeToken()
      case "OB" => new BlackStonesLeftToken()
      case "OW" => new WhiteStonesLeftToken()
      case "PM" => new PrintModeToken()
      case "SY" => new SystemToken()
      case "PC" => new PlaceToken()
      case "EV" | "EVENT" => new EventToken()
      case "RO" | "ROUND" => new RoundToken()
      case "SO" | "SOURCE" => new SourceToken()
      case "US" => new UserToken()
      case "GC" => new GameCommentToken()
      case "RU" => new RuleSetToken()
      case "BS" => new BlackSpeciesToken()
      case "WS" => new WhiteSpeciesToken()
      case "GN" | "GAMENAME" => new GameNameToken()
      case "ID" => new GameIDToken()
      case "CA" => new TextToken()
      case "ST" | "OT" | "PL" | "HA" | "AP" | "CP" | "LT" | "PP" | "RG" => new TextToken()
      case _ => throw new SGFException("unexpected token name:" + tokenName)
    }

    token.parse(st)
    token
  }
}
