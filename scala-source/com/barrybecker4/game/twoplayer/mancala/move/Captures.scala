package com.barrybecker4.game.twoplayer.mancala.move

import com.barrybecker4.common.geometry.Location

/**
  * Map from locations to the number of stones that need to be restored to that location
  * @author Barry Becker
  */
class Captures extends java.util.HashMap[Location, Byte]
