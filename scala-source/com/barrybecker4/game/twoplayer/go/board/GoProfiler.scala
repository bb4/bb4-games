/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board

import com.barrybecker4.game.common.AbstractGameProfiler

/**
 * Keep track of how much time is spent in each time critical part of the
 * computer move processing. Singleton.
 *
 * @author Barry Becker
 */
final class GoProfiler private extends AbstractGameProfiler:

  import GoProfiler.*

  add(AbstractGameProfiler.GENERATE_MOVES)
  add(AbstractGameProfiler.UNDO_MOVE)
  add(UPDATE_GROUPS_AFTER_REMOVE, AbstractGameProfiler.UNDO_MOVE)
  add(UPDATE_STRINGS_AFTER_REMOVE, UPDATE_GROUPS_AFTER_REMOVE)
  add(RECREATE_GROUPS_AFTER_REMOVE, UPDATE_GROUPS_AFTER_REMOVE)
  add(AbstractGameProfiler.MAKE_MOVE)
  add(FIND_CAPTURES, AbstractGameProfiler.MAKE_MOVE)
  add(UPDATE_STRINGS_AFTER_MOVE, AbstractGameProfiler.MAKE_MOVE)
  add(UPDATE_GROUPS_AFTER_MOVE, AbstractGameProfiler.MAKE_MOVE)
  add(RECREATE_GROUPS_AFTER_MOVE, UPDATE_GROUPS_AFTER_MOVE)
  add(AbstractGameProfiler.CALC_WORTH)
  add(UPDATE_TERRITORY, AbstractGameProfiler.CALC_WORTH)
  add(ABSOLUTE_TERRITORY, UPDATE_TERRITORY)
  add(UPDATE_EYES, ABSOLUTE_TERRITORY)
  add(BENSONS_CHECK, ABSOLUTE_TERRITORY)
  add(RELATIVE_TERRITORY, UPDATE_TERRITORY)
  add(GET_ENEMY_GROUPS_NBRS, RELATIVE_TERRITORY)
  add(UPDATE_EMPTY, UPDATE_TERRITORY)
  add(GET_GROUP_NBRS)
  add(FIND_GROUPS)
  add(FIND_STRINGS)
  add(COPY_BOARD)

  def startUpdateStringsAfterRemove(): Unit = this.start(UPDATE_STRINGS_AFTER_REMOVE)
  def stopUpdateStringsAfterRemove(): Unit = this.stop(UPDATE_STRINGS_AFTER_REMOVE)
  def startUpdateGroupsAfterRemove(): Unit = this.start(UPDATE_GROUPS_AFTER_REMOVE)
  def stopUpdateGroupsAfterRemove(): Unit = this.stop(UPDATE_GROUPS_AFTER_REMOVE)
  def startRecreateGroupsAfterMove(): Unit = this.start(RECREATE_GROUPS_AFTER_MOVE)
  def stopRecreateGroupsAfterMove(): Unit = this.stop(RECREATE_GROUPS_AFTER_MOVE)
  def startRecreateGroupsAfterRemove(): Unit = this.start(RECREATE_GROUPS_AFTER_REMOVE)
  def stopRecreateGroupsAfterRemove(): Unit = this.stop(RECREATE_GROUPS_AFTER_REMOVE)
  def startUpdateStringsAfterMove(): Unit = this.start(UPDATE_STRINGS_AFTER_MOVE)
  def stopUpdateStringsAfterMove(): Unit = this.stop(UPDATE_STRINGS_AFTER_MOVE)
  def startUpdateGroupsAfterMove(): Unit = this.start(UPDATE_GROUPS_AFTER_MOVE)
  def stopUpdateGroupsAfterMove(): Unit = this.stop(UPDATE_GROUPS_AFTER_MOVE)
  def startCopyBoard(): Unit = this.start(COPY_BOARD)
  def stopCopyBoard(): Unit = this.stop(COPY_BOARD)
  def startUpdateTerritory(): Unit = this.start(UPDATE_TERRITORY)
  def stopUpdateTerritory(): Unit = this.stop(UPDATE_TERRITORY)
  def startAbsoluteTerritory(): Unit = this.start(ABSOLUTE_TERRITORY)
  def stopAbsoluteTerritory(): Unit = this.stop(ABSOLUTE_TERRITORY)
  def startRelativeTerritory(): Unit = this.start(RELATIVE_TERRITORY)
  def stopRelativeTerritory(): Unit = this.stop(RELATIVE_TERRITORY)
  def startUpdateEmpty(): Unit = this.start(UPDATE_EMPTY)
  def stopUpdateEmpty(): Unit = this.stop(UPDATE_EMPTY)
  def startGetGroupNeightbors(): Unit = this.start(GET_GROUP_NBRS)
  def stopGetGroupNeighbors(): Unit = this.stop(GET_GROUP_NBRS)
  def startFindStrings(): Unit = this.start(FIND_STRINGS)
  def stopFindStrings(): Unit = this.stop(FIND_STRINGS)
  def startFindCaptures(): Unit = this.start(FIND_CAPTURES)
  def stopFindCaptures(): Unit = this.stop(FIND_CAPTURES)
  def startUpdateEyes(): Unit = this.start(UPDATE_EYES)
  def stopUpdateEyes(): Unit = this.stop(UPDATE_EYES)
  def startGetEnemyGroupNbrs(): Unit = this.start(GET_ENEMY_GROUPS_NBRS)
  def stopGetEnemyGroupNbrs(): Unit = this.stop(GET_ENEMY_GROUPS_NBRS)
  def startBensonsCheck(): Unit = this.start(BENSONS_CHECK)
  def stopBensonsCheck(): Unit = this.stop(BENSONS_CHECK)
  override def startCalcWorth(): Unit = this.start(AbstractGameProfiler.CALC_WORTH)
  override def stopCalcWorth(): Unit = this.stop(AbstractGameProfiler.CALC_WORTH)

object GoProfiler:
  private var instance: GoProfiler = null

  private val UPDATE_STRINGS_AFTER_REMOVE = "updating strings after remove"
  private val UPDATE_GROUPS_AFTER_REMOVE = "updating groups after remove"
  private val UPDATE_STRINGS_AFTER_MOVE = "updating strings after move"
  private val UPDATE_GROUPS_AFTER_MOVE = "updating groups after move"
  private val RECREATE_GROUPS_AFTER_MOVE = "recreating groups after move"
  private val RECREATE_GROUPS_AFTER_REMOVE = "recreating groups after remove"
  private val UPDATE_TERRITORY = "updating territory"
  private val ABSOLUTE_TERRITORY = "absolute territory"
  private val RELATIVE_TERRITORY = "relative territory"
  private val UPDATE_EMPTY = "updating empty regions"
  private val GET_GROUP_NBRS = "getting group nbrs"
  private val FIND_GROUPS = "finding groups"
  private val FIND_STRINGS = "finding strings"
  private val FIND_CAPTURES = "finding capturess"
  private val UPDATE_EYES = "update eyes"
  private val GET_ENEMY_GROUPS_NBRS = "get enemy group nbrs"
  private val COPY_BOARD = "copy go board"
  private val BENSONS_CHECK = "Bensons check"

  def getInstance: GoProfiler =
    if instance == null then
      instance = new GoProfiler()
    instance

  def startCalcWorth(): Unit = getInstance.startCalcWorth()
  def stopCalcWorth(): Unit = getInstance.stopCalcWorth()
