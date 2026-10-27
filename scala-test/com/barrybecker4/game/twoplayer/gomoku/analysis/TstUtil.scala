/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}
import org.junit.Assert.assertEquals

import scala.jdk.CollectionConverters.*

/**
 * Test utilities for gomoku analysis tests.
 */
object TstUtil:

  def createLine(linePattern: String, evaluator: LineEvaluator): Line =
    val line = new Line(evaluator)
    for i <- 0 until linePattern.length do
      var piece: GamePiece = null
      val c = linePattern.charAt(i)
      if c == 'X' then piece = new GamePiece(true)
      if c == 'O' then piece = new GamePiece(false)
      val pos = new BoardPosition(0, 0, piece)
      line.append(pos)
    line

  def printLines(lines: java.util.List[Line]): Unit =
    val bldr = new StringBuilder
    val len = lines.size
    for i <- 0 until len do
      bldr.append('"').append(lines.get(i)).append('"')
      if i < len - 1 then bldr.append(", ")
    System.out.println("lines = " + bldr.toString)

  def quoteStringList(strings: java.util.List[CharSequence]): String =
    val bldr = new StringBuilder
    val len = strings.size
    for i <- 0 until len do
      bldr.append('"').append(strings.get(i)).append('"')
      if i < len - 1 then bldr.append(", ")
    bldr.toString

  def checkRecordedPatterns(expectedPatterns: Array[String], line: Line): Unit =
    checkRecordedPatterns(expectedPatterns, line.getLineEvaluator.asInstanceOf[StubLineEvaluator])

  def checkRecordedPatterns(expectedPatterns: Array[String], evaluator: StubLineEvaluator): Unit =
    val checkedPatterns = evaluator.getPatternsChecked
    var i = 0
    System.out.println("checkedPatterns = " + TstUtil.quoteStringList(checkedPatterns))
    assertEquals("Unexpected number of patterns checked",
      expectedPatterns.length, checkedPatterns.size)

    for pat <- checkedPatterns.asScala do
      assertEquals(expectedPatterns(i), pat)
      i += 1
    checkedPatterns.clear()
