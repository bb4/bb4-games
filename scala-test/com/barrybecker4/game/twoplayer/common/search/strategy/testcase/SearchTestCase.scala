package com.barrybecker4.game.twoplayer.common.search.strategy.testcase

import com.barrybecker4.common.xml.DomUtil
import com.barrybecker4.game.common.{GameWeights, GameWeightsStub}
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.common.search.{Searchable, SearchableStub, TwoPlayerMoveStub}
import com.barrybecker4.game.twoplayer.common.search.options.{
  BestMovesSearchOptions,
  BruteSearchOptions,
  MaximizationStyle,
  MonteCarloSearchOptions,
  SearchOptions
}
import com.barrybecker4.game.twoplayer.common.search.strategy.{MemorySearchStrategy, MtdStrategy, SearchStrategy}
import com.barrybecker4.optimization.parameter.ParameterArray
import org.w3c.dom.Node

/**
 * Corresponds to a test-case node in the case xml. It describes which search algorithm
 * with what
 * @author Barry Becker
 */
object SearchTestCase:
  /** It is assumed that all the strategy classes come from this package */
  private val STRATEGY_PACKAGE = "com.barrybecker4.game.twoplayer.common.search.strategy."

  /** the tests assume all the moves are used. We might want to also vary this later */
  private val BEST_MOVE_OPTIONS = new BestMovesSearchOptions(100, 0, 20)

class SearchTestCase(testCaseNode: Node):

  import SearchTestCase.*

  private var className: String = DomUtil.getAttribute(testCaseNode, "strategy")
  // Defaults match search-test-case.dtd (external DTD defaults are no longer applied by DomUtil).
  private var notes: String = DomUtil.getAttribute(testCaseNode, "notes", null)
  private var rootPlayer1: Boolean =
    java.lang.Boolean.parseBoolean(DomUtil.getAttribute(testCaseNode, "rootPlayer1", "true"))
  private var bruteSearchOptions: BruteSearchOptions = new BruteSearchOptions()
  private var monteCarloSearchOptions: MonteCarloSearchOptions = new MonteCarloSearchOptions()
  private var expectedResult: SearchResult = scala.compiletime.uninitialized

  private val children = testCaseNode.getChildNodes
  private var i = 0
  while i < children.getLength do
    processNode(children.item(i))
    i += 1

  /**
   * Create the strategy using reflection and pass it the searchable.
   * @return the search strategy to test
   */
  def createSearchStrategy(): SearchStrategy[TwoPlayerMoveStub, ?] =
    val searchOptions =
      new SearchOptions[TwoPlayerMoveStub, TwoPlayerBoard[TwoPlayerMoveStub]](
        bruteSearchOptions,
        BEST_MOVE_OPTIONS,
        monteCarloSearchOptions
      )
    val searchable: Searchable[?, ?] = new SearchableStub(searchOptions)

    // create the strategy using reflection
    if className.startsWith("MtdStrategy:") then
      // If mtd based strategy, the memory strategy gets wrapped.
      val theClassName = className.substring("MtdStrategy:".length)
      new MtdStrategy(
        createInstanceForClass(theClassName, searchable).asInstanceOf[MemorySearchStrategy[TwoPlayerMoveStub, TwoPlayerBoard[TwoPlayerMoveStub]]]
      )
    else
      createInstanceForClass(className, searchable)

  private def createInstanceForClass(
      className: String,
      searchable: Searchable[?, ?]
  ): SearchStrategy[TwoPlayerMoveStub, ?] =
    val weights: GameWeights = new GameWeightsStub()
    try
      Class
        .forName(STRATEGY_PACKAGE + className)
        .getConstructor(classOf[Searchable[?, ?]], classOf[ParameterArray])
        .newInstance(searchable, weights.getDefaultWeights)
        .asInstanceOf[SearchStrategy[TwoPlayerMoveStub, ?]]
    catch
      case e @ (_: InstantiationException | _: IllegalAccessException | _: NoSuchMethodException |
          _: java.lang.reflect.InvocationTargetException | _: ClassNotFoundException) =>
        e.printStackTrace()
        null

  /** @return name of the search strategy class for this test case */
  def getName: String = className

  def getRootPlayer1: Boolean = rootPlayer1

  override def toString: String =
    getName + ": root is " + (if rootPlayer1 then "player1" else "player2") + ": " + getNotes

  def getExpectedResult: SearchResult = expectedResult

  private def getNotes: String = if notes == null then "" else notes

  private def processNode(child: Node): Unit =
    val name = child.getNodeName
    name match
      case "brute-force-options" =>
        // Defaults match search-test-case.dtd attribute defaults.
        bruteSearchOptions.setLookAhead(Integer.parseInt(DomUtil.getAttribute(child, "look-ahead", "3")))
        bruteSearchOptions.setAlphaBeta(
          java.lang.Boolean.parseBoolean(DomUtil.getAttribute(child, "alpha-beta", "false"))
        )
        bruteSearchOptions.setQuiescence(
          java.lang.Boolean.parseBoolean(DomUtil.getAttribute(child, "quiescence", "false"))
        )
        bruteSearchOptions.setMaxQuiescentDepth(
          Integer.parseInt(DomUtil.getAttribute(child, "max-quiescent-depth", "3"))
        )
      case "monte-carlo-options" =>
        monteCarloSearchOptions.setMaxSimulations(
          Integer.parseInt(DomUtil.getAttribute(child, "max-simulations", "100"))
        )
        monteCarloSearchOptions.setExploreExploitRatio(
          java.lang.Float.parseFloat(DomUtil.getAttribute(child, "explore-exploit-ratio", "1.0")).toDouble
        )
        monteCarloSearchOptions.setRandomLookAhead(
          Integer.parseInt(DomUtil.getAttribute(child, "random-look-ahead", "3"))
        )
        monteCarloSearchOptions.setMaxStyle(
          MaximizationStyle.valueOf(DomUtil.getAttribute(child, "max-style", "WIN_RATE"))
        )
      case "expected-search-result" =>
        val moveId = DomUtil.getAttribute(child, "move-id")
        val inheritedValue = Integer.parseInt(DomUtil.getAttribute(child, "inherited-value"))
        val numConsideredMoves = Integer.parseInt(DomUtil.getAttribute(child, "num-moves-considered"))
        expectedResult = new SearchResult(moveId, inheritedValue, numConsideredMoves)
      case _ =>
        throw new IllegalArgumentException("Invalid element : " + name)
