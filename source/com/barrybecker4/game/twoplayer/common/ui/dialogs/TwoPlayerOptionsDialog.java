/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
public class TwoPlayerOptionsDialog extends GameOptionsDialog
                                    implements ActionListener, ItemListener {

    private JCheckBox gameTreeCheckbox_;

    /**
     * Constructor
     */
    public TwoPlayerOptionsDialog(Component parent, TwoPlayerController controller ) {
        super( parent, controller);
    }

    /**
     * @return controller
     */
    private TwoPlayerController get2PlayerController() {
        return (TwoPlayerController) controller_;
    }

    @Override
    public TwoPlayerOptions getOptions() {

        TwoPlayerOptions options = getTwoPlayerOptions();

        options.setShowGameTree(gameTreeCheckbox_.isSelected());
        return options;
    }

    private TwoPlayerOptions getTwoPlayerOptions() {
        return (TwoPlayerOptions)controller_.getOptions();
    }

    @Override
    public JPanel createControllerParamPanel() {

        return null;
    }

    /**
     * @return debug params tab panel
     */
    @Override
    protected JPanel createDebugParamPanel() {
        JPanel outerContainer = new JPanel(new BorderLayout());
        JPanel p = createDebugOptionsPanel();

        addDebugLevel(p);
        addLoggerSection(p);
        addProfileCheckBox(p);

        // show game tree option
        TwoPlayerOptions options = get2PlayerController().getOptions();
        gameTreeCheckbox_ = new JCheckBox(GameContext.getLabel("SHOW_GAME_TREE"), options.getShowGameTree());
        gameTreeCheckbox_.setToolTipText( GameContext.getLabel("SHOW_GAME_TREE_TIP") );
        gameTreeCheckbox_.addActionListener( this );
        gameTreeCheckbox_.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( gameTreeCheckbox_ );

        outerContainer.add(p, BorderLayout.NORTH);

        return outerContainer;
    }
}