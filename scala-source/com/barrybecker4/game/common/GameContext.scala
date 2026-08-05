/* Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

import com.barrybecker4.common.app.ILog
import com.barrybecker4.common.i18n.{LocaleType, MessageContext}
import com.barrybecker4.game.common.plugin.PluginManager
import com.barrybecker4.sound.{Instruments, MusicMaker}
import com.barrybecker4.ui.util.Log
import java.text.NumberFormat
import java.util.Random

/**
 * Manage game context info such as logging, debugging, resources, and profiling.
 * Perhaps use java properties or config file to define options.
 *
 * @author Barry Becker
 */
object GameContext {

  private var logger: ILog = new Log()

  private val useSound: Boolean = false

  private var musicMaker: MusicMaker = _

  private var RANDOM: Random = new Random(0)

  if (useSound) {
    getMusicMaker.stopAllSounds()
    getMusicMaker.startNote(Instruments.SEASHORE, 40, 2, 3)
  }

  val GAME_ROOT: String = "game/source/com/barrybecker4/game/"
  val GAME_RESOURCE_ROOT: String = "com/barrybecker4/game/"

  private val DEBUG: Int = 0
  private var debug: Int = DEBUG

  private val PROFILING: Boolean = false
  private var profiling: Boolean = PROFILING

  private val COMMON_MESSAGE_BUNDLE: String = "com.barrybecker4.game.common.resources.coreMessages"
  private var messageContext: MessageContext = new MessageContext(COMMON_MESSAGE_BUNDLE)

  def getDebugMode: Int = debug

  def setDebugMode(debugMode: Int): Unit = {
    debug = debugMode
  }

  def isProfiling: Boolean = profiling

  def setProfiling(prof: Boolean): Unit = {
    profiling = prof
  }

  def setLogger(theLogger: ILog): Unit = {
    assert(theLogger != null)
    logger = theLogger
  }

  def getLogger: ILog = logger

  def log(logLevel: Int, message: String): Unit = {
    logger.print(logLevel, getDebugMode, message)
  }

  def getUseSound: Boolean = useSound

  def getMusicMaker: MusicMaker = synchronized {
    if (musicMaker == null) {
      musicMaker = new MusicMaker()
    }
    musicMaker
  }

  def loadResources(gameName: String): Unit = {
    log(1, s"loadGameResources gameName=$gameName")
    val plugin = PluginManager.getInstance.getPlugin(gameName)
    assert(plugin != null, s"Could not find plugin for $gameName")
    log(1, s"plugin = $plugin")
    log(1, s"gameName=$gameName plugin=$plugin")
    val resourcePath = plugin.getMsgBundleBase
    log(2, s"searching for $resourcePath")

    messageContext.setLogger(logger)
    messageContext.setDebugMode(debug)
    messageContext.addResourcePath(resourcePath)
    messageContext.addResourcePath("com.barrybecker4.ui.message")
    log(1, "done loading resources")
  }

  def setLocale(locale: LocaleType): Unit = {
    messageContext.setLocale(locale)
  }

  def getCurrencyFormat: NumberFormat = 
    NumberFormat.getCurrencyInstance(messageContext.getLocale)

  def getLabel(key: String): String = messageContext.getLabel(key)

  def getLocale(name: String, finf: Boolean): LocaleType =
    messageContext.getLocale(name, finf)

  def random(): Random = RANDOM

  def setRandomSeed(seed: Int): Unit = {
    RANDOM = new Random(seed)
  }
}
