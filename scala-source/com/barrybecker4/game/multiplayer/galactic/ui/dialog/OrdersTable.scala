/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.galactic.{Galaxy, Order, Planet}
import com.barrybecker4.ui.table.{BasicTableModel, TableBase, TableColumnMeta}

import java.awt.geom.Point2D
import java.util.{ArrayList, HashMap, List as JList, Map as JMap}
import javax.swing.table.TableModel
import scala.jdk.javaapi.CollectionConverters

/**
 * OrdersTable contains a list of orders that a player has made to direct his ships.
 * All the cells are editable.
 *
 * @see Order
 * @author Barry Becker
 */
class OrdersTable(orders: JList[Order]) extends TableBase(OrdersTable.conv(orders), OrdersTable.columnNames):

  private val lastOrders: JList[Order] = orders

  override def updateColumnMeta(columnMeta: Array[TableColumnMeta]): Unit =
    for i <- 0 until getNumColumns do
      columnMeta(i).setTooltip(OrdersTable.columnTips(i))

  override def createTableModel(columnNames: Array[String]): TableModel =
    BasicTableModel(OrdersTable.columnNames.asInstanceOf[Array[Object]], 0, false)

  def removeRow(rowIndex: Int): Unit =
    getPlayerModel.removeRow(rowIndex)

  def getOrders: JList[Order] =
    val model = table.getModel
    val nRows = model.getRowCount
    val result = new ArrayList[Order](nRows)
    val numOldOrders = lastOrders.size()

    for i <- 0 until nRows do
      val origin = model.getValueAt(i, OrdersTable.ORIGIN_INDEX).toString
      val dest = model.getValueAt(i, OrdersTable.DESTINATION_INDEX).toString
      val numShips = model.getValueAt(i, OrdersTable.NUM_SHIPS_INDEX).asInstanceOf[Integer].intValue()

      val originPlanet = Galaxy.getPlanet(origin.charAt(0))
      val destPlanet = Galaxy.getPlanet(dest.charAt(0))

      val o =
        if i < numOldOrders then
          val currLoc = lastOrders.get(i).getCurrentLocation
          Order(originPlanet, destPlanet, numShips, currLoc)
        else
          originPlanet.deductShips(numShips)
          Order(originPlanet, destPlanet, numShips)

      result.add(o)

    result

  def getCurrentOutGoingShips: JMap[Planet, Integer] =
    val outgoingMap = new HashMap[Planet, Integer]()
    val model = table.getModel
    val nRows = model.getRowCount
    val numOldOrders = lastOrders.size()

    for i <- numOldOrders until nRows do
      val s = model.getValueAt(i, OrdersTable.ORIGIN_INDEX).asInstanceOf[Character]
      val source = Galaxy.getPlanet(s)
      val numShips = model.getValueAt(i, OrdersTable.NUM_SHIPS_INDEX).asInstanceOf[Integer]
      if outgoingMap.get(source) != null then
        val n = outgoingMap.get(source)
        outgoingMap.put(source, Int.box(numShips.intValue() + n.intValue()))
      else
        outgoingMap.put(source, numShips)

    outgoingMap

  private def getPlayerModel: BasicTableModel =
    getModel.asInstanceOf[BasicTableModel]

  override def addRow(order: Any): Unit =
    val o = order.asInstanceOf[Order]
    val d = new Array[AnyRef](OrdersTable.NUM_COLS)
    d(OrdersTable.ORIGIN_INDEX) = Character.valueOf(o.getOrigin.getName)
    d(OrdersTable.DESTINATION_INDEX) = Character.valueOf(o.getDestination.getName)
    d(OrdersTable.NUM_SHIPS_INDEX) = Int.box(o.getFleetSize)
    d(OrdersTable.DISTANCE_INDEX) = Double.box(o.getTimeRemaining)
    getPlayerModel.addRow(d)

object OrdersTable:
  private val ORIGIN_INDEX = 0
  private val DESTINATION_INDEX = 1
  private val NUM_SHIPS_INDEX = 2
  private val DISTANCE_INDEX = 3

  private val ORIGIN = GameContext.getLabel("ORIGIN")
  private val DESTINATION = GameContext.getLabel("DESTINATION")
  private val NUM_SHIPS = GameContext.getLabel("NUM_SHIPS")
  private val DISTANCE = GameContext.getLabel("ETA")

  private val columnNames = Array[String](ORIGIN, DESTINATION, NUM_SHIPS, DISTANCE)

  private val ORIGIN_TIP = GameContext.getLabel("ORIGIN_TIP")
  private val DESTINATION_TIP = GameContext.getLabel("DESTINATION_TIP")
  private val NUM_SHIPS_TIP = GameContext.getLabel("NUM_SHIPS_TIP")
  private val DISTANCE_TIP = GameContext.getLabel("ETA_TIP")

  private val columnTips = Array[String](ORIGIN_TIP, DESTINATION_TIP, NUM_SHIPS_TIP, DISTANCE_TIP)

  private val NUM_COLS = columnNames.length

  private[dialog] def conv(orders: JList[Order]): scala.collection.immutable.Seq[Order] =
    CollectionConverters.asScala(orders).toSeq
