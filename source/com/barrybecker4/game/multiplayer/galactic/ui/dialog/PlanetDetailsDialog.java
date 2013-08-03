/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog;

import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.dialogs.AbstractDialog;

import javax.swing.*;
import java.awt.*;


/**
 * Show summary information about all planets (or maybe just the ones that you own).
 *
 * @author Barry Becker
 */
final class PlanetDetailsDialog extends AbstractDialog {

    private final JPanel mainPanel_ = new JPanel();

    private final GradientButton closeButton_ = new GradientButton();
    private final JLabel infoLabel_ = new JLabel();


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param boardViewer baord viewer
     */
    public PlanetDetailsDialog(Component parent, GameBoardViewer boardViewer ) {
        super( parent );
        GameController controller_=boardViewer.getController();
        showContent();
    }


    /**
     * ui initialization of the tree control.
     */
    @Override
    protected JComponent createDialogContent() {
        return new JPanel();
    }

}

