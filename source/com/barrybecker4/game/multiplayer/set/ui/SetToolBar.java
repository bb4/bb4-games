/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui;

import com.barrybecker4.game.common.ui.panel.GameToolBar;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Set game toolbar
 *
 * @author Barry Becker
 */
public class SetToolBar extends GameToolBar {

    private static final long serialVersionUID = 0L;

    private GradientButton addButton_;
    private GradientButton removeButton_;
    private GradientButton solveButton_;

    private static final ImageIcon addImage = GUIUtil.getIcon(DIR+"plus.gif");
    private static final ImageIcon removeImage = GUIUtil.getIcon(DIR + "minus.gif");
    private static final ImageIcon solveImage = GUIUtil.getIcon(DIR+"greenFlag.gif");



    public SetToolBar(ImageIcon texture, ActionListener listener) {
        super(texture, listener);
    }

    @Override
    protected void addCustomToolBarButtons() {

        addButton_ = createToolBarButton( "Add Card", "Add another card to those shown", addImage );
        removeButton_ = createToolBarButton( "Remove Card", "Remove a card from those shown", removeImage );
        solveButton_ = createToolBarButton( "Show Sets",
                                            "Have the computer determine all the sets present and show them in a separate window.", solveImage );

        add( addButton_ );
        add( removeButton_ );
        add( solveButton_ );
    }

    @Override
    protected boolean hasUndoRedo() {
        return false;
    }


    public JButton getAddButton() { return addButton_; }
    public JButton getRemoveButton() { return removeButton_; }
    public JButton getSolveButton() { return solveButton_; }

}
