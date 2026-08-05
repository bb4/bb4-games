// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.server

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.{GameContext, Move}
import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType
import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.GoOptions
import go.Point
import gtp.GtpServer
import utils.StringUtils
import scala.jdk.CollectionConverters._

/**
 * This class wraps the GoController and provides an interface to a GTP based controller (front end ui).
 *
 * The key commands are:
 *   boardSize
 *   clear_board
 *   gen_move
 *   play
 *   TODO
 *     implement req_genmove
 *     implement final_status_list
 *
 *  @author Barry Becker
 */
class CommandHandler {

  private var delay_ : Int = 0
  private var boardSize_ : Int = 0
  private var controller_ : GoController = _

  GameContext.loadResources("go")
  GameContext.setDebugMode(0)
  initSize(19)

  def handleCommand(cmdLine: String, response: StringBuffer): Boolean = {
    val cmdArray = StringUtils.tokenize(cmdLine)
    val cmdStr = cmdArray(0)
    var status = true

    val cmd = GTPCommand.valueOf(cmdStr)

    cmd match {
      case GTPCommand.boardsize =>
        status = cmdBoardsize(cmdArray, response)
      case GTPCommand.clear_board =>
        status = cmdClearBoard()
      case GTPCommand.echo =>
        echo(cmdLine, response)
      case GTPCommand.echo_err =>
        echoErr(cmdLine)
      case GTPCommand.tesujisoft_bwboard =>
        bwBoard(response)
      case GTPCommand.tesujisoft_delay =>
        status = cmdDelay(cmdArray, response)
      case GTPCommand.tesujisoft_invalid =>
        cmdInvalid()
      case GTPCommand.final_score =>
        status = cmdFinalScore(response)
      case GTPCommand.final_status_list =>
        status = cmdFinalStatusList(cmdArray(1), response)
      case GTPCommand.fixed_handicap =>
        status = cmdFixedHandicap(cmdArray, response)
      case GTPCommand.genmove =>
        status = cmdGenmove(response)
      case GTPCommand.gogui_interrupt =>
      case GTPCommand.komi =>
        status = cmdKomi(cmdArray, response)
      case GTPCommand.name =>
        response.append("GtpTesujisoft")
      case GTPCommand.play =>
        status = cmdPlay(cmdArray, response)
      case GTPCommand.protocol_version =>
        response.append('2')
      case GTPCommand.known_command =>
        GTPCommand.valueOf(cmdArray(1))
      case GTPCommand.list_commands =>
        listCommands(response)
      case GTPCommand.time_settings =>
        cmdTimeSettings(cmdArray, response)
      case GTPCommand.time_left =>
        cmdTimeLeft(cmdArray, response)
      case GTPCommand.undo =>
        cmdUndo(response)
      case GTPCommand.version =>
        response.append(GoController.VERSION)
      case GTPCommand.quit =>
      case _ =>
        response.append("unknown command")
        status = false
    }
    status
  }

  private def initSize(size: Int): Unit = {
    controller_ = new GoController(size, 0)
    val options = controller_.getCurrentPlayer.getOptions.asInstanceOf[TwoPlayerPlayerOptions[GoMove, GoBoard]].getSearchOptions

    options.getBruteSearchOptions.setAlphaBeta(true)
    options.getBruteSearchOptions.setLookAhead(2)
    options.getBruteSearchOptions.setQuiescence(false)
    options.getBestMovesSearchOptions.setPercentageBestMoves(50)
    options.setSearchStrategyMethod(SearchStrategyType.MINIMAX)
    boardSize_ = size
  }

  private def bwBoard(response: StringBuffer): Unit = {
    response.append('\n')
    for (x <- 0 until boardSize_) {
      for (y <- 0 until boardSize_) {
        response.append(if (Math.random() > 0.5) "B " else "W ")
      }
      response.append('\n')
    }
  }

  private def cmdBoardsize(cmdArray: Array[String], response: StringBuffer): Boolean = {
    val argument = GtpServer.parseIntegerArgument(cmdArray, response)
    if (argument == null) return false
    if (argument.m_integer < 1 || argument.m_integer > 100) {
      response.append("Invalid size")
      return false
    }
    initSize(argument.m_integer)
    true
  }

