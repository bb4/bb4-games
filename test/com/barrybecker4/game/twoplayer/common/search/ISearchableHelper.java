/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search;

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;

import java.io.InputStream;

/**
 * @author Barry Becker
 */
public interface ISearchableHelper {

    /**
     * Create the game options
     * @return 2 player options for use when testing..
     */
    SearchOptions createSearchOptions();

    /**
     * @return the controller containing the searchable to test.
     */
    TwoPlayerController<TwoPlayerMove, TwoPlayerBoard<TwoPlayerMove>> createController();

    /**
     * @return test file containing state of saved game to restore.
     */
    InputStream getTestResource(String problemFileBase);

    /**
     * @return test file location containing state of saved game to restore.
     */
    String getTestResourceLocation(String problemFileBase);

    /**
     * @param progress how far into the game are we.
     * @param player1 true if player one has just played.
     * @return get the game file corresponding to the given amount of progress and the specified player.
     */
    InputStream getTestResource(Progress progress, boolean player1);
}
