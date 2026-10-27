/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers

enum CheckersPieceType(val symbol: Char):
  case REGULAR extends CheckersPieceType('O')
  case KING extends CheckersPieceType('X')
