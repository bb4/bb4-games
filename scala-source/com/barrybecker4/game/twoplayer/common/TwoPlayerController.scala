/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.ca.dj.jigo.sgf.SGFException
import com.barrybecker4.game.common.{GameContext, GameController, GameWeights, Move, MoveList}
import com.barrybecker4.game.common.player.{Player, PlayerList, PlayerOptions}
import com.barrybecker4.game.twoplayer.common.persistence.{TwoPlayerGameExporter, TwoPlayerGameImporter}
import com.barrybecker4.game.twoplayer.common.search.Searchable
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable
import com.barrybecker4.game.twoplayer.common.ui.{OptimizationDoneHandler, TwoPlayerPieceRenderer}
import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.optimizee.Optimizee
import com.barrybecker4.optimization.parameter.ParameterArray
import scala.Some
import javax.swing.JOptionPane
import java.awt.Color
import java.io.{FileInputStream, FileNotFoundException, IOException, InputStream}

/**
 * This is an abstract base class for a two player Game Controller.
 * It contains the key logic for 2 player zero sum games with perfect information.
 * Some examples include chess, checkers, go, othello, gomoku, blockade, Mancala, nine-mens morris, etc.
 * It implements Optimizee because the games derived from this class
 * can be optimized to improve their playing ability.
 *
 * Instance of this class process requests from the GameViewer.
 * Specifically, the GameViewer tells the TwoPlayerController what move the human made,
 * and the TwoPlayerController returns information such as the computer's move.
 *
 * @author Barry Becker
 */
