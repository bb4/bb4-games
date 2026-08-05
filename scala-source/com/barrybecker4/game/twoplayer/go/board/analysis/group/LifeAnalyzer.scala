/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.{NeighborAnalyzer, NeighborType}
import com.barrybecker4.game.twoplayer.go.board.elements.eye.{GoEyeList, GoEyeSet, IGoEye}
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}
import com.barrybecker4.game.twoplayer.go.board.elements.string.{GoStringSet, IGoString}

import java.util.{HashMap, LinkedList, List, Map}
import scala.jdk.CollectionConverters._

/**
 * Determine if group is pass-alive using
 * Benson's algorithm for unconditional life.
 * see http://senseis.xmp.net/?BensonSAlgorithm
 *
 * @author Barry Becker
 */
class LifeAnalyzer {

  private var group_ : IGoGroup = _
  private var board_ : GoBoard = _
  private var eyeStringNbrMap: Map[IGoEye, List[IGoString]] = _
  private var stringEyeNbrMap: Map[IGoString, GoEyeList] = _
  private var nbrAnalyzer_ : NeighborAnalyzer = _
  private var analyzerMap_ : GroupAnalyzerMap = _

  def this(group: IGoGroup, board: GoBoard, analyzerMap: GroupAnalyzerMap) = {
    this()
    group_ = group
    board_ = board
    analyzerMap_ = analyzerMap
    nbrAnalyzer_ = new NeighborAnalyzer(board)
  }

  /**
   * Use Benson's algorithm (1977) to determine if a set of strings and eyes within a group
   * is unconditionally alive.
   *
   * @return true if unconditionally alive
   */
  def isUnconditionallyAlive: Boolean = {
    initMaps()

    val eyes = analyzerMap_.getAnalyzer(group_).getEyes(board_)
    findNeighborStringSetsForEyes(eyes)
    createVitalEyeSets(eyes)

    determineUnconditionalLife()
  }

  private def initMaps(): Unit = {
    eyeStringNbrMap = new HashMap[IGoEye, List[IGoString]]()
    stringEyeNbrMap = new HashMap[IGoString, GoEyeList]()
  }

  /**
   * first find the neighbor string sets for each true eye in the group.
   */
  private def findNeighborStringSetsForEyes(eyes: GoEyeSet): Unit = {
    for (eye <- eyes.asScala) {
      val stringNbrs = findNeighborStringsForEye(eye)
      eyeStringNbrMap.put(eye, stringNbrs)
    }
  }

  /**
   * Find the neighbor string sets for a specific eye in the group.
   * @param eye eye to find neighboring strings of.
   * @return living neighbor strings. May be empty, but never null.
   */
  private def findNeighborStringsForEye(eye: IGoEye): List[IGoString] = {
    val nbrStrings = new LinkedList[IGoString]()
    for (pos <- eye.getMembers.asScala) {
      if (pos.isUnoccupied) {
        findNeighborStringsForEyeSpace(eye, pos, nbrStrings)
      }
    }
    nbrStrings
  }

  /**
   * Find the neighbor string sets for a specific empty point within an eye.
   * @param eye eye the eye space string we are currently analyzing.
   * @param pos empty position within eye.
   * @param nbrStrings the list to add neighboring still living strings to.
   */
  private def findNeighborStringsForEyeSpace(eye: IGoEye, pos: GoBoardPosition, nbrStrings: List[IGoString]): Unit = {
    val nbrs =
      nbrAnalyzer_.getNobiNeighbors(pos, eye.isOwnedByPlayer1, NeighborType.FRIEND)
    for (nbr <- nbrs.toArray(new Array[GoBoardPosition](0))) {
      if (nbr.getString.getGroup ne group_) {
        nbrStrings.clear()
        return
      }
      else {
        if (!nbrStrings.contains(nbr.getString)) {
          nbr.getString.setUnconditionallyAlive(true)
          nbrStrings.add(nbr.getString)
        }
      }
    }
  }

