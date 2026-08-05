/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.persistence

import com.barrybecker4.ca.dj.jigo.sgf.{SGFException, SGFGame, SGFLoader}
import com.barrybecker4.ca.dj.jigo.sgf.tokens.{InfoToken, PlacementToken, SGFToken, TextToken}
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.{GamePiece, IRectangularBoard}
import com.barrybecker4.game.common.persistence.GameImporter
import com.barrybecker4.game.twoplayer.common.{TwoPlayerController, TwoPlayerMove, TwoPlayerBoard}
import com.barrybecker4.game.twoplayer.common.persistence.tokens.{Player1MoveToken, Player1NameToken, Player2NameToken, Size2Token, TwoPlayerMoveToken}
import java.io.{IOException, InputStream}

/**
 * Imports the state of a two player game from a file.
 *
 * @author Barry Becker
 */
class TwoPlayerGameImporter[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](controller: TwoPlayerController[M, B])
  extends GameImporter[M, B](controller):

  /**
   * Take what is in the specified file and show it in the viewer.
   * @param iStream some input stream
   */
  def restoreFromStream(iStream: InputStream): Unit =
    if iStream == null then throw new IllegalArgumentException("iStream is null")
    val gameLoader = createLoader
    val game = gameLoader.load(iStream)
    restoreGame(game)

  override def createLoader: SGFLoader =
    new TwoPlayerSGFLoader()

  /**
   * Initialize the board based on the SGF game.
   */
  override def parseSGFGameInfo(game: SGFGame): Unit =
    val gc = controller_.asInstanceOf[TwoPlayerController[?, ?]]

    val e = game.getInfoTokens()
    var numRows = 15 // default unless specified
    var numCols = 12 // default unless specified
    while e.hasMoreElements() do
      e.nextElement() match
        case sizeToken: Size2Token =>
          GameContext.log(2, s"info token columns = ${sizeToken.getNumColumns} rows=${sizeToken.getNumRows}")
          numRows = sizeToken.getNumRows
          numCols = sizeToken.getNumColumns
        case nameToken: Player2NameToken =>
          gc.getPlayers.getPlayer2.setName(nameToken.getName)
        case nameToken: Player1NameToken =>
          gc.getPlayers.getPlayer1.setName(nameToken.getName)
        case _ =>

    gc.getBoard.asInstanceOf[IRectangularBoard[?]].setSize(numRows, numCols)

  /**
   *
   */
  override def processToken(token: SGFToken, moveList: MoveList[M]): Boolean =
    var found = false
    token match
      case _: PlacementToken =>
        moveList.add(createMoveFromToken(token))
        found = true
      case textToken: TextToken =>
        GameContext.log(1, s"text=${textToken.getText()}")
      case _ =>
        GameContext.log(1, s"\nignoring token ${token.getClass().getName}")
    found

  /**
   * Create a move from the two player move Token
   */
  override def createMoveFromToken(token: SGFToken): M =
    val mvToken = token.asInstanceOf[TwoPlayerMoveToken]
    val player1 = token.isInstanceOf[Player1MoveToken]
    TwoPlayerMove.createMove(mvToken.getToY(), mvToken.getToX(), 0, new GamePiece(player1)).asInstanceOf[M]
