package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.{MoveGenerationInvariantChecks, TwoPlayerPlayerOptions}
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions
import com.barrybecker4.game.twoplayer.go.board.GoSearchable
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.GoWeights
import org.scalatest.funsuite.AnyFunSuite
import scala.jdk.CollectionConverters._

class GoMoveGenerationInvariantTest extends AnyFunSuite {

  private val Seeds = Seq(52, 16807, 42)
  private val MaxPlayoutSteps = 120

  private def newSearchable(): GoSearchable = {
    GameContext.setDebugMode(-1)
    GameContext.loadResources("go")
    val controller = new GoController(9, 0)
    controller.getPlayers.asScala.foreach { player =>
      val opts = player.getOptions
        .asInstanceOf[TwoPlayerPlayerOptions[GoMove, com.barrybecker4.game.twoplayer.go.board.GoBoard]]
        .getSearchOptions
        .getBestMovesSearchOptions
      configureFullMoveSet(opts)
    }
    controller.getSearchable().asInstanceOf[GoSearchable]
  }

  private def configureFullMoveSet(opts: BestMovesSearchOptions): Unit = {
    opts.setPercentageBestMoves(100)
    opts.setPercentLessThanBestThresh(0)
    opts.setMinBestMoves(1)
  }

  private def goMoveIdentity(move: GoMove): String =
    if move.isPassingMove then s"pass:p${move.isPlayer1}"
    else if move.isResignationMove then s"resign:p${move.isPlayer1}"
    else s"stone:p${move.isPlayer1}:${move.getToRow},${move.getToCol}"

  Seeds.foreach { seed =>
    test(s"move generation invariants hold for seeded Go playouts (seed=$seed)") {
      val searchable = newSearchable()
      val weights = new GoWeights()

      MoveGenerationInvariantChecks.runSeededPlayout[GoMove](
        searchable,
        _ => weights.getDefaultWeights,
        MaxPlayoutSteps,
        seed,
        (lastMove, context) =>
          val moves = searchable.generateMoves(lastMove, weights.getDefaultWeights)
          MoveGenerationInvariantChecks.checkCommonInvariants(
            searchable,
            moves,
            lastMove,
            weights.getDefaultWeights,
            goMoveIdentity,
            context,
            m => !m.isPassingMove && !m.isResignationMove
          )
          MoveGenerationInvariantChecks.checkGoInvariants(searchable.getBoard, moves, lastMove, context)
      )
    }
  }
}
