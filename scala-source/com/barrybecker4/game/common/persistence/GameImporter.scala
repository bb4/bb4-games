/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.persistence

import com.barrybecker4.ca.dj.jigo.sgf._
import com.barrybecker4.ca.dj.jigo.sgf.tokens.{InfoToken, PlacementToken, SGFToken, SizeToken}
import com.barrybecker4.game.common.{GameContext, GameController, Move, MoveList}
import com.barrybecker4.game.common.board.{IBoard, IRectangularBoard}
import java.io.InputStream
import scala.jdk.CollectionConverters._

/**
 * Import the state of a game from a file.
 *
 * @author Barry Becker
 */
abstract class GameImporter[M <: Move, B <: IBoard[M]](val controller_ : GameController[M, B]) {

  def restoreFromStream(iStream: InputStream): Unit

  def restoreGame(game: SGFGame): Unit = {
    parseSGFGameInfo(game)

    val moveSequence = new MoveList[M]()
    extractMoveList(game.getTree, moveSequence)
    GameContext.log(1, s"move sequence= $moveSequence")
    controller_.reset()

    moveSequence.asScala.foreach { m =>
      GameContext.log(1, s"now making:$m")
      controller_.makeMove(m)
    }
  }

  def createLoader: SGFLoader

  def parseSGFGameInfo(game: SGFGame): Unit = {
    val e = game.getInfoTokens
    var size = 13
    while (e.hasMoreElements) {
      val token = e.nextElement().asInstanceOf[InfoToken]
      token match {
        case sizeToken: SizeToken =>
          GameContext.log(2, s"info token size =${sizeToken.getSize}")
          size = sizeToken.getSize
        case _ =>
      }
    }
    controller_.getBoard.asInstanceOf[IRectangularBoard[M]].setSize(size, size)
  }

  def createMoveFromToken(token: SGFToken): M

  private def extractMoveList(tree: SGFTree, moveList: MoveList[M]): Unit = {
    val trees = tree.getTrees
    val leaves = tree.getLeaves
    while (leaves != null && leaves.hasMoreElements) {
      val tokens = leaves.nextElement().asInstanceOf[SGFLeaf].getTokens
      var found = false

      while (tokens != null && tokens.hasMoreElements && !found) {
        val token = tokens.nextElement().asInstanceOf[SGFToken]
        found = processToken(token, moveList)
      }
    }
    if (trees != null && trees.hasMoreElements) {
      extractMoveList(trees.nextElement().asInstanceOf[SGFTree], moveList)
    }
  }

  def processToken(token: SGFToken, moveList: MoveList[M]): Boolean = {
    var found = false
    token match {
      case _: PlacementToken =>
        val move = createMoveFromToken(token)
        GameContext.log(2, s"creating move=$move")
        moveList.add(move)
        found = true
      case _ =>
        GameContext.log(0, s"ignoring token ${token.getClass.getName}")
    }
    found
  }
}
