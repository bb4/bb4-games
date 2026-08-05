/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs

import com.barrybecker4.game.common.{GameContext, GameWeights}
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.optimization.parameter.types.Parameter

import javax.swing.{BorderFactory, Box, BoxLayout, JLabel, JOptionPane, JPanel, JScrollPane, JTextField}
import java.awt.{Component, Dimension, FlowLayout}

/**
 * Allow for editing the polynomial weights used in the static
 * evaluation function
 *
 * @author Barry Becker
 */
class EditWeightsPanel(
  private val weights_ : ParameterArray,
  private val gameWeights_ : GameWeights
) extends JPanel:

  private var weightFields_ : Array[JTextField] = null
  private var weightsPanel_ : JPanel = null

  initialize()

  protected def initialize(): Unit =
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))

    val instructLabel = new JLabel(GameContext.getLabel("EDIT_WTS_BELOW"))
    weightsPanel_ = createWeightsPanel()
    initWeightsPanel()
    val scrollPane_ = new JScrollPane(weightsPanel_)

    add(instructLabel)
    add(scrollPane_)

  private def createWeightsPanel(): JPanel =
    val p = new JPanel()
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS))
    p.setBorder(BorderFactory.createEtchedBorder())
    p

  private def initWeightsPanel(): Unit =
    val len = weights_.size
    weightFields_ = new Array[JTextField](len)

    val fl = new FlowLayout()
    var i = 0
    while i < len do
      val weightPanel = new JPanel(fl)
      weightPanel.setAlignmentX(Component.LEFT_ALIGNMENT)
      weightPanel.setMaximumSize(EditWeightsPanel.WEIGHT_PANEL_DIM)
      val lab = new JLabel(gameWeights_.getName(i) + " [0.0 - " + gameWeights_.getMaxWeight(i) + ']')
      lab.setToolTipText(gameWeights_.getDescription(i))
      lab.setAlignmentX(Component.LEFT_ALIGNMENT)
      lab.setPreferredSize(EditWeightsPanel.LABEL_DIM)
      weightFields_(i) = new JTextField(java.lang.Double.toString(weights_.get(i).getValue))
      weightFields_(i).setAlignmentX(Component.LEFT_ALIGNMENT)
      weightFields_(i).setPreferredSize(EditWeightsPanel.FIELD_DIM)
      weightPanel.add(lab)
      weightPanel.add(weightFields_(i))
      weightsPanel_.add(weightPanel)
      i += 1
    weightsPanel_.add(Box.createVerticalGlue()) // fill extra space at the bottom

  def ok(): Unit =
    val len = weights_.size
    var sErrors = ""
    var i = 0
    while i < len do
      val v = java.lang.Double.parseDouble(weightFields_(i).getText())
      val p = weights_.get(i)
      if v < p.minValue then
        sErrors = sErrors + v + " is too small for " + p.name + ". The min vlaue of " + p.minValue + " will be used.\n"
      else if v > p.maxValue then
        sErrors = sErrors + v + " is too big for " + p.name + ". The max value of " + p.maxValue + " will be used.\n"
      else
        weights_.get(i).setValue(v)
      i += 1
    if sErrors.length() > 1 then
      JOptionPane.showMessageDialog(this, sErrors, "Parameter is out of Range", JOptionPane.WARNING_MESSAGE)

object EditWeightsPanel:
  private val LABEL_DIM = new Dimension(200, 20)
  private val FIELD_DIM = new Dimension(100, 20)
  private val WEIGHT_PANEL_DIM = new Dimension(900, 25)