  /**
   * Create the neighbor eye sets for each qualified string.
   */
  private def createVitalEyeSets(eyes: GoEyeSet): Unit = {
    for (eye <- eyes.asScala) {
      updateVitalEyesForStringNeighbors(eye)
    }
    GameContext.log(3, "num strings with vital eye nbrs = " + stringEyeNbrMap.size)
  }

  /**
   * @param eye update the string neighbors of this eye
   */
  private def updateVitalEyesForStringNeighbors(eye: IGoEye): Unit = {
    for (str <- eyeStringNbrMap.get(eye).asScala) {
      var vitalEyes: GoEyeList = null
      if (stringEyeNbrMap.containsKey(str)) {
        vitalEyes = stringEyeNbrMap.get(str)
      }
      else {
        vitalEyes = new GoEyeList()
        stringEyeNbrMap.put(str, vitalEyes)
      }

      if (allUnocupiedAdjacentToString(eye, str)) {
        eye.setUnconditionallyAlive(true)
        vitalEyes.add(eye)
      }
    }
  }

  /**
   * @return true if all the empty spaces in this eye are touching the specified string.
   */
  private def allUnocupiedAdjacentToString(eye: IGoEye, string: IGoString): Boolean = {
    for (pos <- eye.getMembers.asScala) {
      if (pos.isUnoccupied) {
        val nbrs =
          nbrAnalyzer_.getNobiNeighbors(pos, eye.isOwnedByPlayer1, NeighborType.FRIEND)
        var thereIsaNbr = false
        for (nbr <- nbrs.toArray(new Array[GoBoardPosition](0))) {
          if (string.getMembers.contains(nbr)) {
            thereIsaNbr = true
          }
        }
        if (!thereIsaNbr) {
          return false
        }
      }
    }
    true
  }

  /**
   * @return true if any of the candidateStrings are unconditionally alive (i.e. pass alive).
   */
  private def determineUnconditionalLife(): Boolean = {
    val livingStrings = findPassAliveStrings()
    !livingStrings.isEmpty
  }

  /**
   * @return the set of strings in the group that are unconditionally alive.
   */
  private def findPassAliveStrings(): GoStringSet = {
    val candidateStrings = new GoStringSet(group_.getMembers)
    var done = false
    
    while (!done && !candidateStrings.isEmpty) {
      initializeEyeLife()
      val it = candidateStrings.iterator()
      done = true
      
      while (it.hasNext) {
        val str = it.next()
        val numLivingAdjacentEyes = findNumLivingAdjacentEyes(str)
        if (numLivingAdjacentEyes < 2) {
          str.setUnconditionallyAlive(false)
          it.remove()
          done = false
        }
      }
    }
    
    candidateStrings
  }

  /**
   * For each eye in the group, determine if it is unconditionally alive by verifying that
   * all its neighbors are unconditional life candidates still.
   */
  private def initializeEyeLife(): Unit = {
    for (eye <- analyzerMap_.getAnalyzer(group_).getEyes(board_).asScala) {
      eye.setUnconditionallyAlive(true)
      for (nbrStr <- eyeStringNbrMap.get(eye).asScala) {
        if (!nbrStr.isUnconditionallyAlive) {
          eye.setUnconditionallyAlive(false)
        }
      }
    }
  }

  /**
   * @return the number of unconditionally alive adjacent eyes.
   */
  private def findNumLivingAdjacentEyes(str: IGoString): Int = {
    var numLivingAdjacentEyes = 0

    val vitalEyeNbrs = stringEyeNbrMap.get(str)
    if (vitalEyeNbrs != null) {
      for (eye <- vitalEyeNbrs.asScala) {
        if (eye.isUnconditionallyAlive) {
          numLivingAdjacentEyes += 1
        }
      }
    }
    numLivingAdjacentEyes
  }
}
