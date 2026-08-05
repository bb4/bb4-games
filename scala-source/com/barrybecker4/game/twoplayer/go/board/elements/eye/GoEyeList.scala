/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.eye

import java.util.LinkedList
import scala.jdk.CollectionConverters._

/**
 * A list of GoEyes.
 *
 * @author Barry Becker
 */
class GoEyeList extends LinkedList[IGoEye] {

  /**
   * Copy constructor.
   * @param eyeList list to initialize with
   */
  def this(eyeList: GoEyeList) = {
    this()
    this.addAll(eyeList)
  }

  /**
   * pretty print a list of all the current groups (and the strings they contain)
   * @return string form of list of stones.
   */
  def toString(title: String): String = {
    val buf = new StringBuilder(title)
    buf.append("\n  ")
    this.asScala.foreach { eye =>
      buf.append(eye.toString).append(", ")
    }
    buf.substring(0, buf.length - 2)
  }
}
