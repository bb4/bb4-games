/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.player

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.PlayerAction
import com.barrybecker4.game.multiplayer.common.{MultiGameController, MultiGamePlayer}
import com.barrybecker4.game.multiplayer.galactic.{GalacticAction, Galaxy, Order, Planet}
import com.barrybecker4.ui.util.GUIUtil

import java.awt.Color
import java.text.MessageFormat
import java.util.{LinkedList, List as JList}
import javax.swing.ImageIcon
import scala.compiletime.uninitialized

class GalacticPlayer(
    name: String,
    private var homePlanet_ : Planet,
    color: Color,
    isHuman: Boolean
) extends MultiGamePlayer(name, color, isHuman):

  protected var orders_ : JList[Order] = new LinkedList[Order]()
  protected var icon_ : ImageIcon = uninitialized
  protected var iconBaseName_ : String = uninitialized
  protected var iconIndex_ : Int = 0

  homePlanet_.setOwner(this)

  def this(name: String, homePlanet: Planet, color: Color, isHuman: Boolean, icon: ImageIcon) =
    this(name, homePlanet, color, isHuman)
    icon_ = icon

  def getDefaultName(i: Int): String =
    val args = Array[AnyRef](Integer.toString(i))
    MessageFormat.format(GameContext.getLabel("GALACTIC_DEFAULT_NAME"), args*)

  def getHomePlanet: Planet = homePlanet_
  def setHomePlanet(homePlanet: Planet): Unit = homePlanet_ = homePlanet

  def getIcon: ImageIcon =
    if icon_ == null then
      icon_ = GUIUtil.getIcon(
        GalacticPlayer.GALACTIC_IMAGE_DIR + iconBaseName_ + (iconIndex_ + 1) + ".png")
    icon_

  def setOrders(orders: JList[Order]): Unit =
    if orders == null then return
    orders_.clear()
    val it = orders.iterator()
    while it.hasNext do orders_.add(it.next())

  override def getAction(controller: MultiGameController[?, ?]): PlayerAction =
    new GalacticAction(getName, getOrders)

  override def setAction(action: PlayerAction): Unit =
    setOrders(action.asInstanceOf[GalacticAction].getOrders)

  def getOrders: JList[Order] =
    GameContext.log(1, s"orders_=$orders_")
    orders_

  def getTotalNumShips: Int =
    var totalNumShips = 0
    val oit = orders_.iterator()
    while oit.hasNext do totalNumShips += oit.next().getFleetSize
    val ownedPlanets = Galaxy.getPlanets(this)
    val pit = ownedPlanets.iterator()
    while pit.hasNext do totalNumShips += pit.next().getNumShips
    totalNumShips

  def getTotalProductionCapacity: Int =
    var totalCapacity = 0
    val ownedPlanets = Galaxy.getPlanets(this)
    val pit = ownedPlanets.iterator()
    while pit.hasNext do totalCapacity += pit.next().getProductionCapacity
    totalCapacity

  override def additionalInfo(): String =
    s"Fleet size: $getTotalNumShips\nHome planet: ${homePlanet_.getName}"

object GalacticPlayer:
  private val GALACTIC_IMAGE_DIR = GameContext.GAME_RESOURCE_ROOT + "multiplayer/galactic/ui/images/"
  val DEFAULT_NUM_SHIPS = 100

  def createGalacticPlayer(name: String, homePlanet: Planet, color: Color, isHuman: Boolean): GalacticPlayer =
    if isHuman then new GalacticHumanPlayer(name, homePlanet, color)
    else GalacticRobotPlayer.getSequencedRobotPlayer(name, homePlanet, color)

  def createGalacticPlayer(
      name: String, homePlanet: Planet, color: Color, isHuman: Boolean, icon: ImageIcon
  ): GalacticPlayer =
    if isHuman then new GalacticHumanPlayer(name, homePlanet, color, icon)
    else GalacticRobotPlayer.getSequencedRobotPlayer(name, homePlanet, color, icon)
