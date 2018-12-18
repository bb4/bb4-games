/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.common.ui.PlayerTable;
import com.barrybecker4.game.multiplayer.galactic.Galaxy;
import com.barrybecker4.game.multiplayer.galactic.Planet;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;
import com.barrybecker4.ui.table.TableColumnMeta;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;


/**
 * GalacticPlayerTable contains a list of players.
 * All the cells are editable.
 * It is initialized with a list of PlayerList and returns a list of PlayerList.
 *
 * @author Barry Becker
 */
public class GalacticPlayerTable extends PlayerTable
                                 implements TableModelListener {

    private static final int ICON_INDEX = 3;
    private static final int HOME_PLANET_INDEX = 4;
    private static final int NUM_SHIPS_INDEX = 5;
    private static final int PRODUCTION_INDEX = 6;

    private static final String ICON = GameContext.getLabel("ICON");
    private static final String HOME_PLANET = GameContext.getLabel("HOME_PLANET");
    private static final String NUM_SHIPS = GameContext.getLabel("NUM_SHIPS");
    private static final String PRODUCTION = GameContext.getLabel("PRODUCTION");

    /** high enough to accommodate the icon. */
    private static final int ROW_HEIGHT = 30;


    private static final String[] galacticColumnNames =  {
         NAME,
         COLOR,
         HUMAN,
         ICON,
         HOME_PLANET,
         NUM_SHIPS,
         PRODUCTION
    };

    /**
     * Constructor
     * @param players to initialize the rows in the table with.
     */
    public GalacticPlayerTable(PlayerList players) {
        super(players, galacticColumnNames);
        table().getModel().addTableModelListener(this);

        setRowHeight(ROW_HEIGHT);
    }


    @Override
    public void updateColumnMeta(TableColumnMeta[] columnMeta) {
        columnMeta[ICON_INDEX].setPreferredWidth(48);
        columnMeta[HOME_PLANET_INDEX].setPreferredWidth(100);
        columnMeta[NUM_SHIPS_INDEX].setPreferredWidth(100);
        columnMeta[PRODUCTION_INDEX].setPreferredWidth(100);
        super.updateColumnMeta(columnMeta);
    }

    /**
     * @return the players represented by rows in the table
     */
    @Override
    public PlayerList getPlayers() {

        TableModel model = table().getModel();
        int nRows = model.getRowCount();
        PlayerList players = new PlayerList();
        for (int i=0; i<nRows; i++) {
            char planetName = (Character) model.getValueAt(i, HOME_PLANET_INDEX);
            Planet planet = Galaxy.getPlanet(planetName);
            planet.setProductionCapacity((Integer) model.getValueAt(i, PRODUCTION_INDEX));
            planet.setNumShips((Integer) (model.getValueAt(i, NUM_SHIPS_INDEX)));
            ImageIcon icon = (ImageIcon) (model.getValueAt(i, ICON_INDEX));
            players.add(GalacticPlayer.createGalacticPlayer(
                                    (String) model.getValueAt(i, NAME_INDEX),
                                    planet,
                                    (Color) model.getValueAt(i, COLOR_INDEX),
                                    ((Boolean) model.getValueAt(i, HUMAN_INDEX)), icon));
        }
        return players;
    }


    /**
     * add a row based on a player object
     * @param player to add
     */
    @Override
    public void addRow(Object player) {

        GalacticPlayer p = (GalacticPlayer) player;
        Object d[] = new Object[getNumColumns()];
        d[NAME_INDEX] = p.getName();
        d[COLOR_INDEX ] = p.getColor();
        d[ICON_INDEX] = p.getIcon();
        d[HOME_PLANET_INDEX] = p.getHomePlanet().getName();
        d[NUM_SHIPS_INDEX] = p.getHomePlanet().getNumShips();
        d[PRODUCTION_INDEX] = p.getHomePlanet().getProductionCapacity();
        d[HUMAN_INDEX] = p.isHuman();

        getPlayerModel().addRow(d);
    }

    @Override
    protected Player createPlayer() {
        int ct = table().getRowCount();
        Planet planet = new Planet((char)('A'+ct),
                GalacticPlayer.DEFAULT_NUM_SHIPS, 10, new ByteLocation(0,0));
        Color newColor = MultiGamePlayer.getNewPlayerColor(getPlayers());
        GalacticPlayer player = GalacticPlayer.createGalacticPlayer(
                                             "Admiral "+(ct+1), planet, newColor, true);
        planet.setOwner(player);
        return player;
    }

    /**
     * The user has changed from human to alien or vice versa.
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
        GameContext.log(0, "table changed " + e.getFirstRow() + " col="+ e.getColumn());
        if (e.getColumn() == HUMAN_INDEX)  {
            int row = e.getFirstRow();
            TableModel m = table().getModel();
            boolean isHuman = (Boolean) m.getValueAt(row, HUMAN_INDEX);
            char c = (Character) m.getValueAt(row, HOME_PLANET_INDEX);
            Planet p = Galaxy.getPlanet(c);
            // Color color = (Color) m.getValueAt(row, COLOR_INDEX);
            // create a dummy player of the correct type and get the image icon.
            GalacticPlayer np =
                    GalacticPlayer.createGalacticPlayer("", p, Color.WHITE, isHuman);
            m.setValueAt(np.getIcon(), row, ICON_INDEX);
        }
    }
}
