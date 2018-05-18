/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.multiplayer.common.ui.MultiGameViewer;
import com.barrybecker4.game.multiplayer.trivial.TrivialAction;
import com.barrybecker4.game.multiplayer.trivial.TrivialController;
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer;
import com.barrybecker4.game.multiplayer.trivial.player.TrivialRobotPlayer;
import com.barrybecker4.game.multiplayer.trivial.ui.render.TrivialGameRenderer;

import javax.swing.*;

/**
 *  Takes a TrivialController as input and displays the
 *  current state of the Game. The TrivialController contains a TrivialTable object
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class TrivialGameViewer extends MultiGameViewer {

    /**
     *  Construct the viewer
     */
    public TrivialGameViewer() {}

    @Override
    protected TrivialController createController() {
        return new TrivialController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return TrivialGameRenderer.getRenderer();
    }

    /**
     * @return the message to display at the completion of the game.
     */
    @Override
    protected String getGameOverMessage() {
        StringBuilder buf = new StringBuilder("Game Over\n");

        // find the player with the most money. That's the winner.
        PlayerList players = controller.getPlayers();
        int max = -1;
        TrivialPlayer winner = null;
        for (final Player p : players) {
            TrivialPlayer tp = (TrivialPlayer) p.getActualPlayer();
            if (tp.getValue() > max) {
                max = tp.getValue();
                winner = tp;
            }
        }

        assert winner != null;
        buf.append(winner.getName()).append(" won the game with a value of ").append(winner.getValue()).append('.');
        return buf.toString();
    }

    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    @Override
    public boolean doComputerMove(Player player) {
        assert(!player.isHuman());
        TrivialRobotPlayer robot = (TrivialRobotPlayer)player;
        TrivialController pc = (TrivialController) controller;

        PlayerAction action = robot.getAction(pc);
        GameContext.log(0, "making trivial robot move! : " + action );

        String msg = applyAction(action, robot);
        pc.addRecentRobotAction(action);

        if (parent_ != null) {
            JOptionPane.showMessageDialog(parent_, msg, robot.getName(), JOptionPane.INFORMATION_MESSAGE);
        }
        //sendGameChangedEvent(null);   need?
        refresh();
        pc.advanceToNextPlayer();

        return false;
    }

    /**
     * @param action to take
     * @param player to apply it to
     * @return message to show if on client.
     */
    @Override
    protected String applyAction(PlayerAction action,  Player player) {
        assert action != null;
        TrivialPlayer p = (TrivialPlayer) player;
        TrivialAction act = (TrivialAction) action;
        String msg = null;

        switch (act.getActionName()) {
            case KEEP_HIDDEN :
                msg = p.getName() +" has opted to keep his number hidden.";
                break;
            case REVEAL:
                p.revealValue();
                msg = p.getName() +" has revealed his number.";
                break;
        }
        return msg;
    }

}
