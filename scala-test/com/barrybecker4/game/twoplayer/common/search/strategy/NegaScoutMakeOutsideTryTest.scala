/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.search.Searchable
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, MonteCarloSearchOptions, SearchOptions}
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey
import com.barrybecker4.game.twoplayer.common.search.tree.{IGameTreeViewable, NodeAttributes, SearchTreeNode}
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.optimization.parameter.ParameterArray
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * makeInternalMove must be inside the same try/finally as undo.
 * If addNodeToTree throws after make, the stone must still be undone.
 */
class NegaScoutMakeOutsideTryTest:

  @Test
  def undoesWhenAddNodeToTreeThrows(): Unit =
    val stub = new ThrowingTreeSearchable
    val strategy = new NegaScoutStrategy[GoMove, GoBoard](stub, null)
    strategy.setGameTreeEventListener(stub.tree)
    try
      strategy.search(stub.root, stub.tree.getRootNode)
    catch
      case _: RuntimeException => () // expected from addNode
    assertEquals(0, stub.depth, "board depth after addNodeToTree throws mid-make")

/**
 * First child make succeeds; adding that node to the tree throws.
 */
private class ThrowingTreeSearchable extends Searchable[GoMove, GoBoard]:

  val root: GoMove = move(3, 3, black = true)
  private val childMove: GoMove = move(1, 2, black = false)

  var depth: Int = 0
  private var hashSerial: Long = 0L

  val tree: IGameTreeViewable = new IGameTreeViewable:
    private val rootNode = new SearchTreeNode(root, NodeAttributes())
    override def getRootNode: SearchTreeNode = rootNode
    override def addNode(parent: SearchTreeNode, child: SearchTreeNode): Unit =
      throw new RuntimeException("tree add failed")
    override def addPrunedNodes(
      list: MoveList[?],
      parent: SearchTreeNode,
      i: Int,
      attributes: NodeAttributes
    ): Unit = ()
    override def resetTree(p: com.barrybecker4.game.twoplayer.common.TwoPlayerMove): Unit = ()

  private val options: SearchOptions[GoMove, GoBoard] =
    new SearchOptions[GoMove, GoBoard](
      SearchStrategyType.NEGASCOUT,
      new BruteSearchOptions(1, 0),
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
    treeViewer: IGameTreeViewable
  ): GoMove = throw new UnsupportedOperationException()
  override def done(move: GoMove, recordWin: Boolean): Boolean = false
  override def worth(lastMove: GoMove, weights: ParameterArray): Int = lastMove.getValue
  override def generateMoves(lastMove: GoMove, weights: ParameterArray): MoveList[GoMove] =
    val list = new MoveList[GoMove]()
    if lastMove eq root then list.add(childMove)
    list
  override def generateUrgentMoves(lastMove: GoMove, weights: ParameterArray): MoveList[GoMove] =
    new MoveList[GoMove]()
  override def inJeopardy(move: GoMove, weights: ParameterArray): Boolean = false
  override def getBoard: GoBoard = new GoBoard(5, 0)
  override def getMoveList: MoveList[GoMove] = new MoveList[GoMove]()
  override def getNumMoves(): Int = depth
  override def copy(): Searchable[GoMove, GoBoard] = this
  override def getHashKey(): HashKey =
    hashSerial += 1
    new HashKey(hashSerial)

  private def move(row: Int, col: Int, black: Boolean): GoMove =
    new GoMove(new ByteLocation(row, col), 0, new GoStone(black))
