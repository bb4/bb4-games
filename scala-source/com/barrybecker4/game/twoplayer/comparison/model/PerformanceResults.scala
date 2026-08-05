// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.game.common.persistence.GameExporter
import com.barrybecker4.ui.util.ImageUtil

import java.awt.image.BufferedImage
import java.io.PrintWriter

/**
 * Right now this just contains the name of the config and the
 * search options, but we may add the game weights too at some point.
 *
 * @author Barry Becker
 */
class PerformanceResults {

  private var player1Won = false
  private var wasTie = false
  private var strengthOfWin = 0.0
  private var numMoves = 0
  private var timeMillis = 0L
  private var finalStateImg: BufferedImage = null
  private var exporter: GameExporter[?, ?] = null
  private var description = "p1Config_vs_p2Config"

  private var normalizedNumSeconds = 1.0
  private var normalizedNumMoves = 1.0
  private var normalized = false

  def this(
    p1Won: Boolean, wasTie: Boolean, strengthOfWin: Double,
    numMoves: Int, timeMillis: Long, finalImage: BufferedImage,
    exporter: GameExporter[?, ?], description: String
  ) = {
    this()
    this.player1Won = p1Won
    this.wasTie = wasTie
    this.strengthOfWin = strengthOfWin
    this.numMoves = numMoves
    this.timeMillis = timeMillis
    this.finalStateImg = finalImage
    this.exporter = exporter
    this.description = description
  }

  def getOutcome: Outcome =
    if (wasTie) Outcome.TIE
    else if (player1Won) Outcome.PLAYER1_WON
    else Outcome.PLAYER2_WON

  def getFinalImage: BufferedImage = finalStateImg

  def getPlayer1Won: Boolean = player1Won

  def getWasTie: Boolean = wasTie

  def getStrengthOfWin: Double = strengthOfWin

  def getNumSeconds: Double = timeMillis.toDouble / 1000.0

  def getNumMoves: Int = numMoves

  def getTimeFormatted: String = {
    val numSecs = getNumSeconds
    val numMinutes = (numSecs / 60).toInt
    val seconds = numSecs - numMinutes * 60
    val minFmt = if (numMinutes > 0) FormatUtil.formatNumber(numMinutes) + " min " else ""
    minFmt + seconds + " secs"
  }

  def getNormalizedNumSeconds: Double = normalizedNumSeconds

  def getNormalizedNumMoves: Double = {
    assert(normalized)
    normalizedNumMoves
  }

  def normalize(maxTotals: ResultMaxTotals): Unit = {
    normalizedNumSeconds = getNumSeconds / maxTotals.maxTotalTimeSeconds
    normalizedNumMoves = getNumMoves.toDouble / maxTotals.maxTotalMoves
    normalized = true
  }

  def getOutcomeVerb(player1Perpective: Boolean): String =
    getOutcome match {
      case Outcome.PLAYER1_WON => if (player1Perpective) "won" else "lost"
      case Outcome.PLAYER2_WON => if (player1Perpective) "lost" else "won"
      case Outcome.TIE => "tied"
    }

  def getDescription: String = description

  def saveTo(path: String): Unit = {
    FileUtil.verifyDirectoryExistence(path)

    val baseName = path + FileUtil.FILE_SEPARATOR + getDescription
    exporter.saveToFile(baseName, null)
    ImageUtil.saveAsImage(baseName, getFinalImage, ImageUtil.ImageType.PNG)
    val writer = FileUtil.createPrintWriter(baseName + ".txt")
    writer.write(toString)
    writer.close()
  }

  def toHtmlString(substring: Boolean): String = {
    val bldr = new StringBuilder
    if (!substring)
      bldr.append("<html>")
    bldr.append("<b>").append(description).append("</b><br>")
    if (getWasTie) {
      bldr.append("it was a tie")
    } else {
      bldr.append("player <b>").append(if (getPlayer1Won) "1" else "2").append("</b> won by ")
      bldr.append(getStrengthOfWin)
    }
    bldr.append("<br> in ")
    bldr.append("<b>").append(getTimeFormatted).append("</b>")
    bldr.append("<br> and ").append(getNumMoves).append(" moves. ")
    if (!substring)
      bldr.append("</html>")
    bldr.toString
  }

  override def toString: String = {
    val bldr = new StringBuilder
    bldr.append(description).append("\n")
    if (getWasTie) {
      bldr.append("it was a tie")
    } else {
      bldr.append("player ").append(if (getPlayer1Won) "1" else "2").append(" won by ")
      bldr.append(getStrengthOfWin)
    }
    bldr.append("\n in ").append(getTimeFormatted)
    bldr.append(" and ").append(getNumMoves).append(" moves. ")
    bldr.append("\ntime (sec) = ").append(FormatUtil.formatNumber(timeMillis.toDouble / 1000.0))
    bldr.append("\nnumMoves = ").append(numMoves)
    bldr.append("\nnormalized time = ").append(normalizedNumSeconds)
    bldr.append("\nnormalized numMoves = ").append(normalizedNumMoves)
    bldr.toString
  }
}
