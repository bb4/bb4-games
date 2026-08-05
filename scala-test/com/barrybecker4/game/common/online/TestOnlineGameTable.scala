/* Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.online

import com.barrybecker4.game.common.GameOptions
import com.barrybecker4.game.common.player.Player

/** Exposes [[OnlineGameTable]] constructor for tests. */
final class TestOnlineGameTable(name: String, initialPlayer: Player, options: GameOptions)
  extends OnlineGameTable(name, initialPlayer, options)
