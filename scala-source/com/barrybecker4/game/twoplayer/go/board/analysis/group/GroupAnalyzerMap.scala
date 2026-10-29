/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup

import java.util.{Map, WeakHashMap}

/**
 * Maintains a map from groups to GroupAnalyzers.
 * If we request an analyzer for a group that is not in the map, we add it.
 */
class GroupAnalyzerMap {

  private val analyzerMap: Map[IGoGroup, GroupAnalyzer] = new WeakHashMap[IGoGroup, GroupAnalyzer]()

  /**
   * Get an analyzer for a specific group.
   * If the analyzer is not there yet, it gets added.
   * @param group the group to analyze.
   * @return the analyzer for the specified group
   */
  def getAnalyzer(group: IGoGroup): GroupAnalyzer = {
    val cachedAnalyzer = analyzerMap.get(group)
    if (cachedAnalyzer != null) {
      return cachedAnalyzer
    }
    val analyzer = new GroupAnalyzer(group, this)
    analyzerMap.put(group, analyzer)

    analyzer
  }

  def clear(): Unit = {
    analyzerMap.clear()
  }

  override def toString: String = {
    analyzerMap.toString
  }
}
