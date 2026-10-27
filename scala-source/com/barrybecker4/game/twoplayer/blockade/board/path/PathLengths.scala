/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.path

import scala.jdk.CollectionConverters.*

class PathLengths(
  var shortestLength: Int = Integer.MAX_VALUE,
  var secondShortestLength: Int = Integer.MAX_VALUE,
  var furthestLength: Int = 0
):
  
  private var valid: Boolean = true

  def this() = this(Integer.MAX_VALUE, Integer.MAX_VALUE, 0)

  def updatePathLengths(paths: PathList): Unit =
    if paths.isValid then
      valid = false
      return

    for path <- paths.asScala do
      val len = path.getLength
      if len < shortestLength then
        secondShortestLength = shortestLength
        shortestLength = len
      else if len < secondShortestLength then
        secondShortestLength = len

      if len > furthestLength then
        furthestLength = len

  def isValid: Boolean = valid

  override def equals(obj: Any): Boolean = obj match
    case compPathLengths: PathLengths =>
      this.shortestLength == compPathLengths.shortestLength &&
        this.secondShortestLength == compPathLengths.secondShortestLength &&
        this.furthestLength == compPathLengths.furthestLength
    case _ => false

  override def hashCode(): Int =
    var hash = 3
    hash = 97 * hash + this.shortestLength
    hash = 97 * hash + this.secondShortestLength
    hash = 97 * hash + this.furthestLength
    hash = 97 * hash + (if this.valid then 1 else 0)
    hash

  override def toString: String =
    s"shortestLength=$shortestLength secondShortestLength=$secondShortestLength furthestLength=$furthestLength"
