/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.persistence

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.TwoPlayerController
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameExporter
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.GoOptions
import java.io.{IOException, Writer}

/**
 * Exports the state of a Go game to a file.
 *
 * @author Barry Becker
 */
class GoGameExporter(controller: TwoPlayerController[GoMove, GoBoard]) 
  extends TwoPlayerGameExporter[GoMove, GoBoard](controller) {

  private val komi: Float = controller.getOptions.asInstanceOf[GoOptions].getKomi

  /**
   * save the current state of the go game to a file in SGF (4) format
   * @param fileName name of the file to save the state to
   * @param ae the exception that occurred causing us to want to save state
   */
  override def saveToFile(fileName: String, ae: AssertionError): Unit = {
    GameContext.log(1, "saving state to :" + fileName)
    val b = board_

    try {
      val out = createWriter(fileName)
      out.write("(;\n")
      out.write("FF[4]\n")
      out.write("GM[1]\n")
      out.write("CA[UTF-8]\n")
      out.write("ST[2]\n")
      out.write("RU[japanese]\n")
      out.write("SZ[" + b.getNumRows + "]\n")
      out.write("PB[" + players.getPlayer1.getName + "]\n")
      out.write("PW[" + players.getPlayer2.getName + "]\n")
      out.write("KM[" + komi + "]\n")
      out.write("PC[US]\n")
      out.write("HA[" + b.getHandicap + "]\n")
      out.write("GN[test1]\n")

      writeMoves(b.getMoveList, out)
      writeExceptionIfAny(ae, out)

      out.write(')')
      out.close()
    } catch {
      case ioe: IOException => ioe.printStackTrace()
    }
  }

  /**
   * return the SGF (4) representation of the move
   * SGF stands for Smart Game Format and is commonly used for Go
   */
  override def getSgfForMove(move: GoMove): String = {
    if (move.getPiece == null) {
      return "[]"
    }
    val buf = new StringBuilder()
    val player = if (move.getPiece.isOwnedByPlayer1) 'B' else 'W'
    buf.append(';')
    buf.append(player)
    buf.append('[')
    buf.append(('a' + move.getToCol - 1).toChar)
    buf.append(('a' + move.getToRow - 1).toChar)
    buf.append(']')
    buf.append('\n')
    buf.toString
  }
}
