/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList}
import scala.jdk.CollectionConverters._

/**
 * MetaData for the different possible Eye shapes of size 7.
 * There are 14 different subtypes to consider (if you count the 2 sub-subtypes of E1112234).
 *
 * @author Barry Becker
 */
class E7Information(subTypeDesc: String) extends AbstractEyeSubtypeInformation {

  import E7Information.Eye7Type
  private val `type`: Eye7Type = Eye7Type.valueOf(subTypeDesc)

  `type` match {
    case Eye7Type.E1122222 => initialize(true, 7)
    case Eye7Type.E1112223 => initialize(true, 7)
    case Eye7Type.E1122233 => initialize(true, 7)
    case Eye7Type.E1111233 => initialize(true, 7)
    case Eye7Type.E1222223 => initialize(true, 7)
    case Eye7Type.E1111224 => initialize(true, 7)
    case Eye7Type.E1112333 => initialize(true, 7)
    case Eye7Type.E1222333 => initialize(true, 7)
    case Eye7Type.E1112234 => initialize(false, 7)
    case Eye7Type.E1112234a =>
      initialize(false, 7, Array(2.07f, 3.05f, 4.06f, 2.07f), Array(1.03f))
    case Eye7Type.E1112234b =>
      initialize(false, 7, Array(3.07f, 4.07f), Array(1.03f))
    case Eye7Type.E1222234 => initialize(false, 7, Array(4.08f), Array(1.04f))
    case Eye7Type.E1122224 => initialize(false, 7, Array(2.05f, 4.07f), Array(1.02f))
    case Eye7Type.E2222224 => initialize(false, 7, Array(4.10f), Array(1.04f, 1.04f))
  }

  /**
   * @return eye status for E7 types.
   */
  override def determineStatus(eye: IGoEye, board: GoBoard): EyeStatus = {
    val nbrMap = new EyeNeighborMap(eye)
    `type` match {
      case Eye7Type.E1122222 | Eye7Type.E1112223 | Eye7Type.E1122233 | Eye7Type.E1111233 |
           Eye7Type.E1222223 | Eye7Type.E1111224 | Eye7Type.E1112333 | Eye7Type.E1222333 =>
        handleSubtypeWithLifeProperty(eye, board)
      case Eye7Type.E1112234 =>
        val subtype = determineE1112234Subtype(nbrMap)
        if subtype == Eye7Type.E1112234a then
          handleVitalPointCases(nbrMap, eye, 4)
        else
          handleVitalPointCases(nbrMap, eye, 2)
      case Eye7Type.E1222234 => handleVitalPointCases(nbrMap, eye, 1)
      case Eye7Type.E1122224 => handleVitalPointCases(nbrMap, eye, 2)
      case Eye7Type.E2222224 => handleVitalPointCases(nbrMap, eye, 1)
      case Eye7Type.E1112234a | Eye7Type.E1112234b => EyeStatus.NAKADE
    }
  }

  /**
   * find the spaces with only 1 nbr
   * if the box defined by those positions contains the other spaces, then case b, else a
   * @return the subtype E1112234a or E1112234b
   */
  private def determineE1112234Subtype(nbrMap: EyeNeighborMap): Eye7Type = {
    val oneNbrPoints = new GoBoardPositionList()
    val otherPoints = new GoBoardPositionList()

    for pos <- nbrMap.keySet().asScala do
      if nbrMap.getNumEyeNeighbors(pos) == 1 then
        oneNbrPoints.add(pos)
      else
        otherPoints.add(pos)

    assert(oneNbrPoints.size == 3)
    var bounds = new Box(oneNbrPoints.getFirst.getLocation, oneNbrPoints.get(1).getLocation)
    bounds = bounds.expandBy(oneNbrPoints.get(2).getLocation)

    for otherPt <- otherPoints.asScala do
      if !bounds.contains(otherPt.getLocation) then
        return Eye7Type.E1112234a
    Eye7Type.E1112234b
  }

  override def getTypeName: String = `type`.toString
}

object E7Information:
  /** Different sorts of eye with 7 spaces. */
  enum Eye7Type extends java.lang.Enum[Eye7Type]:
    case E1122222, E1112223, E1122233, E1111233, E1222223, E1111224, E1112333,
         E1222333, E1112234, E1112234a, E1112234b, E1222234, E1122224, E2222224
