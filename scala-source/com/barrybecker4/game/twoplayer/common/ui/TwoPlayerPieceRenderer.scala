/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui

import com.barrybecker4.game.common.board.{Board, BoardPosition, GamePiece}
import com.barrybecker4.game.common.ui.viewer.GamePieceRenderer
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove

import java.awt.{Color, Graphics2D, Point}
import java.text.DecimalFormat

/**
 * a singleton class that takes a game piece and renders it for the TwoPlayerBoardViewer.
 * We use a separate piece rendering class to avoid having ui in the piece class itself.
 * This allows us to more cleanly separate the client pieces from the server.
 *
 * @author Barry Becker
 */
class TwoPlayerPieceRenderer extends GamePieceRenderer:

  /**
   *  @return color the player1 pieces should be. Ignored if using icons to represent the pieces.
   */
  def getPlayer1Color(): Color = TwoPlayerPieceRenderer.DEFAULT_PLAYER1_COLOR

  /**
   *  @return color the player2 pieces should be. Ignored if using icons to represent the pieces.
   */
  def getPlayer2Color(): Color = TwoPlayerPieceRenderer.DEFAULT_PLAYER2_COLOR

  /**
   * @return the game piece render color.
   */
  override def getPieceColor(piece: GamePiece): Color =
    val playerColor = if piece.isOwnedByPlayer1 then getPlayer1Color() else getPlayer2Color()
    new Color(playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue(), 255 - piece.getTransparency)

  /**
   * @return color for annotation text (if any).
   */
  override def getTextColor(piece: GamePiece): Color =
    var textColor = TwoPlayerPieceRenderer.PLAYER2_TEXT_COLOR
    if piece.isOwnedByPlayer1 then
      textColor = TwoPlayerPieceRenderer.PLAYER1_TEXT_COLOR
    textColor

  /**
   * show the next moves in a special way.
   */
  def renderNextMove(g2: Graphics2D, move: TwoPlayerMove, cellSize: Int, margin: Int, b: Board[?]): Unit =
    if move.isPassOrResignation then return

    assert(move.getPiece != null, "piece for next move is null: " + move)

    g2.setColor(getPieceColor(move.getPiece))

    val position = b.getPosition(move.getToRow, move.getToCol)
    val pieceSize = (TwoPlayerPieceRenderer.NEXT_MOVE_SIZE_FRAC * getPieceSize(cellSize, move.getPiece)).toInt
    val pos = getPosition(position, cellSize, pieceSize, margin)
    g2.setFont(GamePieceRenderer.BASE_FONT)
    g2.fillOval(pos.x, pos.y, pieceSize, pieceSize)
    g2.setColor(if move.isUrgent then TwoPlayerPieceRenderer.URGENT_COLOR else Color.DARK_GRAY)
    g2.drawString(TwoPlayerPieceRenderer.format_.format(move.getValue), pos.x - 3, pos.y + 2)

object TwoPlayerPieceRenderer:
  /** there must be one of these for each derived class too. */
  private var renderer_ : GamePieceRenderer = null

  // We should move the color options to the PlayerOptions class so they can be customized.
  val DEFAULT_PLAYER1_COLOR = new Color(180, 60, 100)
  val DEFAULT_PLAYER2_COLOR = new Color(10, 120, 255)

  private val PLAYER1_TEXT_COLOR = new Color(255, 250, 255)
  private val PLAYER2_TEXT_COLOR = new Color(0, 50, 30)

  private val URGENT_COLOR = new Color(245, 10, 0)
  private val format_ = new DecimalFormat("###,###.#")
  private val NEXT_MOVE_SIZE_FRAC = 0.2

  def getRenderer(): GamePieceRenderer =
    if renderer_ == null then
      renderer_ = new TwoPlayerPieceRenderer()
    renderer_
