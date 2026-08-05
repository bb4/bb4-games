package com.barrybecker4.game.twoplayer.chess

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.common.player.{Player, PlayerList}
import java.awt.Color
import org.scalatest.funsuite.AnyFunSuite

class ChessMoveGenerationTest extends AnyFunSuite {

  private def createBoard(): ChessBoard = {
    val board = new ChessBoard()
    board.reset()
    board
  }

  /** Empty board for single-piece scenarios (constructor reset fills starting rows). */
  private def emptyBoard(): ChessBoard = {
    val board = new ChessBoard()
    for (r <- 1 to board.getNumRows; c <- 1 to board.getNumCols) {
      board.setPosition(new BoardPosition(r, c, null))
    }
    board
  }

  private def createSearchable(board: ChessBoard): ChessSearchable = {
    val p1Opts = new ChessPlayerOptions("p1", Color.WHITE)
    val p2Opts = new ChessPlayerOptions("p2", Color.BLACK)
    // Characterization needs the full legal move set, not the play-time 80% cut.
    p1Opts.getSearchOptions.getBestMovesSearchOptions.setPercentageBestMoves(100)
    p2Opts.getSearchOptions.getBestMovesSearchOptions.setPercentageBestMoves(100)
    val players = new PlayerList()
    players.add(new Player(p1Opts, true))
    players.add(new Player(p2Opts, false))
    new ChessSearchable(board, players)
  }

  test("initial position generates correct number of moves for player1") {
    val board = createBoard()
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 20, s"Expected 20 opening moves for player1, got ${moves.size()}")
  }

  test("initial position generates correct number of moves for player2") {
    val board = createBoard()
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    val firstMove = ChessMove.createMove(
      new ByteLocation(2, 1), new ByteLocation(3, 1), null, 0,
      new ChessPiece(true, ChessPieceType.PAWN))
    
    val moves = searchable.generateMoves(firstMove, weights.getPlayer2Weights)
    assert(moves.size() == 20, s"Expected 20 opening moves for player2, got ${moves.size()}")
  }

  test("knight at center generates 8 moves") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(4, 4, new ChessPiece(true, ChessPieceType.KNIGHT)))
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 8, s"Expected 8 knight moves from center, got ${moves.size()}")
  }

  test("rook at corner generates 14 moves on empty board") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(1, 1, new ChessPiece(true, ChessPieceType.ROOK)))
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 14, s"Expected 14 rook moves from corner, got ${moves.size()}")
  }

  test("bishop at center generates correct diagonal moves") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(4, 4, new ChessPiece(true, ChessPieceType.BISHOP)))
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 13, s"Expected 13 bishop moves from center, got ${moves.size()}")
  }

  test("queen at center generates correct moves") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(4, 4, new ChessPiece(true, ChessPieceType.QUEEN)))
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 27, s"Expected 27 queen moves from center, got ${moves.size()}")
  }

  test("king at center generates 8 moves") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(4, 4, new ChessPiece(true, ChessPieceType.KING)))
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 8, s"Expected 8 king moves from center, got ${moves.size()}")
  }

  test("pawn first move can advance 2 squares") {
    val board = emptyBoard()
    val pawn = new ChessPiece(true, ChessPieceType.PAWN)
    board.setPosition(new BoardPosition(2, 4, pawn))
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 2, s"Expected 2 pawn moves (1 or 2 forward), got ${moves.size()}")
  }

  test("pawn after first move can only advance 1 square") {
    val board = emptyBoard()
    val pawn = new ChessPiece(true, ChessPieceType.PAWN)
    pawn.setFirstTimeMoved(false)
    board.setPosition(new BoardPosition(4, 4, pawn))
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    assert(moves.size() == 1, s"Expected 1 pawn move (1 forward), got ${moves.size()}")
  }

  test("pawn can capture diagonally") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(4, 4, new ChessPiece(true, ChessPieceType.PAWN)))
    board.setPosition(new BoardPosition(5, 5, new ChessPiece(false, ChessPieceType.PAWN)))
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    import scala.jdk.CollectionConverters._
    val moves = searchable.generateMoves(null, weights.getPlayer1Weights)
    val captureMove = moves.asScala.find(m => m.getToRow == 5 && m.getToCol == 5)
    assert(captureMove.isDefined, "Expected diagonal capture move")
    assert(captureMove.get.captureList != null, "Capture move should have captureList")
  }

  test("check detection works correctly") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(1, 1, new ChessPiece(true, ChessPieceType.KING)))
    board.setPosition(new BoardPosition(1, 8, new ChessPiece(false, ChessPieceType.ROOK)))
    
    val rookPos = board.getPosition(1, 8)
    val isCheck = board.isKingCheckedByPosition(rookPos, null)
    assert(isCheck, "Rook should check king on same row")
  }

  test("self-check move is filtered out") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(4, 4, new ChessPiece(true, ChessPieceType.KING)))
    board.setPosition(new BoardPosition(4, 5, new ChessPiece(true, ChessPieceType.ROOK)))
    board.setPosition(new BoardPosition(4, 8, new ChessPiece(false, ChessPieceType.ROOK)))
    
    val moveToCheck = ChessMove.createMove(
      new ByteLocation(4, 5), new ByteLocation(5, 5), null, 0,
      new ChessPiece(true, ChessPieceType.ROOK))
    
    val causesSelfCheck = board.causesSelfCheck(moveToCheck)
    assert(causesSelfCheck, "Moving blocking rook should cause self-check")
  }

  test("initial board worth is approximately zero") {
    val board = createBoard()
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    val worth = searchable.worth(null, weights.getDefaultWeights)
    assert(math.abs(worth) < 100, s"Expected near-zero initial worth, got $worth")
  }

  test("board with extra queen has positive worth") {
    val board = emptyBoard()
    board.setPosition(new BoardPosition(1, 1, new ChessPiece(true, ChessPieceType.KING)))
    board.setPosition(new BoardPosition(2, 2, new ChessPiece(true, ChessPieceType.QUEEN)))
    board.setPosition(new BoardPosition(8, 8, new ChessPiece(false, ChessPieceType.KING)))
    val searchable = createSearchable(board)
    val weights = new ChessWeights()
    
    val worth = searchable.worth(null, weights.getDefaultWeights)
    assert(worth > 100, s"Expected large positive worth with extra queen, got $worth")
  }

  test("piece type symbols are correct") {
    assert(ChessPieceType.PAWN.getSymbol == 'P')
    assert(ChessPieceType.ROOK.getSymbol == 'R')
    assert(ChessPieceType.KNIGHT.getSymbol == 'N')
    assert(ChessPieceType.BISHOP.getSymbol == 'B')
    assert(ChessPieceType.QUEEN.getSymbol == 'Q')
    assert(ChessPieceType.KING.getSymbol == 'K')
  }

  test("ChessPiece type index is correct") {
    val pawn = new ChessPiece(true, ChessPieceType.PAWN)
    val king = new ChessPiece(true, ChessPieceType.KING)
    assert(pawn.typeIndex() == 1)
    assert(king.typeIndex() == 6)
  }

  implicit class MoveListOps(moves: com.barrybecker4.game.common.MoveList[ChessMove]) {
    def asScala: Seq[ChessMove] = {
      import scala.jdk.CollectionConverters._
      moves.asScala.toSeq
    }
  }
}
