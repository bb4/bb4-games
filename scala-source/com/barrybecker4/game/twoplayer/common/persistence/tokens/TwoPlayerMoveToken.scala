/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.persistence.tokens

import com.barrybecker4.ca.dj.jigo.sgf.Point
import com.barrybecker4.ca.dj.jigo.sgf.tokens.PlacementToken
import java.io.{IOException, StreamTokenizer}

/**
 * A generic two player move.
 * The superclass for Player1MoveToken and Player2MoveToken.
 */
abstract class TwoPlayerMoveToken extends PlacementToken:

  protected val toPoint: Point = new Point()

  /**
   * Parse in the position of the piece.
   */
  override protected def parseContent(st: StreamTokenizer): Boolean =
    val parsed = parsePoint(st, toPoint)
    st.nextToken() == StreamTokenizer.TT_WORD && parsed

  /**
   * Parses a point, sets the X and Y values of the PlacementToken
   * accordingly.  This can be called repeatedly for LTokens which take
   * any number of points (see: PlacementListToken).
   *
   * The first opening '[' must have already been read; thus leaving two
   * letters and a closing ']'.  This method reads everything up to and
   * including ']'.  If a ']' follows immediately after the '[', then this
   * move is considered a pass in FF[4].
   *
   * The letters are from 'a' through 'Z', inclusive, to represent row
   * (or column) 1 through 52, respectfully.
   *
   * @return true - The point was perfectly parsed.
   *         false - The point wasn't perfectly parsed.
   */
  protected def parsePoint(st: StreamTokenizer, pt: Point): Boolean =
    st.nextToken()

    pt.x = TwoPlayerMoveToken.coordFromChar(st.sval.charAt(0))
    pt.y = TwoPlayerMoveToken.coordFromChar(st.sval.charAt(1))

    st.nextToken() == ']'.toInt

  /**
   * @return  The X coordinate of the placement.
   */
  def getToX(): Int = toPoint.x

  /**
   * @return  The Y coordinate of the placement.
   */
  def getToY(): Int = toPoint.y

object TwoPlayerMoveToken:
  /**
   * @param ch a token whose value ranges between 'a' through 'z', or 'A'  through 'Z',
   * @return the appropriate row/column value.  If
   * the token isn't between 'a' and 'z', or 'A' and 'Z', this returns 0;
   */
  def coordFromChar(ch: Int): Int =
    if ch >= 'a' && ch <= 'z' then
      ch - 'a' + 1
    else if ch >= 'A' && ch <= 'Z' then
      ch - 'A' + 1
    else
      0
