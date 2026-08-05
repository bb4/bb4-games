/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs.searchoptions

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.search.SearchAttribute
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType
import com.barrybecker4.ui.components.RadioButtonPanel

import javax.swing.*
import java.awt.*
import java.awt.event.ItemEvent
import java.awt.event.ItemListener

/**
 * The algorithm tab - allows selecting two player search options.
 *
 * @author Barry Becker
 */
class SearchOptionsPanel(private val searchOptions: SearchOptions[?, ?]) extends JPanel with ItemListener:

  /** search algorithm radio button group */
  private var strategyButtons: Array[JRadioButton] = _
  private var algorithm: SearchStrategyType = _

  private var bruteOptionsPanel: BruteSearchOptionsPanel = _
  private var monteCarloOptionsPanel: MonteCarloOptionsPanel = _
  private var bestMovesOptionsPanel: BestMovesOptionsPanel = _

  private var initialized = false

  initialize()

  private def initialize(): Unit =
    setLayout(BoxLayout(this, BoxLayout.Y_AXIS))
    setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(),
        GameContext.getLabel("PERFORMANCE_OPTIONS")
      )
    )

    val label = JLabel("     ") // initial space
    label.setAlignmentX(Component.LEFT_ALIGNMENT)
    add(label)

    // radio buttons for which search algorithm to use
    val algorithmLabel = JLabel(GameContext.getLabel("SELECT_SEARCH_ALGORITHM"))
    algorithmLabel.setAlignmentX(Component.LEFT_ALIGNMENT)
    add(algorithmLabel)

    add(createStrategyRadioButtons())

    bruteOptionsPanel = BruteSearchOptionsPanel(searchOptions.getBruteSearchOptions)
    monteCarloOptionsPanel = MonteCarloOptionsPanel(searchOptions.getMonteCarloSearchOptions)
    bestMovesOptionsPanel = BestMovesOptionsPanel(searchOptions.getBestMovesSearchOptions)

    add(bruteOptionsPanel)
    add(monteCarloOptionsPanel)
    add(bestMovesOptionsPanel)
    showOptionsBasedOnAlgorithm()
    initialized = true

  def getOptions: SearchOptions[?, ?] =
    bruteOptionsPanel.updateBruteOptionsOptions()
    monteCarloOptionsPanel.updateMonteCarloOptionsOptions()
    bestMovesOptionsPanel.updateBestMovesOptions()

    searchOptions.setSearchStrategyMethod(getSelectedStrategy(searchOptions.getSearchStrategyMethod))
    searchOptions

  /**
   * @return Radio buttons for selecting the strategy.
   */
  private def createStrategyRadioButtons(): JPanel =
    val p = JPanel()
    p.setLayout(BoxLayout(p, BoxLayout.Y_AXIS))

    val buttonGroup = ButtonGroup()
    val strategies = SearchStrategyType.values
    val numStrategies = strategies.length
    strategyButtons = new Array[JRadioButton](numStrategies)

    algorithm = searchOptions.getSearchStrategyMethod
    for i <- 0 until numStrategies do
      val alg = strategies(i)
      strategyButtons(i) = JRadioButton(alg.getLabel)
      strategyButtons(i).setToolTipText(alg.getTooltip)
      strategyButtons(i).addItemListener(this)
      p.add(RadioButtonPanel(strategyButtons(i), buttonGroup, algorithm == alg))
    p

  /**
   * Invoked when a radio button has changed its selection state.
   */
  override def itemStateChanged(e: ItemEvent): Unit =
    algorithm = getSelectedStrategy(searchOptions.getSearchStrategyMethod)
    if initialized then
      showOptionsBasedOnAlgorithm()

  private def showOptionsBasedOnAlgorithm(): Unit =
    val bruteForceStrategy = algorithm.hasAttribute(SearchAttribute.BRUTE_FORCE)
    bruteOptionsPanel.setVisible(bruteForceStrategy)
    monteCarloOptionsPanel.setVisible(!bruteForceStrategy)

  private def getSelectedStrategy(defaultStrategy: SearchStrategyType): SearchStrategyType =
    val strategies = SearchStrategyType.values
    val numStrategies = strategies.length
    for i <- 0 until numStrategies do
      if strategyButtons(i).isSelected then
        return strategies(i)
    defaultStrategy

  def ok(): Unit =
    getOptions
