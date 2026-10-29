/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.persistence

import com.barrybecker4.ca.dj.jigo.sgf.{SGFGame, SGFLoader}
import com.barrybecker4.ca.dj.jigo.sgf.tokens.*
import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.common.board.IRectangularBoard
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameImporter
import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.GoOptions

/**
 * Imports the state of a Go game from a file.
 */
class GoGameImporter(controller: GoController)
  extends TwoPlayerGameImporter[GoMove, GoBoard](controller):

  override def createLoader: SGFLoader = new GoSGFLoader()

  /**
   * Initialize the board based on the SGF game.
   */
  override def parseSGFGameInfo(game: SGFGame): Unit =
    val gc = controller_
    val players = gc.getPlayers

    val e = game.getInfoTokens
    var size = 13
    while e.hasMoreElements do
      e.nextElement() match
        case sizeToken: SizeToken =>
          size = sizeToken.getSize
        case komiToken: KomiToken =>
          gc.getOptions.asInstanceOf[GoOptions].setKomi(komiToken.getKomi)
        case nameToken: WhiteNameToken =>
          players.getPlayer2.setName(nameToken.getName)
        case nameToken: BlackNameToken =>
          players.getPlayer1.setName(nameToken.getName)
        case _: RuleSetToken =>
        case token =>
          GameContext.log(1, s"Ignoring  token =${token.getClass.getName} while parsing.")

    gc.getBoard.asInstanceOf[IRectangularBoard[?]].setSize(size, size)

  override def processToken(token: SGFToken, moveList: MoveList[GoMove]): Boolean =
    token match
      case _: MoveToken =>
        moveList.add(createMoveFromToken(token))
        true
      case placementToken: AddBlackToken =>
        addMoves(placementToken, moveList)
        true
      case placementToken: AddWhiteToken =>
        addMoves(placementToken, moveList)
        true
      case _: TextToken =>
        false
      case _ =>
        GameContext.log(0, s"Ignoring token ${token.getClass.getName} while processing.")
        false

  /**
   * add a sequence of moves all at once.
   * Such as placing handicaps when reading from an sgf file.
   */
  private def addMoves(token: PlacementListToken, moveList: MoveList[GoMove]): Unit =
    val points = token.getPoints
    val player1 = token.isInstanceOf[AddBlackToken]

    while points.hasNext do
      val point = points.next()
      moveList.add(new GoMove(new ByteLocation(point.y, point.x), 0, new GoStone(player1)))

  override def createMoveFromToken(token: SGFToken): GoMove =
    val mvToken = token.asInstanceOf[MoveToken]
    if mvToken.isPass then
      GoMove.createPassMove(0, !mvToken.isWhite)
    else
      new GoMove(
        new ByteLocation(mvToken.getY, mvToken.getX),
        0, new GoStone(!mvToken.isWhite)
      )
