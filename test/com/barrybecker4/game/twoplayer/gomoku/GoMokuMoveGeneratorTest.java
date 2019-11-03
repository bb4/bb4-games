/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.barrybecker4.optimization.parameter.ParameterArray;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @author Barry Becker
 */
public class GoMokuMoveGeneratorTest {

    /** instance under test */
    private GoMokuMoveGenerator generator;

    private GoMokuHelper helper;
    private GoMokuController controller;

    private ParameterArray weights;

    @Before
    public void setUp() {

        helper = new GoMokuHelper();
        controller = helper.createController();

        BestMovesSearchOptions bmOptions =
                controller.getSearchable().getSearchOptions().getBestMovesSearchOptions();
        bmOptions.setMinBestMoves(1);
        bmOptions.setPercentageBestMoves(50);  // take better half
        bmOptions.setPercentLessThanBestThresh(50); // include those greater than 50% of best value

        weights = controller.getComputerWeights().getDefaultWeights();
        generator = new GoMokuMoveGenerator();
    }

    /** The center position should be the only generated move. */
    @Test
    public void testNextMoveWhenNoMovesOnBoard() throws Exception {

        MoveList<TwoPlayerMove> actMoves = generator.generateMoves(
                controller.getSearchable(), (TwoPlayerMove) controller.getLastMove(), weights);

        MoveList<TwoPlayerMove> expMoves = new MoveList<>();
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(6, 6), 0, new GamePiece(true)));

        assertEquals("Unexpected first move.", expMoves, actMoves);
    }

    @Test
    public void testGeneratedMovesSomeMovesAlready() throws Exception {
        restore("midGameP1ToPlay");

        MoveList<TwoPlayerMove> actMoves = generator.generateMoves(
                controller.getSearchable(), (TwoPlayerMove) controller.getLastMove(), weights);

        System.out.println("move=" + printMoves(actMoves));
        MoveList<TwoPlayerMove> expMoves = new MoveList<>();
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(13, 10), 0, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(9, 13), -14, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(11, 13), -24, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(9, 9), -24, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(11, 10), -26, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(10, 13), -30, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(13, 12), -32, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(8, 10), -32, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(13, 9), -40, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(12, 12), -40, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(11, 8), -40, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(7, 12), -40, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(13, 8), -42, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(14, 10), -48, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(11, 7), -48, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(10, 12), -48, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(10, 9), -48, new GamePiece(true)));
        //expMoves.add(TwoPlayerMove.createMove(new ByteLocation(10, 8), -48, new GamePiece(true)));
        //expMoves.add(TwoPlayerMove.createMove(new ByteLocation(8, 14), -48, new GamePiece(true)));
        //expMoves.add(TwoPlayerMove.createMove(new ByteLocation(8, 13), -48, new GamePiece(true)));
        //expMoves.add(TwoPlayerMove.createMove(new ByteLocation(7, 14), -48, new GamePiece(true)));
        //expMoves.add(TwoPlayerMove.createMove(new ByteLocation(7, 11), -48, new GamePiece(true)));
        //expMoves.add(TwoPlayerMove.createMove(new ByteLocation(6, 13), -48, new GamePiece(true)));
        //expMoves.add(TwoPlayerMove.createMove(new ByteLocation(12, 13), -54, new GamePiece(true)));

        assertEquals("Unexpected moves.", expMoves, actMoves);
    }

    @Test
    public void testGeneratedMovesAfterFirstMove() throws Exception {
        restore("p2ToPlayAfterFirstMove");

        MoveList<TwoPlayerMove> actMoves = generator.generateMoves(
                controller.getSearchable(), (TwoPlayerMove) controller.getLastMove(), weights);

        MoveList<TwoPlayerMove> expMoves = new MoveList<>();
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, new GamePiece(false)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(1, 3), 0, new GamePiece(false)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, new GamePiece(false)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(2, 3), 0, new GamePiece(false)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(3, 1), 0, new GamePiece(false)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(3, 2), 0, new GamePiece(false)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(3, 3), 0, new GamePiece(false)));

        assertEquals("Unexpected moves.", expMoves, actMoves);
    }

    @Test
    public void testGeneratedMovesAfterSecondMove() throws Exception {
        restore("p1ToPlayAfterSecondMove");

        MoveList<TwoPlayerMove> actMoves = generator.generateMoves(
                controller.getSearchable(), (TwoPlayerMove) controller.getLastMove(), weights);

        MoveList<TwoPlayerMove> expMoves = new MoveList<>();
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(4, 4), 0, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(4, 3), 0, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(4, 2), 0, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(2, 4), 8, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(2, 3), 8, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(2, 2), 8, new GamePiece(true)));

        assertEquals("Unexpected moves.", expMoves, actMoves);
    }

    @Test
    public void testGeneratedMovesMidGame() throws Exception {

        // make sure we get all legal reasonable moves
        BestMovesSearchOptions bmOptions = controller.getSearchable().getSearchOptions().getBestMovesSearchOptions();
        bmOptions.setMinBestMoves(100);
        bmOptions.setPercentageBestMoves(100);
        bmOptions.setPercentLessThanBestThresh(0);

        restore("../analysis/differencers/exampleBoard");

        MoveList<TwoPlayerMove> actMoves = generator.generateMoves(
                controller.getSearchable(), (TwoPlayerMove) controller.getLastMove(), weights);

        MoveList<TwoPlayerMove> expMoves = new MoveList<>();
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(5, 3), 4597, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(5, 8), 4565, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(8, 2), 46, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(7, 3), 46, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(7, 2), 38, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(9, 3), 30, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(8, 8), 30, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(8, 4), 30, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(6, 7), 28, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(6, 6), 22, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(2, 3), 22, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(9, 4), 14, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(4, 5), 14, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(3, 3), 14, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(7, 7), 10, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(3, 5), 10, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(8, 1), 8, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(7, 5), 8, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(10, 3), 6, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(7, 8), 6, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(4, 4), 6, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(4, 3), 6, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(4, 2), 6, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(3, 8), 6, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(2, 5), 6, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(1, 5), 6, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(1, 3), 6, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(7, 6), 2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(9, 8), 0, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(9, 7), 0, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(9, 5), 0, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(6, 1), 0, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(10, 8), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(10, 6), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(10, 2), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(10, 1), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(9, 6), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(9, 1), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(6, 8), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(6, 2), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(5, 1), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(4, 8), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(4, 7), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(4, 6), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(4, 1), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(2, 8), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(2, 7), -2, new GamePiece(true)));
        expMoves.add(TwoPlayerMove.createMove(new ByteLocation(2, 6), -2, new GamePiece(true)));

        assertEquals("Unexpected moves. act=\n" + printMoves(actMoves),
                expMoves, actMoves);
    }

    protected void restore(String problemFileBase) throws Exception {
        controller.restoreFromStream(helper.getTestResource(problemFileBase));
    }

    protected String printMoves(MoveList<TwoPlayerMove> moves) {
        StringBuilder bldr = new StringBuilder();
        for (TwoPlayerMove m : moves) {
            bldr.append("expMoves.add(");
            bldr.append((m).getConstructorString());
            bldr.append(");\n");
        }
        return bldr.toString();
    }

}
