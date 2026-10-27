/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search

/** The approximate amount of progress we have made in a 2 player game. @author Barry Becker */
enum Progress:
  case BEGINNING // the very start of the game.
  case MIDDLE // middle game - where most of the action is
  case END // final end-game
