/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList}
import scala.jdk.CollectionConverters._
import java.util.Arrays

/**
 * Enum for the different possible Eye shapes.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 *
 * @author Barry Becker
 */
abstract class AbstractEyeSubtypeInformation extends AbstractEyeInformation {
  private var life: Boolean = false
  private var size: Byte = 0
  private var vitalPoints: Array[Float] = Array.empty
  private var endPoints: Array[Float] = Array.empty

  private val EMPTY_POINTS: Array[Float] = Array.empty

  def initialize(life: Boolean, eyeSize: Int): Unit = {
    initialize(life, eyeSize, EMPTY_POINTS, EMPTY_POINTS)
  }

  /**
   * Initialize the subtype information.
   */
  def initialize(life: Boolean, eyeSize: Int, vitalPts: Array[Float]): Unit = {
    initialize(life, eyeSize, vitalPts, EMPTY_POINTS)
  }

  def initialize(life: Boolean, eyeSize: Int, vitalPts: Array[Float], endPts: Array[Float]): Unit = {
    this.life = life
    this.size = eyeSize.toByte
    this.vitalPoints = vitalPts
    this.endPoints = endPts
  }

  /**
   * @return the number of spaces in they eye (maybe be filled with some enemy stones).
   */
  def getSize: Byte = size

  override def hasLifeProperty: Boolean = life

  override def getVitalPoints: Array[Float] = vitalPoints

  override def getEndPoints: Array[Float] = endPoints

  /**
   * We only need to consider the non-life property status.
   * @param eye eye
   * @param board   board
   * @return status of the eye shape.
   */
  override def determineStatus(eye: IGoEye, board: GoBoard): EyeStatus = EyeStatus.NAKADE

  /**
   * If all the vital points have been filled, then we have nakade status (one big eye).
   * If all but one vital point has been filled, then we are unsettles - could be on eor two eyes.
   * If 2 or more vitals are still open, then we assume that this will become 2 eyes.
   * @return status of shape with numVitals vital points.
   */
  def handleVitalPointCases(nbrMap: EyeNeighborMap, eye: IGoEye, numVitals: Int): EyeStatus = {
    val vitalFilledSpaces = findSpecialFilledSpaces(nbrMap, getVitalPoints, eye)
    val numFilledVitals = vitalFilledSpaces.size
    assert(numFilledVitals <= numVitals,
      s"The number of filled vitals ($numFilledVitals) " +
        s"was greater than the total number of vitals ($numVitals) vitals=" +
        Arrays.toString(getVitalPoints.asInstanceOf[Array[AnyRef]]) + " eye=" + eye)

    if (numFilledVitals == numVitals) {
      EyeStatus.NAKADE
    } else if (numFilledVitals == numVitals - 1) {
      EyeStatus.UNSETTLED
    } else {
      EyeStatus.ALIVE
    }
  }

  /**
   * I suppose, in very rare cases, there could be a same side stone among the enemy filled spaces in the eye.
   * @return the eye spaces that have enemy stones in them.
   */
  def findFilledSpaces(eye: IGoEye): GoBoardPositionList = {
    val filledSpaces = new GoBoardPositionList()
    for (space <- eye.getMembers.asScala) {
      if (space.isOccupied) {
        assert(eye.isOwnedByPlayer1 != space.getPiece.isOwnedByPlayer1)
        filledSpaces.add(space)
      }
    }
    filledSpaces
  }

  /**
   * @return the set of special spaces (vital or end) that have enemy stones in them.
   */
  def findSpecialFilledSpaces(nbrMap: EyeNeighborMap, specialPoints: Array[Float], eye: IGoEye): GoBoardPositionList = {
    val specialFilledSpaces = new GoBoardPositionList()
    for (space <- eye.getMembers.asScala) {
      if (space.isOccupied) {
        assert(eye.isOwnedByPlayer1 != space.getPiece.isOwnedByPlayer1)
        if (nbrMap.isSpecialPoint(space, specialPoints)) {
          specialFilledSpaces.add(space)
        }
      }
    }
    specialFilledSpaces
  }

  /**
   * When the eye type has the life property, we can only be alive or alive in atari.
   * @return either alive or alive in atari (rare)
   */
  def handleSubtypeWithLifeProperty(eye: IGoEye, board: GoBoard): EyeStatus = {
    val filledSpaces = findFilledSpaces(eye)
    if (eye.size - filledSpaces.size == 1 && eye.getGroup.getLiberties(board).size == 1) {
      EyeStatus.ALIVE_IN_ATARI
    } else {
      EyeStatus.ALIVE
    }
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: AbstractEyeSubtypeInformation =>
      (this eq that) ||
        (getTypeName == that.getTypeName &&
          life == that.life &&
          size == that.size &&
          Arrays.equals(endPoints, that.endPoints) &&
          Arrays.equals(vitalPoints, that.vitalPoints))
    case _ => false
  }

  override def hashCode(): Int = {
    var result = if (life) 1 else 0
    result = 31 * result + size.toInt
    result = 31 * result + (if (vitalPoints != null) Arrays.hashCode(vitalPoints.asInstanceOf[Array[AnyRef]]) else 0)
    result = 31 * result + (if (endPoints != null) Arrays.hashCode(endPoints.asInstanceOf[Array[AnyRef]]) else 0)
    result = 31 * result + getTypeName.hashCode
    result
  }

  override def toString: String = {
    val bldr = new StringBuilder()
    bldr.append(this.getTypeName)
    bldr.append(" lifeProp=").append(life)
    bldr.append(" size=").append(size)
    bldr.toString
  }
}
