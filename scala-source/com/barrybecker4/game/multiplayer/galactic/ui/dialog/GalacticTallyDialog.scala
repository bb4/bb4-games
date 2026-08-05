/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog

import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.multiplayer.common.{MultiGameController, MultiGamePlayer}
import com.barrybecker4.game.multiplayer.common.ui.{SummaryTable, TallyDialog}
import com.barrybecker4.game.multiplayer.galactic.GalacticController

import java.awt.Component
import java.util.List as JList

/**
 * Show a summary of the final results.
 * We will show how many planets and how many ships each remaining player has.
 *
 * @author Barry Becker
 */
final class GalacticTallyDialog(parent: Component, gc: GalacticController)
  extends TallyDialog(parent, gc):

  override def createSummaryTable(players: PlayerList): SummaryTable =
    GalacticSummaryTable(players)

  override def findWinners(players: PlayerList): JList[? <: MultiGamePlayer] =
    gc.determineWinners()
