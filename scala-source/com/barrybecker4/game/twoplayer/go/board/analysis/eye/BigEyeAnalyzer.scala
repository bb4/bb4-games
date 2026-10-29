/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye

import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.{EyeInformation, EyeType}
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

/**
 * Determine properties about a big eye on the board.
 * This analyzer is only used by the EyeTypeAnalyzer.
 * It classifies eyes that are not false eye and have between 2 and 8 spaces_.
 * Some of those spaces_ may have enemy stones in them.
 * See EyeTypeAnalyzer
 */
class BigEyeAnalyzer(eye: IGoEye) {

  private val spaces_ : GoBoardPositionSet = eye.getMembers
  private val size = spaces_.size
  assert(size > 3 && size < 8)

  /**
   * For some eyes (like big eyes) there are one or more key points that will make a single eye if
   * the opponent plays first (or first and second if 2 key points), or 2 eyes if you play first.
   * We refer to the paper "When One Eye is Sufficient: A Static Classification"
   * to classify the different eye types based solely on eye-point neighbors.
   *
   * The pattern formed by the sorted list of neighbor counts uniquely determines the type.
   *
   * @return the eye type determined based on the properties and neighbors of the positions in the spaces_ list.
   */
  def determineEyeInformation(): EyeInformation = {
    val counts = new ListBuffer[Int]()

    for (space <- spaces_.asScala) {
      counts += getNumEyeNobiNeighbors(space)
    }
    val sortedCounts = counts.sorted.toList

    getEyeInformation(sortedCounts)
  }

  private def getEyeInformation(counts: List[Int]): EyeInformation = {
    val bldr = new StringBuilder("E")
    for (num <- counts) {
      bldr.append(num)
    }
    val eyeType = EyeType.valueOf("E" + counts.size)

    eyeType.getInformation(bldr.toString)
  }

  /**
   * @param space eye space to check
   * @return number of eye-space nobi neighbors.
   * these neighbors may either be blanks or dead stones of the opponent
   */
  private def getNumEyeNobiNeighbors(space: GoBoardPosition): Int = {
    var numNbrs = 0
    for (eyeSpace <- spaces_.asScala) {
      if (space.isNeighbor(eyeSpace)) {
        numNbrs += 1
      }
    }
    numNbrs
  }
}
