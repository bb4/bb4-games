// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model.config

import scala.jdk.CollectionConverters._

/**
 * A list of search config options .
 *
 * @author Barry Becker
 */
class SearchOptionsConfigList extends java.util.ArrayList[SearchOptionsConfig] {

  def this(size: Int) = {
    this()
  }

  /**
   * @throws IllegalStateException if we already have a configuration with that name.
   */
  override def add(config: SearchOptionsConfig): Boolean = {
    this.asScala.foreach { element =>
      if (element.getName == config.getName) {
        throw new IllegalStateException(
          "Cannot have two configurations with the same name")
      }
    }
    super.add(config)
  }
}
