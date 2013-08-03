// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.execution;

import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPanel;
import com.barrybecker4.ui.dialogs.AbstractDialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class GameRunnerDialog extends AbstractDialog {

    private TwoPlayerPanel gamePanel;

    /** Constructor */
    public GameRunnerDialog(TwoPlayerPanel panel) {
        this.gamePanel = panel;
        this.setMinimumSize(new Dimension(600, 500));
        showContent();
    }

    @Override
    protected JComponent createDialogContent() {

        return gamePanel;
    }
}
