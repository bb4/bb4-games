/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.search.Searchable
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, MonteCarloSearchOptions, SearchOptions}
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.optimization.parameter.ParameterArray
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test

/**
 * A re-search that returns no move must still undo the stone it just made.
 */
class NegaScoutUndoTest:

  @Test
  def negaScoutUndoesWhenQuiescenceReturnsNull(): Unit =
    val stub = new BalanceSearchable(lookAhead = 1, emptyQuiescenceOnSecondVisit = true)
    val strategy = new NegaScoutStrategy[GoMove, GoBoard](stub, null)
    strategy.search(stub.root, null)
    assertTrue("re-search should have visited the second move twice", stub.secondMoveVisits >= 2)
    assertEquals("board depth after a null quiescence re-search", 0, stub.depth)

  @Test
  def negaScoutMemoryUndoesWhenResearchReturnsNull(): Unit =
    val stub = new BalanceSearchable(lookAhead = 2, emptyMovesOnSecondVisit = true)
    val strategy = new NegaScoutMemoryStrategy[GoMove, GoBoard](stub, null)
    strategy.search(stub.root, null)
    assertTrue("re-search should have generated moves for the second move twice", stub.secondMoveGenerations >= 2)
    assertEquals("board depth after a null re-search", 0, stub.depth)

/**
 * Counts make/undo calls. The second root move forces a NegaScout re-search whose
 * second visit returns null, which used to skip the undo.
 */
private class BalanceSearchable(
  lookAhead: Int,
  emptyQuiescenceOnSecondVisit: Boolean = false,
  emptyMovesOnSecondVisit: Boolean = false
) extends Searchable[GoMove, GoBoard]:

  val root: GoMove = move(3, 3, black = true, value = 0)
  private val moveA: GoMove = move(1, 2, black = false, value = 0)
  private val moveB: GoMove = move(2, 2, black = false, value = -100)
  private val moveC: GoMove = move(3, 4, black = true, value = 0)
  private val moveD: GoMove = move(4, 4, black = true, value = -100)

  var depth: Int = 0
  var secondMoveVisits: Int = 0
  var secondMoveGenerations: Int = 0
  private var hashSerial: Long = 0L

  private val options: SearchOptions[GoMove, GoBoard] =
    val brute = new BruteSearchOptions(lookAhead, 4)
    brute.setQuiescence(true)
    brute.setAlphaBeta(true)
    new SearchOptions[GoMove, GoBoard](
      SearchStrategyType.NEGASCOUT,
      brute,
      new BestMovesSearchOptions(),
      new MonteCarloSearchOptions()
    )

  override def getSearchOptions: SearchOptions[GoMove, GoBoard] = options

  override def makeInternalMove(move: GoMove): Unit = depth += 1

  override def undoInternalMove(move: GoMove): Unit = depth -= 1

  override def getSearchStrategy: SearchStrategy[GoMove, GoBoard] = null

  override def searchForNextMove(
    weights: ParameterArray,
    lastMove: GoMove,
    treeViewer: com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable
  ): GoMove =
    throw new UnsupportedOperationException()

  override def done(move: GoMove, recordWin: Boolean): Boolean = false

  override def worth(lastMove: GoMove, weights: ParameterArray): Int = lastMove.getValue

  override def generateMoves(lastMove: GoMove, weights: ParameterArray): MoveList[GoMove] =
    val list = new MoveList[GoMove]()
    if lastMove eq root then
      list.add(moveA)
      list.add(moveB)
    else if lastMove eq moveA then
      list.add(moveC)
    else if lastMove eq moveB then
      secondMoveGenerations += 1
      if !emptyMovesOnSecondVisit || secondMoveGenerations == 1 then
        list.add(moveD)
    list

  override def generateUrgentMoves(lastMove: GoMove, weights: ParameterArray): MoveList[GoMove] =
    new MoveList[GoMove]()

  /** Second visit of move B is in jeopardy, so quiescence runs and finds nothing. */
  override def inJeopardy(move: GoMove, weights: ParameterArray): Boolean =
    if !emptyQuiescenceOnSecondVisit || (move ne moveB) then
      return false
    secondMoveVisits += 1
    secondMoveVisits >= 2

  override def getBoard: GoBoard = new GoBoard(5, 0)

  override def getMoveList: MoveList[GoMove] = new MoveList[GoMove]()

  override def getNumMoves(): Int = depth

  override def copy(): Searchable[GoMove, GoBoard] = this

  override def getHashKey(): HashKey =
    hashSerial += 1
    new HashKey(hashSerial)

  private def move(row: Int, col: Int, black: Boolean, value: Int): GoMove =
    new GoMove(new ByteLocation(row, col), value, new GoStone(black))
