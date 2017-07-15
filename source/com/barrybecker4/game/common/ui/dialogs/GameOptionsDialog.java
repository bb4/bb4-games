/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.dialogs;

import com.barrybecker4.common.i18n.LocaleType;
import com.barrybecker4.common.i18n.MessageContext;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.ui.components.ColorInputPanel;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.components.NumberInput;
import com.barrybecker4.ui.components.RadioButtonPanel;
import com.barrybecker4.ui.dialogs.OptionsDialog;
import com.barrybecker4.ui.util.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
public abstract class GameOptionsDialog extends OptionsDialog
                                        implements ItemListener {
    /**
     * the options get set directly on the game controller that is passed in.
     */
    protected final GameController controller_;
    private static final long serialVersionUID = 0L;

    // debug params
    private NumberInput dbgLevelField_;
    private JRadioButton consoleOutputButton_;
    private JRadioButton windowOutputButton_;
    private JRadioButton fileOutputButton_;
    private int logDestination_;
    private JCheckBox profileCheckbox_;

    private JButton boardColorButton_;
    private JButton gridColorButton_;

    private JComboBox localeComboBox_;

    private GradientButton okButton_;

    /**
     *  constructor
     */
    protected GameOptionsDialog( Component parent, GameController controller ) {
        super(parent);
        controller_ = controller;

        showContent();
    }

    @Override
    protected JComponent createDialogContent()  {
        JPanel mainPanel = new JPanel(true);
        mainPanel.setLayout( new BorderLayout() );

        // contains tabs for Algorithm, Debugging, and Look and Feel
        JTabbedPane tabbedPanel = new JTabbedPane();

        JPanel controllerParamPanel = createControllerParamPanel();
        JPanel debugParamPanel = createDebugParamPanel();
        JPanel lookAndFeelParamPanel = createLookAndFeelParamPanel();
        JPanel localePanel =  createLocalePanel();

        JPanel buttonsPanel = createButtonsPanel();

        if (controllerParamPanel!=null)  {
            tabbedPanel.add( controllerParamPanel.getName(), controllerParamPanel );
        }
        tabbedPanel.add( GameContext.getLabel("DEBUG"), debugParamPanel );
        tabbedPanel.add( GameContext.getLabel("LOOK_AND_FEEL"), lookAndFeelParamPanel );
        tabbedPanel.add( GameContext.getLabel("LOCALE"), localePanel );

        mainPanel.add( tabbedPanel, BorderLayout.CENTER );
        mainPanel.add( buttonsPanel, BorderLayout.SOUTH );

        return mainPanel;
    }

    @Override
    public String getTitle() {
        return GameContext.getLabel("GAME_OPTIONS");
    }


    /**
     * @return game options tab panel.
     */
    protected JPanel createControllerParamPanel() {
        JComponent[] comps = getControllerParamComponents();
        if (comps == null)
            return null;

        JPanel p = new JPanel(true);

        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setBorder( BorderFactory.createTitledBorder(
                       BorderFactory.createEtchedBorder(),
                         GameContext.getLabel("GAME_OPTIONS")) );

        for (JComponent c : comps) {
            p.add(c);
        }
        p.add(Box.createVerticalGlue());

        p.setName(GameContext.getLabel("GAME"));
        return p;
    }

    /**
     * @return general game options tab panel.
     */
    protected JComponent[] getControllerParamComponents() {
        return null;
    }


    /** create the OK Cancel buttons that go at the bottom */
    @Override
    protected JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout(), true );

        okButton_ =  new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("USE_OPTIONS") );
        initBottomButton(cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("RESUME") );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add(cancelButton);

        return buttonsPanel;
    }

    /**
     * @return debug params tab panel
     */
    protected JPanel createDebugParamPanel() {
        JPanel p = createDebugOptionsPanel();

        addDebugLevel(p);
        addLoggerSection(p);
        addProfileCheckBox(p);

        return p;
    }

    protected JPanel createDebugOptionsPanel() {
        JPanel p =  new JPanel(true);
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                     GameContext.getLabel("DEBUG_OPTIONS") ) );

        JLabel label = new JLabel( "     " );
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( label );

        return p;
    }

    protected void addDebugLevel(JPanel p) {
        dbgLevelField_ =
                new NumberInput( GameContext.getLabel("DEBUG_LEVEL"), GameContext.getDebugMode(),
                                 GameContext.getLabel("DEBUG_LEVEL_TIP"), 0, 3, true);
        p.add( dbgLevelField_ );
    }

    /**
     * add a section for logging options to panel p.
     * @param p  the panel to add to
     */
    protected void addLoggerSection(JPanel p) {
        // radio buttons for where to send the log info
        JLabel logLabel = new JLabel( GameContext.getLabel("SEND_LOG_OUTPUT") );
        logLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( logLabel );

        ButtonGroup buttonGroup = new ButtonGroup();
        consoleOutputButton_ = createRadioButton("CONSOLE");
        windowOutputButton_ = createRadioButton("SEPARATE_WINDOW");
        fileOutputButton_ = createRadioButton("THIS_FILE");

        p.add( new RadioButtonPanel( consoleOutputButton_, buttonGroup, true ) );
        p.add( new RadioButtonPanel( windowOutputButton_, buttonGroup, false ) );
        p.add( new RadioButtonPanel( fileOutputButton_, buttonGroup, false ) );
        logDestination_ = GameContext.getLogger().getDestination();
        switch (logDestination_) {
            case Log.LOG_TO_CONSOLE:
                consoleOutputButton_.setSelected( true );
                break;
            case Log.LOG_TO_WINDOW:
                windowOutputButton_.setSelected( true );
                break;
            case Log.LOG_TO_FILE:
                fileOutputButton_.setSelected( true );
                break;
            default : assert false : "invalid destination : " + logDestination_;
        }
    }

    protected JRadioButton createRadioButton(String messageKey) {
        JRadioButton button = new JRadioButton( GameContext.getLabel(messageKey) );
        button.addItemListener(this);
        return button;
    }

    protected void addProfileCheckBox(JPanel p)  {
        // show profile info option
        profileCheckbox_ = new JCheckBox( GameContext.getLabel("SHOW_PROFILE_STATS"), GameContext.isProfiling() );
        profileCheckbox_.setToolTipText( GameContext.getLabel("SHOW_PROFILE_STATS_TIP") );
        profileCheckbox_.addActionListener( this );
        profileCheckbox_.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( profileCheckbox_ );
    }

    /**
     * @return look & feel params tab panel
     */
    JPanel createLookAndFeelParamPanel() {

        JPanel p = new JPanel(true);
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.setBorder( BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), GameContext.getLabel("DEBUG_OPTIONS") ) );

        JLabel label = new JLabel( "     " );
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( label );

        // sound option
        JCheckBox soundCheckbox = new JCheckBox(GameContext.getLabel("USE_SOUND"), GameContext.getUseSound());
        soundCheckbox.setToolTipText(GameContext.getLabel("USE_SOUND_TIP"));
        soundCheckbox.addActionListener(this);
        soundCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(soundCheckbox);

        // use images
        JCheckBox imagesCheckbox = new JCheckBox(GameContext.getLabel("USE_IMAGES"), GameContext.getDebugMode() > 1);
        imagesCheckbox.setToolTipText(GameContext.getLabel("USE_IMAGES_TIP"));
        imagesCheckbox.setEnabled(false);  // not yet implemented
        imagesCheckbox.addActionListener(this);
        imagesCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(imagesCheckbox);

        //call super methods to add color select entries
        boardColorButton_ = new JButton("...");
        GameBoardViewer v = (GameBoardViewer) controller_.getViewer();
        boardColorButton_.setBackground(v.getBackground());
        gridColorButton_ = new JButton("...");
        gridColorButton_.setBackground(v.getGridColor());
        JPanel boardColorPanel = new ColorInputPanel(GameContext.getLabel("SELECT_BOARD_COLOR"),
                                                     GameContext.getLabel("SELECT_BOARD_COLOR_TIP"),
                                                     boardColorButton_);
        JPanel gridColorPanel = new ColorInputPanel(GameContext.getLabel("SELECT_GRID_COLOR"),
                                                    GameContext.getLabel("SELECT_GRID_COLOR_TIP"),
                                                    gridColorButton_);
        p.add( boardColorPanel );
        p.add( gridColorPanel );
        JPanel spacer = new JPanel(true);
        spacer.setPreferredSize(new Dimension(100, 1));
        p.add( spacer );

        return p;
    }

    /**
     *  This panel allows the user to set the desired locale through the ui.
     * Perhaps we should not allow this. It does not really make sense.
     * @return locale tab panel.
     */
    JPanel createLocalePanel() {
         JPanel p = new JPanel(true);
         p.setLayout( new BorderLayout() );
         p.setAlignmentX( Component.LEFT_ALIGNMENT );
         p.setBorder(
             BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 GameContext.getLabel("LOCALE_OPTIONS")
         ));


         localeComboBox_ = createLocaleCombo();
         p.add( localeComboBox_,  BorderLayout.NORTH );

         JPanel filler = new JPanel(true);
         p.add(filler, BorderLayout.CENTER);
         return p;
     }

    private JComboBox createLocaleCombo() {
        JComboBox<String> localeComboBox = new JComboBox<>();
        localeComboBox.setToolTipText( GameContext.getLabel("LOCALE_TIP") );

        // add the available locales to the dropdown
        LocaleType[] locales = LocaleType.values();
        for (final LocaleType newVar : locales) {
            String item = GameContext.getLabel(newVar.toString());
            localeComboBox.addItem(item);
        }
        localeComboBox.setSelectedItem(MessageContext.DEFAULT_LOCALE);
        localeComboBox.addActionListener( this );
        localeComboBox.setAlignmentX( Component.LEFT_ALIGNMENT );
        return localeComboBox;
    }

    /**
     * ok button pressed.
     */
    void ok() {
        GameContext.setDebugMode( dbgLevelField_.getIntValue() );
        GameContext.setProfiling( profileCheckbox_.isSelected() );
        GameContext.getLogger().setDestination( logDestination_ );

        GameBoardViewer v = ((GameBoardViewer)controller_.getViewer());
        v.setBackground( boardColorButton_.getBackground() );
        v.setGridColor( gridColorButton_.getBackground() );

        LocaleType[] locales = LocaleType.values();
        GameContext.setLocale(locales[localeComboBox_.getSelectedIndex()]);

        // game specific options
        GameOptions options = getOptions();
        String msgs = options.testValidity();
        if (msgs == null) {
            controller_.setOptions(options);
            this.setVisible( false );
        }
        else {
            JOptionPane.showMessageDialog(this, msgs, "Invalid Options", JOptionPane.ERROR_MESSAGE);
        }
    }

    public abstract GameOptions getOptions();

    /**
     * called when a button has been pressed
     */
    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed(e);
        Object source = e.getSource();
        if ( source == okButton_ ) {
            ok();
        }
        else if (source == localeComboBox_) {
            GameContext.log(0, "locale="+localeComboBox_.getSelectedItem());
        }
    }

    /**
     * Invoked when a radio button has changed its selection state.
     */
    public void itemStateChanged( ItemEvent e ) {
        if ( consoleOutputButton_ != null && consoleOutputButton_.isSelected() ) {
            logDestination_ = Log.LOG_TO_CONSOLE;
        } else if ( windowOutputButton_ != null && windowOutputButton_.isSelected() ) {
            logDestination_ = Log.LOG_TO_WINDOW;
        } else if ( fileOutputButton_ != null && fileOutputButton_.isSelected() )    {
            logDestination_ = Log.LOG_TO_FILE;
        }
    }

}