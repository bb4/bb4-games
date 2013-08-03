// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.server;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.barrybecker4.game.twoplayer.go.GoController;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import com.barrybecker4.game.twoplayer.go.options.GoOptions;
import go.Point;
import gtp.GtpServer;
import utils.StringUtils;

import java.util.List;

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
public class CommandHandler {

    /** Delay every command (seconds) */
    private int delay_;
    private int boardSize_;
    private GoController controller_;


    /**
     * Constructor
     * Load the resources for the go game, and initialize it.
     * Perhaps pass in log?
     * @throws Exception
     */
    public CommandHandler() throws Exception {

        GameContext.loadResources("go");
        GameContext.setDebugMode(0);
        initSize(19);
    }

    public boolean handleCommand(String cmdLine, StringBuffer response) {

        String[] cmdArray = StringUtils.tokenize(cmdLine);
        String cmdStr = cmdArray[0];
        boolean status = true;
        // log("handling command="+ cmdStr);

        GTPCommand cmd = GTPCommand.valueOf(cmdStr);

        switch (cmd) {
            case boardsize :
                status = cmdBoardsize(cmdArray, response);
                break;
            case clear_board :
                status = cmdClearBoard();
                break;
            case echo :
                echo(cmdLine, response);
                break;
            case echo_err :
                echoErr(cmdLine);
                break;
            case tesujisoft_bwboard :
                bwBoard(response);
                break;
            case tesujisoft_delay :
                status = cmdDelay(cmdArray, response);
                break;
            case tesujisoft_invalid :
                cmdInvalid();
                break;
            case final_score :
                status = cmdFinalScore(response);
                break;
            case final_status_list :
                status = cmdFinalStatusList(cmdArray[1], response);
                break;
            case fixed_handicap :
                status = cmdFixedHandicap(cmdArray, response);
                break;
            case genmove :
                status = cmdGenmove(response);
                break;
            case gogui_interrupt :
                break;
            case komi :
                status = cmdKomi(cmdArray, response);
                break;
            case name :
                response.append("GtpTesujisoft");
                break;
            case play :
                status = cmdPlay(cmdArray, response);
                break;
            case protocol_version :
                response.append('2'); break;
            case known_command :
                GTPCommand.valueOf(cmdArray[1]); break;
            case list_commands :
                listCommands(response);
                break;
            case time_settings :
                cmdTimeSettings(cmdArray, response); break;
            case time_left :
                cmdTimeLeft(cmdArray, response); break;
            case undo :
                cmdUndo(response); break;
            case version :
                response.append(GoController.VERSION); break;
            case quit : break;
            default :
                response.append("unknown command");
                status = false;
                break;
        }
        return status;
    }

    private void initSize(int size) {
        controller_ = new GoController(size, 0);
        SearchOptions options =
            ( (TwoPlayerPlayerOptions)controller_.getCurrentPlayer().getOptions() ).getSearchOptions();

        options.getBruteSearchOptions().setAlphaBeta(true);
        options.getBruteSearchOptions().setLookAhead(2);
        options.getBruteSearchOptions().setQuiescence(false);
        options.getBestMovesSearchOptions().setPercentageBestMoves(50);
        options.setSearchStrategyMethod(SearchStrategyType.MINIMAX);
        boardSize_ = size;
    }

    private void bwBoard(StringBuffer response) {
        response.append('\n');
        for (int x = 0; x < boardSize_; ++x) {
            for (int y = 0; y < boardSize_; ++y)  {
                response.append(Math.random() > 0.5 ? "B " : "W ");
            }
            response.append('\n');
        }
    }

    private boolean cmdBoardsize(String[] cmdArray, StringBuffer response) {
        GtpServer.IntegerArgument argument = GtpServer.parseIntegerArgument(cmdArray, response);
        if (argument == null)
            return false;
        if (argument.m_integer < 1 || argument.m_integer > 100) {
            response.append("Invalid size");
            return false;
        }
        initSize(argument.m_integer);
        return true;
    }

    private boolean cmdClearBoard() {
        controller_.reset();
        return true;
    }

    private boolean cmdDelay(String[] cmdArray, StringBuffer response) {
        GtpServer.IntegerArgument argument = GtpServer.parseIntegerArgument(cmdArray, response);
        if (argument == null) {
            response.delete(0, response.length());
            response.append(delay_);
            return true;
        }
        if (argument.m_integer < 0)  {
            response.append("Argument must be positive");
            return false;
        }
        delay_ = argument.m_integer;
        return true;
    }

    private static void listCommands(StringBuffer response) {
        for (int i=0; i<GTPCommand.values().length; i++) {
            response.append(GTPCommand.values()[i]).append("\n");
        }
    }

