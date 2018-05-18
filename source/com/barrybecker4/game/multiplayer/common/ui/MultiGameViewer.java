// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.common.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.IRectangularBoard;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.online.SurrogateMultiPlayer;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Takes a Controller as input and displays the current state of the game.
 * For example, the TrivialController contains a TrivialTable object
 * which describes this state.
 *
 * @author Barry Becker
 */
public abstract class MultiGameViewer<M extends Move, B extends Board<M>> extends GameBoardViewer<M, B> {

    private boolean winnerDialogShown_;

    // Construct the application
    protected MultiGameViewer() {}

    @Override
    protected abstract MultiGameController<M, B> createController();

    /**
     * start over with a new game using the current options.
     */
    @Override
    public void startNewGame()  {
        reset();
        winnerDialogShown_ = false;
        sendGameChangedEvent(null);  // get the info panel to refresh with 1st players name
        Player firstPlayer = controller.getPlayers().getFirstPlayer();

        if (firstPlayer.isSurrogate()) {
            doSurrogateMove((SurrogateMultiPlayer) firstPlayer);
        }
        else if (!firstPlayer.isHuman()) {
            controller.computerMovesFirst();
        }
    }

     /**
      * display a dialog at the end of the game showing who won and other relevant
      * game specific information.
      */
    @Override
    public void showWinnerDialog() {
        String message = getGameOverMessage();
        JOptionPane.showMessageDialog( this, message, GameContext.getLabel("GAME_OVER"),
                   JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * @return   the message to display at the completion of the game.
     */
    @Override
    protected abstract String getGameOverMessage();

    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    public abstract boolean doComputerMove(Player player);

    /**
     * Wait for the surrogate player to move and show it on the screen when done.
     *
     * @param player surrogate player to move
     * @return done return true if the game is over after moving
     */
    public boolean doSurrogateMove(SurrogateMultiPlayer player) {

        SurrogateMoveWorker worker = new SurrogateMoveWorker(this);
        worker.requestSurrogateMove(player);
        return false;
    }

    /**
     * Does nothing by default.
     * @param action to take
     * @param player to apply it to
     * @return message to show if on client.
     */
    protected String applyAction(PlayerAction action,  Player player) {
        GameContext.log(0, "ignoring action " + action);
        return "";
    }

    /**
     * Implements the GameChangedListener interface.
     * Called when the game has changed in some way
     */
    @Override
    public void gameChanged(GameChangedEvent<M> evt) {
        if (controller.isDone() && !winnerDialogShown_)  {
            winnerDialogShown_ = true;
            showWinnerDialog();
        }
        else if (!winnerDialogShown_) {
             super.gameChanged(evt);
        }
    }


    /**
     * Many multiplayer games don't use this.
     * @param lastMove the move to show (but now record)
     */
    public Move createMove(Move lastMove) {
        // unused for now
        return null;
    }

    /**
     * @return the tooltip for the panel given a mouse event
     */
    @Override
    public String getToolTipText( MouseEvent e ) {
        Location loc = getBoardRenderer().createLocation(e);
        StringBuilder sb = new StringBuilder( "<html><font=-3>" );

        if (controller.getBoard() != null) {
            BoardPosition space = ((IRectangularBoard) controller.getBoard()).getPosition(loc);
            if ( space != null && space.isOccupied() && GameContext.getDebugMode() >= 0 ) {
                sb.append("<br>");
                sb.append( loc );
            }
            sb.append( "</font></html>" );
        }
        return sb.toString();
    }
}
