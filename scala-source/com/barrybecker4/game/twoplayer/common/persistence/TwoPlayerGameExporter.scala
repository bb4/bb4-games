/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.persistence

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.IGameController
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.persistence.GameExporter
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import java.io.IOException
import java.io.Writer

/**
 * Exports the state of a two player game to a file.
 *
 * @author Barry Becker
 */
class TwoPlayerGameExporter[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](controller: IGameController[M, B])
  extends GameExporter[M, B](controller.getBoard.copy().asInstanceOf[B]):

  protected val players: PlayerList = new PlayerList()
  players.addAll(controller.getPlayers)

  /**
   * save the current state of the game to a file in SGF (4) format.
   * SGF stands for Smart Game Format. Its text based but should be xml.
   * @param fileName name of the file to save the state to
   * @param ae the exception that occurred causing us to want to save state
   */
  override def saveToFile(fileName: String, ae: AssertionError): Unit =
    GameContext.log(1, s"saving state to :$fileName")

    try
      val out = createWriter(fileName)
      // SGF header info
      out.write("(;\n")
      out.write("FF[4]\n")
      out.write("GM[1]\n")
      out.write(s"SZ2[${board_.getNumRows}][${board_.getNumCols}]\n")
      out.write(s"Player1[${players.getPlayer1.getName}]\n")
      out.write(s"Player2[${players.getPlayer2.getName}]\n")
      out.write("GN[test1]\n")

      writeMoves(board_.getMoveList, out)
      writeExceptionIfAny(ae, out)

      out.write(')')
      out.close()
    catch
      case ioe: IOException =>
        ioe.printStackTrace()

  protected def writeMoves(moves: MoveList[M], out: Writer): Unit =
    val it = moves.iterator()
    GameContext.log(1, s"movelist size= ${moves.size}")
    while it.hasNext() do
      val move = it.next()
      out.write(getSgfForMove(move))

  protected def writeExceptionIfAny(ae: AssertionError, out: Writer): Unit =
    // include error info and stack trace in the comments to help debug
    if ae != null then
      out.write("C[")
      if ae.getMessage() != null then
        out.write(ae.getMessage())
      out.write("]\n")

  /**
   * return the SGF (4) representation of the move
   * SGF stands for Smart Game Format and is commonly used for Go
   */
  override def getSgfForMove(move: M): String =
    // passes are not represented in SGF - so just skip it if the piece is null.
    val buf = new StringBuilder("")
    val player = if move.isPlayer1 then "P1" else "P2"
    buf.append(';')
    buf.append(player)
    serializePosition(move.getToLocation, buf)
    buf.append('\n')
    buf.toString()
