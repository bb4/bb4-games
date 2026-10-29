/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

/**
 * Enum for the different possible Eye shapes.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 * Extends java.lang.Enum for Java interop.
 */
enum EyeType(eyeSize: Int) extends java.lang.Enum[EyeType]:

  /** False eye always have the potential to become no eyes */
  case FalseEye extends EyeType(0)

  case E1 extends EyeType(1)
  case E2 extends EyeType(2)
  case E3 extends EyeType(3)
  case E4 extends EyeType(4)
  case E5 extends EyeType(5)
  case E6 extends EyeType(6)
  case E7 extends EyeType(7)

  /** Usually 2 or more eyes, but may be none or one in some rare cases. */
  case TerritorialEye extends EyeType(8)

  /** @return the number of spaces in the eye (may be filled with some enemy stones). */
  def getSize: Byte = eyeSize.toByte

  /** @return eye information for this type */
  def getInformation(name: String): EyeInformation = this match
    case EyeType.FalseEye => new FalseEyeInformation()
    case EyeType.E1 => new E1Information()
    case EyeType.E2 => new E2Information()
    case EyeType.E3 => new E3Information()
    case EyeType.E4 => new E4Information(name)
    case EyeType.E5 => new E5Information(name)
    case EyeType.E6 => new E6Information(name)
    case EyeType.E7 => new E7Information(name)
    case EyeType.TerritorialEye => new TerritorialEyeInformation()
