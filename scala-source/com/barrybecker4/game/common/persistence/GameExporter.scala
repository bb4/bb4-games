/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.persistence

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.Move
import com.barrybecker4.game.common.board.IBoard
import com.barrybecker4.game.common.ui.SgfFileFilter
import java.io.{FileWriter, IOException, Writer}

/**
 * Export the state of a game to a file.
 *
 * @author Barry Becker
 */
abstract class GameExporter[M <: Move, B <: IBoard[M]](val board_ : B) {

  def saveToFile(fileName: String, ae: AssertionError): Unit

  def getSgfForMove(move: M): String

  def createWriter(fileName: String): Writer =
    new FileWriter(ensureSuffix(fileName))

  private def ensureSuffix(name: String): String = {
    val suffix = s".${SgfFileFilter.SGF_EXTENSION}"
    if (!name.endsWith(suffix)) {
      name + suffix
    } else {
      name
    }
  }

  def serializePosition(pos: Location, buf: StringBuilder): Unit = {
    buf.append('[')
    buf.append(('a' + pos.col - 1).toChar)
    buf.append(('a' + pos.row - 1).toChar)
    buf.append(']')
  }
}
