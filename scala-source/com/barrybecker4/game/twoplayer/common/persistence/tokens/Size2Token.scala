/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.persistence.tokens

import com.barrybecker4.ca.dj.jigo.sgf.SGFException
import com.barrybecker4.ca.dj.jigo.sgf.tokens.{InfoToken, SGFToken}
import java.io.{IOException, StreamTokenizer}

/**
 * The size of the game board.
 * Unlike the size of a go board, 2 dimensions must be specified.
 */
class Size2Token extends SGFToken with InfoToken {

  private var numRows: Int = _
  private var numColumns: Int = _

  /**
   * Parse the dimensions of the board
   */
  @throws[IOException]
  @throws[SGFException]
  override protected def parseContent(st: StreamTokenizer): Boolean = {
    try {
      val token = st.nextToken()
      numRows = Integer.parseInt(st.sval)
      if (st.nextToken() != ']'.toInt) return false
      if (st.nextToken() != '['.toInt) return false
      st.nextToken()
      numColumns = Integer.parseInt(st.sval)
      if (st.nextToken() != ']'.toInt) return false
    } catch {
      case nfe: NumberFormatException =>
        return false
    }
    true
  }

  def getNumRows: Int = numRows

  def getNumColumns: Int = numColumns
}
