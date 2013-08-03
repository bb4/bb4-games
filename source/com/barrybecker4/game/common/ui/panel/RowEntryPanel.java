// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 *  A row entry panel in a game info panel.
 *
 *  @author Barry Becker
 */
public class RowEntryPanel extends JPanel {

    /**
     * Constructor for when there are two panels to combine.
     */
    public RowEntryPanel(JComponent firstComp, JComponent secondComp) {
         setOpaque(false);

        setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
        setMaximumSize( new Dimension( 300, 16 ) );
        if ( firstComp != null ) {
            add( firstComp );
        }
        if ( secondComp != null ) {
            add( secondComp );
        }
    }

    /** alternate constructor */
    public RowEntryPanel( JComponent firstComp ) {
        this( firstComp, null );
    }
}