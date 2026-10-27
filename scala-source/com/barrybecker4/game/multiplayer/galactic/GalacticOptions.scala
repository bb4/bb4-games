/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic

import com.barrybecker4.game.multiplayer.common.MultiGameOptions

class GalacticOptions(
    maxNumPlayers: Int,
    numRobotPlayers: Int,
    private var numPlanets_ : Int,
    private var planetProductionRate_ : Int,
    private var maxYearsToPlay_ : Int,
    private var initialFleetSize_ : Int,
    private var neutralsBuild_ : Boolean
) extends MultiGameOptions(maxNumPlayers, numRobotPlayers):

  def this() =
    this(
      GalacticOptions.GALACTIC_PLAYER_LIMIT, 1,
      GalacticOptions.DEFAULT_NUM_PLANETS,
      GalacticOptions.DEFAULT_PLANET_PRODUCTION_RATE,
      GalacticOptions.DEFAULT_MAX_YEARS,
      GalacticOptions.DEFAULT_NEUTRAL_FLEET_SIZE,
      GalacticOptions.DEFAULT_NEUTRALS_BUILD
    )
    setMaxNumPlayers(GalacticOptions.GALACTIC_PLAYER_LIMIT)

  def getNumPlanets: Int = numPlanets_
  def setNumPlanets(numPlanets: Int): Unit = numPlanets_ = numPlanets

  def getPlanetProductionRate: Int = planetProductionRate_
  def setPlanetProductionRate(planetProductionRate: Int): Unit = planetProductionRate_ = planetProductionRate

  def getMaxYearsToPlay: Int = maxYearsToPlay_
  def setMaxYearsToPlay(maxYearsToPlay: Int): Unit = maxYearsToPlay_ = maxYearsToPlay

  def doNeutralsBuild: Boolean = neutralsBuild_
  def setNeutralsBuild(neutralsBuild: Boolean): Unit = neutralsBuild_ = neutralsBuild

  def getInitialFleetSize: Int = initialFleetSize_
  def setInitialFleetSize(initialFleetSize: Int): Unit = initialFleetSize_ = initialFleetSize

object GalacticOptions:
  private val DEFAULT_PLANET_PRODUCTION_RATE = 2
  private val DEFAULT_NEUTRAL_FLEET_SIZE = 10
  private val DEFAULT_MAX_YEARS = 10
  private val DEFAULT_NEUTRALS_BUILD = false
  private val DEFAULT_NUM_PLANETS = 30
  private val GALACTIC_PLAYER_LIMIT = 12
