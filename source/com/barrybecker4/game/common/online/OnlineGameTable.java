/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online;

import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Some number of players sitting around a virtual game table online.
 * Each table has a name, set of Players and other game specific properties.
 *
 * @author Barry Becker
 */
public class OnlineGameTable implements Serializable {

    private static final long serialVersionUID = 1;

    /** the unique name of the virtual online table.  */
    private String name_;

    /** the player who created this table, even if they are not sitting here anymore.  */
    private Player owner_;

    /** list of players currently sitting at the table.  */
    private PlayerList players_;

    private GameOptions gameOptions_;

    /** most recent human player to join the table.   */
    private Player newestHumanPlayer_;


    protected OnlineGameTable(String name, Player initialPlayer, GameOptions options) {
        this(name, initialPlayer, new Player[] {initialPlayer}, options);
    }

    private OnlineGameTable(String name, Player owner, Player[] initialPlayers, GameOptions options) {
        name_ = name;
        owner_ = owner;
        newestHumanPlayer_ = owner;
        players_ = new PlayerList();
        gameOptions_ = options;
        players_.addAll(Arrays.asList(initialPlayers));
    }

    /**
     * @return true if all the required players are seated.
     */
    public boolean isReadyToPlay() {
        return players_.size() == getGameOptions().getMaxNumPlayers();
    }

    /**
     * @return the name of the table.
     */
    public String getName() {
        return name_;
    }

    public void setName(String name) {
        name_ = name;
    }

    public PlayerList getPlayers() {
        return players_;
    }

    public Player getOwner() {
        return owner_;
    }

    public Player getNewestHumanPlayer() {
        return newestHumanPlayer_;
    }

    public int getNumPlayersNeeded() {
        return gameOptions_.getMaxNumPlayers();
    }

    public GameOptions getGameOptions() {
        return gameOptions_;
    }

    /**
     * @return the list of players at the table in a command delimited list.
     */
    public String getPlayerNames() {
        StringBuilder buf = new StringBuilder("");
        if (players_.isEmpty()) {
            return "-";
        }
        for (Player p : players_) {
            buf.append(p.getName()).append(", ");
        }
        return buf.substring(0, buf.length() - 2);
    }

    public void changeName(String oldName, String newName) {
        for (Player p : players_) {
            if (p.getName().equals(oldName)) {
                p.setName(newName);
            }
        }
    }

    public void addPlayer(Player player) {
        players_.add(player);
        if (player.isHuman())
            newestHumanPlayer_ = player;
    }

    public void removePlayer(Player player) {
        players_.remove(player);
        if (player.equals(newestHumanPlayer_))
            newestHumanPlayer_ = null;
    }

    public boolean hasPlayer(String playerName) {
        for (Player p : players_) {
            if (p.getName().equals(playerName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if no players or only robots
     */
    public boolean hasNoHumanPlayers() {
        for (Player p : players_) {
            if (p.isHuman()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the names of the players in a comma delimited list.
     */
    public String getPlayersString() {
        return players_.toString();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(20);
        buf.append("Name: ").append(name_).append(" - ");
        buf.append("Players:").append(getPlayersString());
        return buf.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OnlineGameTable)) return false;

        OnlineGameTable that = (OnlineGameTable) o;

        if (name_ != null ? !name_.equals(that.name_) : that.name_ != null) return false;
        if (newestHumanPlayer_ != null ?
                !newestHumanPlayer_.equals(that.newestHumanPlayer_) : that.newestHumanPlayer_ != null)
            return false;
        if (owner_ != null ? !owner_.equals(that.owner_) : that.owner_ != null) return false;
        if (players_ != null ? !players_.equals(that.players_) : that.players_ != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name_ != null ? name_.hashCode() : 0;
        result = 31 * result + (owner_ != null ? owner_.hashCode() : 0);
        result = 31 * result + (players_ != null ? players_.hashCode() : 0);
        result = 31 * result + (newestHumanPlayer_ != null ? newestHumanPlayer_.hashCode() : 0);
        return result;
    }
}
