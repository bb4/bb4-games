/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.persistence

import com.barrybecker4.ca.dj.jigo.sgf.{SGFGame, SGFLoader}
import com.barrybecker4.ca.dj.jigo.sgf.tokens.{SGFToken, TextToken}
import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.blockade.BlockadeController
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition}
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall
import com.barrybecker4.game.twoplayer.blockade.persistence.tokens.{BlockadeMoveToken, Player1BlockadeMoveToken}
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameImporter
import com.barrybecker4.game.twoplayer.common.persistence.tokens.{Player1NameToken, Player2NameToken, Size2Token}

/**
 * Imports the state of a Blockade game from a file.
 *
 * @author Barry Becker
 */
class BlockadeGameImporter(controller: BlockadeController)
  extends TwoPlayerGameImporter[BlockadeMove, BlockadeBoard](controller):

  override def createLoader: SGFLoader =
    new BlockadeSGFLoader()

  /**
   * Initialize the board based on the SGF game.
   */
  override def parseSGFGameInfo(game: SGFGame): Unit =
    val gc = controller.asInstanceOf[BlockadeController]

    val e = game.getInfoTokens()
    var numRows = 15 // default unless specified
    var numCols = 12 // default unless specified
    while e.hasMoreElements() do
      e.nextElement() match
        case sizeToken: Size2Token =>
          numRows = sizeToken.getNumRows
          numCols = sizeToken.getNumColumns
        case nameToken: Player2NameToken =>
          gc.getPlayers.getPlayer2.setName(nameToken.getName)
        case nameToken: Player1NameToken =>
          gc.getPlayers.getPlayer1.setName(nameToken.getName)
        case _ =>
    gc.getBoard.setSize(numRows, numCols)

  override def processToken(token: SGFToken, moveList: MoveList[BlockadeMove]): Boolean =
    token match
      case _: BlockadeMoveToken =>
        moveList.add(createMoveFromToken(token))
        true
      case textToken: TextToken =>
        GameContext.log(1, "text=" + textToken.getText())
        false
      case _ =>
        GameContext.log(1, "\nignoring token " + token.getClass.getName)
        false

  /**
   * Create a blockade move from the MoveToken
   */
  override def createMoveFromToken(token: SGFToken): BlockadeMove =
    val mvToken = token.asInstanceOf[BlockadeMoveToken]
    val player1 = token.isInstanceOf[Player1BlockadeMoveToken]
    var wall: BlockadeWall = null
    if mvToken.hasWall then
      wall = new BlockadeWall(
        new BlockadeBoardPosition(mvToken.getWallPoint1.y, mvToken.getWallPoint1.x),
        new BlockadeBoardPosition(mvToken.getWallPoint2.y, mvToken.getWallPoint2.x)
      )

    BlockadeMove.createMove(
      new ByteLocation(mvToken.getFromY, mvToken.getFromX),
      new ByteLocation(mvToken.getToY(), mvToken.getToX()),
      0,
      new GamePiece(player1),
      wall
    )
