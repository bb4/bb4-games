// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.path

import com.barrybecker4.game.twoplayer.blockade.board.Homes
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

class PathList extends java.util.LinkedList[Path]:

  def this(paths: Array[Path]) =
    this()
    for path <- paths do
      add(path)

  def isValid: Boolean =
    size() == 0 || (size() < Homes.NUM_HOMES && (get(0).getLength > 0))

  def getTotalPathLength: Int =
    this.asScala.foldLeft(0)((total, path) => total + path.getPathLength)
