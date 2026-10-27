/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.persistence

import com.barrybecker4.game.twoplayer.blockade.persistence.tokens.{Player1BlockadeMoveToken, Player2BlockadeMoveToken}
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerSGFLoader
import com.barrybecker4.game.twoplayer.common.persistence.tokens.TwoPlayerMoveToken

/**
 * Loads a blockade game from a text file.
 *
 * @author Barry Becker
 */
class BlockadeSGFLoader extends TwoPlayerSGFLoader:

  override protected def createPlayer1MoveToken(): TwoPlayerMoveToken =
    new Player1BlockadeMoveToken()

  override protected def createPlayer2MoveToken(): TwoPlayerMoveToken =
    new Player2BlockadeMoveToken()
