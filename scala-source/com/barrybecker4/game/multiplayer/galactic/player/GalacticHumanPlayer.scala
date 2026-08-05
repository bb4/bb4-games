/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.player

import com.barrybecker4.game.multiplayer.galactic.Planet

import java.awt.Color
import javax.swing.ImageIcon

class GalacticHumanPlayer(name: String, homePlanet: Planet, color: Color)
  extends GalacticPlayer(name, homePlanet, color, true):

  iconBaseName_ = "human_icon"
  iconIndex_ = GalacticHumanPlayer.iconIndexCounter_ % GalacticHumanPlayer.NUM_ICONS
  GalacticHumanPlayer.iconIndexCounter_ += 1

  def this(name: String, homePlanet: Planet, color: Color, icon: ImageIcon) =
    this(name, homePlanet, color)
    if icon != null then icon_ = icon

object GalacticHumanPlayer:
  private val NUM_ICONS = 5
  private var iconIndexCounter_ = 0
