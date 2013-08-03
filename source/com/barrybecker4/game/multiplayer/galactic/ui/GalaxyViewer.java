/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.IRectangularBoard;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.ui.MultiGameViewer;
import com.barrybecker4.game.multiplayer.galactic.BattleSimulation;
import com.barrybecker4.game.multiplayer.galactic.GalacticController;
import com.barrybecker4.game.multiplayer.galactic.GalacticTurn;
import com.barrybecker4.game.multiplayer.galactic.Galaxy;
import com.barrybecker4.game.multiplayer.galactic.Order;
import com.barrybecker4.game.multiplayer.galactic.Planet;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticRobotPlayer;
import com.barrybecker4.game.multiplayer.galactic.ui.dialog.BattleDialog;
import com.barrybecker4.game.multiplayer.galactic.ui.dialog.GalacticTallyDialog;
import com.barrybecker4.game.multiplayer.galactic.ui.renderers.GalaxyRenderer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

/**
 *  Takes a GalacticController as input and displays the
 *  current state of the Galactic Empire Game. The GalacticController contains a Galaxy object
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class GalaxyViewer extends MultiGameViewer {

    /** Construct the application   */
    public GalaxyViewer() {}

    @Override
    protected MultiGameController createController() {
        return new GalacticController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return GalaxyRenderer.getRenderer();
    }

    /**
     * display a dialog at the end of the game showing who won and other relevant
     * game specific information.
     */
    @Override
    public void showWinnerDialog() {
        GalacticTallyDialog tallyDialog = new GalacticTallyDialog(parent_, (GalacticController)controller_);
        tallyDialog.showDialog();
    }

    /**
     * @return   the message to display at the completion of the game.
     */
    @Override
    protected String getGameOverMessage() {
        return "Game Over";
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
        GalacticRobotPlayer robot = (GalacticRobotPlayer)player;
        GalacticController gc = (GalacticController) controller_;
        GameContext.log(1, "now doing computer move. about to make orders");

        robot.makeOrders((Galaxy)getBoard(), gc.getNumberOfYearsRemaining());

        /* // records the result on the board.
        Move lastMove = getController().getLastMove();
        GalacticTurn gmove = GalacticTurn.createMove((lastMove==null)? 0 : lastMove.moveNumber + 1);
        gc.makeMove(gmove);
        */
        this.refresh();

        gc.advanceToNextPlayer();
        return false;
    }

    /**
     * This will run all the battle simulations needed to calculate the result and put it in the new move.
     * Simulations may actually be a reinforcements instead of a battle.
     * @param lastMove the move to show (but now record)
     */
    @Override
    public GalacticTurn createMove(Move lastMove) {
        GalacticTurn gmove = GalacticTurn.createMove();

        // for each order of each player, apply it for one year
        // if there are battles, show them in the battle dialog and record the result in the move.
        PlayerList players = controller_.getPlayers();

        for (final Player player : players) {
            //GalacticAction ga = (GalacticAction)gp.getAction((MultiGameController)controller_);
            List orders = ((GalacticPlayer) player).getOrders();
            executeOrders(gmove, orders);
        }
        return gmove;
    }

    private void executeOrders(GalacticTurn gmove, List orders) {
        Iterator orderIt = orders.iterator();
        while (orderIt.hasNext()) {
            Order order = (Order) orderIt.next();
            executeOrder(gmove, orderIt, order);
        }
    }

    /**
     * Execute the specified order.
     * Have we reached our destination?
     * if so, show and record the battle, or add reinforcements as appropriate, and then remove the order from the list.
     * If not, adjust the distance remaining.
     * @param move move for all players in round
     * @param orderIt iterates over the orders
     * @param order current order
     */
    private void executeOrder(GalacticTurn move, Iterator orderIt, Order order) {

        order.incrementYear();
        if (order.hasArrived()) {

            Planet destPlanet = order.getDestination();
            BattleSimulation battle = new BattleSimulation(order, destPlanet);
            move.addSimulation(battle);

            if (!controller_.getPlayers().allPlayersComputer()) {
                showBattle(battle);
            }

            destPlanet.setOwner(battle.getOwnerAfterAttack());
            destPlanet.setNumShips(battle.getNumShipsAfterAttack());

            // remove this order as it has arrived.
            orderIt.remove();
        }
    }

    /**
     * Show battle dialog if not all computers playing.
     * The dialog needs the user to dismiss it when done.
     * It is not shown if all computer players.
     * @param battle  the battle to show in a separate dialog
     */
    private void showBattle(BattleSimulation battle) {
        BattleDialog bDlg = new BattleDialog(parent_, battle, this);
        //bDlg.setLocationRelativeTo(this);

        Point p = this.getParent().getLocationOnScreen();
        // offset the dlg so the Galaxy grid is visible as a reference.
        bDlg.setLocation((int) (p.getX() + getParent().getWidth()),
                         (int) (p.getY() + 0.65 * getParent().getHeight()));
        bDlg.setModal(true);
        bDlg.setVisible(true);
    }

    public void showPlanetUnderAttack(Planet planet, boolean showAttacked) {
        planet.setUnderAttack(showAttacked);
        this.refresh();
    }

    public void highlightPlanet(Planet planet, boolean hightlighted) {
        planet.setHighlighted(hightlighted);
        this.refresh();
    }

    /**
     * @return the tooltip for the panel given a mouse event
     */
    @Override
    public String getToolTipText( MouseEvent e ) {
        Location loc = getBoardRenderer().createLocation(e);
        StringBuilder sb = new StringBuilder( "<html><font=-3>" );

        BoardPosition space = ((IRectangularBoard)controller_.getBoard()).getPosition( loc );
        if ( space != null && space.isOccupied() && GameContext.getDebugMode() >= 0 ) {
            sb.append(((Planet)space.getPiece()).toHtml());
            sb.append("<br>");
            sb.append( loc );
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }
}
