/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.persistence

import com.barrybecker4.ca.dj.jigo.sgf.{SGFException, SGFLoader}
import com.barrybecker4.ca.dj.jigo.sgf.tokens.*
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.persistence.tokens.{Player1MoveToken, Player1NameToken, Player2MoveToken, Player2NameToken, Size2Token, TwoPlayerMoveToken}
import java.io.{IOException, StreamTokenizer}

/**
 * Loads a two player game from a text file.
 *
 * @author Barry Becker
 */
class TwoPlayerSGFLoader extends SGFLoader:

  /**
   * Reads an SGF token, provided a StreamTokenizer to help with parsing the
   * text into SGFTokens.
   *
   * @param st - The StreamTokenizer from which to read an SGF token.
   * @return An SGFToken representing a piece of information about the game.
   */
  override protected def readToken(st: StreamTokenizer): SGFToken =
    var token: SGFToken = null
    val tokenName = st.sval.toUpperCase()

    // moves are the most common token in an SGF file.
    tokenName match
      case "P1" =>
        token = createPlayer1MoveToken()
      case "P2" =>
        token = createPlayer2MoveToken()

      // Comments, notes, and figures are next most common.
      case "C" | "COMMENT" =>
        token = new CommentToken()
      case "N" | "NAME" =>
        token = new NodeNameToken()

      // Lastly, tokens that belong to the first leaf of the first variation
      // appear once.  These are intentionally placed in this position as a
      // standard convention for JiGo's SGF API.
      case "FF" =>
        token = new FileFormatToken()
      case "GM" | "GAME" =>
        token = new GameTypeToken()
      case "SZ2" | "SIZE" =>
        token = new Size2Token()
      case "PLAYER1" =>
        token = new Player1NameToken()
      case "PLAYER2" =>
        token = new Player2NameToken()
      case "DT" | "DATE" =>
        token = new DateToken()
      case "RE" | "RESULT" =>
        token = new ResultToken()
      case "GC" =>
        token = new GameCommentToken()
      case "GN" | "GAMENAME" =>
        token = new GameNameToken()
      case "ID" =>
        token = new GameIDToken()
      case "CA" =>
        token = new TextToken()
      case _ =>
        throw new SGFException(s"unexpected token name:$tokenName")

    parseToken(token, st)
    token

  /**
   * Now that we know what type of token we have, ask it to parse itself.
   * Most of the parsing is done by the TextToken class.  All tokens are
   * subclasses of SGFToken.
   * @param token game token of some type.
   * @param st stream tokenizer
   */
  private def parseToken(token: SGFToken, st: StreamTokenizer): Unit =
    token.parse(st)
    GameContext.log(2, s"parsed token = ${token.toString()}")

  protected def createPlayer1MoveToken(): TwoPlayerMoveToken =
    new Player1MoveToken()

  protected def createPlayer2MoveToken(): TwoPlayerMoveToken =
    new Player2MoveToken()
