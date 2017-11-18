// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.ui.components.TexturedPanel;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Shows game status.
 *
 * @author Barry Becker
 */
public class StatusBar extends TexturedPanel {

    /** font for the undo/redo buttons    */
    private static final Font STATUS_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY(), Font.PLAIN, 10 );

    /**
     * Construct the panel.
     */
    public StatusBar(ImageIcon texture) {
        super(texture);

        JLabel statusBarLabel = new JLabel();
        statusBarLabel.setFont(STATUS_FONT);
        statusBarLabel.setOpaque(false);
        statusBarLabel.setText( GameContext.getLabel("STATUS_MSG"));

        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(1000, 16));
        add(statusBarLabel, BorderLayout.WEST);
    }

}
