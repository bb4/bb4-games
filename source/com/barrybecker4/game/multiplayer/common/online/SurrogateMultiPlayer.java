/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.online;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineChangeListener;
import com.barrybecker4.game.common.online.server.IServerConnection;
import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;

/**
 * On the server, all players are surrogates except for the robot players.
 * On the client, all players are surrogates except for the human player that is controlling that client.
 *
 * @author Barry Becker
 */
public class SurrogateMultiPlayer extends MultiGamePlayer implements OnlineChangeListener {

    /** the player we are a surrogate for */
    private final MultiGamePlayer player;

    /** wait about 4 seconds for the player to move before timing out. */
    private static final int TIMEOUT_DURATION = 6000;


    /**
     * @param player the player we are a surrogate for.
     * @param connection to the server so we can get updated actions.
     */
    public SurrogateMultiPlayer(MultiGamePlayer player, IServerConnection connection) {
        super(player.getName(), player.getColor(), player.isHuman());
        this.player = player;
        connection.addOnlineChangeListener(this);
    }

    /**
     * Update ourselves based on what was broadcast to or from the server.
     */
    @Override
    public synchronized boolean handleServerUpdate(GameCommand cmd) {

        if (cmd.getName() == GameCommand.Name.DO_ACTION) {
            PlayerAction action = (PlayerAction) cmd.getArgument();
            if (action.getPlayerName().equals(player.getName())) {
                GameContext.log(0, "Setting surrogate(" + player.getName()
                        + ") action="+action + " on "+this+",  Thread=" + Thread.currentThread().getName());
                synchronized (player) {
                    player.setAction(action);
                    player.notifyAll();  // unblock the wait below
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setAction(PlayerAction action) {
        throw new IllegalStateException("must not set action directly on a surrogate");
    }

    /**
     * Blocks until the action has been received.
     * Wait gives other threads time to execute until we receive a notify and can continue.
     * @param controller game controller
     * @return an action for this player. Block until the real player, for which we are a surrogate,
     *    has played and we have an action to return.
     */
    @Override
    public PlayerAction getAction(MultiGameController controller) {

        try {
            long t1 = System.currentTimeMillis();
            System.out.println(player.getName() + " now waiting for surrogate action on "
                    + this + ",  Thread=" + Thread.currentThread().getName());

            PlayerAction action = null;
            synchronized (player) {

                while (action == null) {
                    player.wait(TIMEOUT_DURATION);
                    if ((System.currentTimeMillis() - t1) > (TIMEOUT_DURATION - 10)) {
                        System.out.println("****** TIMEOUT! Waiting for "+ player.getName() + " to play.");
                    }
                    action = player.getAction(controller);
                    player.setAction(null);
                }
            }

            float time = (float)(System.currentTimeMillis() - t1)/1000.0f;
            System.out.println("got action =" + action + " for "
                    + player.getName() + " after " + time + "seconds on "
                    + this + ",  Thread=" + Thread.currentThread().getName());
            return action;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The player that we are representing (that is actually located somewhere else)
     * @return the specific game player backed by another player of the same type somewhere else.
     */
    @Override
    public MultiGamePlayer getActualPlayer() {
        return player;
    }

    @Override
    public boolean isSurrogate() {
        return true;
    }
}
