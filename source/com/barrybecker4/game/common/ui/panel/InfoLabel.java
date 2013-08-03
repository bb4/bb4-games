// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * An info label has a label a colon and a value.
 * @author Barry Becker
 */
public class InfoLabel extends JLabel {

    private static final String COLON = ' ' + GameContext.getLabel("COLON")+ ' ';
    private static final Font LABEL_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12 );

    public InfoLabel() {
       this(null);
    }

    public InfoLabel(String s) {
        super(s + COLON);
        setFont(LABEL_FONT);
        setOpaque(false);
    }
}
