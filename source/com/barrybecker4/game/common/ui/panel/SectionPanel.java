// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 *  A section panel within the GaelInfoPanel.
 *
 *  @author Barry Becker
 */
public class SectionPanel extends JPanel {


    private static final Font SECTION_TITLE_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.BOLD, 12 );

    /**
     * Constructor
     */
    public SectionPanel() {
        styleSectionPanel(this, "");
    }

    public SectionPanel(String title) {
        styleSectionPanel(this, title);
    }


    /**
     * Create a panel with an etched border for the section.
     * @param panel the panel to style
     * @param title  the title of the panel.
     */
    public static JPanel styleSectionPanel(JPanel panel, String title) {

        panel.setOpaque(false);
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );

        panel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), title,
                     TitledBorder.LEFT, TitledBorder.TOP, SECTION_TITLE_FONT) );
        return panel;
    }
}