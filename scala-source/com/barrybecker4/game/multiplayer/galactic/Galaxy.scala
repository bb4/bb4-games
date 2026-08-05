/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer

import java.util.{ArrayList, HashMap, List as JList, Map as JMap}

class Galaxy(numRows: Int, numCols: Int) extends Board[GalacticTurn]():
  setSize(numRows, numCols)

  def this(g: Galaxy) =
    this(g.getNumRows, g.getNumCols)

  override def copy(): Galaxy = new Galaxy(this)

  def initPlanets(players: PlayerList, options: GalacticOptions): Unit =
    Galaxy.hmPlanets_.clear()
    Galaxy.numPlanets_ = options.getNumPlanets
    if Galaxy.planets_ == null then Galaxy.planets_ = new ArrayList[Planet]()
    Galaxy.planets_.clear()

    var i = 0
    while i < Galaxy.getNumPlanets do
      var position = getPosition(1, 1)
      while
        val randRow = (Math.random() * getNumRows).toInt + 1
        val randCol = (Math.random() * getNumCols).toInt + 1
        position = getPosition(randRow, randCol)
        position.isOccupied
      do ()

      val production = (1 + Math.max(0, GameContext.random().nextGaussian()) * options.getPlanetProductionRate).toInt
      val initialFleet = (1 + Math.max(0, GameContext.random().nextGaussian()) * options.getInitialFleetSize).toInt
      val planet = new Planet(Galaxy.PLANET_NAMES(i), initialFleet, production, position.getLocation)
      position.setPiece(planet)

      val pit = players.iterator()
      while pit.hasNext do
        val newVar = pit.next().asInstanceOf[GalacticPlayer]
        if planet.getName == newVar.getHomePlanet.getName then
          val home = newVar.getHomePlanet
          position.setPiece(home)
          home.setLocation(position.getLocation)

      Galaxy.planets_.add(position.getPiece.asInstanceOf[Planet])
      Galaxy.hmPlanets_.put(
        java.lang.Character.valueOf(planet.getName),
        position.getPiece.asInstanceOf[Planet]
      )
      i += 1

  def setNumPlanets(numPlanets: Int): Unit =
    if numPlanets > Galaxy.MAX_NUM_PLANETS then
      GameContext.log(0, s"You are not allowed to have more than ${Galaxy.MAX_NUM_PLANETS}")
      Galaxy.numPlanets_ = Galaxy.MAX_NUM_PLANETS
    else
      Galaxy.numPlanets_ = numPlanets

  override def getMaxNumMoves: Int = positions_.getNumBoardSpaces

  override def makeInternalMove(move: GalacticTurn): Boolean =
    Galaxy.build()
    getMoveList.add(move)
    true

  override def undoInternalMove(move: GalacticTurn): Unit =
    GameContext.log(0, "undo no implemented yet.")

object Galaxy:
  private val DEFAULT_NUM_PLANETS = 20
  val MAX_NUM_PLANETS = 80
  val MIN_NUM_PLANETS = 3

  private val PLANET_NAMES: Array[Char] =
    val names = new Array[Char](60)
    var ct = 0
    var c = 'A'
    while c <= 'Z' do
      names(ct) = c; ct += 1; c = (c + 1).toChar
    c = 'a'
    while c <= 'z' do
      names(ct) = c; ct += 1; c = (c + 1).toChar
    c = '1'
    while c <= '8' do
      names(ct) = c; ct += 1; c = (c + 1).toChar
    names

  private var numPlanets_ = DEFAULT_NUM_PLANETS
  private var planets_ : JList[Planet] = null
  private val hmPlanets_ : JMap[Character, Planet] = new HashMap[Character, Planet]()

  def getPlanets(): JList[Planet] = new ArrayList[Planet](planets_)

  def getPlanets(player: GalacticPlayer): JList[Planet] =
    if player == null then return getPlanets()
    val playerPlanets = new ArrayList[Planet]()
    val it = planets_.iterator()
    while it.hasNext do
      val planet = it.next()
      if planet.getOwner == player then playerPlanets.add(planet)
    playerPlanets

  def getNumPlanets: Int = numPlanets_

  def getPlanet(name: Char): Planet =
    val p = hmPlanets_.get(java.lang.Character.valueOf(name))
    assert(p != null)
    p

  private def build(): Unit =
    val it = planets_.iterator()
    while it.hasNext do it.next().incrementYear()

  def allPlanetsOwnedByOnePlayer(): Boolean =
    val player = planets_.get(0).getOwner
    val it = planets_.iterator()
    while it.hasNext do
      if it.next().getOwner != player then return false
    true
