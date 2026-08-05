/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.common.ui.ComputerMoveProgressBar
import com.barrybecker4.game.common.ui.panel.GameChangedEvent
import com.barrybecker4.game.common.ui.panel.GameChangedListener
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.common.ui.viewer.GamePieceRenderer
import com.barrybecker4.game.twoplayer.common.ComputerMoveRequester
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.common.TwoPlayerController
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.TwoPlayerViewModel
import com.barrybecker4.optimization.parameter.ParameterArrayWithFitness

import javax.swing.JOptionPane
import javax.swing.SwingUtilities

/**
 * This class contains a TwoPlayerController and displays the current state of the Game.
 * The TwoPlayerController contains a Board which describes the game state.
 * The game specific TwoPlayerController is created upon construction to be used internally.
 * This class delegates to a boardRenderer to render the board and its pieces.
 * There should be no references to swing classes outside this ui subpackage.
 *
 *  @author Barry Becker
 */
abstract class AbstractTwoPlayerBoardViewer[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]]
    extends GameBoardViewer[M, B]
    with GameChangedListener[M]
    with TwoPlayerViewModel[M, B]:

  /** Responsible for showing move progress visually (with a progress bar). */
  private var moveRequester: ComputerMoveRequester[M, B] = scala.compiletime.uninitialized

  private var progressBar: ComputerMoveProgressBar = scala.compiletime.uninitialized

  /**
   * Show this cached board if we are in the middle of processing the next one
   * (to avoid concurrency problems)
   */
  private var cachedGameBoard: B = scala.compiletime.uninitialized

  /** we occasionally want to show the computer's considered next moves in the ui. */
  private var nextMoves: Array[M] = scala.compiletime.uninitialized

  /** playback a sequence of moves  */
  private var moveSequencePlayer: MoveSequencePlayback[M, B] = scala.compiletime.uninitialized

  // Initialize in constructor
  controller.setViewer(this)
  moveRequester = ComputerMoveRequester[M, B](get2PlayerController())
  moveSequencePlayer = MoveSequencePlayback[M, B](get2PlayerController())

  /**
   * @return our game controller
   */
  def get2PlayerController(): TwoPlayerController[M, B] =
    controller.asInstanceOf[TwoPlayerController[M, B]]

  /**
   * Set an optional progress bar for showing progress as the computer thinks about its next move.
   */
  override def setProgressBar(progressBar: ComputerMoveProgressBar): Unit =
    this.progressBar = progressBar

  def getNextMoves: Array[M] = nextMoves

  private def setNextMoves(nextMoves: Array[M]): Unit =
    this.nextMoves = nextMoves

  def getPieceRenderer: GamePieceRenderer =
    getBoardRenderer.getPieceRenderer

  /**
   * run many games and use hill-climbing to find optimal weights.
   */
  private def runOptimization(): Unit =
    val viewer = this

    get2PlayerController().runOptimization(new OptimizationDoneHandler:
      override def done(optimizedParams: ParameterArrayWithFitness): Unit =
        JOptionPane.showMessageDialog(
          viewer,
          GameContext.getLabel("OPTIMIZED_WEIGHTS_TXT") + optimizedParams,
          GameContext.getLabel("OPTIMIZED_WEIGHTS"),
          JOptionPane.INFORMATION_MESSAGE
        )
    )

  /**
   * return the game to its original state.
   */
  override def reset(): Unit =
    controller.reset()
    val board = getBoard
    commonReset(board)

  /**
   * start over with a new game using the current options.
   */
  override def startNewGame(): Unit =
    reset()
    val controller = get2PlayerController()
    if get2PlayerController().getOptions.isAutoOptimize() then
      runOptimization()
    else if controller.getPlayers.allPlayersComputer then
      controller.computerMovesFirst()
      doComputerMove(false)
    else if controller.doesComputerMoveFirst() then
      // computer vs human opponent
      controller.computerMovesFirst()
      refresh()

  /**
   * Register the human's move.
   * @param move the move to make.
   * @return true if the game is over now.
   */
  private def manMoves(move: M): Boolean =
    val controller = get2PlayerController()
    if GameContext.getUseSound then
      GameContext.getMusicMaker.playNote(controller.getOptions.getPreferredTone(), 45, 0, 200, 1000)

    // need to clear the cache, otherwise we may render a stale board.
    cachedGameBoard = null.asInstanceOf[B]
    controller.manMoves(move)

    // need to refresh here to show man moves in human only game
    if controller.getPlayers.allPlayersHuman then
      refresh()

    // Second arg was true, but then we did final update twice.
    val done = controller.getSearchable().done(move, false)
    // This is needed to update the current player info
    sendGameChangedEvent(move)
    done

  /**
   * The computer plays against itself.
   */
  override def doComputerVsComputerGame(): Unit =
    var done = false

    // if player one has not already moved, make sure they do
    if get2PlayerController().getMoveList.isEmpty then
      get2PlayerController().computerMovesFirst()
      val isEmpty = get2PlayerController().getMoveList.isEmpty
      assert(!isEmpty, "Error: null before search")

    // don't run this on the event dispatch thread.
    while !done do
      done = doComputerMove(false)
      // if done the final move was placed
      if !done then
        done = doComputerMove(true)

  /**
   * Make the computer move and show it on the screen.
   * Since this can take a very long time we will show the user a progress bar
   * to give feedback.
   *
   * @param isPlayer1 if the computer player now moving is player 1.
   * @return done always returns false unless auto optimizing
   */
  private def doComputerMove(isPlayer1: Boolean): Boolean =
    setCursor(waitCursor_)

    try
      if progressBar != null then
        progressBar.doComputerMove(moveRequester)
      moveRequester.requestComputerMove(isPlayer1)
    catch
      case ae: AssertionError =>
        // if any errors occur during search, I want to save the state of the game to
        // a file so the error can be easily reproduced.
        assertFailed(ae)
        false

  /**
   * Currently this does not actually step forward just one search step, but instead
   * stops after PROGRESS_STEP_DELAY more milliseconds.
   */
  override final def step(): Unit =
    progressBar.step()

  /**
   * resume computation
   */
  override final def continueProcessing(): Unit =
    moveRequester.continueProcessing()

  /**
   * Called when the controller has found the computer's move (usually after a long asynchronous search).
   * The runnable body will run on the event-dispatch thread when the search has completed.
   * @param m the move that was selected by the computer.
   */
  override def computerMoved(m: M): Unit =
    val postMoveCleanup = PostMoveCleanup(m)
    SwingUtilities.invokeLater(postMoveCleanup)

  /**
   * Implements the GameChangedListener interface.
   * Called when the game has changed in some way
   * @param evt change event
   */
  override def gameChanged(evt: GameChangedEvent[M]): Unit =
    val c = get2PlayerController()
    assert(c == evt.getController)

    // note: we don't show the winner dialog if we are having the computer play against itself.
    if c.getSearchable().done(evt.getMove, true) && c.getOptions.getShowGameOverDialog() then
      showWinnerDialog()
    else
      if get2PlayerController().getPlayers.allPlayersComputer && evt.getMove != null then
        continuePlay(evt.getMove)

  /**
   * Let the computer go next if one of the players is a computer.
   * @param move the current move (it must not be null)
   * @return false if the game is at an end, otherwise return true
   */
  final def continuePlay(move: M): Boolean =
    val controller = get2PlayerController()
    val done =
      if controller.getPlayers.allPlayersComputer then
        continueAllComputerPlayers(move, controller)
      else
        continueWithHumans(move, controller)
    !done

  private def continueAllComputerPlayers(move: M, controller: TwoPlayerController[M, B]): Boolean =
    refresh()
    doComputerMove(!move.isPlayer1)

  private def continueWithHumans(move: M, controller: TwoPlayerController[M, B]): Boolean =
    var done = false
    if controller.isPlayer1sTurn then
      assert(!controller.isProcessing)
      done = manMoves(move)
      if !controller.isPlayer1sTurn && !controller.getPlayers.getPlayer2.isHuman && !done then
        done = doComputerMove(false)
    else
      done = manMoves(move)
      if controller.isPlayer1sTurn && !controller.getPlayers.getPlayer1.isHuman && !done then
        done = doComputerMove(true)
    done

  /**
   * some moves require that the human players be given some kind of notification.
   * @param m the last move made
   */
  protected def warnOnSpecialMoves(m: TwoPlayerMove): Unit =
    if m == null then return

    if m.isPassingMove && !get2PlayerController().getPlayers.allPlayersComputer then
      JOptionPane.showMessageDialog(
        this,
        GameContext.getLabel("COMPUTER_PASSES"),
        GameContext.getLabel("INFORMATION"),
        JOptionPane.INFORMATION_MESSAGE
      )

  /**
   * return the game to its state before the last human move.
   */
  def undoLastManMove(): Unit =
    val c = get2PlayerController()
    val players = c.getPlayers
    if players.allPlayersComputer then return

    val move = c.undoLastMove()
    if move != null then
      undoneMoves_.add(move)
      if !players.allPlayersHuman then
        undoneMoves_.add(c.undoLastMove())
      refresh()
    else
      JOptionPane.showMessageDialog(
        this,
        GameContext.getLabel("NO_MOVES_TO_UNDO"),
        GameContext.getLabel("WARNING"),
        JOptionPane.WARNING_MESSAGE
      )

  /**
   * redo the last human player's move.
   */
  def redoLastManMove(): Unit =
    val c = get2PlayerController()
    val players = c.getPlayers
    if undoneMoves_.isEmpty then
      JOptionPane.showMessageDialog(
        null,
        GameContext.getLabel("NO_MOVES_TO_REDO"),
        GameContext.getLabel("WARNING"),
        JOptionPane.WARNING_MESSAGE
      )
      return

    if players.allPlayersComputer then return

    c.makeMove(undoneMoves_.removeLast())
    if !players.allPlayersHuman then
      c.makeMove(undoneMoves_.removeLast())
    refresh()

  final def showMoveSequence(moveSequence: java.util.List[M]): Unit =
    showMoveSequence(moveSequence, getController.getNumMoves)

  private final def showMoveSequence(moveSequence: java.util.List[M], numMovesToBackup: Int): Unit =
    showMoveSequence(moveSequence, numMovesToBackup, null)

  final def showMoveSequence(
      moveSequence: java.util.List[M],
      numMovesToBackup: Int,
      nextMoves: Array[M]
  ): Unit =
    moveSequencePlayer.makeMoveSequence(moveSequence, numMovesToBackup)
    setNextMoves(nextMoves)
    refresh()

  /**
   * @return   the message to display at the completion of the game.
   */
  override def getGameOverMessage: String =
    GameOverMessage(get2PlayerController()).getText()

  /**
   * @return the cached game board if we are in the middle of processing.
   */
  override def getBoard: B =
    val c = get2PlayerController()

    if cachedGameBoard == null then
      cachedGameBoard = c.getBoard.copy().asInstanceOf[B]

    if c.isProcessing && !c.getOptions.isAutoOptimize() then
      cachedGameBoard
    else
      c.getBoard

  private class PostMoveCleanup(lastMove: M) extends Runnable:
    override def run(): Unit =
      setCursor(origCursor_)
      if GameContext.getUseSound then
        GameContext.getMusicMaker.playNote(
          get2PlayerController().getOptions.getPreferredTone(),
          45,
          0,
          200,
          1000
        )
      showLastMove()
      cachedGameBoard = null.asInstanceOf[B]
      if !get2PlayerController().getOptions.isAutoOptimize() then
        // show a pop-up for certain exceptional cases.
        // For example, in chess we warn on a checking move.
        warnOnSpecialMoves(lastMove)
        sendGameChangedEvent(lastMove)
      if progressBar != null then
        progressBar.cleanup()
