/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog;

import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.dialogs.AbstractDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Draw stats about the players and planet ownership in the Galaxy.
 *
 * @author Barry Becker
 */
final class StatsDialog extends AbstractDialog {

    private final JPanel mainPanel_ = new JPanel(true);

    private final GradientButton closeButton_ = new GradientButton();


    /**
     * constructor.
     * @param parent frame to display relative to
     * @param boardViewer board viewer
     */
    public StatsDialog(Component parent, GameBoardViewer boardViewer ) {
        super( parent );
        showContent();
    }

    /**
     * ui initialization of the tree control.
     */
    @Override
    public JComponent createDialogContent() {
        return new JPanel(true);
    }
}

