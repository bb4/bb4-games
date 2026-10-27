/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui

import com.barrybecker4.ui.file.ExtensionFileFilter

/**
 * File filter for SGF files.
 *
 * @author Barry Becker
 */
class SgfFileFilter extends ExtensionFileFilter(SgfFileFilter.SGF_EXTENSION)

object SgfFileFilter {
  val SGF_EXTENSION: String = "sgf"
}
