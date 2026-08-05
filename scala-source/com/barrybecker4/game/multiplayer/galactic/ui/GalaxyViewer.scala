/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui

import com.barrybecker4.game.common.{GameContext, Move}
import com.barrybecker4.game.common.board.{BoardPosition, IRectangularBoard}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.multiplayer.common.ui.MultiGameViewer
import com.barrybecker4.game.multiplayer.galactic.{
  BattleSimulation, GalacticController, GalacticTurn, Galaxy, Order, Planet
}
import com.barrybecker4.game.multiplayer.galactic.player.{GalacticPlayer, GalacticRobotPlayer}
import com.barrybecker4.game.multiplayer.galactic.ui.dialog.{BattleDialog, GalacticTallyDialog}
import com.barrybecker4.game.multiplayer.galactic.ui.renderers.GalaxyRenderer

import java.awt.Point
import java.awt.event.MouseEvent
import java.util.{Iterator as JIterator, List as JList}

/**
 * Takes a GalacticController as input and displays the
 * current state of the Galactic Empire Game.
 *
 * @author Barry Becker
 */
class GalaxyViewer extends MultiGameViewer[GalacticTurn, Galaxy]:

  override def createController() =
    GalacticController().asInstanceOf[com.barrybecker4.game.multiplayer.common.MultiGameController[GalacticTurn, Galaxy]]

  override def getBoardRenderer: GameBoardRenderer = GalaxyRenderer.getRenderer

  override def showWinnerDialog(): Unit =
    GalacticTallyDialog(parent_, controller.asInstanceOf[GalacticController]).showDialog

  override def getGameOverMessage: String = "Game Over"

  override def doComputerMove(player: Player): Boolean =
    assert(!player.isHuman)
    val robot = player.asInstanceOf[GalacticRobotPlayer]
    val gc = controller.asInstanceOf[GalacticController]
    GameContext.log(1, "now doing computer move. about to make orders")

    robot.makeOrders(getBoard.asInstanceOf[Galaxy], gc.getNumberOfYearsRemaining)
    refresh()
    gc.advanceToNextPlayer()
    false

  override def createMove(lastMove: Move): GalacticTurn =
    val gmove = GalacticTurn.createMove()
    val players = controller.getPlayers

    val pit = players.iterator()
    while pit.hasNext do
      val player = pit.next()
      val orders = player.asInstanceOf[GalacticPlayer].getOrders
      executeOrders(gmove, orders)

    gmove

  private def executeOrders(gmove: GalacticTurn, orders: JList[Order]): Unit =
    val orderIt = orders.iterator()
    while orderIt.hasNext do
      val order = orderIt.next()
      executeOrder(gmove, orderIt, order)

  private def executeOrder(gmove: GalacticTurn, orderIt: JIterator[Order], order: Order): Unit =
    order.incrementYear()
    if order.hasArrived() then
      val destPlanet = order.getDestination
      val battle = BattleSimulation(order, destPlanet)
      gmove.addSimulation(battle)

      if !controller.getPlayers.allPlayersComputer then
        showBattle(battle)

      destPlanet.setOwner(battle.getOwnerAfterAttack)
      destPlanet.setNumShips(battle.getNumShipsAfterAttack)
      orderIt.remove()

  private def showBattle(battle: BattleSimulation): Unit =
    val bDlg = BattleDialog(parent_, battle, this)
    val p = getParent.getLocationOnScreen
    bDlg.setLocation(
      (p.getX + getParent.getWidth).toInt,
      (p.getY + 0.65 * getParent.getHeight).toInt)
    bDlg.setModal(true)
    bDlg.setVisible(true)

  def showPlanetUnderAttack(planet: Planet, showAttacked: Boolean): Unit =
    planet.setUnderAttack(showAttacked)
    refresh()

  def highlightPlanet(planet: Planet, highlighted: Boolean): Unit =
    planet.setHighlighted(highlighted)
    refresh()

  override def getToolTipText(e: MouseEvent): String =
    val loc = getBoardRenderer.createLocation(e)
    val sb = new StringBuilder("<html><font=-3>")

    val space = controller.getBoard.asInstanceOf[IRectangularBoard[GalacticTurn]].getPosition(loc)
    if space != null && space.isOccupied && GameContext.getDebugMode >= 0 then
      sb.append(space.getPiece.asInstanceOf[Planet].toHtml)
      sb.append("<br>")
      sb.append(loc)
    sb.append("</font></html>")
    sb.toString
