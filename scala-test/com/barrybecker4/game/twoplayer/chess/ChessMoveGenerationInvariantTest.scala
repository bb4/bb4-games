package com.barrybecker4.game.twoplayer.chess

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.twoplayer.common.MoveGenerationInvariantChecks
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions
import java.awt.Color
import org.scalatest.funsuite.AnyFunSuite

class ChessMoveGenerationInvariantTest extends AnyFunSuite {

  private val Seeds = Seq(52, 16807, 42)
  private val MaxPlayoutSteps = 200

  private def newSearchable(): ChessSearchable = {
    val board = new ChessBoard()
    board.reset()
    val p1Opts = new ChessPlayerOptions("p1", Color.WHITE)
    val p2Opts = new ChessPlayerOptions("p2", Color.BLACK)
    configureFullMoveSet(p1Opts.getSearchOptions.getBestMovesSearchOptions)
    configureFullMoveSet(p2Opts.getSearchOptions.getBestMovesSearchOptions)
    val players = new PlayerList()
    players.add(new Player(p1Opts, true))
    players.add(new Player(p2Opts, false))
    new ChessSearchable(board, players)
  }

  private def configureFullMoveSet(opts: BestMovesSearchOptions): Unit = {
    opts.setPercentageBestMoves(100)
    opts.setPercentLessThanBestThresh(0)
    opts.setMinBestMoves(1)
  }

  private def chessMoveIdentity(move: ChessMove): String =
    s"p${move.isPlayer1}:${move.getFromRow},${move.getFromCol}->${move.getToRow},${move.getToCol}"

  Seeds.foreach { seed =>
    test(s"move generation invariants hold for seeded chess playouts (seed=$seed)") {
      GameContext.setDebugMode(-1)
      val searchable = newSearchable()
      val weights = new ChessWeights()

      MoveGenerationInvariantChecks.runSeededPlayout[ChessMove](
        searchable,
        lastMove =>
          if MoveGenerationInvariantChecks.player1ToMove(lastMove) then
            weights.getPlayer1Weights
          else
            weights.getPlayer2Weights,
        MaxPlayoutSteps,
        seed,
        (lastMove, context) =>
          val w =
            if MoveGenerationInvariantChecks.player1ToMove(lastMove) then
              weights.getPlayer1Weights
            else
              weights.getPlayer2Weights
          val moves = searchable.generateMoves(lastMove, w)
          MoveGenerationInvariantChecks.checkCommonInvariants(
            searchable,
            moves,
            lastMove,
            w,
            chessMoveIdentity,
            context
          )
          MoveGenerationInvariantChecks.checkChessInvariants(searchable, moves, context)
      )
    }
  }
}
