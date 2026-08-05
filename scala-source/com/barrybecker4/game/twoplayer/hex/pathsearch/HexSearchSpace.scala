package com.barrybecker4.game.twoplayer.hex.pathsearch

import com.barrybecker4.common.geometry.{IntLocation, Location}
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.hex.{HexBoard, HexBoardUtil}
import com.barrybecker4.search.space.AbstractSearchSpace
import scala.Option
import scala.collection.immutable.Seq
import scala.jdk.javaapi.CollectionConverters

import java.util.{Collections, LinkedList, List as JList}

/**
 * @author Barry Becker
 */
class HexSearchSpace(board: HexBoard, player1: Boolean) 
    extends AbstractSearchSpace[HexState, HexTransition](
      new HexState(board, if player1 then IntLocation(0, 2) else IntLocation(2, 0))
    ):

  private val maxPlus1 = board.getNumRows + 1
  private val PATH_STEP_COST = 10

  override def isGoal(state: HexState): Boolean =
    val loc = state.getLocation
    if player1 then loc.row == maxPlus1 else loc.col == maxPlus1

  /**
   * @return transitions all adjacent positions that are either friendly or empty.
   *   Friendly positions have a cost of 0, while empty ones have a cost of 1
   *   A good optimization would be to avoid adding the place that we just came from.
   */
  override def legalTransitions(state: HexState): Seq[HexTransition] =
    val nbrs = new LinkedList[HexTransition]()
    val loc = state.getLocation
    val board = state.getBoard

    for dir <- 0 until 6 do
      val nbrLoc = HexBoardUtil.getNeighborLocation(loc, dir)
      if isValid(nbrLoc, player1) then
        val pos = board.getPosition(nbrLoc)
        if pos == null then
          if (!player1 && (nbrLoc.col < 1 || nbrLoc.col > board.getNumCols)) ||
              player1 && (nbrLoc.row < 1 || nbrLoc.row > board.getNumRows) then
            nbrs.add(HexTransition(nbrLoc, 0))
        else if pos.isOccupied && pos.getPiece.isOwnedByPlayer1 == player1 then
          nbrs.add(HexTransition(nbrLoc, 0))
        else if !pos.isOccupied then
          nbrs.add(HexTransition(nbrLoc, PATH_STEP_COST))

    Collections.sort(nbrs)
    CollectionConverters.asScala(nbrs).toSeq

  /**
   * @param loc the location to check
   * @param player1 true if player1
   * @return true if the move is in bounds of the board.
   * Player 1 cannot touch the left/right sides and Player 2 cannot touch the top/bottom sides.
   */
  private def isValid(loc: Location, player1: Boolean): Boolean =
    val row = loc.row
    val col = loc.col
    val max = initialState.getBoard.getNumRows
    if player1 then
      row >= 0 && row <= maxPlus1 && col > 0 && col <= max
    else
      row > 0 && row <= max && col >= 0 && col <= maxPlus1

  override def transition(lastState: HexState, transition: HexTransition): HexState =
    new HexState(lastState.getBoard, transition.getLocation)

  override def alreadySeen(state: HexState, seen: scala.collection.mutable.Set[HexState]): Boolean =
    if !seen.contains(state) then
      seen.add(state)
      false
    else
      true

  /**
   * Estimated distance to the goal state
   * It is important that this distance underestimates the true distance, otherwise, we may get into a
   * situation where it will pick a more direct path that is not the shortest cost.
   */
  override def distanceFromGoal(state: HexState): Int =
    val lastLocation = state.getLocation
    (PATH_STEP_COST - 1) *
      (if player1 then maxPlus1 - lastLocation.row else maxPlus1 - lastLocation.col)

  /**
   * @param transition  transition to get cost of
   * @return either 0 or 1
   */
  override def getCost(transition: HexTransition): Int =
    transition.getCost

  override def refresh(state: HexState, numTries: Long): Unit =
    ()

  override def finalRefresh(path: Option[Seq[HexTransition]], state: Option[HexState], 
                           numTries: Long, elapsedMillis: Long): Unit =
    ()
