/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.gomoku.analysis

/**
 * The different gomoku run directions to check.
 * Extends java.lang.Enum for Java interop.
 *
 * @author Barry Becker
 */
enum Direction extends java.lang.Enum[Direction]:
  case VERTICAL, HORIZONTAL, DOWN_DIAGONAL, UP_DIAGONAL
