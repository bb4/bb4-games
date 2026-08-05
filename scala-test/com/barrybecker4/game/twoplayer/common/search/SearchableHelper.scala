/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search

import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions

import java.io.InputStream

/** Helper methods to aid in testing two player searchables. @author Barry Becker */
object SearchableHelper:
  /** moved all test cases here so they are not included in the jar and do not need to be searched */
  val EXTERNAL_TEST_CASE_DIR = "/com/barrybecker4/game/twoplayer/"

abstract class SearchableHelper extends ISearchableHelper:

  private val SGF_EXTENSION = ".sgf"

  override def createSearchOptions: SearchOptions[?, ?] =
    new SearchOptions()

  override def getTestResource(problemFileBase: String): InputStream =
    getClass.getResourceAsStream(getTestResourceLocation(problemFileBase))

  override def getTestResourceLocation(problemFileBase: String): String =
    getTestCaseDir + problemFileBase + SGF_EXTENSION

  override def getTestResource(progress: Progress, player1: Boolean): InputStream =
    val fName = progress match
      case Progress.BEGINNING => getStartGameMoveFileName(player1)
      case Progress.MIDDLE => getMiddleGameMoveFileName(player1)
      case Progress.END => getEndGameMoveFileName(player1)
    getTestResource(fName)

  protected def getStartGameMoveFileName(player1: Boolean): String
  protected def getMiddleGameMoveFileName(player1: Boolean): String
  protected def getEndGameMoveFileName(player1: Boolean): String

  /** @return directory location of test files. */
  def getTestCaseDir: String
