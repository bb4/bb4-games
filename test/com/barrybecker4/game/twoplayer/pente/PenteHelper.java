/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente;

import com.barrybecker4.game.twoplayer.common.search.SearchableHelper;

/**
 * @author Barry Becker
 */
public class PenteHelper extends SearchableHelper {

    @Override
    public PenteController createController() {
        return new PenteController(10, 10);
    }

    @Override
    public String getTestCaseDir() {
        return EXTERNAL_TEST_CASE_DIR + "pente/cases/searchable/";
    }

    @Override
    protected String getStartGameMoveFileName(boolean player1) {
        return player1 ? "x" : "y";
    }

    @Override
    protected String getMiddleGameMoveFileName(boolean player1) {
        return player1 ? "x" : "y";
    }

    @Override
    protected String getEndGameMoveFileName(boolean player1) {
        return player1 ? "x" : "y";
    }
}