/* Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.server

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.game.twoplayer.go.GoController
import gtp.GtpServer
import utils.{ErrorMessage, Options, StringUtils}
import java.io.{File, FileNotFoundException, FileOutputStream, InputStream, OutputStream, PrintStream}

/**
 * This class wraps the GoController and provides an interface to a GTP based controller (front end ui).
 * GoGui is typically the controller I have used, but it could be any GTP based UI.
 *
 * This wraps my Tesujisoft go engine so that a client program can use it.
 * If you want to have the Tesujisoft go engine play on KGS use GtpKgsTesujisoftGoClient.
 *
 * Inherits the ability to connect a Go program supporting GTP to a socket from GtpServer.
 *
 *  @author Barry Becker
 */
class GtpTesujisoftGoServer(in: InputStream, out: OutputStream, logger: PrintStream) 
  extends GtpServer(in, out, logger) {

  private val thread_ = Thread.currentThread()
  private val cmdHandler = new CommandHandler()

  override def handleCommand(cmdLine: String, response: StringBuffer): Boolean = {
    cmdHandler.handleCommand(cmdLine, response)
  }

  override def interruptCommand(): Unit = {
    thread_.interrupt()
  }
}

object GtpTesujisoftGoServer {

  /**
   * Handle the command line arguments
   */
  @throws[ErrorMessage]
  @throws[FileNotFoundException]
  private def handleArgs(args: Array[String]): PrintStream = {
    val options = Array(
      "config:",
      "help",
      "log",
      "version"
    )
    val opt = Options.parse(args, options)
    if (opt.isSet("help")) {
      val helpText =
        "Usage: java -classpath " +
          FileUtil.getHomeDir +
          "/classes com.barrybecker4.game.twoplayer.go.server.GtpTesujisoftGoServer [options]\n" +
          '\n' +
          "-config       config file\n" +
          "-help         display this help and exit\n" +
          "-log file     log GTP stream to file\n" +
          "-version      print version and exit\n"
      System.out.print(helpText)
      return null
    }
    if (opt.isSet("version")) {
      System.out.println("GtpTesujisoft " + GoController.VERSION)
      return null
    }
    val log: PrintStream =
      if (opt.isSet("log")) {
        val file = new File(opt.getString("log"))
        new PrintStream(new FileOutputStream(file))
      } else {
        val logFile = FileUtil.getHomeDir + "/temp/" + "log.txt"
        val file = new File(logFile)
        new PrintStream(new FileOutputStream(file))
      }
    log
  }

  def main(args: Array[String]): Unit = {
    try {
      val log = handleArgs(args)

      val gtpTSGoServer = new GtpTesujisoftGoServer(System.in, System.out, log)
      gtpTSGoServer.mainLoop()
      log.close()
    } catch {
      case t: Throwable =>
        StringUtils.printException(t)
        System.exit(-1)
    }
  }
}
