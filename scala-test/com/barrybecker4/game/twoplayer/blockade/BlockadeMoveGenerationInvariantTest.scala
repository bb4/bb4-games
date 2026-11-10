package com.barrybecker4.game.twoplayer.blockade

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.common.{MoveGenerationInvariantChecks, TwoPlayerPlayerOptions}
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import org.scalatest.funsuite.AnyFunSuite
import scala.jdk.CollectionConverters._

class BlockadeMoveGenerationInvariantTest extends AnyFunSuite {

  private val Seeds = Seq(52, 16807, 42)
  private val MaxPlayoutSteps = 80

  private def newSearchable(): com.barrybecker4.game.twoplayer.blockade.BlockadeSearchable = {
    GameContext.setDebugMode(-1)
    GameContext.loadResources("blockade")
    val controller = new BlockadeController()
    controller.getPlayers.asScala.foreach { player =>
      val opts = player.getOptions
        .asInstanceOf[TwoPlayerPlayerOptions[BlockadeMove, BlockadeBoard]]
        .getSearchOptions
        .getBestMovesSearchOptions
      configureFullMoveSet(opts)
    }
    controller.getSearchable().asInstanceOf[BlockadeSearchable]
  }

  private def configureFullMoveSet(opts: BestMovesSearchOptions): Unit = {
    opts.setPercentageBestMoves(100)
    opts.setPercentLessThanBestThresh(0)
    opts.setMinBestMoves(1)
  }

  private def blockadeMoveIdentity(move: BlockadeMove): String =
    val wallPart =
      if move.getWall == null then "noWall"
      else
        val positions = move.getWall.getPositions.asScala.map(p => s"${p.getRow},${p.getCol}").mkString(";")
        s"wall:$positions"
    s"p${move.isPlayer1}:${move.getFromRow},${move.getFromCol}->${move.getToRow},${move.getToCol}:$wallPart"

  Seeds.foreach { seed =>
    test(s"move generation invariants hold for seeded Blockade playouts (seed=$seed)") {
      val searchable = newSearchable()
      val weights = new BlockadeWeights()

      MoveGenerationInvariantChecks.runSeededPlayout[BlockadeMove](
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
            blockadeMoveIdentity,
            context
          )
          MoveGenerationInvariantChecks.checkBlockadeInvariants(searchable, moves, context)
      )
    }
  }
}
