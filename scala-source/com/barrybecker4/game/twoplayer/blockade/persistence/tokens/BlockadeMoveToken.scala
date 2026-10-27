/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.persistence.tokens

import com.barrybecker4.ca.dj.jigo.sgf.Point
import com.barrybecker4.game.twoplayer.common.persistence.tokens.TwoPlayerMoveToken
import java.io.StreamTokenizer

class BlockadeMoveToken extends TwoPlayerMoveToken:
  
  private val fromPoint = Point()
  private var wallPoint1: Point = scala.compiletime.uninitialized
  private var wallPoint2: Point = scala.compiletime.uninitialized

  override def parseContent(st: StreamTokenizer): Boolean =
    var parsed = parsePoint(st, fromPoint)
    if st.nextToken() != '['.toInt then
      return false
    parsed = parsed && parsePoint(st, toPoint)
    if st.nextToken() != StreamTokenizer.TT_WORD then
      return false
    if st.sval.equals("wall") then
      parseWall(st)
    parsed

  def parseWall(st: StreamTokenizer): Boolean =
    if st.nextToken() != '['.toInt then
      return false
    var token = st.nextToken()

    wallPoint1 = Point()
    wallPoint1.x = TwoPlayerMoveToken.coordFromChar(st.sval.charAt(0))
    wallPoint1.y = TwoPlayerMoveToken.coordFromChar(st.sval.charAt(1))

    var parsed = st.nextToken() == ']'.toInt && st.nextToken() == '['.toInt

    token = st.nextToken()

    wallPoint2 = Point()
    wallPoint2.x = TwoPlayerMoveToken.coordFromChar(st.sval.charAt(0))
    wallPoint2.y = TwoPlayerMoveToken.coordFromChar(st.sval.charAt(1))

    parsed = parsed && (st.nextToken() == ']'.toInt)
    parsed

  def getFromPoint: Point = fromPoint

  def getFromX: Int = fromPoint.x

  def getFromY: Int = fromPoint.y

  def hasWall: Boolean = wallPoint1 != null

  def getWallPoint1: Point = wallPoint1

  def getWallPoint2: Point = wallPoint2
