/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel;
import com.barrybecker4.game.common.ui.panel.InfoLabel;
import com.barrybecker4.game.common.ui.panel.RowEntryPanel;
import com.barrybecker4.game.common.ui.panel.SectionPanel;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerGeneralInfoPanel;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerInfoPanel;
import com.barrybecker4.game.twoplayer.go.GoController;
import com.barrybecker4.game.twoplayer.go.board.BoardValidator;
import com.barrybecker4.game.twoplayer.go.board.GoSearchable;
import com.barrybecker4.game.twoplayer.go.ui.rendering.GoBoardRenderer;
import com.barrybecker4.ui.legend.ContinuousColorLegend;

import javax.swing.*;

/**
 *  Show information and statistics specific to the current game of go.
 *
 *  @author Barry Becker
 */
final class GoInfoPanel extends TwoPlayerInfoPanel implements GameChangedListener {

    // do not initialize these to null.
    // if you do, things will not work. With the java 1.3.1_02 compiler, they will get initialized to values when
    // you call createCustomInfoPanel from the super class constructor, but then they will then get initialized
    // to null when it is done calling the super class constructor and then calls the constructor for this class.
    private JLabel p1CapturesLabel_;
    private JLabel p2CapturesLabel_;
    private JLabel p1TerritoryLabel_;
    private JLabel p2TerritoryLabel_;

    private JPanel legendPanel_;

    /**
     * Constructor
     */
    GoInfoPanel( GameController controller ) {
        super( controller );
    }

    @Override
    protected String getTitleText() {
        return GameContext.getLabel("GO_INFO");
    }

    @Override
    protected void createSubPanels() {
        super.createSubPanels();

        legendPanel_ = createLegendPanel();
        this.add( legendPanel_ );
        legendPanel_.setVisible(GameContext.getDebugMode() > 0);
    }

    @Override
    protected GeneralInfoPanel createGeneralInfoPanel(Player player) {
        return new TwoPlayerGeneralInfoPanel(player);
    }

    /**
     * This panel shows information that is specific to go - specifically
     * captures and territory estimates
     */
    @Override
    protected JPanel createCustomInfoPanel() {

        JPanel customPanel = createPanel();
        customPanel.setLayout( new BoxLayout( customPanel, BoxLayout.Y_AXIS ) );

        p1CapturesLabel_ = new InfoLabel();
        p2CapturesLabel_ = new InfoLabel();

        p1TerritoryLabel_ = new InfoLabel();
        p2TerritoryLabel_ = new InfoLabel();

        JPanel capturesPanel = new SectionPanel(GameContext.getLabel("NUMBER_OF_CAPTURES"));
        PlayerList players = getController().getPlayers();
        JLabel p1 = new InfoLabel( players.getPlayer1().getName());
        JLabel p2 = new InfoLabel( players.getPlayer2().getName());
        capturesPanel.add(new RowEntryPanel( p1, p1CapturesLabel_ ));
        capturesPanel.add(new RowEntryPanel( p2, p2CapturesLabel_ ));

        JPanel territoryPanel = new SectionPanel(GameContext.getLabel("EST_TERRITORY"));
        JLabel blackTerr = new InfoLabel( GameContext.getLabel("EST_BLACK_TERR"));
        JLabel whiteTerr = new InfoLabel( GameContext.getLabel("EST_WHITE_TERR"));
        territoryPanel.add(new RowEntryPanel( blackTerr, p1TerritoryLabel_ ));
        territoryPanel.add(new RowEntryPanel( whiteTerr, p2TerritoryLabel_ ));

        customPanel.add( capturesPanel );
        customPanel.add( territoryPanel );

        return customPanel;
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new SectionPanel("Group Health Legend");
        ContinuousColorLegend legend =
                new ContinuousColorLegend(null, GoBoardRenderer.COLORMAP, false);
        legendPanel.add(legend);
        return legendPanel;
    }

    /**
     * update the info with controller stats when the game changes.
     */
    @Override
    public void gameChanged( GameChangedEvent gce ) {

        super.gameChanged( gce );
        GoController goController = (GoController) controller_;

        if ( p1CapturesLabel_ == null )
            return;

        GoSearchable searchable = (GoSearchable) goController.getSearchable().copy();
        p1CapturesLabel_.setText( searchable.getNumCaptures( false ) + " " );
        p2CapturesLabel_.setText( searchable.getNumCaptures( true ) + " " );

        new BoardValidator(searchable.getBoard()).confirmStonesInValidGroups();
        p1TerritoryLabel_.setText( searchable.getTerritoryEstimate( true ) + " " );
        p2TerritoryLabel_.setText( searchable.getTerritoryEstimate( false ) + " " );

        legendPanel_.setVisible(GameContext.getDebugMode() > 0);
    }

}
