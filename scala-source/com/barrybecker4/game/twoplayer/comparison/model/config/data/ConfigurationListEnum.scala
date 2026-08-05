// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model.config.data

import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfigList

/**
 * Different types of canned configurations
 * @author Barry Becker
 */
enum ConfigurationListEnum(val label: String, val configList: SearchOptionsConfigList)
  extends java.lang.Enum[ConfigurationListEnum] {

  case DEFAULT_CONFIGS extends ConfigurationListEnum("Default configurations", new DefaultSearchConfigurations())
  case NEGAMAX_CONFIGS extends ConfigurationListEnum("Negamax configurations", new NegaMaxConfigurations())
  case NEGAMAX_ALL_MOVE_CONFIGS extends ConfigurationListEnum("Negamax all move configurations", new NegaMaxAllMoveConfigurations())
  case NEGASCOUT_CONFIGS extends ConfigurationListEnum("Negascout configurations", new NegaScoutConfigurations())
  case UCT_CONFIGS extends ConfigurationListEnum("UCT Variations", new UCTSearchConfigurations())
  case EMPTY_CONFIGS extends ConfigurationListEnum("Empty", new EmptyConfigurations())

  def getConfigList: SearchOptionsConfigList = configList

  override def toString: String = label
}
