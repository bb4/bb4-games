/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search

import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerMove}
import org.junit.{Before, Test}

/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will exercise these methods for specific game instances.
 * @author Barry Becker
 */
abstract class SearchableBaseTst:

  /** The searchable instance under test. */
  protected var searchable: Searchable[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]] =
    scala.compiletime.uninitialized

  protected var helper: ISearchableHelper = scala.compiletime.uninitialized

  @Before
  def setUp(): Unit =
    helper = createSearchableHelper

  /** @return the helper that will help us create the controller, options and other related info. */
  protected def createSearchableHelper: ISearchableHelper

  /** verify that we can retrieve the lookahead value. */
  @Test
  def testLookaheadValue(): Unit

  /** verify that we can retrieve the lookahead value. */
  @Test
  def testAlphaBetaValue(): Unit

  /** verify that we can retrieve the quiescence value. */
  @Test
  def testQuiescenceValue(): Unit
