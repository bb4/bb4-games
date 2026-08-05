/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs.searchoptions

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.search.options.BruteSearchOptions
import com.barrybecker4.ui.components.NumberInput

import javax.swing.*

/**
 * Panel that shows the options for search strategies that use brute force (like minimax and derivatives).
 *
 * @author Barry Becker
 */
class BruteSearchOptionsPanel(private val bruteOptions: BruteSearchOptions) extends JPanel:

  private var lookAheadField: NumberInput = scala.compiletime.uninitialized
  private var alphabetaCheckbox: JCheckBox = scala.compiletime.uninitialized
  private var quiescenceCheckbox: JCheckBox = scala.compiletime.uninitialized

  private val MAX_ALLOWED_LOOKAHEAD = 20

  initialize()

  /**
   * update the options based on the user selections.
   */
  def updateBruteOptionsOptions(): Unit =
    bruteOptions.setAlphaBeta(alphabetaCheckbox.isSelected)
    bruteOptions.setQuiescence(quiescenceCheckbox.isSelected)
    bruteOptions.setLookAhead(lookAheadField.getIntValue)

  /**
   * create the panel with ui element for setting the options
   */
  private def initialize(): Unit =
    setLayout(BoxLayout(this, BoxLayout.Y_AXIS))

    // look ahead
    lookAheadField =
      NumberInput(
        GameContext.getLabel("MOVES_TO_LOOKAHEAD"),
        bruteOptions.getLookAhead,
        GameContext.getLabel("MOVES_TO_LOOKAHEAD_TIP"),
        1,
        MAX_ALLOWED_LOOKAHEAD,
        true
      )

    this.add(lookAheadField)

    // alpha-beta pruning option
    alphabetaCheckbox =
      JCheckBox(GameContext.getLabel("USE_PRUNING"), bruteOptions.getAlphaBeta)
    alphabetaCheckbox.setToolTipText(GameContext.getLabel("USE_PRUNING_TIP"))
    this.add(alphabetaCheckbox)

    // show profile info option
    quiescenceCheckbox = JCheckBox(GameContext.getLabel("USE_QUIESCENCE"), bruteOptions.getQuiescence)
    quiescenceCheckbox.setToolTipText(GameContext.getLabel("USE_QUIESCENCE_TIP"))
    this.add(quiescenceCheckbox)