    private boolean cmdFinalScore(StringBuffer response) {
        double blackScore = controller_.getFinalScore(true);
        double whiteScore = controller_.getFinalScore(false);
        if (blackScore > whiteScore) {
            response.append("B + ").append(blackScore - whiteScore);
        } else if (blackScore < whiteScore) {
            response.append("W + ").append(whiteScore - blackScore);
        } else {
            response.append('0');
        }
        return true;
    }

    /**
     * need to implement
     */
    private static boolean cmdFinalStatusList(String cmd, StringBuffer response) {
        assert false : "final_status_list command not yet implemented";
        return true;
    }

    /**
     * Create a fixed handicap command
     * @return true if success
     */
    private boolean cmdFixedHandicap(String[] cmdArray, StringBuffer response) {
        GtpServer.IntegerArgument argument =
            GtpServer.parseIntegerArgument(cmdArray, response);
        if (argument == null)
             return false;

        int numHandicapStones = argument.m_integer;
        controller_.setHandicap(numHandicapStones);

        List moves = controller_.getMoveList();

        if  (moves == null || moves.size() == 0) {
            response.append("Invalid number of handicap stones");
            return false;
        }
        StringBuilder pointList = new StringBuilder(128);
        for (Object move : moves) {
            GoMove pos = (GoMove) move;
            Point point = new Point(pos.getToCol(), pos.getToRow());
            if (pointList.length() > 0)
                pointList.append(' ');
            pointList.append(point);
        }
        response.append(pointList);

        return true;
    }

    private boolean cmdGenmove(StringBuffer response) {
        boolean blackPlays = controller_.getCurrentPlayer().equals(controller_.getPlayers().getPlayer1());
        controller_.requestComputerMove(blackPlays, true);

        GoMove m = (GoMove) controller_.getLastMove();

        Point point = new Point(m.getToRow()-1, m.getToCol()-1);
        response.append(Point.toString(point));

        return true;
    }

    private void cmdInvalid() {

        System.err.println("This is an invalid GTP response.\n" +
                             "It does not start with a status character.\n");
        //printInvalidResponse("*");  // m_out
    }

    private boolean cmdPlay(String[] cmdArray, StringBuffer response) {
        GtpServer.ColorPointArgument argument =
            GtpServer.parseColorPointArgument(cmdArray, response, boardSize_);
        if (argument == null)
            return false;

        Point point = argument.m_point;

        if (point != null)  {
            boolean isBlack = controller_.getCurrentPlayer().equals(controller_.getPlayers().getPlayer1());
            GoMove move = new GoMove(new ByteLocation(point.getX()+1, point.getY()+1),  0, new GoStone(isBlack));
            controller_.manMoves(move);
        }
        return true;
    }

    private boolean cmdKomi(String[] cmdArray, StringBuffer response) {
        GtpServer.DoubleArgument argument =
            GtpServer.parseDoubleArgument(cmdArray, response);
        if (argument == null)
             return false;

        float komi = (float)argument.m_double;
        ((GoOptions)controller_.getOptions()).setKomi(komi);
        return true;
    }

    /**
     * Arguments: int main_time, int byo_yomi_time, int byo_yomi_stones
     * Fails:     syntax error
     * Returns:   nothing
     */
    private boolean cmdTimeSettings(String[] cmdArray, StringBuffer response) {

         //System.err.println("arg len for time_settings="+ cmdArray.magnitude);
         //System.err.println("time_settings = main="+ cmdArray[1] ); //+"  byo_yomi=" + byo_yomi_time +" stones=" + byo_yomi_stones);
         return true;
    }

    /**
     * Arguments: int main_time, int byo_yomi_time, int byo_yomi_stones
     *Fails:     syntax error
     * Returns:   nothing
     */
    private boolean cmdTimeLeft(String[] cmdArray, StringBuffer response) {

        //System.err.println("arg len for time_left ="+ cmdArray.magnitude);
        //System.err.println("time_left = main="+ cmdArray[1] ); //+"  byo_yomi=" + byo_yomi_time +" stones=" + byo_yomi_stones);
        return true;
    }

    private boolean cmdUndo(StringBuffer response) {
        Move m = controller_.undoLastMove();
        if (m == null) {
            response.append("cannot undo");
            return false;
        }
        return true;
    }

    private static void echo(String cmdLine, StringBuffer response) {
        int index = cmdLine.indexOf(" ");
        if (index < 0)
            return;
        response.append(cmdLine.substring(index + 1));
    }

    private static void echoErr(String cmdLine) {
        int index = cmdLine.indexOf(" ");
        if (index < 0)
            return;
        System.err.println(cmdLine.substring(index + 1));
    }
}

