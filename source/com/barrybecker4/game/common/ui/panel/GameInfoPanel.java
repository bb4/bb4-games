/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.online.ui.ChatPanel;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.ui.components.TexturedPanel;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.Dimension;

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
public abstract class GameInfoPanel extends TexturedPanel
                                    implements GameChangedListener {

    protected GameController controller_ = null;
    protected GeneralInfoPanel generalInfoPanel_;

    private static final int DEFAULT_MIN_WIDTH = 210;
    private static final int MAX_HEIGHT = 1000;

    /**
     * Constructor
     */
    protected GameInfoPanel( GameController controller ) {
        super(null);
        controller_ = controller;

        this.setBorder( BorderFactory.createLoweredBevelBorder() );
        this.setToolTipText( getTitleText() );
        this.setPreferredSize(new Dimension(getMinWidth(), MAX_HEIGHT));
        this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        createSubPanels();

        if (controller_.isOnlinePlayAvailable())  {
            ChatPanel chat = new ChatPanel(controller_.getServerConnection());
            chat.setPreferredSize(new Dimension( getMinWidth(), MAX_HEIGHT));
            add( chat );
        }
        else {
            // this pushes everything to the top
            JPanel filler = createPanel();
            filler.setPreferredSize(new Dimension(getMinWidth(), MAX_HEIGHT));
            add( filler );
        }
    }

    /**
     *  create all the sub panels in the desired order.
     *  Subclasses may override to get a different ordering.
     */
    protected void createSubPanels() {

        // the custom panel shows game specific info like captures etc.
        JPanel customPanel = createCustomInfoPanel();
        if ( customPanel != null )    {
            this.add( customPanel );
        }

        generalInfoPanel_ = createGeneralInfoPanel(controller_.getCurrentPlayer());
        add( generalInfoPanel_);
    }

    protected int getMinWidth() {
        return DEFAULT_MIN_WIDTH;
    }

    /**
     * restore to new game state.
     */
    public void reset() {}

    /**
     * @return title to display at the top of the game info window.
     */
    protected String getTitleText() {
        return GameContext.getLabel("GAME_INFORMATION");
    }

    /**
     * This panel shows information that is specific to the game type (like captures or territory estimates).
     */
    protected JPanel createCustomInfoPanel()  {
        return null; // none by default
    }

    /**
     * this is general information that is applicable to every 2 player game.
     */
    protected abstract GeneralInfoPanel createGeneralInfoPanel(Player player);


    protected final JPanel createPanel() {
        JPanel p = new JPanel(true);
        p.setOpaque(false);
        return p;
    }

    protected static Border createMarginBorder() {
        return BorderFactory.createEmptyBorder(3,3,3,3);
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    @Override
    public void gameChanged( GameChangedEvent gce ) {
        if ( controller_ == null ) {
            return;
        }

        generalInfoPanel_.update(controller_);
    }
}