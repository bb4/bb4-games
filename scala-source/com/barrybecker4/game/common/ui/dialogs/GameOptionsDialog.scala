/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.dialogs

import com.barrybecker4.common.i18n.{LocaleType, MessageContext}
import com.barrybecker4.game.common.{GameContext, GameController, GameOptions, GameViewModel}
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.ui.components.{ColorInputPanel, GradientButton, NumberInput, RadioButtonPanel}
import com.barrybecker4.ui.dialogs.OptionsDialog
import com.barrybecker4.ui.util.Log

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ItemEvent, ItemListener}

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
abstract class GameOptionsDialog(parent: Component, val controller_ : GameController[?, ?])
  extends OptionsDialog(parent) with ItemListener {

  // debug params
  private var dbgLevelField_ : NumberInput = null
  private var consoleOutputButton_ : JRadioButton = null
  private var windowOutputButton_ : JRadioButton = null
  private var fileOutputButton_ : JRadioButton = null
  private var logDestination_ : Int = 0
  private var profileCheckbox_ : JCheckBox = null

  private var boardColorButton_ : JButton = null
  private var gridColorButton_ : JButton = null

  private var localeComboBox_ : JComboBox[String] = null

  private var okButton_ : GradientButton = null

  showContent()

  override def createDialogContent: JComponent = {
    val mainPanel = new JPanel(true)
    mainPanel.setLayout(new BorderLayout())

    // contains tabs for Algorithm, Debugging, and Look and Feel
    val tabbedPanel = new JTabbedPane()

    val controllerParamPanel = createControllerParamPanel()
    val debugParamPanel = createDebugParamPanel()
    val lookAndFeelParamPanel = createLookAndFeelParamPanel()
    val localePanel = createLocalePanel()

    val buttonsPanel = createButtonsPanel

    if (controllerParamPanel != null) {
      tabbedPanel.add(controllerParamPanel.getName, controllerParamPanel)
    }
    tabbedPanel.add(GameContext.getLabel("DEBUG"), debugParamPanel)
    tabbedPanel.add(GameContext.getLabel("LOOK_AND_FEEL"), lookAndFeelParamPanel)
    tabbedPanel.add(GameContext.getLabel("LOCALE"), localePanel)

    mainPanel.add(tabbedPanel, BorderLayout.CENTER)
    mainPanel.add(buttonsPanel, BorderLayout.SOUTH)

    mainPanel
  }

  override def getTitle: String =
    GameContext.getLabel("GAME_OPTIONS")

  /**
   * @return game options tab panel.
   */
  def createControllerParamPanel(): JPanel = {
    val comps = getControllerParamComponents
    if (comps == null)
      return null

    val p = new JPanel(true)

    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS))
    p.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(),
        GameContext.getLabel("GAME_OPTIONS")))

    comps.foreach(p.add(_))
    p.add(Box.createVerticalGlue())

    p.setName(GameContext.getLabel("GAME"))
    p
  }

  /**
   * @return general game options tab panel.
   */
  def getControllerParamComponents: Array[JComponent] =
    null

  /** create the OK Cancel buttons that go at the bottom */
  override def createButtonsPanel: JPanel = {
    val buttonsPanel = new JPanel(new FlowLayout(), true)

    okButton_ = new GradientButton()
    initBottomButton(okButton_, GameContext.getLabel("OK"), GameContext.getLabel("USE_OPTIONS"))
    initBottomButton(cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("RESUME"))

    buttonsPanel.add(okButton_)
    buttonsPanel.add(cancelButton)

    buttonsPanel
  }

  /**
   * @return debug params tab panel
   */
  def createDebugParamPanel(): JPanel = {
    val p = createDebugOptionsPanel()

    addDebugLevel(p)
    addLoggerSection(p)
    addProfileCheckBox(p)

    p
  }

  def createDebugOptionsPanel(): JPanel = {
    val p = new JPanel(true)
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS))
    p.setAlignmentX(Component.LEFT_ALIGNMENT)
    p.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(),
        GameContext.getLabel("DEBUG_OPTIONS")))

    val label = new JLabel("     ")
    label.setAlignmentX(Component.LEFT_ALIGNMENT)
    p.add(label)

    p
  }

  def addDebugLevel(p: JPanel): Unit = {
    dbgLevelField_ =
      new NumberInput(
        GameContext.getLabel("DEBUG_LEVEL"), GameContext.getDebugMode,
        GameContext.getLabel("DEBUG_LEVEL_TIP"), 0, 3, true)
    p.add(dbgLevelField_)
  }

  /**
   * add a section for logging options to panel p.
   * @param p  the panel to add to
   */
  def addLoggerSection(p: JPanel): Unit = {
    // radio buttons for where to send the log info
    val logLabel = new JLabel(GameContext.getLabel("SEND_LOG_OUTPUT"))
    logLabel.setAlignmentX(Component.LEFT_ALIGNMENT)
    p.add(logLabel)

    val buttonGroup = new ButtonGroup()
    consoleOutputButton_ = createRadioButton("CONSOLE")
    windowOutputButton_ = createRadioButton("SEPARATE_WINDOW")
    fileOutputButton_ = createRadioButton("THIS_FILE")

    p.add(new RadioButtonPanel(consoleOutputButton_, buttonGroup, true))
    p.add(new RadioButtonPanel(windowOutputButton_, buttonGroup, false))
    p.add(new RadioButtonPanel(fileOutputButton_, buttonGroup, false))
    logDestination_ = GameContext.getLogger.getDestination

    // Not sure why a switch c statement will not work here. Scala constants not recognized as consts.
    if (logDestination_ == Log.LOG_TO_CONSOLE) {
      consoleOutputButton_.setSelected(true)
    } else if (logDestination_ == Log.LOG_TO_WINDOW) {
      windowOutputButton_.setSelected(true)
    } else if (logDestination_ == Log.LOG_TO_FILE) {
      fileOutputButton_.setSelected(true)
    } else {
      assert(false, "invalid destination : " + logDestination_)
    }
  }

  def createRadioButton(messageKey: String): JRadioButton = {
    val button = new JRadioButton(GameContext.getLabel(messageKey))
    button.addItemListener(this)
    button
  }

  def addProfileCheckBox(p: JPanel): Unit = {
    // show profile info option
    profileCheckbox_ = new JCheckBox(GameContext.getLabel("SHOW_PROFILE_STATS"), GameContext.isProfiling)
    profileCheckbox_.setToolTipText(GameContext.getLabel("SHOW_PROFILE_STATS_TIP"))
    profileCheckbox_.addActionListener(this)
    profileCheckbox_.setAlignmentX(Component.LEFT_ALIGNMENT)
    p.add(profileCheckbox_)
  }

  /**
   * @return look & feel params tab panel
   */
  private def createLookAndFeelParamPanel(): JPanel = {
    val p = new JPanel(true)
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS))
    p.setAlignmentX(Component.LEFT_ALIGNMENT)
    p.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), GameContext.getLabel("DEBUG_OPTIONS")))

    val label = new JLabel("     ")
    label.setAlignmentX(Component.LEFT_ALIGNMENT)
    p.add(label)

    // sound option
    val soundCheckbox = new JCheckBox(GameContext.getLabel("USE_SOUND"), GameContext.getUseSound)
    soundCheckbox.setToolTipText(GameContext.getLabel("USE_SOUND_TIP"))
    soundCheckbox.addActionListener(this)
    soundCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT)
    p.add(soundCheckbox)

    // use images
    val imagesCheckbox = new JCheckBox(GameContext.getLabel("USE_IMAGES"), GameContext.getDebugMode > 1)
    imagesCheckbox.setToolTipText(GameContext.getLabel("USE_IMAGES_TIP"))
    imagesCheckbox.setEnabled(false) // not yet implemented
    imagesCheckbox.addActionListener(this)
    imagesCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT)
    p.add(imagesCheckbox)

    //call super methods to add color select entries
    boardColorButton_ = new JButton("...")
    val v = controller_.getViewer.asInstanceOf[GameBoardViewer[_, _]]
    boardColorButton_.setBackground(v.getBackground)
    gridColorButton_ = new JButton("...")
    gridColorButton_.setBackground(v.getGridColor)
    val boardColorPanel = new ColorInputPanel(
      GameContext.getLabel("SELECT_BOARD_COLOR"),
      GameContext.getLabel("SELECT_BOARD_COLOR_TIP"),
      boardColorButton_)
    val gridColorPanel = new ColorInputPanel(
      GameContext.getLabel("SELECT_GRID_COLOR"),
      GameContext.getLabel("SELECT_GRID_COLOR_TIP"),
      gridColorButton_)
    p.add(boardColorPanel)
    p.add(gridColorPanel)
    val spacer = new JPanel(true)
    spacer.setPreferredSize(new Dimension(100, 1))
    p.add(spacer)

    p
  }

  /**
   *  This panel allows the user to set the desired locale through the ui.
   * Perhaps we should not allow this. It does not really make sense.
   * @return locale tab panel.
   */
  private def createLocalePanel(): JPanel = {
    val p = new JPanel(true)
    p.setLayout(new BorderLayout())
    p.setAlignmentX(Component.LEFT_ALIGNMENT)
    p.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(),
        GameContext.getLabel("LOCALE_OPTIONS")
      ))

    localeComboBox_ = createLocaleCombo()
    p.add(localeComboBox_, BorderLayout.NORTH)

    val filler = new JPanel(true)
    p.add(filler, BorderLayout.CENTER)
    p
  }

  private def createLocaleCombo(): JComboBox[String] = {
    val localeComboBox = new JComboBox[String]()
    localeComboBox.setToolTipText(GameContext.getLabel("LOCALE_TIP"))

    // add the available locales to the dropdown
    var i = 0
    while (i < LocaleType.VALUES.length) {
      val newVar = LocaleType.VALUES(i).asInstanceOf[LocaleType]
      val item = GameContext.getLabel(newVar.toString)
      localeComboBox.addItem(item)
      i += 1
    }
    localeComboBox.setSelectedItem(MessageContext.DEFAULT_LOCALE)
    localeComboBox.addActionListener(this)
    localeComboBox.setAlignmentX(Component.LEFT_ALIGNMENT)
    localeComboBox
  }

  /**
   * ok button pressed.
   */
  def ok(): Unit = {
    GameContext.setDebugMode(dbgLevelField_.getIntValue)
    GameContext.setProfiling(profileCheckbox_.isSelected)
    GameContext.getLogger.setDestination(logDestination_)

    val v = controller_.getViewer.asInstanceOf[GameBoardViewer[_, _]]
    v.setBackground(boardColorButton_.getBackground)
    v.setGridColor(gridColorButton_.getBackground)

    val locale = LocaleType.VALUES(localeComboBox_.getSelectedIndex).asInstanceOf[LocaleType]
    GameContext.setLocale(locale)

    // game specific options
    val options = getOptions
    val msgs = options.testValidity()
    if (msgs == null) {
      controller_.setOptions(options)
      this.setVisible(false)
    } else {
      JOptionPane.showMessageDialog(this, msgs, "Invalid Options", JOptionPane.ERROR_MESSAGE)
    }
  }

  def getOptions: GameOptions

  /**
   * called when a button has been pressed
   */
  override def actionPerformed(e: ActionEvent): Unit = {
    super.actionPerformed(e)
    val source = e.getSource
    if (source eq okButton_) {
      ok()
    } else if (source eq localeComboBox_) {
      GameContext.log(0, "locale=" + localeComboBox_.getSelectedItem)
    }
  }

  /**
   * Invoked when a radio button has changed its selection state.
   */
  override def itemStateChanged(e: ItemEvent): Unit = {
    if (consoleOutputButton_ != null && consoleOutputButton_.isSelected) {
      logDestination_ = Log.LOG_TO_CONSOLE
    } else if (windowOutputButton_ != null && windowOutputButton_.isSelected) {
      logDestination_ = Log.LOG_TO_WINDOW
    } else if (fileOutputButton_ != null && fileOutputButton_.isSelected) {
      logDestination_ = Log.LOG_TO_FILE
    }
  }
}
