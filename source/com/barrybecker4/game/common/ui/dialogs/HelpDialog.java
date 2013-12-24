/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.dialogs;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.dialogs.AbstractDialog;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

/**
 * A dialog to show version information and instructions on how to play the game.
 *
 * @author Barry Becker
 */
public final class HelpDialog extends AbstractDialog
                              implements ActionListener {

    private static final String VERSION = GameContext.getLabel("VERSION");
    private static final String COPYRIGHT = GameContext.getLabel("COPYRIGHT");
    private static final Font FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12 );

    private final GradientButton okButton_ = new GradientButton();
    private static final long serialVersionUID = 0L;

    // these will get replaced
    private String gameName_ = GameContext.getLabel("GAME_TUTORIAL");
    private String comments_ = GameContext.getLabel("AUTHOR");

    private String overviewText_ = null;

    /**
     * Constructor
     * @param parent the parent frame. (used for positioning).
     * @param gameName name of the game (checkers, go,... etc )
     * @param comments supplementary info
     * @param text game specific instructions to display.
     */
    public HelpDialog(Component parent, String gameName, String comments, String text) {
        super( parent );

        gameName_ = gameName;
        comments_ = comments;
        overviewText_ = text;
        showContent();
    }

    /**
     * builds the ui.
     */
    @Override
    protected JComponent createDialogContent() {

        Object[] arg = {gameName_};
        this.setTitle( MessageFormat.format(GameContext.getLabel("ABOUT"), arg));

        setResizable( false );
        JPanel overviewPanel = new JPanel();
        overviewPanel.setLayout( new BorderLayout() );

        FlowLayout flowLayout1 = new FlowLayout();
        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout( flowLayout1 );

        JPanel logoInsetsPanel = new JPanel();
        logoInsetsPanel.setLayout( flowLayout1 );
        logoInsetsPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );

        okButton_.setText( GameContext.getLabel("OK") );
        okButton_.addActionListener( this );

        JTextArea overviewTextArea = createOverviewTextArea();
        overviewTextArea.setFont(FONT);

        JPanel summaryPanel = createSummaryPanel();

        JLabel logo = new JLabel();
        logo.setIcon(GUIUtil.getIcon(GameContext.GAME_RESOURCE_ROOT + "common/ui/images/help.gif"));
        logo.setForeground(Color.GREEN);

        logoInsetsPanel.add( logo, null );
        summaryPanel.add( logoInsetsPanel, BorderLayout.WEST );

        overviewPanel.add( overviewTextArea, BorderLayout.CENTER );

        bottomButtonPanel.add( okButton_, null );
        overviewPanel.add( bottomButtonPanel, BorderLayout.SOUTH );
        overviewPanel.add( summaryPanel, BorderLayout.NORTH );

        return overviewPanel;
    }

    /**
     * @return the body overview text area.
     */
    private JTextArea createOverviewTextArea() {

        final JTextArea overviewTextArea = new JTextArea();
        overviewTextArea.setText( overviewText_ );
        overviewTextArea.setBorder(
             BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                                                BorderFactory.createEmptyBorder(5,5,5,5)));
        overviewTextArea.setMaximumSize(new Dimension(500,700));
        overviewTextArea.setPreferredSize(new Dimension(450,300));

        overviewTextArea.setWrapStyleWord(true);
        overviewTextArea.setLineWrap(true);

        overviewTextArea.setAutoscrolls(true);
        overviewTextArea.setEditable(false);

        return overviewTextArea;
    }

    private JPanel createSummaryPanel() {
        final JPanel summaryPanel = new JPanel();

        summaryPanel.setLayout( new BorderLayout() );

        final JPanel summaryInsetsPanel = new JPanel();
        final JLabel label1 = new JLabel();
        final JLabel label2 = new JLabel();
        final JLabel label3 = new JLabel();
        final JLabel label4 = new JLabel();
        label1.setText( gameName_ );
        label2.setText( VERSION );
        //label3.setText( COPYRIGHT );
        label4.setText( comments_ );

        summaryInsetsPanel.add( label1, null );
        summaryInsetsPanel.add( label2, null );
        //summaryInsetsPanel.add( label3, null );
        summaryInsetsPanel.add( label4, null );

        summaryInsetsPanel.setLayout( new GridLayout(4, 1) );
        summaryInsetsPanel.setBorder( new EmptyBorder( 10, 60, 10, 10 ) );

        summaryPanel.add( summaryInsetsPanel, BorderLayout.CENTER );

        return summaryPanel;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed(e);
        if ( e.getSource() == okButton_ ) {
            cancel();
        }
    }
}
