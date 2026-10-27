/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.galactic.{BattleSimulation, Planet}
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer
import com.barrybecker4.game.multiplayer.galactic.ui.GalaxyViewer
import com.barrybecker4.sound.Instruments
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.dialogs.OptionsDialog

import java.awt.{BorderLayout, Color, Component, Dimension, Graphics, Graphics2D}
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._
import javax.swing.border.EtchedBorder
import scala.compiletime.uninitialized

/**
 * Play a battle sequence that is stored in a GalacticTurn.
 *
 * @author Barry Becker
 */
final class BattleDialog(parent: Component, battle: BattleSimulation, viewer: GalaxyViewer)
  extends OptionsDialog(parent) with ActionListener:

  private val descriptionLabel = JEditorPane()
  private val canvas = BattleCanvas()
  private val startButton = GradientButton()
  private val closeButton = GradientButton()
  private val infoLabel = JLabel()

  setResizable(false)
  setModal(true)
  showContent()

  override def getTitle: String = "Battle Sequence"

  override def createDialogContent: JComponent =
    val mainPanel = JPanel(true)
    mainPanel.setLayout(BorderLayout())

    val viewerPanel = JPanel(true)
    viewerPanel.setLayout(BorderLayout())
    viewerPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3))
    infoLabel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)))
    infoLabel.setVerticalAlignment(SwingConstants.TOP)
    infoLabel.setPreferredSize(Dimension(BattleDialog.WIDTH, 200))
    infoLabel.setBackground(Color(180, 100, 255))

    viewerPanel.add(infoLabel, BorderLayout.SOUTH)

    val buttonsPanel = createButtonsPanel

    val defendingPlanet = battle.getPlanet
    val text = "There is a battle at " + defendingPlanet.getName + ".\n"

    descriptionLabel.setEditable(false)
    descriptionLabel.setContentType("text/html")
    descriptionLabel.setText(text)

    canvas.setPreferredSize(Dimension(BattleDialog.WIDTH, 120))
    val canvasPanel = JPanel(true)
    canvasPanel.setDoubleBuffered(true)
    canvasPanel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createEmptyBorder(5, 5, 5, 5),
      BorderFactory.createLineBorder(Color.BLACK, 1)))
    canvasPanel.add(canvas)

    mainPanel.add(descriptionLabel, BorderLayout.NORTH)
    mainPanel.add(canvasPanel, BorderLayout.CENTER)
    mainPanel.add(buttonsPanel, BorderLayout.SOUTH)

    viewer.showPlanetUnderAttack(battle.getPlanet, true)

    val numAttackShips = battle.getOrder.getFleetSize
    val numDefendShips = battle.getPlanet.getNumShips
    refresh(numAttackShips, numDefendShips)

    mainPanel

  override def createButtonsPanel: JPanel =
    val buttonsPanel = JPanel(BorderLayout(), true)
    buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5))

    initBottomButton(startButton, "Fight!", "Begin the battle sequence")
    initBottomButton(closeButton, GameContext.getLabel("CLOSE"), "Close dialog")

    buttonsPanel.add(startButton, BorderLayout.CENTER)
    buttonsPanel.add(closeButton, BorderLayout.EAST)
    closeButton.setEnabled(false)

    buttonsPanel

  override def actionPerformed(event: ActionEvent): Unit =
    val source = event.getSource
    if source eq closeButton then
      setVisible(false)
    else if source eq startButton then
      startButton.setEnabled(false)
      closeButton.setEnabled(true)
      invalidate()
      paint(getGraphics)
      val battleThread = Thread(canvas)
      SwingUtilities.invokeLater(battleThread)

  def refresh(attackers: Int, defenders: Int): Unit =
    canvas.setFleetSizes(attackers, defenders)

  private class BattleCanvas extends JPanel with Runnable:
    private var attackers = 0
    private var defenders = 0

    setDoubleBuffered(true)

    def setFleetSizes(attackers: Int, defenders: Int): Unit =
      this.attackers = attackers
      this.defenders = defenders
      paint(getGraphics)

    override def run(): Unit =
      val destPlanet = battle.getPlanet
      var numAttackShips = battle.getOrder.getFleetSize
      var numDefendShips = destPlanet.getNumShips

      val sequence = battle.getHitSequence
      if sequence.isEmpty then
        GameContext.getMusicMaker.playNote(Instruments.APPLAUSE, 45, 0, 200, 1000)
        GameContext.getMusicMaker.playNote(70, 50, 900)
        GameContext.getMusicMaker.playNote(90, 40, 1000)
        descriptionLabel.setText("Planet " + destPlanet.getName + " has been reinforced.")
      else
        val useSound = GameContext.getUseSound
        val it = sequence.iterator()
        if useSound then
          GameContext.getMusicMaker.playNote(Instruments.GUNSHOT, 45, 0, 200, 1000)

        while it.hasNext do
          val p = it.next().asInstanceOf[GalacticPlayer]
          val total = numAttackShips + numDefendShips
          val time = 1 + BattleDialog.BATTLE_SPEED / (1 + total)
          if p eq battle.getOrder.getOwner then
            if useSound then GameContext.getMusicMaker.playNote(100, time, 800)
            numAttackShips -= 1
          else
            if useSound then GameContext.getMusicMaker.playNote(80, time, 800)
            numDefendShips -= 1

          BattleDialog.this.refresh(numAttackShips, numDefendShips)
          ThreadUtil.sleep(time)

        assert(numAttackShips == 0 || numDefendShips == 0,
          s"numAttackShips=$numAttackShips numDefendShips=$numDefendShips")
        val winMessage =
          if numAttackShips == 0 then
            "Planet " + destPlanet.getName + " has successfully defended itself."
          else
            battle.getOrder.getOwner.getName + " has conquered planet " + destPlanet.getName

        descriptionLabel.setText("<html>" + descriptionLabel.getText + "<b>" + winMessage + "/b></html>")

      viewer.showPlanetUnderAttack(battle.getPlanet, false)

    override def paint(g: Graphics): Unit =
      if g == null then return

      val g2 = canvas.getGraphics.asInstanceOf[Graphics2D]

      g2.setColor(Color.WHITE)
      g2.fillRect(0, 0, getWidth, getHeight)

      val attacker = battle.getOrder.getOwner
      var title = "Attacker : " + attacker.getName
      drawPlayerRep(g2, attacker, attacker.getColor, attackers, title,
        BattleDialog.LEFT_MARGIN, BattleDialog.LEFT_IMAGE_MARGIN, BattleDialog.ATTACKER_Y)

      val defender = battle.getPlanet.getOwner
      val defenderColor = if defender == null then Planet.NEUTRAL_COLOR else defender.getColor
      val planetName = battle.getPlanet.getName.toString
      title = "Defender :" + (if defender == null then planetName else defender.getName + " at " + planetName)
      drawPlayerRep(g2, defender, defenderColor, defenders, title,
        BattleDialog.LEFT_MARGIN, BattleDialog.LEFT_IMAGE_MARGIN, BattleDialog.DEFENDER_Y)

    private def drawPlayerRep(
        g2: Graphics2D, player: GalacticPlayer, color: Color,
        numShips: Int, title: String,
        margin: Int, imageMargin: Int, yPos: Int): Unit =
      if player != null then
        g2.drawImage(player.getIcon.getImage, margin, yPos - 10,
          BattleDialog.IMAGE_WIDTH, BattleDialog.IMAGE_HEIGHT, this)

      g2.setColor(color)
      g2.fillRect(imageMargin, yPos + 5, numShips, BattleDialog.BAR_THICKNESS)

      g2.setColor(color.darker())
      g2.drawString(title, BattleDialog.LEFT_IMAGE_MARGIN, yPos)
      g2.drawString(numShips.toString, BattleDialog.LEFT_IMAGE_MARGIN + numShips + 15, yPos + 22)
      if numShips > 0 then
        g2.drawRect(BattleDialog.LEFT_IMAGE_MARGIN, yPos + 5, numShips, BattleDialog.BAR_THICKNESS)

object BattleDialog:
  private val BATTLE_SPEED = 2000
  private val WIDTH = 300
  private val LEFT_MARGIN = 7
  private val LEFT_IMAGE_MARGIN = 60
  private val ATTACKER_Y = 20
  private val DEFENDER_Y = 70
  private val IMAGE_WIDTH = LEFT_IMAGE_MARGIN - 2 * LEFT_MARGIN
  private val IMAGE_HEIGHT = DEFENDER_Y - ATTACKER_Y - 10
  private val BAR_THICKNESS = 20
