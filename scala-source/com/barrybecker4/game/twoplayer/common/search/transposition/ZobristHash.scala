/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import java.util.Random

/**
 * A Zobrist Hash is a technique for creating a key for a game board configuration.
 * see http://en.wikipedia.org/wiki/Zobrist_hashing
 * The key is not guaranteed to be unique between positions, but collisions
 * should be exceedingly rare.
 * Keeps track of the random numbers to use for the state at each position.
 * No need to create more than one of these per game type.
 *
 * @author Barry Becker
 */
final class ZobristHash(
  private val board: TwoPlayerBoard[_],
  randomSeed: Int = 0,
  private val includeHistory: Boolean = false
) {

  import scala.compiletime.uninitialized
  
  private var randomNumberTable: Array[Array[Array[Long]]] = uninitialized
  private var RANDOM: Random = uninitialized
  private var currentKey: HashKey = uninitialized

  injectRandom(new Random(randomSeed))

  def this(board: TwoPlayerBoard[_]) = this(board, 0, false)

  /**
   * @return the current Zobrist hash key for the board state.
   */
  def getKey: HashKey = currentKey

  def applyMove(move: Location, stateIndex: Int): Unit = {
    applyPositionToKey(move, stateIndex)
  }

  def applyPassingMove(): Unit = {
    currentKey.applyMove(null, ZobristHash.PASS_MOVE_KEY)
  }

  def applyMoveNumber(number: Int): Unit = {
    currentKey.applyMove(null, number)
  }

  private def injectRandom(r: Random): Unit = {
    RANDOM = r
    initialize()
  }

  /**
   * The number of states for a position is the number of pieces (or combinations of pieces if more than one
   * piece type is allowed) at a given position times the number of players (always 2?).
   * So for example, in chess, the numStates would be 7 * 2 = 14. For go, its 2.
   */
  private def initialize(): Unit = {
    val nrows = board.getNumRows
    val ncols = board.getNumCols
    val numStatesPerPosition = board.getNumPositionStates
    randomNumberTable = Array.ofDim[Long](nrows, ncols, numStatesPerPosition)

    for (i <- 0 until nrows; j <- 0 until ncols; state <- 0 until numStatesPerPosition) {
      randomNumberTable(i)(j)(state) = RANDOM.nextLong()
    }
    currentKey = getInitialKey(board)
  }

  /**
   * @param board board state to initialize hash from.
   * @return the Zobrist Hash Key created from XORing together all the position states.
   */
  private def getInitialKey(board: TwoPlayerBoard[_]): HashKey = {
    currentKey = createHashKey()
    val nrows = board.getNumRows
    val ncols = board.getNumCols

    for (i <- 1 to nrows; j <- 1 to ncols) {
      val pos = board.getPosition(i, j)
      if (pos.isOccupied) {
        applyPositionToKey(ByteLocation(i.toByte, j.toByte), board.getStateIndex(pos))
      }
    }
    currentKey
  }

  private def createHashKey(): HashKey = {
    if (includeHistory) new HistoricalHashKey() else new HashKey()
  }

  private def applyPositionToKey(location: Location, stateIndex: Int): Unit = {
    val specialNum = randomNumberTable(location.row - 1)(location.col - 1)(stateIndex)
    currentKey.applyMove(location, specialNum)
  }
}

object ZobristHash {
  private val PASS_MOVE_KEY = 3249913293473197278L
}
