/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog

import com.barrybecker4.game.common.{GameContext, GameController, GameOptions}
import com.barrybecker4.game.multiplayer.common.ui.MultiGameOptionsDialog
import com.barrybecker4.game.multiplayer.galactic.{GalacticOptions, Galaxy}
import com.barrybecker4.ui.components.NumberInput

import java.awt.Component
import java.awt.event.{ActionListener, ItemListener}
import javax.swing.{JCheckBox, JComponent}
import scala.compiletime.uninitialized

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
class GalacticOptionsDialog(parent: Component, controller: GameController[?, ?])
  extends MultiGameOptionsDialog(parent, controller) with ActionListener with ItemListener:

  private var numPlanets_ : NumberInput = uninitialized
  private var planetProductionRate_ : NumberInput = uninitialized
  private var maxYearsToPlay_ : NumberInput = uninitialized
  private var initialFleetSize_ : NumberInput = uninitialized
  private var neutralsBuild_ : JCheckBox = uninitialized

  override def getControllerParamComponents: Array[JComponent] =
    val options = controller_.getOptions.asInstanceOf[GalacticOptions]

    numPlanets_ = NumberInput(
      GameContext.getLabel("NUMBER_OF_PLANETS"), options.getNumPlanets,
      GameContext.getLabel("NUMBER_OF_PLANETS_TIP"),
      Galaxy.MIN_NUM_PLANETS, Galaxy.MAX_NUM_PLANETS, true)
    planetProductionRate_ = NumberInput(
      GameContext.getLabel("PLANETS_PRODUCTION_RATE"), options.getPlanetProductionRate,
      GameContext.getLabel("PLANETS_PRODUCTION_RATE_TIP"), 0, 10, true)
    initialFleetSize_ = NumberInput(
      GameContext.getLabel("INITIAL_FLEET_SIZE"), options.getInitialFleetSize,
      GameContext.getLabel("INITIAL_FLEET_SIZE_TIP"), 1, 100, true)
    neutralsBuild_ = JCheckBox(GameContext.getLabel("SHOULD_NEUTRALS_BUILD"), options.doNeutralsBuild)
    neutralsBuild_.setToolTipText(GameContext.getLabel("SHOULD_NEUTRALS_BUILD_TIP"))
    maxYearsToPlay_ = NumberInput(
      GameContext.getLabel("MAX_YEARS_TO_PLAY"), options.getMaxYearsToPlay,
      GameContext.getLabel("MAX_YEARS_TO_PLAY_TIP"), 1, 100, true)

    initMultiControllerParamComponents(options)
    Array[JComponent](
      numPlanets_, planetProductionRate_, initialFleetSize_, neutralsBuild_, maxYearsToPlay_,
      maxNumPlayers, numRobotPlayers)

  override def getOptions: GameOptions =
    GalacticOptions(
      maxNumPlayers.getIntValue,
      numRobotPlayers.getIntValue,
      numPlanets_.getIntValue,
      planetProductionRate_.getIntValue,
      maxYearsToPlay_.getIntValue,
      initialFleetSize_.getIntValue,
      neutralsBuild_.isSelected)
