/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.strategy

/**
 * Enum for how we want to evaluate moves.
 * Extends java.lang.Enum for Java interop.
 *
 * @author Barry Becker
 */
enum EvaluationPerspective extends java.lang.Enum[EvaluationPerspective]:

  /**
   * Always evaluate from player ones point of view.
   * This is done for minimax for example.
   */
  case ALWAYS_PLAYER1

  /**
   * At a given level, evaluate the potential moves according to the player who's turn it is.
   */
  case CURRENT_PLAYER
