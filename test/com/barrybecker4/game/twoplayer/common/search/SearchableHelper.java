/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search;

import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;

import java.io.InputStream;

/**
 * Helper methods to aid in testing two player searchables.
 * @author Barry Becker
 */
public abstract class SearchableHelper implements ISearchableHelper {

    /** moved all test cases here so they are not included in the jar and do not need to be searched */
    public static final String EXTERNAL_TEST_CASE_DIR = "/com/barrybecker4/game/twoplayer/";

    private static final String SGF_EXTENSION = ".sgf";

    @Override
    public SearchOptions createSearchOptions() {
        return new SearchOptions();
    }

    @Override
    public InputStream getTestResource(String problemFileBase) {
        return getClass().getResourceAsStream(getTestResourceLocation(problemFileBase));
    }

    @Override
    public String getTestResourceLocation(String problemFileBase) {
        return getTestCaseDir() + problemFileBase + SGF_EXTENSION;
    }

    @Override
    public InputStream getTestResource(Progress progress, boolean player1) {
        String fName = null;
        switch (progress) {
            case BEGINNING : fName = getStartGameMoveFileName(player1);
                break;
            case MIDDLE : fName = getMiddleGameMoveFileName(player1);
                break;
            case END : fName = getEndGameMoveFileName(player1);
                break;
        }
        return getTestResource(fName);
    }

    protected abstract String getStartGameMoveFileName(boolean player1);
    protected abstract String getMiddleGameMoveFileName(boolean player1);
    protected abstract String getEndGameMoveFileName(boolean player1);


    /**
     * @return directory location of test files.
     */
    protected abstract String getTestCaseDir();
}
