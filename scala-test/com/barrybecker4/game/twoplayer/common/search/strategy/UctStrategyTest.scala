/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.twoplayer.blockade.{BlockadeController, BlockadeSearchable}
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.checkers.{CheckersBoard, CheckersSearchable, CheckersPlayerOptions, CheckersWeights}
import com.barrybecker4.game.twoplayer.chess.{ChessBoard, ChessSearchable, ChessPlayerOptions, ChessWeights}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove, TwoPlayerPlayerOptions, TwoPlayerSearchable}
import com.barrybecker4.game.twoplayer.common.search.Progress
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, SearchOptions}
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.options.GoWeights
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoSearchable}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.hex.{HexController, HexSearchable}
import com.barrybecker4.game.twoplayer.hex.HexBoard
import com.barrybecker4.game.twoplayer.mancala.{MancalaController, MancalaSearchable}
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard
import com.barrybecker4.game.twoplayer.mancala.move.MancalaMove
import com.barrybecker4.game.twoplayer.tictactoe.{TicTacToeController, TicTacToeHelper, TicTacToeSearchable, TicTacToeWeights}
import com.barrybecker4.optimization.parameter.ParameterArray
import java.awt.Color
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import scala.jdk.CollectionConverters.*

/**
 * Integration tests for [[UctStrategy]] against real games (issue #51).
 */
class UctStrategyTest extends AnyFunSuite with BeforeAndAfterEach {

  private val DefaultSeed = 424242
  private val SmallMaxSimulations = 40

  override def beforeEach(): Unit = {
    GameContext.setDebugMode(-1)
    GameContext.setRandomSeed(DefaultSeed)
  }

  private def configureFullMoveSet(opts: BestMovesSearchOptions): Unit = {
    opts.setPercentageBestMoves(100)
    opts.setPercentLessThanBestThresh(0)
    opts.setMinBestMoves(1)
  }

  private def configureUct(options: SearchOptions[?, ?], maxSimulations: Int): Unit = {
    options.setSearchStrategyMethod(SearchStrategyType.UCT)
    options.getMonteCarloSearchOptions.setMaxSimulations(maxSimulations)
    options.getMonteCarloSearchOptions.setRandomLookAhead(12)
    options.getMonteCarloSearchOptions.setExploreExploitRatio(1.0)
    configureFullMoveSet(options.getBestMovesSearchOptions)
  }