abstract class TwoPlayerController[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]] extends GameController[M, B]:

  protected var player1sTurn: Boolean = true

  /**
   * These weights determine how the computer values each move as parameters to a game dependent evaluation function.
   */
  protected var weights_ : GameWeights = _

  /** if this becomes non-null, we will fill in the game tree for display in a UI. */
  private var gameTreeViewer_ : IGameTreeViewable = _

  /** Worker represents a separate thread for finding the next move. */
  private var worker_ : TwoPlayerSearchWorker[M, B] = _

  /** Capable of searching for the best next move */
  private var searchable_ : Searchable[M, B] = _

  /**
   * Construct the game controller.
   */
  setPlayers(createPlayers())
  worker_ = new TwoPlayerSearchWorker[M, B](this)

  override def getOptions: TwoPlayerOptions =
    if gameOptions == null then
      gameOptions = createOptions()
    gameOptions.asInstanceOf[TwoPlayerOptions]

  /**
   * @return custom set of search and game options.
   */
  protected def createOptions(): TwoPlayerOptions

  override def getViewer: TwoPlayerViewModel[M, B] =
    viewer.asInstanceOf[TwoPlayerViewModel[M, B]]

  /**
   * Return the game board back to its initial opening state
   */
  override def reset(): Unit =
    worker_.interrupt()
    super.reset()
    searchable_ = null
    getPlayers.reset()
    player1sTurn = true

  /**
   * save the current state of the game to a file in SGF (4) format (standard game format).
   * This should some day be xml (xgf)
   */
  override def getExporter: TwoPlayerGameExporter[M, B] =
    new TwoPlayerGameExporter[M, B](this)

  override def getMoveList: MoveList[M] =
    getBoard.getMoveList

  override def restoreFromFile(fileName: String): Unit =
    try
      val iStream = new FileInputStream(fileName)
      GameContext.log(2, s"opening $fileName")
      restoreFromStream(iStream)
    catch
      case fnfe: FileNotFoundException =>
        JOptionPane.showMessageDialog(null, s"file $fileName was not found.${fnfe.getMessage()}")
      case ioe: IOException =>
        JOptionPane.showMessageDialog(null, s"IOException occurred while reading $fileName :${ioe.getMessage()}")
      case sgfe: SGFException =>
        JOptionPane.showMessageDialog(null, s"file $fileName had an SGF error while loading: ${sgfe.getMessage()}")
        sgfe.printStackTrace()

  def restoreFromStream(iStream: InputStream): Unit =
    val importer = new TwoPlayerGameImporter[M, B](this)
    importer.restoreFromStream(iStream)
    val m = getLastMove
    if m != null then
      val value = getSearchable().worth(m, weights_.getDefaultWeights)
      m.setValue(value)

  /**
   * @return true if the computer is supposed to make the first move.
   */
  def doesComputerMoveFirst(): Boolean =
    !getPlayers.getPlayer1.isHuman

  /**
   * @return the 2 players.
   */
  protected def createPlayers(): PlayerList =
    val players = new PlayerList()

    val p1Opts = createPlayerOptions(GameContext.getLabel("PLAYER1"), TwoPlayerPieceRenderer.DEFAULT_PLAYER1_COLOR)
    val p2Opts = createPlayerOptions(GameContext.getLabel("PLAYER2"), TwoPlayerPieceRenderer.DEFAULT_PLAYER2_COLOR)

    players.add(new Player(p1Opts, true))
    players.add(new Player(p2Opts, false))
    players

  /** color is ignored right now. Comes from the piece renderer */
  protected def createPlayerOptions(playerName: String, color: Color): PlayerOptions =
    new TwoPlayerPlayerOptions(playerName, color)

  /**
   * @return the search strategy to use to find the next move.
   */
  final def getSearchStrategy: SearchStrategy[M, B] =
    getSearchable().getSearchStrategy

  /**
   * @return true if it is currently player1's turn.
   */
  final def isPlayer1sTurn: Boolean = player1sTurn

  /**
   * @return true if player2 is a computer player
   */
  override final def getCurrentPlayer: Player =
    if player1sTurn then getPlayers.getPlayer1 else getPlayers.getPlayer2

  /**
   * If called before the end of the game it just returns 0 - same as it does in the case of a tie.
   * @return some measure of how overwhelming the win was. May need to negate based on which player one.
   */
  override def getStrengthOfWin: Int =
    if !getPlayers.anyPlayerWon then
      return 0
    getBoard.getTypicalNumMoves / getNumMoves

  /**
   * this returns a reference to the weights class for editing
   * @return  contains the weights used for computer player1 and 2.
   */
  final def getComputerWeights(): GameWeights = weights_

  /**
   * retract the most recently played move
   * @return  the move which was undone (null returned if no prior move)
   */
  override def undoLastMove(): M =
    val lastMove = getLastMove
    getSearchable().undoInternalMove(lastMove)
    if lastMove != null then
      player1sTurn = lastMove.isPlayer1
    lastMove

  /**
   * Currently online play not available for 2 player games - coming soon!
   * @return false
   */
  override def isOnlinePlayAvailable: Boolean = false

  /**
   * The computer will search for and make its next move.
   * The search for the best computer move happens on a separate thread so the UI does not lock up.
   * @param player1 if true then the computer moving is player1
   * @return the move the computer selected (may return null if no move possible)
   */
  def findComputerMove(player1: Boolean): M =
    player1sTurn = player1

    // we cannot find a computer move if no move played yet.
    if getMoveList.isEmpty then return null.asInstanceOf[M]

    val move = getMoveList.getLastMove
    val lastMove = move.copy().asInstanceOf[M]

    val weights = if player1 then weights_.getPlayer1Weights else weights_.getPlayer2Weights

    if gameTreeViewer_ != null then
      gameTreeViewer_.resetTree(lastMove)

    val selectedMove = getSearchable().searchForNextMove(weights, lastMove, gameTreeViewer_)

    if selectedMove != null then
      makeMove(selectedMove)
      GameContext.log(2, s"computer move :${selectedMove.toString}")

    selectedMove

  /**
   * record the humans move p.
   * @param move the move the player made
   * @return the same move with some of the fields filled in
   */
  final def manMoves(move: M): Move =
    makeMove(move)
    // we pass the default weights because we just need to know if the game is over
    move.setValue(getSearchable().worth(move, weights_.getDefaultWeights))
    move

  /**
   * this makes the specified move (assumed valid) and adds it to the move list.
   * Calling this does not keep track of weights or the search.
   * Its most common use is for browsing the game tree.
   * @param move the move to play.
   */
  override def makeMove(move: M): Unit =
    getSearchable().makeInternalMove(move)
    player1sTurn = !move.isPlayer1

  /**
   * When this method is called the game controller will asynchronously search for the next move
   * for the computer to make. It returns immediately (unless the computer is playing itself).
   * Usually returns false, but will return true if it is a computer vs computer game, and the
   * game is over.
   * @param player1ToMove true if is player one to move.
   * @return true if the game is over.
   * @throws AssertionError thrown if something bad happened while searching.
   */
  def requestComputerMove(player1ToMove: Boolean): Boolean =
    requestComputerMove(player1ToMove, getPlayers.allPlayersComputer)

  /**
   * Request the next computer move. It will be the best move that the computer can find.
   * Launches a separate thread to do the search for the next move so the UI is not blocked.
   * @param player1ToMove true if player one to move.
   * @param synchronous if true then the method does not return until the next move has been found.
   * @return true if the game is over
   * @throws AssertionError if something bad happened while searching.
   */
  def requestComputerMove(player1ToMove: Boolean, synchronous: Boolean): Boolean =
    worker_.requestComputerMove(player1ToMove, synchronous)

  /**
   * Let the computer play against itself for a long time as it optimizes its parameters.
   * @param handler will be called when the optimization is done processing.
   */
  def runOptimization(handler: OptimizationDoneHandler): Unit =
    val optimizer = new Optimizer(this.getOptimizee, new Some(getOptions.getAutoOptimizeFile()))
    val weights = getComputerWeights().getDefaultWeights
    new TwoPlayerOptimizationWorker(optimizer, weights, handler).execute()

  /**
   * @return true if the viewer is currently processing (i.e. searching)
   */
  def isProcessing: Boolean = worker_.isProcessing

  def pause(): Unit =
    if getSearchStrategy == null then
      GameContext.log(1, "There is no search to pause")
      return
    getSearchStrategy.pause()
    GameContext.log(1, "search strategy paused.")

  def isPaused(): Boolean = getSearchStrategy.isPaused

  /**
   * if desired we can set a game tree viewer. If non-null then this
   * will be updated as the search is conducted. The GameTreeDialog
   * is an example of something that implements this interface and can
   * be used to view the game tree as the search is progressing.
   *
   * Here's how the GameTreeDialog is able to show the game tree:
   * When the user indicates that they want to see the GameTreeDialog,
   * the game panel gives the GameTreeDialog to the Controller:
   * controller.setGameTreeViewer( treeDialog_ );
   * Then whenever a move by either party occurs, the GameTreeDialog receives
   * a game tree event. The GameTreeDialog renders the tree that was build up during search.
   * It already has a reference to the root of the tree.
   * If this method is never called, the controller knows
   * that it should not bother to create the tree when searching.
   */
  final def setGameTreeViewable(gameTreeViewable: IGameTreeViewable): Unit =
    gameTreeViewer_ = gameTreeViewable

  override def isDone: Boolean =
    val lastMove = getLastMove
    getSearchable().done(lastMove, false)

  final def getOptimizee: Optimizee =
    new TwoPlayerOptimizee(this)

  def getSearchable(): Searchable[M, B] = synchronized {
    if searchable_ == null then
      searchable_ = createSearchable(getBoard, getPlayers)
    searchable_
  }

  protected def createSearchable(board: B, players: PlayerList): Searchable[M, B]
