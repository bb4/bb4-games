/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.common.concurrency.Worker;
import com.barrybecker4.game.common.Move;

/**
 * Searches for the next computer move in a separate thread.
 *
 * @author Barry Becker
 */
class TwoPlayerSearchWorker {

    private TwoPlayerController controller_;

    /** Worker represents a separate thread for computing the next move. */
    private Worker worker_;

    /** this is true while the computer thinks about its next move. */
    private boolean processing_ = false;


    /**
     * Construct the search worker.
     */
    public TwoPlayerSearchWorker(TwoPlayerController controller) {
        controller_ = controller;
    }

    /**
     * Apply whatever the best move is that we have found so far - even though we are not done.
     */
    public void interrupt() {
        if (isProcessing()) {
            controller_.pause();
            if (worker_ != null) {
                worker_.interrupt();
                processing_ = false;
                // make the move even though we did not finish computing it
                Move move = (Move)worker_.get();
                if (move != null) {
                    controller_.getTwoPlayerViewer().computerMoved(move);
                }
            }
            ThreadUtil.sleep(100);
        }
    }

    /**
     * Request the next computer move. It will be the best move that the computer can find.
     * Launches a separate thread to do the search for the next move.
     * @param isPlayer1 true if player one to move.
     * @param synchronous if true then the method does not return until the next move has been found.
     * @return true if the game is over
     * @throws AssertionError  if something bad happened while searching.
     */
     public boolean requestComputerMove(final boolean isPlayer1, final boolean synchronous) throws AssertionError {

         worker_ = new Worker() {

             private Move move_;

             @Override
             public Object construct() {
                 processing_ = true;

                 move_ = controller_.findComputerMove( isPlayer1 );

                 return move_;
             }

              @Override
              public void finished() {

                  // move_ could be null if there was no legal move
                  if (controller_.getTwoPlayerViewer() != null)  {
                      controller_.getTwoPlayerViewer().computerMoved(move_);
                  }
                  processing_ = false;
              }
         };

         worker_.start();

         if (synchronous) {

             // this blocks until the value is available
             TwoPlayerMove m = (TwoPlayerMove)worker_.get();
             boolean d = controller_.getSearchable().done(m, true);
             return d;
         }
         return false;
     }

    /**
     *  @return true if the viewer is currently processing (i.e. searching)
     */
    public boolean isProcessing() {
        return processing_;
    }
}