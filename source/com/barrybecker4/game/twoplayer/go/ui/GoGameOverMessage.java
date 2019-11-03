/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.ui.GameOverMessage;
import com.barrybecker4.game.twoplayer.go.GoController;
import com.barrybecker4.game.twoplayer.go.board.GoSearchable;

/**
 *  Takes a GoController as input and displays the
 *  current state of the Go Game. The GoController contains a GoBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
final class GoGameOverMessage extends GameOverMessage {

    private static final String STONES_CAPTURED = GameContext.getLabel("CAPTURES_EQUALS");
    private static final String TERRITORY = GameContext.getLabel("TERRITORY_EQUALS");
    private static final String SCORE = GameContext.getLabel("SCORE_EQUALS");


    /**
     * Constructor
     */
    public GoGameOverMessage(GoController controller) {
         super(controller);
    }


    /**
     * @return   the message to display at the completion of the game.
     */
    @Override
    public String getText() {

        GoController gc = (GoController) controller;

        String message = "\n";

        GoSearchable searchable = (GoSearchable) controller.getSearchable();
        int blackCaptures = searchable.getNumCaptures(true) + searchable.getNumDeadStonesOnBoard(true);
        int whiteCaptures = searchable.getNumCaptures(false) + searchable.getNumDeadStonesOnBoard(false);

        String p1Name = gc.getPlayers().getPlayer1().getName();
        String p2Name = gc.getPlayers().getPlayer2().getName();

        message += p1Name + ' ' + STONES_CAPTURED + blackCaptures + '\n';
        message += p2Name + ' ' + STONES_CAPTURED + whiteCaptures + "\n\n";

        int blackTerritory = gc.getFinalTerritory(true);
        int whiteTerritory = gc.getFinalTerritory(false);
        message += p1Name + ' ' + TERRITORY + blackTerritory +'\n';
        message += p2Name + ' ' + TERRITORY + whiteTerritory +"\n\n";

        message += p1Name + ' ' + SCORE + gc.getFinalScore(true) +'\n';
        message += p2Name + ' ' + SCORE + gc.getFinalScore(false) +'\n';

        return super.getText() +'\n'+ message;
    }

}
