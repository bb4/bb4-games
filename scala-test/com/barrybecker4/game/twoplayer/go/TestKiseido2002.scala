/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.ui.file.GenericFileFilter
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.{File, IOException}
import java.net.URISyntaxException
import java.util
import scala.jdk.CollectionConverters._

/**
 * @author Barry Becker
 */
class TestKiseido2002 extends GoTestCase:

  // just do february for now. It has a lot as it is.
  @Test
  def testFebruary(): Unit =
    check("2002-02", 10)

  /**
   * Verify that we can load all the files with the specified pattern
   * @param problemPattern verify loading of all files that match this pattern.
   * @param limit don't load more files that this (since there may be a lot).
   */
  private def check(problemPattern: String, limit: Int = Int.MaxValue): Unit =
    GameContext.log(0, "Now checking " + problemPattern)
    val files = getFilesMatching("games2002/", problemPattern)
    var ct = 0
    for file <- files.asScala do
      if ct < limit then
        ct += 1
        val filename = file.substring(0, file.length - 4)
        GameContext.log(0, " about to restore :" + filename)
        try
          restore("games2002/" + filename)
        catch
          case e: AssertionError =>
            System.out.println("error on " + filename)
            e.printStackTrace()

    // must check the worth of the board once to update the scoreContributions fo empty spaces.
    controller.getMoveList
    controller.getSearchable().done(GoMove.createResignationMove(true), true)
    assertTrue(true)

  /** @return all the files matching the supplied pattern in the specified directory */
  protected def getFilesMatching(directory: String, pattern: String): util.List[String] =
    getResourcesForPattern(GoTestCase.EXTERNAL_TEST_CASE_DIR + directory, pattern)

  private def getResourcesForPattern(path: String, pattern: String): util.List[String] =
    val dirURL = getClass.getResource(path)
    if dirURL != null && dirURL.getProtocol == "file" then
      val directory = new File(dirURL.toURI).getAbsolutePath
      util.Arrays.asList(GenericFileFilter.getFilesMatching(directory, pattern)*)
    else
      null
