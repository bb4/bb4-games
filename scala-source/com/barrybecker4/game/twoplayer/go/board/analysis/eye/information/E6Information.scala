/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList}
import scala.jdk.CollectionConverters._

/**
 * MetaData for the different possible Eye shapes of size 6.
 * There are 8 different subtypes to consider.
 *
 * @author Barry Becker
 */
class E6Information(subTypeDesc: String) extends AbstractEyeSubtypeInformation {

  import E6Information.Eye6Type
  private val `type`: Eye6Type = Eye6Type.valueOf(subTypeDesc)

  `type` match {
    case Eye6Type.E112222 => initialize(true, 6)
    case Eye6Type.E111223 => initialize(true, 6)
    case Eye6Type.E111133 => initialize(true, 6)
    case Eye6Type.E112233 => initialize(false, 6)
    case Eye6Type.E112233a => initialize(false, 6, Array(3.06f, 3.06f))
    case Eye6Type.E112233b => initialize(false, 6, Array(3.05f, 3.05f, 2.06f, 2.06f))
    case Eye6Type.E122223 =>
      initialize(false, 6, Array(2.04f, 3.06f, 2.05f, 2.05f, 2.04f), Array(1.02f))
    case Eye6Type.E112224 => initialize(false, 6, Array(4.06f), Array(2.04f))
    case Eye6Type.E111124 => initialize(false, 6, Array(2.05f, 4.05f), Array(1.02f))
    case Eye6Type.E222233 => initialize(false, 6, Array(3.07f, 3.07f))
  }

  /**
   * @return eye status for E6 types.
   */
  override def determineStatus(eye: IGoEye, board: GoBoard): EyeStatus = {
    val nbrMap = new EyeNeighborMap(eye)
    `type` match {
      case Eye6Type.E112222 | Eye6Type.E111223 | Eye6Type.E111133 =>
        handleSubtypeWithLifeProperty(eye, board)
      case Eye6Type.E112233 =>
        val subtype = determineE112233Subtype(nbrMap)
        if subtype == Eye6Type.E112233a then
          handleVitalPointCases(nbrMap, eye, 2)
        else
          handleVitalPointCases(nbrMap, eye, 4)
      case Eye6Type.E122223 =>
        handleVitalPointCases(nbrMap, eye, 5)
      case Eye6Type.E112224 =>
        val endFilledSpaces = findSpecialFilledSpaces(nbrMap, getEndPoints, eye)
        endFilledSpaces.size match {
          case 0 => handleVitalPointCases(nbrMap, eye, 2)
          case 1 => handleVitalPointCases(nbrMap, eye, 1)
          case _ =>
            assert(false, "unexpected number of end spaces filled")
            EyeStatus.NAKADE
        }
      case Eye6Type.E111124 => handleVitalPointCases(nbrMap, eye, 2)
      case Eye6Type.E222233 => handleVitalPointCases(nbrMap, eye, 2)
      case Eye6Type.E112233a | Eye6Type.E112233b => EyeStatus.NAKADE
    }
  }

  /**
   * find the 2 spaces with only 1 nbr
   * if the box defined by those 2 positions contains the other 4 spaces, then case b, else a
   * @return the subtype E112233a or E112233b
   */
  private def determineE112233Subtype(nbrMap: EyeNeighborMap): Eye6Type = {
    val oneNbrPoints = new GoBoardPositionList()
    val otherPoints = new GoBoardPositionList()

    for pos <- nbrMap.keySet().asScala do
      if nbrMap.getNumEyeNeighbors(pos) == 1 then
        oneNbrPoints.add(pos)
      else
        otherPoints.add(pos)

    assert(oneNbrPoints.size == 2,
      "Did not get 2 one nbr points. Instead got " + oneNbrPoints.size + "\n nbrmap=" + nbrMap)
    val bounds = new Box(oneNbrPoints.getFirst.getLocation, oneNbrPoints.get(1).getLocation)

    if otherPoints.asScala.exists(pt => !bounds.contains(pt.getLocation)) then
      Eye6Type.E112233a
    else
      Eye6Type.E112233b
  }

  override def getTypeName: String = `type`.toString
}

object E6Information:
  /** Different sorts of eye with 6 spaces. */
  enum Eye6Type extends java.lang.Enum[Eye6Type]:
    case E112222, E111223, E111133, E112233, E112233a, E112233b,
         E122223, E112224, E111124, E222233
