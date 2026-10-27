/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.cache.{ScoreCache, ScoreEntry}
import com.barrybecker4.game.twoplayer.tictactoe.TicTacToeBoard
import org.junit.Assert.assertEquals
import org.junit.{Before, Test}

import scala.jdk.CollectionConverters.*

/**
 * Various uniqueness tests for hash keys.
 * @author Barry Becker
 */
class HashUniquenessTest:

  private var hash: ZobristHash = scala.compiletime.uninitialized
  private var board: TicTacToeBoard = scala.compiletime.uninitialized
  private var scoreCache: ScoreCache = scala.compiletime.uninitialized
  private var rowMax: Int = scala.compiletime.uninitialized
  private var colMax: Int = scala.compiletime.uninitialized

  @Before
  def setUp(): Unit =
    board = new TicTacToeBoard()
    scoreCache = new ScoreCache()
    hash = createZobristHash()
    rowMax = 3
    colMax = 3

  /**
   * We will play every possible tic tac toe move and verify that the zobrist hashes generated
   * for unique board positions are unique.
   * In theory, it is possible that there are collisions, but i n practice it should be exceedingly rare.
   * To simplify the number of total moves we will just consider the 2 spaces in the upper left of the ttt board.
   */
  @Test
  def testNoCacheStatisticsTwoByOneBoard(): Unit =
    rowMax = 1
    colMax = 2
    playAllMoves(0)
    verifyScoreCacheStats(0, 4)

  /**
   * Same as above but now just run for the 3 spaces in the top row.
   */
  @Test
  def testNoCacheStatisticsThreeByOneBoard(): Unit =
    rowMax = 1
    colMax = 3
    playAllMoves(0)
    verifyScoreCacheStats(3, 12)

  /**
   * We will play every possible move on a 2x2 board and verify that the Zobrist hashes
   * generated for unique board positions are unique. To simplify the number of total
   * moves we will just consider the 4 spaces in the upper left of the ttt board.
   */
  @Test
  def testNoCacheStatisticsTwoByTwo(): Unit =
    rowMax = 2
    colMax = 2
    playAllMoves(0)
    verifyScoreCacheStats(30, 34)

  private def verifyScoreCacheStats(expHits: Int, expMisses: Int): Unit =
    System.out.println("scoreCache =" + scoreCache.toString)
    assertEquals("Unexpected number of cache hits", expHits, scoreCache.getCacheHits)
    assertEquals("Unexpected number of cache misses", expMisses, scoreCache.getCacheMisses)

  /** Play all possible tic tac toe moves */
  private def playAllMoves(depth: Int): Unit =
    val player1ToMove = depth % 2 == 0
    val openMoves = findOpenMoves(player1ToMove)
    for move <- openMoves.asScala do
      board.makeMove(move)
      applyMoveToHash(move.getToLocation)
      val key = hash.getKey
      val cachedScore = scoreCache.get(key)
      val boardScore = score(board)
      if cachedScore.isDefined then
        if boardScore != cachedScore.get.getScore then
          val tempHash = createZobristHash()
          System.out.println(
            "Dupe found: key " +
              "\nwas found in cache. The cached value was " + cachedScore.get +
              "\nand we computed worth=" + boardScore + " for board=" + board +
              "\n expected hash is " + tempHash.getKey
          )
      else
        scoreCache.put(key, new ScoreEntry(boardScore, board.toString))
      playAllMoves(depth + 1)
      // removes it from hash. must be done before next line.
      applyMoveToHash(move.getToLocation)
      board.undoMove()

  private def findOpenMoves(player1: Boolean): MoveList[TwoPlayerMove] =
    val moves = new MoveList[TwoPlayerMove]()
    var i = 1
    while i <= rowMax do
      var j = 1
      while j <= colMax do
        if board.getPosition(i, j).isUnoccupied then
          moves.add(TwoPlayerMove.createMove(i, j, 0, new GamePiece(player1)))
        j += 1
      i += 1
    moves

  private def score(b: TicTacToeBoard): Int =
    var scoreVal = 0.0
    val player1 = b.getMoveList.getLastMove.isPlayer1
    var i = 1
    while i <= rowMax do
      var j = 1
      while j <= colMax do
        if board.getPosition(i, j).isOccupied then
          scoreVal += ((i - 1) * 3 + j - 1) * (if player1 then 1 else 10)
        j += 1
      i += 1
    scoreVal.toInt

  private def applyMoveToHash(move: Location): Unit =
    val stateIndex = board.getStateIndex(board.getPosition(move))
    hash.applyMove(move, stateIndex)

  private def createZobristHash(): ZobristHash =
    new ZobristHash(board, 0, true)
