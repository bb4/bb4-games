/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.viewer.animation

import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{JComponent, Timer}

/**
 * Drives a paint-only piece move animation on the EDT.
 * Does not mutate board state — only updates [[MoveAnimationState]] on the renderer.
 */
class PieceMoveAnimator(
    renderer: GameBoardRenderer,
    viewer: JComponent,
    onComplete: () => Unit
) {
  private var timer: Timer = scala.compiletime.uninitialized
  private var state: MoveAnimationState = scala.compiletime.uninitialized
  private var pauseRemainingMs: Int = 0
  private var finished: Boolean = false

  def start(animationState: MoveAnimationState): Unit = {
    stop()
    finished = false
    state = animationState
    pauseRemainingMs = 0
    renderer.setMoveAnimationState(state)
    timer = new Timer(PieceMoveAnimator.TICK_MS, new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = onTick()
    })
    timer.start()
    viewer.repaint()
  }

  def stop(): Unit = {
    if (timer != null) {
      timer.stop()
      timer = null
    }
    renderer.setMoveAnimationState(null)
    state = null
  }

  private def onTick(): Unit = {
    if (state == null || finished) return

    if (pauseRemainingMs > 0) {
      pauseRemainingMs -= PieceMoveAnimator.TICK_MS
      viewer.repaint()
      return
    }

    if (state.isComplete) {
      finish()
      return
    }

    val delta = PieceMoveAnimator.TICK_MS.toDouble / PieceMoveAnimator.SEGMENT_MS
    state.progress = Math.min(1.0, state.progress + delta)

    if (state.progress >= 1.0) {
      state.completeCurrentHop()
      if (state.isComplete) {
        finish()
        return
      }
      pauseRemainingMs = PieceMoveAnimator.PAUSE_MS
    }

    viewer.repaint()
  }

  private def finish(): Unit = {
    if (finished) return
    finished = true
    stop()
    viewer.repaint()
    onComplete()
  }
}

object PieceMoveAnimator {
  val SEGMENT_MS: Int = 250
  val PAUSE_MS: Int = 50
  val TICK_MS: Int = 33

  /** Smoothstep easing for segment progress. */
  def ease(t: Double): Double = {
    val x = Math.max(0.0, Math.min(1.0, t))
    x * x * (3.0 - 2.0 * x)
  }
}
