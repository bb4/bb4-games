/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.server.GtpTesujisoftGoServer
import org.igoweb.kgs.client.gtp.{GtpClient, Options}
import javax.swing.SwingUtilities
import java.io.{File, FileInputStream, FileOutputStream, IOException, PrintStream}
import java.util.Properties
import java.util.logging.{Level, Logger}

/**
 * Use this class to connect to KGS using lib/kgsGtp.jar and have my Tesujisoft go engine play on KGS.
 * kgsGtp is a java program that connects Go playing programs to the KGS Go server.
 * http://senseis.xmp.net/?KgsGtp
 * It uses GTP to communicate with your engine, giving commands like "play w cd5" and receives the engine's responses.
 *
 *  @author Barry Becker
 */
object GtpKgsTesujisoftGoClient {

  def main(args: Array[String]): Unit = {
    try {
      val logFile = FileUtil.getHomeDir + "/game/temp/" + "log.txt"

      val file = new File(logFile)
      val log = new PrintStream(new FileOutputStream(file))
      log.println("log=" + logFile)

      val gtpTSGoServer = new GtpTesujisoftGoServer(System.in, System.out, log)

      val props = new Properties()
      val inStream = new FileInputStream(FileUtil.getHomeDir +
        GameContext.GAME_ROOT + "twoplayer/go/resources/tesujiBot.properties")
      props.load(inStream)
      log.println("props=" + props.toString)
      inStream.close()

      log.println("step 0")
      SwingUtilities.invokeLater(new Runnable() {
        override def run(): Unit = {
          try {
            gtpTSGoServer.mainLoop()
          } catch {
            case ex: IOException =>
              Logger.getLogger(classOf[GtpKgsTesujisoftGoClient.type].getName).log(Level.SEVERE, null, ex)
          }
        }
      })

      val kgsOptions = new Options(props, logFile)
      log.println("step 1")
      val gtpClient = new GtpClient(System.in, System.out, kgsOptions)

      log.println("step 2")
      gtpClient.go()
      log.println("step 3")

      log.close()
    } catch {
      case t: Throwable =>
        t.printStackTrace()
        System.exit(-1)
    }
  }
}
