/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.cache

import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey

/**
 * Holds the score and the board state for [[ScoreCache]].
 * Optional `info` is game-specific debug detail (e.g. Go WorthInfo) kept as AnyRef
 * so common does not depend on game packages.
 */
class ScoreEntry(
  private val key: HashKey,
  private val score: Int,
  private val boardDesc: String,
  private val info: AnyRef = null
):

  def this(score: Int, boardDesc: String) =
    this(null, score, boardDesc, null)

  def getScore: Int = score

  override def toString: String =
    s"Cached scoreEntry (for key=$key)\n = $score for\n$boardDesc\n info=$info"
