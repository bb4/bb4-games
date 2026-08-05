// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.path

import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition}
import scala.jdk.CollectionConverters.*

class PathCache:
  
  private var cachedPaths: PathList = _

  def update(pos: BlockadeBoardPosition, board: BlockadeBoard): Unit = synchronized {
    val paths = board.findShortestPaths(pos)
    cachedPaths = paths
  }

  def getShortestPaths: PathList = synchronized {
    cachedPaths
  }

  private def isPathCacheBroken(board: BlockadeBoard): Boolean = synchronized {
    if cachedPaths == null then
      return true
    
    cachedPaths.asScala.exists(path => path.isBlocked(board))
  }
