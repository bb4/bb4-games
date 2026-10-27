/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.chess.{ChessPiece, ChessPieceType}
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer
import com.barrybecker4.ui.util.GUIUtil

import javax.swing.ImageIcon
import java.awt.{Color, Graphics2D, Point}

class ChessPieceRenderer extends TwoPlayerPieceRenderer {

  override def getPlayer1Color(): Color = ChessPieceRenderer.DEFAULT_PLAYER1_COLOR
  override def getPlayer2Color(): Color = ChessPieceRenderer.DEFAULT_PLAYER2_COLOR

  override def renderForShow(g2: Graphics2D, pos: Point, piece: GamePiece, pieceSize: Int): Unit = {
    val chessPiece = piece.asInstanceOf[ChessPiece]
    val playerIdx = if (chessPiece.isOwnedByPlayer1) 0 else 1

    chessPiece.getPieceType match {
      case ChessPieceType.PAWN =>
        g2.drawImage(ChessPieceRenderer.pawnImage(playerIdx).getImage, pos.x, pos.y, pieceSize, pieceSize, null)
      case ChessPieceType.ROOK =>
        g2.drawImage(ChessPieceRenderer.rookImage(playerIdx).getImage, pos.x, pos.y, pieceSize, pieceSize, null)
      case ChessPieceType.KNIGHT =>
        g2.drawImage(ChessPieceRenderer.knightImage(playerIdx).getImage, pos.x, pos.y, pieceSize, pieceSize, null)
      case ChessPieceType.BISHOP =>
        g2.drawImage(ChessPieceRenderer.bishopImage(playerIdx).getImage, pos.x, pos.y, pieceSize, pieceSize, null)
      case ChessPieceType.QUEEN =>
        g2.drawImage(ChessPieceRenderer.queenImage(playerIdx).getImage, pos.x, pos.y, pieceSize, pieceSize, null)
      case ChessPieceType.KING =>
        g2.drawImage(ChessPieceRenderer.kingImage(playerIdx).getImage, pos.x, pos.y, pieceSize, pieceSize, null)
    }
  }
}

object ChessPieceRenderer {
  private var renderer_ : TwoPlayerPieceRenderer = scala.compiletime.uninitialized

  private val DEFAULT_PLAYER1_COLOR = new Color(30, 170, 10)
  private val DEFAULT_PLAYER2_COLOR = new Color(170, 0, 180)

  private val IMAGE_DIR = GameContext.GAME_RESOURCE_ROOT + "twoplayer/chess/ui/images/"
  
  private val pawnImage: Array[ImageIcon] = Array(
    GUIUtil.getIcon(IMAGE_DIR + "pawn1.png"),
    GUIUtil.getIcon(IMAGE_DIR + "pawn2.png")
  )
  private val rookImage: Array[ImageIcon] = Array(
    GUIUtil.getIcon(IMAGE_DIR + "rook1.png"),
    GUIUtil.getIcon(IMAGE_DIR + "rook2.png")
  )
  private val knightImage: Array[ImageIcon] = Array(
    GUIUtil.getIcon(IMAGE_DIR + "knight1.png"),
    GUIUtil.getIcon(IMAGE_DIR + "knight2.png")
  )
  private val bishopImage: Array[ImageIcon] = Array(
    GUIUtil.getIcon(IMAGE_DIR + "bishop1.png"),
    GUIUtil.getIcon(IMAGE_DIR + "bishop2.png")
  )
  private val queenImage: Array[ImageIcon] = Array(
    GUIUtil.getIcon(IMAGE_DIR + "queen1.png"),
    GUIUtil.getIcon(IMAGE_DIR + "queen2.png")
  )
  private val kingImage: Array[ImageIcon] = Array(
    GUIUtil.getIcon(IMAGE_DIR + "king1.png"),
    GUIUtil.getIcon(IMAGE_DIR + "king2.png")
  )

  def getRenderer: TwoPlayerPieceRenderer = {
    if (renderer_ == null) renderer_ = new ChessPieceRenderer()
    renderer_
  }
}
