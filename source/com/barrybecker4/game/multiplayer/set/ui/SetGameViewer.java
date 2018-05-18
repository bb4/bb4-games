/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui;

import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.barrybecker4.game.multiplayer.common.ui.MultiGameViewer;
import com.barrybecker4.game.multiplayer.set.Card;
import com.barrybecker4.game.multiplayer.set.SetBoard;
import com.barrybecker4.game.multiplayer.set.SetController;
import com.barrybecker4.game.multiplayer.set.SetPlayer;
import com.barrybecker4.game.multiplayer.set.ui.render.SetGameRenderer;

import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 *  Shows the current cards in the Set Game in a canvas.
 *
 * @author Barry Becker
 */
public final class SetGameViewer extends MultiGameViewer {

    /**
     * Constructor
     */
    SetGameViewer() {
        NumberFormat formatter_=new DecimalFormat();
        formatter_.setGroupingUsed(true);
        formatter_.setMaximumFractionDigits(0);
    }


    @Override
    public void startNewGame() {
        controller.reset();
    }

    @Override
    protected ViewerMouseListener createViewerMouseListener() {
        return new SetViewerMouseListener(this);
    }

    /**
     * @return the game specific controller for this viewer.
     */
    @Override
    protected MultiGameController createController() {
        return new SetController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return SetGameRenderer.getRenderer();
    }

    /**
     * @return the cached game board if we are in the middle of processing.
     */
    @Override
    public SetBoard getBoard() {
        return (SetBoard) controller.getBoard();
    }

    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    @Override
    public boolean doComputerMove(Player player) {
        assert false : " no computer player for set yet. coming soon!";
        return false;
    }

    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    @Override
    public boolean doSurrogateMove(SurrogateMultiPlayer player) {
        assert false : " no online play for Set game yet. coming soon!";
        return false;
    }

    @Override
    public String getGameOverMessage() {
        SetPlayer winner = (SetPlayer) ((SetController) controller).determineWinners().get(0);
        return "the game is over. The winner is " + winner.getName() + " with " + winner.getNumSetsFound() + "sets";
    }

    public List<Card> getSelectedCards() {
        return ((SetBoard) controller.getBoard()).getSelectedCards();
    }

    /**
     * @return the tooltip for the panel given a mouse event
     */
    @Override
    public String getToolTipText( MouseEvent e ) {
        return null;
    }
}

