/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.options

import com.barrybecker4.game.twoplayer.common.search.SearchWindow

/**
 * The options for search strategies that use brute-force minimax search like MiniMax, NegaMax, NegaScout,
 * and also the memory and aspiration variations of these strategies.
 * These methods usually use a search window to do pruning of tree branches.
 *
 * @author Barry Becker
 */
class BruteSearchOptions(defaultLookAhead: Int):

  import BruteSearchOptions.*

  private var alphaBeta: Boolean = ALPHA_BETA
  private var quiescence: Boolean = QUIESCENCE
  private var lookAhead: Int = defaultLookAhead
  private var maxQuiescentDepth: Int = DEFAULT_MAX_QUIESCENT_DEPTH
  private var initialSearchWindow: SearchWindow = DEFAULT_SEARCH_WINDOW

  def this() =
    this(BruteSearchOptions.DEFAULT_LOOK_AHEAD)

  def this(defaultLookAhead: Int, maxQuiescentDepth: Int) =
    this(defaultLookAhead)
    this.maxQuiescentDepth = maxQuiescentDepth

  def getDefaultLookAhead: Int = DEFAULT_LOOK_AHEAD

  final def getLookAhead: Int = lookAhead

  final def setLookAhead(look: Int): Unit =
    lookAhead = look

  final def getAlphaBeta: Boolean = alphaBeta

  final def setAlphaBeta(ab: Boolean): Unit =
    alphaBeta = ab

  final def getQuiescence: Boolean = quiescence

  final def setQuiescence(q: Boolean): Unit =
    quiescence = q

  def setMaxQuiescentDepth(value: Int): Unit =
    maxQuiescentDepth = value

  def getMaxQuiescentDepth: Int = maxQuiescentDepth

  final def setInitialSearchWindow(window: SearchWindow): Unit =
    initialSearchWindow = window

  def getInitialSearchWindow: SearchWindow = initialSearchWindow.copy()

  override def toString: String =
    s"lookAhead:$lookAhead  alphaBeta:$alphaBeta  quiescence:$quiescence  maxQuiescentDepth:$maxQuiescentDepth"

object BruteSearchOptions:
  private val DEFAULT_LOOK_AHEAD: Int = 4
  private val ALPHA_BETA: Boolean = true
  private val QUIESCENCE: Boolean = false
  private val DEFAULT_MAX_QUIESCENT_DEPTH: Int = 8
  private val DEFAULT_SEARCH_WINDOW: SearchWindow = new SearchWindow()
