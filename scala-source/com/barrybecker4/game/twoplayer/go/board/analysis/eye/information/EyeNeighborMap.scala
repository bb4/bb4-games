/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionSet}
import scala.jdk.CollectionConverters._
import java.util.{HashMap, Map}

/**
 * Maps eye positions to lists of neighboring eye spaces_.
 *
 * @author Barry Becker
 */
class EyeNeighborMap(private val eye_ : IGoEye) {

  private val nbrMap_ : Map[GoBoardPosition, GoBoardPositionList] = createMap()

  /**
   * @return list of eye neighbors for the specified eyeSpace position.
   */
  def getEyeNeighbors(eyeSpace: GoBoardPosition): GoBoardPositionList = nbrMap_.get(eyeSpace)

  /**
   * @return number of eye neighbors for the specified eyeSpace position.
   */
  def getNumEyeNeighbors(eyeSpace: GoBoardPosition): Int = getEyeNeighbors(eyeSpace).size

  def keySet(): GoBoardPositionSet = new GoBoardPositionSet(nbrMap_.keySet)

  /**
   * @return true if identifying index for space is in array of specialPoints.
   */
  def isSpecialPoint(space: GoBoardPosition, specialPoints: Array[Float]): Boolean = {
    val index = getEyeNeighborIndex(space)
    specialPoints.exists(_ == index)
  }

  /**
   * @return Number of eye neighbors + (the sum of all the neighbors neighbors)/100.
   */
  private def getEyeNeighborIndex(eyeSpace: GoBoardPosition): Float = {
    var nbrNbrSum = 0.0f
    for (pos <- getEyeNeighbors(eyeSpace).asScala) {
      nbrNbrSum += getNumEyeNeighbors(pos)
    }
    getEyeNeighbors(eyeSpace).size + nbrNbrSum / 100.0f
  }

  /**
   * Do a breadth first search of all the positions in the eye, adding their nbr set to the map as we go.
   * @return the new neighbor map
   */
  private def createMap(): Map[GoBoardPosition, GoBoardPositionList] = {
    val nbrMap = new HashMap[GoBoardPosition, GoBoardPositionList]()
    eye_.setVisited(false)

    val queue = new GoBoardPositionList()
    val firstPos = eye_.getMembers.iterator.next
    firstPos.setVisited(true)
    queue.add(firstPos)

    val count = processSearchQueue(queue, nbrMap)

    if (count != eye_.getMembers.size) {
      throw new IllegalArgumentException("The eye string must not have been nobi connected because " +
        "not all memebers were searched. " + eye_)
    }
    eye_.setVisited(false)
    nbrMap
  }

  /**
   * Do a breadth first search of all the positions in the eye, adding their nbr set to the map as we go.
   * @return the number of element that were searched.
   */
  private def processSearchQueue(queue: GoBoardPositionList, nbrMap: Map[GoBoardPosition, GoBoardPositionList]): Int = {
    var count = 0
    while (!queue.isEmpty) {
      val current = queue.remove(0)
      val nbrs = getEyeNobiNeighbors(current)
      nbrMap.put(current, nbrs)
      count += 1
      for (space <- nbrs.asScala) {
        if (!space.isVisited) {
          space.setVisited(true)
          queue.add(space)
        }
      }
    }
    count
  }

  /**
   * @param space eye space to check
   * @return number of eye-space nobi neighbors.
   * these neighbors may either be blanks or dead stones of the opponent
   */
  private def getEyeNobiNeighbors(space: GoBoardPosition): GoBoardPositionList = {
    val nbrs = new GoBoardPositionList()
    for (eyeSpace <- eye_.getMembers.asScala) {
      if (space.isNeighbor(eyeSpace)) {
        nbrs.add(eyeSpace)
      }
    }
    nbrs
  }

  override def toString: String = nbrMap_.toString
}
