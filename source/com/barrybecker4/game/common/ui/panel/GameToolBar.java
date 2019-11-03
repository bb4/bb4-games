/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.components.TexturedToolBar;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Toolbar that appears a the top of the game application window.
 * Offers standard actions for all games, but can be given game specific buttons too.
 * @author Barry Becker
 */
public class GameToolBar extends TexturedToolBar {

    private static final String CORE_IMAGE_PATH = GameContext.GAME_RESOURCE_ROOT + "common/ui/images/";
    private static final long serialVersionUID = 0L;

    private GradientButton newGameButton_;
    private GradientButton undoButton_;
    private GradientButton redoButton_;
    private GradientButton optionsButton_;
    //protected GradientButton resignButton_;
    private GradientButton helpButton_;

    protected static final String DIR = CORE_IMAGE_PATH;
    private static final ImageIcon newGameImage = GUIUtil.getIcon(DIR+"newGame.gif");
    private static final ImageIcon helpImage = GUIUtil.getIcon(DIR+"help.gif");
    private static final ImageIcon undoImage = GUIUtil.getIcon(DIR+"undo_on.gif");
    private static final ImageIcon redoImage = GUIUtil.getIcon(DIR+"redo_on.gif");
    private static final ImageIcon undoImageDisabled = GUIUtil.getIcon(DIR+"undo_off.gif");
    private static final ImageIcon redoImageDisabled = GUIUtil.getIcon(DIR + "redo_off.gif");
    private static final ImageIcon optionsImage = GUIUtil.getIcon(DIR+"iconDesktop.gif");

    public GameToolBar(ImageIcon texture, ActionListener listener) {
        super(texture, listener);
        init();
    }

    private void init() {
        newGameButton_ = createToolBarButton( GameContext.getLabel("NEW_GAME_BTN"),
                GameContext.getLabel("NEW_GAME_BTN_TIP"), newGameImage );
        undoButton_ = createToolBarButton( "", GameContext.getLabel("UNDO_BTN_TIP"), undoImage );
        undoButton_.setDisabledIcon(undoImageDisabled);
        undoButton_.setEnabled(false);    // nothing to undo initially
        redoButton_ = createToolBarButton( "", GameContext.getLabel("REDO_BTN_TIP"), redoImage );
        redoButton_.setDisabledIcon(redoImageDisabled);
        redoButton_.setEnabled(false);    // nothing to redo initially
        optionsButton_ = createToolBarButton( GameContext.getLabel("OPTIONS_BTN"),
                                              GameContext.getLabel("OPTIONS_BTN_TIP"), optionsImage );
        helpButton_ = createToolBarButton( GameContext.getLabel("HELP_BTN"),
                                           GameContext.getLabel("HELP_BTN_TIP"), helpImage );

        add( newGameButton_ );
        if (hasUndoRedo()) {
            add( undoButton_ );
            add( redoButton_ );
        }
        addCustomToolBarButtons();
        add( optionsButton_ );
        add( Box.createHorizontalGlue() );
        add( helpButton_ );
    }

    /**
      * override to add your own game dependent buttons to the toolbar.
      */
    protected void addCustomToolBarButtons() {}

    public JButton getNewGameButton() { return newGameButton_; }
    public JButton getUndoButton() { return undoButton_; }
    public JButton getRedoButton() { return redoButton_; }
    public JButton getOptionsButton() { return optionsButton_; }
    //public JButton getResignButton() { return resignButton_; }
    public JButton getHelpButton() { return helpButton_; }

    protected boolean hasUndoRedo() {
        return true;
    }
}