  private def applyUctToPlayers(controller: TwoPlayerController[?, ?], maxSimulations: Int): Unit =
    controller.getPlayers.asScala.foreach { player =>
      val options = player.getOptions
        .asInstanceOf[TwoPlayerPlayerOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
        .getSearchOptions
      options.setSearchStrategyMethod(SearchStrategyType.UCT)
      options.getMonteCarloSearchOptions.setMaxSimulations(maxSimulations)
      options.getMonteCarloSearchOptions.setRandomLookAhead(12)
      options.getMonteCarloSearchOptions.setExploreExploitRatio(1.0)
      configureFullMoveSet(options.getBestMovesSearchOptions)
    }

  private def assertBoardUnchangedBySearch[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
      searchable: TwoPlayerSearchable[M, B],
      weights: ParameterArray,
      maxSimulations: Int,
      context: String
  ): Unit = {
    val lastMove = searchable.getMoveList.getLastMove.asInstanceOf[M]
    val numMovesBefore = searchable.getNumMoves()
    val hashBefore = searchable.getHashKey().getKey
    val strategy = new UctStrategy[M, B](searchable, weights)
    strategy.search(lastMove, new SearchTreeNode(lastMove))
    assert(
      searchable.getNumMoves() == numMovesBefore,
      s"$context: move count changed after UCT search (${numMovesBefore} -> ${searchable.getNumMoves()})"
    )
    assert(
      searchable.getHashKey().getKey == hashBefore,
      s"$context: board hash changed after UCT search"
    )
  }

  private def assertCopyIsIndependent[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
      searchable: TwoPlayerSearchable[M, B],
      weights: ParameterArray,
      context: String
  ): Unit = {
    val lastMove =
      if searchable.getNumMoves() == 0 then null.asInstanceOf[M]
      else searchable.getMoveList.getLastMove.asInstanceOf[M]
    val numMovesBefore = searchable.getNumMoves()
    val hashBefore = searchable.getHashKey().getKey
    val copy = searchable.copy().asInstanceOf[TwoPlayerSearchable[M, B]]
    val moves = copy.generateMoves(lastMove, weights)
    assert(moves.size() > 0, s"$context: expected at least one legal move")
    val move = moves.get(0)
    copy.makeInternalMove(move)
    assert(searchable.getNumMoves() == numMovesBefore, s"$context: original move list changed after copy was played")
    assert(searchable.getHashKey().getKey == hashBefore, s"$context: original board hash changed after copy was played")
  }

  test("UCT search leaves TicTacToe board unchanged") {
    val helper = new TicTacToeHelper()
    val controller = new TicTacToeController()
    applyUctToPlayers(controller, SmallMaxSimulations)
    controller.restoreFromStream(helper.getTestResource(Progress.MIDDLE, true))
    val searchable = controller.getSearchable().asInstanceOf[TicTacToeSearchable]
    val weights = controller.getComputerWeights().asInstanceOf[TicTacToeWeights].getPlayer1Weights
    assertBoardUnchangedBySearch(searchable, weights, SmallMaxSimulations, "TicTacToe mid-game")
  }

  test("UCT search leaves chess board unchanged") {
    val board = new ChessBoard()
    board.reset()
    val p1Opts = new ChessPlayerOptions("p1", Color.WHITE)
    val p2Opts = new ChessPlayerOptions("p2", Color.BLACK)
    configureUct(p1Opts.getSearchOptions, SmallMaxSimulations)
    configureUct(p2Opts.getSearchOptions, SmallMaxSimulations)
    val players = new PlayerList()
    players.add(new Player(p1Opts, true))
    players.add(new Player(p2Opts, false))
    val searchable = new ChessSearchable(board, players)
    val weights = new ChessWeights().getPlayer1Weights
    val opening = searchable.generateMoves(null, weights).get(0)
    searchable.makeInternalMove(opening)
    assertBoardUnchangedBySearch(searchable, weights, SmallMaxSimulations, "Chess after one move")
  }

  private def chessSearchable(): ChessSearchable = {
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

  private def checkersSearchable(): CheckersSearchable = {
    val board = new CheckersBoard()
    board.reset()
    val p1Opts = new CheckersPlayerOptions("p1", Color.WHITE)
    val p2Opts = new CheckersPlayerOptions("p2", Color.BLACK)
    configureFullMoveSet(p1Opts.getSearchOptions.getBestMovesSearchOptions)
    configureFullMoveSet(p2Opts.getSearchOptions.getBestMovesSearchOptions)
    val players = new PlayerList()
    players.add(new Player(p1Opts, true))
    players.add(new Player(p2Opts, false))
    new CheckersSearchable(board, players)
  }

  private def ticTacToeSearchable(): TicTacToeSearchable =
    new TicTacToeController().getSearchable()

  private def hexSearchable(): HexSearchable = {
    val controller = new HexController()
    configureFullMoveSet(
      controller.getPlayers.get(0).getOptions
        .asInstanceOf[TwoPlayerPlayerOptions[TwoPlayerMove, HexBoard]]
        .getSearchOptions
        .getBestMovesSearchOptions
    )
    controller.getSearchable().asInstanceOf[HexSearchable]
  }

  private def mancalaSearchable(): MancalaSearchable = {
    val controller = new MancalaController()
    configureFullMoveSet(
      controller.getPlayers.get(0).getOptions
        .asInstanceOf[TwoPlayerPlayerOptions[MancalaMove, MancalaBoard]]
        .getSearchOptions
        .getBestMovesSearchOptions
    )
    controller.getSearchable().asInstanceOf[MancalaSearchable]
  }

  private def blockadeSearchable(): BlockadeSearchable = {
    GameContext.loadResources("blockade")
    val controller = new BlockadeController()
    configureFullMoveSet(
      controller.getPlayers.get(0).getOptions
        .asInstanceOf[TwoPlayerPlayerOptions[BlockadeMove, BlockadeBoard]]
        .getSearchOptions
        .getBestMovesSearchOptions
    )
    controller.getSearchable().asInstanceOf[BlockadeSearchable]
  }

  private def goSearchable(): GoSearchable = {
    GameContext.loadResources("go")
    val controller = new GoController(9, 0)
    configureFullMoveSet(
      controller.getPlayers.get(0).getOptions
        .asInstanceOf[TwoPlayerPlayerOptions[GoMove, GoBoard]]
        .getSearchOptions
        .getBestMovesSearchOptions
    )
    controller.getSearchable().asInstanceOf[GoSearchable]
  }

  val copyIndependenceCases: Seq[(String, () => (TwoPlayerSearchable[?, ?], ParameterArray))] = Seq(
    ("TicTacToe", () => (ticTacToeSearchable(), new TicTacToeWeights().getPlayer1Weights)),
    ("Chess", () => (chessSearchable(), new ChessWeights().getPlayer1Weights)),
    ("Checkers", () => (checkersSearchable(), new CheckersWeights().getPlayer1Weights)),
    ("Hex", () => (hexSearchable(), new com.barrybecker4.game.twoplayer.hex.HexWeights().getPlayer1Weights)),
    ("Mancala", () => (mancalaSearchable(), new com.barrybecker4.game.twoplayer.mancala.MancalaWeights().getPlayer1Weights)),
    ("Blockade", () => (blockadeSearchable(), new com.barrybecker4.game.twoplayer.blockade.BlockadeWeights().getPlayer1Weights)),
    ("Go", () => (goSearchable(), new GoWeights().getDefaultWeights))
  )

  copyIndependenceCases.foreach { case (name, factory) =>
    test(s"Searchable.copy() is independent for $name") {
      val (searchable, weights) = factory()
      assertCopyIsIndependent(searchable, weights, name)
    }
  }

  test("playSimulation returns scores in [0, 1]") {
    val controller = new TicTacToeController()
    applyUctToPlayers(controller, SmallMaxSimulations)
    val helper = new TicTacToeHelper()
    controller.restoreFromStream(helper.getTestResource(Progress.MIDDLE, true))
    val searchable = controller.getSearchable().asInstanceOf[TicTacToeSearchable]
    val weights = controller.getComputerWeights().asInstanceOf[TicTacToeWeights].getPlayer1Weights
    val strategy = new UctStrategy(searchable, weights)
    val lastMove = searchable.getMoveList.getLastMove
    val root = UctNode[TwoPlayerMove](lastMove)
    var i = 0
    while i < 60 do
      val score = strategy.playSimulation(root, null)
      assert(score >= 0.0f && score <= 1.0f, s"playSimulation score out of range: $score")
      i += 1
  }

  test("UCT finds immediate winning move in TicTacToe") {
    val controller = new TicTacToeController()
    applyUctToPlayers(controller, 200)
    def play(row: Int, col: Int, player1: Boolean): Unit = {
      val move = TwoPlayerMove.createMove(row, col, 0, new GamePiece(player1))
      controller.makeMove(move)
    }
    play(1, 1, true)
    play(2, 2, false)
    play(1, 2, true)
    play(3, 3, false)

    val searchable = controller.getSearchable().asInstanceOf[TicTacToeSearchable]
    val weights = controller.getComputerWeights().asInstanceOf[TicTacToeWeights].getPlayer1Weights
    val lastMove = searchable.getMoveList.getLastMove
    assert(lastMove.isPlayer1 == false, "Player 1 should be to move")

    val strategy = new UctStrategy(searchable, weights)
    val next = strategy.search(lastMove, new SearchTreeNode(lastMove))
    val winSpot = new ByteLocation(1, 3)
    assert(
      next.getToLocation == winSpot,
      s"Expected winning move at $winSpot but got ${next.getToLocation}"
    )
    assert(next.getValue >= SearchStrategy.WINNING_VALUE, "Winning move should have winning evaluation")
  }

  test("UCT search is reproducible with a fixed GameContext random seed") {
    val helper = new TicTacToeHelper()
    def searchWithSeed(seed: Int): TwoPlayerMove = {
      GameContext.setRandomSeed(seed)
      val controller = new TicTacToeController()
      applyUctToPlayers(controller, 80)
      controller.restoreFromStream(helper.getTestResource(Progress.MIDDLE, true))
      val searchable = controller.getSearchable().asInstanceOf[TicTacToeSearchable]
      val weights = controller.getComputerWeights().asInstanceOf[TicTacToeWeights].getPlayer1Weights
      val lastMove = searchable.getMoveList.getLastMove
      new UctStrategy(searchable, weights).search(lastMove, new SearchTreeNode(lastMove))
    }

    val seed = 99173
    val first = searchWithSeed(seed)
    val second = searchWithSeed(seed)
    assert(first == second, s"Repeated UCT search differed: $first vs $second")
  }
}
