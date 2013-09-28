// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.analysis.differencers;

import com.barrybecker4.game.twoplayer.common.search.SearchableHelper;
import com.barrybecker4.game.twoplayer.pente.PenteBoard;
import com.barrybecker4.game.twoplayer.pente.PenteController;
import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;
import com.barrybecker4.game.twoplayer.pente.pattern.PentePatterns;
import com.barrybecker4.game.twoplayer.pente.pattern.SimpleWeights;
import com.barrybecker4.optimization.parameter.ParameterArray;
import org.junit.Before;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;


/**
 * Base class for all differencer tests.
 *
 * @author Barry Becker
 */
public abstract class ValueDifferencerTst {

    private static final String TEST_CASE_DIR =
           SearchableHelper.EXTERNAL_TEST_CASE_DIR + "pente/cases/analysis/differencers/";

    /** instance under test */
    protected ValueDifferencer differencer;
    protected ValueDifferencerFactory differencerFactory;
    protected ParameterArray weights = new SimpleWeights().getDefaultWeights();
    protected StubLineFactory lineFactory;


    @Before
    public void setUp() throws Exception {
        PenteBoard board = restoreBoard("exampleBoard");
        Patterns patterns = new PentePatterns();
        lineFactory = new StubLineFactory();
        differencerFactory = new ValueDifferencerFactory(patterns, lineFactory);
        differencer = createDifferencer(board, patterns);
    }

    protected abstract ValueDifferencer createDifferencer(PenteBoard board, Patterns patterns);

    /**
     * Restore a game file
     * @param problemFileBase the saved game to restore and test.
     */
    private PenteBoard restoreBoard(String problemFileBase) throws Exception {
        PenteController controller = new PenteController();
        String path = TEST_CASE_DIR + problemFileBase + ".sgf";
        InputStream iStream = getClass().getResourceAsStream(path);
        if (iStream == null)
            throw new IllegalArgumentException("bad path: " + path);

        controller.restoreFromStream(getClass().getResourceAsStream(path));
        return (PenteBoard) controller.getBoard();
    }

    protected void verifyLine(int row, int col, String expectedLineContent) {
        int diff = differencer.findValueDifference(row, col, weights);
        assertEquals("Unexpected difference", 0, diff);
        assertEquals("Unexpected line created", expectedLineContent, lineFactory.getLineContent());
    }
}