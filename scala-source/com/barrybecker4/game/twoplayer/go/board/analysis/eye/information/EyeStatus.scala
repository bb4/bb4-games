/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

/**
 * Enum for the different possible center Eye Status'.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 *
 * Scores match [[EyeShapeScores]].
 * Extends java.lang.Enum for Java interop.
 */
enum EyeStatus(
  private val label: String,
  private val description: String,
  private val score: Float
) extends java.lang.Enum[EyeStatus]:

  /**
   * The eye will end up as only one eye and this will not be sufficient to
   * live. A nakade eye can be the result of: (1) an eye with an empty set of vital
   * points or (2) an eye with all the set of vital points filled by the opponent's stones.
   */
  case NAKADE extends EyeStatus(
    "Nakade",
    "Ends up as one eye and this will not be sufficient to live.",
    1.0f)

  /**
   * The eye can end up as a nakade eye or an
   * alive eye depending on the next color to play. An unsettled
   * eye is the result of an eye with one and only one empty
   * intersection in the set of vital points.
   */
  case UNSETTLED extends EyeStatus(
    "Unsettled",
    "Ends up either nakade or alive depending on who moves first.",
    1.2f)

  /**
   * The string owning the eye is alive no matter who plays first and no matter what the surrounding conditions are.
   */
  case ALIVE extends EyeStatus(
    "Alive",
    "Unconditionally alive no matter who plays first.",
    2.0f)

  /**
   * This is a particular case in which the surrounding conditions
   * determine the status of the eye.
   */
  case ALIVE_IN_ATARI extends EyeStatus(
    "Alive in atari",
    "There are only one or zero empty intersections adjacent to the surrounding" +
      " block, and capturing the opponent stones inside the eye grants an alive status.",
    0.19f)

  /**
   * A false eye is not an eye. It must not be scored as nakade, unsettled, or ko.
   */
  case FALSE extends EyeStatus(
    "False",
    "Not an eye, no matter who plays first.",
    0.0f)

  /**
   * For example if may be a false eye with only one space in the eye - in other words a ko.
   */
  case KO extends EyeStatus(
    "Ko",
    "Will never be an eye no matter who plays first.",
    0.19f)

  /**
   * This can arise when there is a ko on the edge or corner.
   */
  case UNSETTLED_KO extends EyeStatus(
    "Unsettled ko",
    "Unsettled ko status can only happen on the edge or in the corner.",
    0.19f)

  /**
   * Other possibilities: Unknown, dead, false
   */
  case UNCLASSIFIED extends EyeStatus(
    "Unclassified",
    "The status has not been determined yet",
    1.0f)

  override def toString: String = label

  def getDescription: String = description

  def getScore: Float = score
