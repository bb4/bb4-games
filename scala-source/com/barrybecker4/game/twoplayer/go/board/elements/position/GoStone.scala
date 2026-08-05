/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.position

import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.go.board.PositionalScore
import com.barrybecker4.game.twoplayer.go.board.elements.IGoMember

/**
 * A GoStone describes the physical marker at a location on the board.
 * It is either a black or white stone.
 *
 * @see GoBoardPosition
 * @author Barry Becker
 */
class GoStone(player1: Boolean, health: Float)
  extends GamePiece(player1, GamePiece.REGULAR_PIECE) with IGoMember {

  /**
   * The health is a number representing the influence of player1(black).
   * A living black stone has a positive health, while a black stone in poor health
   * has a negative health. The reverse is true for white.
   * the range is (-1.0 to 1.0)
   */
  private var health_ : Float = health

  /**
   * If true then the stone is dead and implicitly removed from the board.
   * this can only get set to true at the very end of the game when both players have passed.
   */
  private var isDead_ : Boolean = false

  /** This structure is used to store a detailed breakdown of this stones score. (for debugging only). */
  private var positionalScore_ : PositionalScore = null

  /** Java-compatible ctor: new GoStone(player1) */
  def this(player1: Boolean) =
    this(player1, 0.0f)

  /** copy constructor */
  def this(stone: GoStone) =
    this(stone.isOwnedByPlayer1, stone.health_)
    this.isDead_ = stone.isDead_
    this.positionalScore_ = stone.positionalScore_

  /**
   * create a deep copy of this stone
   */
  override def copy(): GoStone = new GoStone(this)

  def setPositionalScore(s: PositionalScore): Unit = {
    positionalScore_ = s
  }

  def setHealth(health: Float): Unit = {
    health_ = health
  }

  def getHealth: Float = health_

  def getLabel: String = if (isOwnedByPlayer1) "B" else "W"

  /**
   * @return true if the stone is dead.
   */
  def isDead: Boolean = isDead_

  /**
   * set the dead state of the stone to true.
   * It will now be rendered differently.
   */
  def setDead(dead: Boolean): Unit = {
    isDead_ = dead
  }

  /**
   * @return a deep copy of this stone
   * @throws CloneNotSupportedException
   */
  @throws[CloneNotSupportedException]
  override def clone(): AnyRef = super.clone()

  override def getDescription: String = {
    val sb = new StringBuilder()
    sb.append(if (ownedByPlayer1) "Black" else "White").append("stone")
    if (positionalScore_ != null) {
      sb.append(positionalScore_.toString(true))
    }
    sb.toString
  }

  /**
   * Print more compactly than super class.
   */
  override def toString: String = {
    val sb = new StringBuilder()
    sb.append(if (ownedByPlayer1) 'B' else 'W')
    sb.toString
  }
}