  private def cmdClearBoard(): Boolean = {
    controller_.reset()
    true
  }

  private def cmdDelay(cmdArray: Array[String], response: StringBuffer): Boolean = {
    val argument = GtpServer.parseIntegerArgument(cmdArray, response)
    if (argument == null) {
      response.delete(0, response.length())
      response.append(delay_)
      return true
    }
    if (argument.m_integer < 0) {
      response.append("Argument must be positive")
      return false
    }
    delay_ = argument.m_integer
    true
  }

  private def listCommands(response: StringBuffer): Unit = {
    for (i <- GTPCommand.values.indices) {
      response.append(GTPCommand.values(i)).append("\n")
    }
  }

  private def cmdFinalScore(response: StringBuffer): Boolean = {
    val blackScore = controller_.getFinalScore(true)
    val whiteScore = controller_.getFinalScore(false)
    if (blackScore > whiteScore) {
      response.append("B + ").append(blackScore - whiteScore)
    } else if (blackScore < whiteScore) {
      response.append("W + ").append(whiteScore - blackScore)
    } else {
      response.append('0')
    }
    true
  }

  private def cmdFinalStatusList(cmd: String, response: StringBuffer): Boolean = {
    assert(false, "final_status_list command not yet implemented")
    true
  }

  private def cmdFixedHandicap(cmdArray: Array[String], response: StringBuffer): Boolean = {
    val argument = GtpServer.parseIntegerArgument(cmdArray, response)
    if (argument == null) return false

    val numHandicapStones = argument.m_integer
    controller_.setHandicap(numHandicapStones)

    val moves = controller_.getMoveList

    if (moves == null || moves.size == 0) {
      response.append("Invalid number of handicap stones")
      return false
    }
    val pointList = new StringBuilder(128)
    for (move <- moves.asScala) {
      val pos = move.asInstanceOf[GoMove]
      val point = new Point(pos.getToCol, pos.getToRow)
      if (pointList.length > 0) pointList.append(' ')
      pointList.append(point)
    }
    response.append(pointList)
    true
  }

  private def cmdGenmove(response: StringBuffer): Boolean = {
    val blackPlays = controller_.getCurrentPlayer.equals(controller_.getPlayers.getPlayer1)
    controller_.requestComputerMove(blackPlays, true)

    val m = controller_.getLastMove

    val point = new Point(m.getToRow - 1, m.getToCol - 1)
    response.append(Point.toString(point))
    true
  }

  private def cmdInvalid(): Unit = {
    System.err.println("This is an invalid GTP response.\n" +
      "It does not start with a status character.\n")
  }

  private def cmdPlay(cmdArray: Array[String], response: StringBuffer): Boolean = {
    val argument = GtpServer.parseColorPointArgument(cmdArray, response, boardSize_)
    if (argument == null) return false

    val point = argument.m_point

    if (point != null) {
      val isBlack = controller_.getCurrentPlayer.equals(controller_.getPlayers.getPlayer1)
      val move = new GoMove(new ByteLocation(point.getX + 1, point.getY + 1), 0, new GoStone(isBlack))
      controller_.manMoves(move)
    }
    true
  }

  private def cmdKomi(cmdArray: Array[String], response: StringBuffer): Boolean = {
    val argument = GtpServer.parseDoubleArgument(cmdArray, response)
    if (argument == null) return false

    val komi = argument.m_double.toFloat
    controller_.getOptions.asInstanceOf[GoOptions].setKomi(komi)
    true
  }

  private def cmdTimeSettings(cmdArray: Array[String], response: StringBuffer): Boolean = true

  private def cmdTimeLeft(cmdArray: Array[String], response: StringBuffer): Boolean = true

  private def cmdUndo(response: StringBuffer): Boolean = {
    val m = controller_.undoLastMove()
    if (m == null) {
      response.append("cannot undo")
      return false
    }
    true
  }

  private def echo(cmdLine: String, response: StringBuffer): Unit = {
    val index = cmdLine.indexOf(" ")
    if (index < 0) return
    response.append(cmdLine.substring(index + 1))
  }

  private def echoErr(cmdLine: String): Unit = {
    val index = cmdLine.indexOf(" ")
    if (index < 0) return
    System.err.println(cmdLine.substring(index + 1))
  }
}
