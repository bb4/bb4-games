package com.barrybecker4.game.twoplayer.blockade.persistence

import com.barrybecker4.game.twoplayer.blockade.BlockadeController
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameExporter

class BlockadeGameExporter(controller: BlockadeController) 
    extends TwoPlayerGameExporter[BlockadeMove, BlockadeBoard](controller) {
  
  override def getSgfForMove(move: BlockadeMove): String = "[]"
}
