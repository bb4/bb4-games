/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.ui.panel.GameToolBar;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * @author Barry Becker
 */
class GoToolBar extends GameToolBar {

    /** go needs an extra button for passing. Do not init with null. */
    private GradientButton passButton_;

    /** go needs an extra button for resigning.  */
    private GradientButton resignButton_;


    GoToolBar(ImageIcon texture, ActionListener listener) {
        super(texture, listener);
    }


     /**
      * add the button for passing and resigning.
      */
    @Override
    protected final void addCustomToolBarButtons()
    {
        passButton_ = createToolBarButton( GameContext.getLabel("PASS_BTN"),
                                           GameContext.getLabel("PASS_BTN_TIP"),
                                           null/*passImage_*/ );
        add( passButton_ );

        resignButton_ = createToolBarButton( GameContext.getLabel("RESIGN_BTN"),
                                           GameContext.getLabel("RESIGN_BTN_TIP"),
                                           null/*resignImage_*/ );
        add( resignButton_ );
    }

    public JButton getPassButton() {
        return passButton_;
    }

    public JButton getResignButton() {
        return resignButton_;
    }

}
