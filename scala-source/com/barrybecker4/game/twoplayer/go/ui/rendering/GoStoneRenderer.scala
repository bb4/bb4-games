/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui.rendering

import com.barrybecker4.ui.util.ImageUtil
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.{Board, BoardPosition}
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.ui.util.GUIUtil
import javax.swing.ImageIcon
import java.awt.{Color, Font, Graphics2D, Image, Point}
import java.awt.image.{BufferedImage, RescaleOp}

/**
 * Singleton class that takes a go piece and renders it for the GoBoardViewer.
 * @author Barry Becker
 */
final class GoStoneRenderer private() extends TwoPlayerPieceRenderer {

  override def getPlayer1Color(): Color = GoStoneRenderer.PLAYER1_STONE_COLOR

  override def getPlayer2Color(): Color = GoStoneRenderer.PLAYER2_STONE_COLOR

  /**
   * This draws the actual piece.
   * Draws the go stone as an image.
   * Apply a RescaleOp filter to adjust the transparency if need be.
   *
   * @param g2 graphics context
   * @param position of the piece to render
   */
  override def render(g2: Graphics2D, position: BoardPosition, cellSize: Int, margin: Int, board: Board[?]): Unit = {
    val stonePos = position.asInstanceOf[GoBoardPosition]
    if (GameContext.getDebugMode > 0) {
      drawTerritoryShading(g2, position, cellSize, margin)
    }

    val stone = position.getPiece.asInstanceOf[GoStone]
    if (stone != null) {
      val inAtari = stonePos.isInAtari(board.asInstanceOf[GoBoard])
      drawStone(g2, position, cellSize, margin, inAtari, stone)
    }
  }

  /**
   * As a debugging aid draw the background as a function of the territorial score (-1 : 1)
   */
  private def drawTerritoryShading(g2: Graphics2D, position: BoardPosition, cellSize: Int, margin: Int): Unit = {
    val score = position.asInstanceOf[GoBoardPosition].getScoreContribution
    val pc = if (score > 0) GoStoneRenderer.PLAYER1_STONE_COLOR else GoStoneRenderer.PLAYER2_STONE_COLOR
    val op = (100 * Math.abs(score)).toInt
    if (op > 255) {
      GameContext.log(1, "error: score too big =" + score + "\n")
    }
    val c = new Color(pc.getRed, pc.getGreen, pc.getBlue, Math.min(255, op))
    g2.setColor(c)
    g2.fillRect(margin + cellSize * (position.getCol - 1),
      margin + cellSize * (position.getRow - 1),
      cellSize, cellSize)
  }

  /**
   * Draw the stone and its decoration.
   */
  private def drawStone(g2: Graphics2D, position: BoardPosition, cellSize: Int, margin: Int, inAtari: Boolean,
                        stone: GoStone): Unit = {
    val pieceSize = getPieceSize(cellSize, stone)
    val pos = getPosition(position, cellSize, pieceSize, margin)
    val transp = stone.getTransparency
    var img = GoStoneRenderer.getImage(stone)
    if (transp > 0) {
      GoStoneRenderer.scaleFactors_(3) = (255 - transp).toFloat / 255
      val transparencyOp = new RescaleOp(GoStoneRenderer.scaleFactors_, GoStoneRenderer.OFFSETS, null)
      val bufImg = ImageUtil.makeBufferedImage(GoStoneRenderer.getImage(stone))
      img = transparencyOp.filter(bufImg, null)
    }
    g2.drawImage(img, pos.x, pos.y, pieceSize, pieceSize, null)

    if (GameContext.getDebugMode > 0 && inAtari) {
      g2.setColor(GoStoneRenderer.ATARI_COLOR)
      g2.fillOval(pos.x, pos.y, GoStoneRenderer.ATARI_MARKER_RADIUS, GoStoneRenderer.ATARI_MARKER_RADIUS)
    }
    if (stone.getAnnotation != null) {
      drawAnnotations(g2, cellSize, stone, pieceSize, pos)
    }
  }

  /**
   * Textual notes on the board position to aid debugging.
   */
  private def drawAnnotations(g2: Graphics2D, cellSize: Int, stone: GoStone, pieceSize: Int, pos: Point): Unit = {
    val offset = (cellSize - pieceSize) >> 1
    g2.setFont(GoStoneRenderer.ANNOTATION_FONT)
    val xPos = pos.x + 2 * offset
    val yPos = pos.y + 4 * offset
    g2.setColor(if (stone.isOwnedByPlayer1) Color.WHITE else Color.BLACK)
    g2.drawString(stone.getAnnotation, xPos, yPos)
    g2.setColor(if (stone.isOwnedByPlayer1) Color.BLACK else Color.WHITE)
    g2.drawString(stone.getAnnotation, xPos + 1, yPos + 1)
  }
}

object GoStoneRenderer {
  private var renderer_ : TwoPlayerPieceRenderer = scala.compiletime.uninitialized

  private val PLAYER1_STONE_COLOR = new Color(90, 90, 90)
  private val PLAYER2_STONE_COLOR = new Color(230, 230, 230)

  private val ATARI_COLOR = new Color(255, 210, 90, 255)
  private val ATARI_MARKER_RADIUS = 6

  private val DIR = GameContext.GAME_RESOURCE_ROOT + "twoplayer/go/ui/images/"
  val BLACK_STONE_IMG: ImageIcon = GUIUtil.getIcon(DIR + "goStoneBlack.png")
  val WHITE_STONE_IMG: ImageIcon = GUIUtil.getIcon(DIR + "goStoneWhite.png")
  private val BLACK_STONE_DEAD_IMG = GUIUtil.getIcon(DIR + "goStoneBlackDead.png")
  private val WHITE_STONE_DEAD_IMG = GUIUtil.getIcon(DIR + "goStoneWhiteDead.png")

  private val scaleFactors_ = Array(1.0f, 1.0f, 1.0f, 1.0f)
  private val OFFSETS = Array(0.0f, 0.0f, 0.0f, 0.0f)
  private val ANNOTATION_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.BOLD, 16)

  val DEFAULT_PLAYER1_COLOR: Color = PLAYER1_STONE_COLOR
  val DEFAULT_PLAYER2_COLOR: Color = PLAYER2_STONE_COLOR

  def getRenderer: TwoPlayerPieceRenderer = {
    if (renderer_ == null) renderer_ = new GoStoneRenderer()
    renderer_
  }

  /**
   * @return  the image to show for the graphical representation of the go stone
   */
  private def getImage(stone: GoStone): Image = {
    if (stone.isDead) {
      if (stone.isOwnedByPlayer1) BLACK_STONE_DEAD_IMG.getImage else WHITE_STONE_DEAD_IMG.getImage
    } else {
      if (stone.isOwnedByPlayer1) BLACK_STONE_IMG.getImage else WHITE_STONE_IMG.getImage
    }
  }
}
