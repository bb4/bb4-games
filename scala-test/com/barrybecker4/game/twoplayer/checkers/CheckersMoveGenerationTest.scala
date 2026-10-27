package com.barrybecker4.game.twoplayer.checkers

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.common.player.{Player, PlayerList}
import java.awt.Color
import org.scalatest.funsuite.AnyFunSuite

class CheckersMoveGenerationTest extends AnyFunSuite {

  private def createBoard(): CheckersBoard = {
    val board = new CheckersBoard()
    board.reset()
    board
  }

  /** Empty board for single-piece scenarios (constructor reset fills starting rows). */
  private def emptyBoard(): CheckersBoard = {
    val board = new CheckersBoard()
    for (r <- 1 to board.getNumRows; c <- 1 to board.getNumCols) {
      board.setPosition(new BoardPosition(r, c, null))
    }
    board
  }

  private def createSearchable(board: CheckersBoard): CheckersSearchable = {
    val p1Opts = new CheckersPlayerOptions("p1", Color.WHITE)
    val p2Opts = new CheckersPlayerOptions("p2", Color.BLACK)
    p1Opts.getSearchOptions.getBestMovesSearchOptions.setPercentageBestMoves(100)
    p2Opts.getSearchOptions.getBestMovesSearchOptions.setPercentageBestMoves(100)
    val players = new PlayerList()
    players.add(new Player(p1Opts, true))
    players.add(new Player(p2Opts, false))
    new CheckersSearchable(board, players)
  }

  test("initial position generates correct number of moves for player1") {
    val board = createBoard()
    val searchable = createSearchable(board)
    val weights = new CheckersWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 7, s"Expected 7 opening moves for player1, got ${moves.size()}")
  }

  test("initial position generates correct number of moves for player2") {
    val board = createBoard()
    val searchable = createSearchable(board)
    val weights = new CheckersWeights()
    
    // Make a dummy move for player1 to switch turn
    val firstMove = CheckersMove.createMove(
      new ByteLocation(3, 1), new ByteLocation(4, 2), null, 0,
      new CheckersPiece(true, CheckersPieceType.REGULAR))
    
    val moves = searchable.generateMoves(firstMove, weights.getPlayer2Weights)
    assert(moves.size() == 7, s"Expected 7 opening moves for player2, got ${moves.size()}")
  }

  test("single checker at (4,4) generates correct diagonal moves") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(4, 4, new CheckersPiece(true, CheckersPieceType.REGULAR)))
    val searchable = createSearchable(board)
    val weights = new CheckersWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 2, s"Expected 2 forward diagonal moves, got ${moves.size()}")
  }

  test("king at (4,4) generates correct moves in all directions") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(4, 4, new CheckersPiece(true, CheckersPieceType.KING)))
    val searchable = createSearchable(board)
    val weights = new CheckersWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 4, s"Expected 4 diagonal moves for king, got ${moves.size()}")
  }

  test("jump move is generated when opponent piece is adjacent") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(4, 4, new CheckersPiece(true, CheckersPieceType.REGULAR)))
    board.setPosition(new BoardPosition(5, 5, new CheckersPiece(false, CheckersPieceType.REGULAR)))
    val searchable = createSearchable(board)
    val weights = new CheckersWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 2, s"Expected jump and regular move, got ${moves.size()}")
    
    import scala.jdk.CollectionConverters._
    val jumpMove = moves.asScala.find(m => m.getToRow == 6 && m.getToCol == 6)
    assert(jumpMove.isDefined, "Expected jump move to (6,6)")
    assert(jumpMove.get.captureList != null, "Jump move should have captures")
  }

  test("initial board worth is approximately zero") {
    val board = createBoard()
    val searchable = createSearchable(board)
    val weights = new CheckersWeights()
    
    val worth = searchable.worth(null, weights.getDefaultWeights)
    assert(math.abs(worth) < 100, s"Expected near-zero initial worth, got $worth")
  }

  test("board with only player1 pieces has large positive worth") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(2, 2, new CheckersPiece(true, CheckersPieceType.REGULAR)))
    board.setPosition(new BoardPosition(3, 3, new CheckersPiece(true, CheckersPieceType.KING)))
    val searchable = createSearchable(board)
    val weights = new CheckersWeights()
    
    val worth = searchable.worth(null, weights.getDefaultWeights)
    assert(worth > 1000, s"Expected large positive worth, got $worth")
  }

  test("board with only player2 pieces has large negative worth") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(6, 6, new CheckersPiece(false, CheckersPieceType.REGULAR)))
    board.setPosition(new BoardPosition(7, 7, new CheckersPiece(false, CheckersPieceType.KING)))
    val searchable = createSearchable(board)
    val weights = new CheckersWeights()
    
    val worth = searchable.worth(null, weights.getDefaultWeights)
    assert(worth < -1000, s"Expected large negative worth, got $worth")
  }

  test("move to king row sets kinged flag") {
    val move = CheckersMove.createMove(
      new ByteLocation(7, 7), new ByteLocation(8, 8), null, 0,
      new CheckersPiece(true, CheckersPieceType.REGULAR))
    
    assert(move.kinged, "Move to row 8 should set kinged flag")
    assert(move.getPiece.getType == CheckersPiece.KING, "Piece should be promoted to king")
  }

  test("CheckersMove equals and hashCode work correctly") {
    val move1 = CheckersMove.createMove(
      new ByteLocation(3, 1), new ByteLocation(4, 2), null, 100,
      new CheckersPiece(true, CheckersPieceType.REGULAR))
    
    val move2 = CheckersMove.createMove(
      new ByteLocation(3, 1), new ByteLocation(4, 2), null, 200,
      new CheckersPiece(true, CheckersPieceType.REGULAR))
    
    assert(move1.equals(move2), "Moves with same from/to should be equal")
    // TwoPlayerMove.hashCode mixes getValue() even when equals ignores it — preserve that contract.
    val move3 = CheckersMove.createMove(
      new ByteLocation(3, 1), new ByteLocation(4, 2), null, 100,
      new CheckersPiece(true, CheckersPieceType.REGULAR))
    assert(move1.hashCode() == move3.hashCode(), "Equal moves with same value should have same hashCode")
  }

  implicit class MoveListOps(moves: com.barrybecker4.game.common.MoveList[CheckersMove]) {
    def asScala: Seq[CheckersMove] = {
      import scala.jdk.CollectionConverters._
      moves.asScala.toSeq
    }
  }
}
