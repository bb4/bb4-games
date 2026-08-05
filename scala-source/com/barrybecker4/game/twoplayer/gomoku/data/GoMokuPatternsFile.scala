/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.data

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.gomoku.pattern.GoMokuPatterns

import java.io._

/**
 *  Encapsulates the domain knowledge for GoMoku.
 *  Its primary client is the GoMokuController class.
 *  These are key patterns that can occur in the game and are weighted
 *  by importance to let the computer play better.
 *
 *  @author Barry Becker
 */
class GoMokuPatternsFile extends GoMokuPatterns {

  private var patterns_ : Array[String] = scala.compiletime.uninitialized

  readPatternFile()

  override def getNumPatterns: Int = patterns_.length

  override def getPatternString(i: Int): String = patterns_(i)

  protected def getPatternFile: String = {
    GameContext.GAME_RESOURCE_ROOT + "gomoku/GoMoku.patterns1.dat"
  }

  protected def getExportFile: String = {
    GameContext.GAME_RESOURCE_ROOT + "gomoku/GoMoku.export.dat"
  }

  protected def readPatternFile(): Unit = {
    val patternFileName = getPatternFile
    val file = new File(patternFileName)
    var patternFile: FileInputStream = null

    try {
      patternFile = new FileInputStream(file)
    } catch {
      case e: FileNotFoundException =>
        GameContext.log(0, "file " + patternFileName + " not found." + e.getMessage)
        return
    }
    val iStreamReader = new InputStreamReader(patternFile)
    val inData = new BufferedReader(iStreamReader)
    val inStream = new StreamTokenizer(inData)
    inStream.commentChar('#')
    inStream.slashSlashComments(true)
    inStream.wordChars('_', '_' + 1)

    try {
      val numPatterns = inStream.nval.toInt
      patterns_ = new Array[String](numPatterns)
      for (i <- 0 until numPatterns) {
        var token = inStream.nextToken()
        if (token == StreamTokenizer.TT_WORD) {
          patterns_(i) = inStream.sval
        }
        else
          GameContext.log(0, "unexpected token type = " + token + "   nval = " + inStream.nval)
        token = inStream.nextToken()
        var wtIndex = 0
        if (token == StreamTokenizer.TT_NUMBER) {
          wtIndex = inStream.nval.toInt
        }
        else
          GameContext.log(0, "unexpected token type = " + token + "   sval = " + inStream.sval)

        val pattern = patterns_(i)
        setPatternWeightInTable(pattern, wtIndex)
      }
      iStreamReader.close()
    } catch {
      case e: IOException =>
        GameContext.log(0, "error occurred while reading " + patternFileName)
    }
  }

  protected def writePatternFile(): Unit = {
    val exportFile = getExportFile
    val file = new File(exportFile)
    var patternFile: FileOutputStream = null

    try {
      patternFile = new FileOutputStream(file)
    } catch {
      case e: FileNotFoundException =>
        GameContext.log(0, "can't open " + exportFile + " for write")
        e.printStackTrace()
        return
    }
    val oStreamWriter = new OutputStreamWriter(patternFile)
    val outData = new BufferedWriter(oStreamWriter)

    try {
      val numPatterns = getNumPatterns
      GameContext.log(0, s"there are $numPatterns patterns. ")
      for (i <- 0 until numPatterns) {
        outData.write(s""""${patterns_(i)}", """)
      }
      outData.write("\r\n")
      for (i <- 0 until numPatterns) {
        val index = getWeightIndexForPattern(patterns_(i))
        outData.write(s"$index, ")
      }
      outData.write("\r\n")
      outData.flush()
      patternFile.flush()
      outData.close()
      patternFile.close()
    } catch {
      case e: IOException =>
        GameContext.log(0, "error occurred while writing " + exportFile)
        e.printStackTrace()
    }
  }
}
