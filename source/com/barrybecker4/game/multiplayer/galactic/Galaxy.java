/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Representation of a Galaxy as a Game Board
 *
 * @author Barry Becker
 */
public class Galaxy extends Board<GalacticTurn> {

    private static final int DEFAULT_NUM_PLANETS = 20;
    public static final int MAX_NUM_PLANETS = 80;
    public static final int MIN_NUM_PLANETS = 3;

    private static char[] PLANET_NAMES ;
    static {
        PLANET_NAMES = new char[60];
        int ct=0;
        for (int c='A'; c<='Z'; c++) {
            PLANET_NAMES[ct++]=(char)c;
        }
        for (int c='a'; c<='z'; c++) {
            PLANET_NAMES[ct++]=(char)c;
        }
        for (int c='1'; c<='8'; c++) {
            PLANET_NAMES[ct++]=(char)c;
        }
    }

    // @@ I don't think the following 3 props should not be static. Not sure why I made them that way.
    private static int numPlanets_ = DEFAULT_NUM_PLANETS;

    // the list of planets on the board.
    // Does not change during the game.
    private static List<Planet> planets_ = null;

    private static Map<Character,Planet> hmPlanets_ = new HashMap<Character,Planet>();


    /** constructor
     *  @param numRows num rows
     *  @param numCols num cols
     */
    public Galaxy( int numRows, int numCols ) {
        setSize( numRows, numCols );
    }

    /** Copy constructor */
    protected Galaxy(Galaxy g) {
       super(g);
    }

    @Override
    public Galaxy copy() {
        return new Galaxy(this);
    }

    public void initPlanets(PlayerList players, GalacticOptions options) {
        hmPlanets_.clear();

        numPlanets_ = options.getNumPlanets();

        if (planets_ == null)  {
            planets_ = new ArrayList<Planet>();
        }

        planets_.clear();
        for (int i = 0; i < getNumPlanets(); i++)
        {
            // find a random position
            int randRow;
            int randCol;
            BoardPosition position;
            // find an unoccupied position to place the new planet
            do {
                randRow = (int)(Math.random() * getNumRows())+1;
                randCol = (int)(Math.random() * getNumCols())+1;
                position = this.getPosition(randRow, randCol);
            } while (position.isOccupied());

            // initial ships and production factor
            int production = (int)( 1 + Math.max(0, GameContext.random().nextGaussian()) * options.getPlanetProductionRate());
            int initialFleet = (int)( 1 + Math.max(0, GameContext.random().nextGaussian()) * options.getInitialFleetSize());
            Planet planet = new Planet(PLANET_NAMES[i], initialFleet,
                                       production, position.getLocation());
            position.setPiece(planet);

            // substitute in the players home planets that have already been created.
            for (Player p : players) {
                GalacticPlayer newVar = (GalacticPlayer) p;
                if (planet.getName() == newVar.getHomePlanet().getName()) {
                    Planet home = newVar.getHomePlanet();
                    position.setPiece(home);    // replace
                    home.setLocation(position.getLocation());
                }
            }
            // add the planet to our list
            planets_.add((Planet)position.getPiece());

            hmPlanets_.put(planet.getName(), (Planet)position.getPiece());
        }
    }

    /**
     * This method returns a copy of the planet list
     * @return an array of all the planets in the galaxy.
     */
    public static List<Planet> getPlanets()
    {
        return new ArrayList<Planet>(planets_);
    }

    /**
     * @param player  (if null return all planets in the galaxy)
     * @return the planets owned by the specified player.
     */
    public static List<Planet> getPlanets(GalacticPlayer player)
    {
        if (player==null)
            return getPlanets();
        List<Planet> playerPlanets = new ArrayList<>();
        for (Planet planet : planets_) {

            if (planet.getOwner() == player)
                playerPlanets.add(planet);
        }
        return playerPlanets;
    }

    /**

     * @return  the number of planets in this galaxy.
     */
    public static int getNumPlanets()
    {
        return numPlanets_;
    }

    /**
     * @param numPlanets to have in this galaxy
     */
    public void setNumPlanets(int numPlanets)
    {
        if (numPlanets > MAX_NUM_PLANETS) {
            GameContext.log(0, "You are not allowed to have more than "+ MAX_NUM_PLANETS );
            numPlanets_ = MAX_NUM_PLANETS;
        }
        numPlanets_ = numPlanets;
    }

    /**
     *
     * @param name  name of the planet to find
     * @return the planet that has the specified name
     */
    public static Planet getPlanet(char name) {
        Planet p = hmPlanets_.get(name);
        assert (p != null);
        return p;
    }

    @Override
    public int getMaxNumMoves() {
        return positions_.getNumBoardSpaces();
    }

    /**
     * given a move specification, execute it on the board
     * This applies the results for all the battles for one year (turn).
     *
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    @Override
    protected boolean makeInternalMove( GalacticTurn move ) {
        // first allow all the planets to build for the year
        build();
        // go through all the battle results in order and adjust the planets to account for one elapsed year.

        //GalacticTurn gmove = (GalacticTurn)move;
        //destPlanet.setOwner( battle.getOwnerAfterAttack());
        //destPlanet.setNumShips( battle.getNumShipsAfterAttack() );
        return true;
    }

    private static void build() {
        for (Planet aPlanets_ : planets_)
            aPlanets_.incrementYear();
    }

    /**
     * For galactic empire, undoing a move means turning time back a year and
     * restoring the state of the game one full turn earlier
     */
    @Override
    protected void undoInternalMove( GalacticTurn move ) {
        GameContext.log(0,  "undo no implemented yet." );
    }

    /**
     * @return true if all the planets are owned by a single player
     */
    public static boolean allPlanetsOwnedByOnePlayer() {

        Iterator it = planets_.iterator();
        Player player = planets_.get(0).getOwner();
        while (it.hasNext()) {
            Planet p = (Planet)it.next();
            if (p.getOwner() != player)
                return false;
        }
        return true;
    }

}
