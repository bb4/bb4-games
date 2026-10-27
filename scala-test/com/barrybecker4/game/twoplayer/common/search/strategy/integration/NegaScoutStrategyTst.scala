/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.integration

import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType

/** @author Barry Becker */
abstract class NegaScoutStrategyTst extends AbstractStrategyTst:
  override protected def getSearchStrategyToTest: SearchStrategyType =
    SearchStrategyType.NEGASCOUT
