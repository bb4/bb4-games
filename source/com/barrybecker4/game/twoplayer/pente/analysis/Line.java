/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente.analysis;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;

/**
 * Represents a run of symbols to be evaluated on the board.
 * All the symbols in the line should be of the same side.
 * @author Barry Becker
 */
public class Line {

    /** contains the symbols in the line (run) */
    protected StringBuilder line;
    private LineEvaluator evaluator;

    /**
     * Constructor
     * @param evaluator used to evaluate the score for the line.
     */
    public Line(LineEvaluator evaluator) {
        this.evaluator = evaluator;
        line = new StringBuilder();
    }

    /**
     * Extend the line by an additional position.
     * @param pos the position to extend ourselves by.
     */
    public void append(BoardPosition pos) {
        assert (pos != null): "Cannot append at null board position.";
        if ( pos.getPiece() == null )  {
            line.append( Patterns.UNOCCUPIED );
        }
        else {
            char symb = pos.getPiece().getSymbol();
            line.append( symb );
        }
    }

    /**
     * We return the difference in value between how the board looked before the
     * move was played (from both points of view) to after the move was played
     * (from both points of view. It's important that we look at it from both
     * sides because creating a near win is noticed from the moving players point of view
     * while blocks are noted from the opposing viewpoint.
     *
     * @param position position in the string to compute value difference for.
     * @return the difference in worth after making a move compared with before.
     *
     */
    public int computeValueDifference(int position) {

        char symb = line.charAt( position );
        assert symb != Patterns.UNOCCUPIED:
            "The piece just played at position "+ position + " in line "+ line +" must be a symbol.";
        boolean player1Perspective = (symb == GamePiece.P1_SYMB);

        int len = line.length();
        if ( len < evaluator.getMinInterestingLength() ) {
            return 0; // not an interesting pattern.
        }

        line.setCharAt( position, Patterns.UNOCCUPIED );
        int maxpos = len - 1;

        int oldScore = evaluator.evaluate(line, player1Perspective, position, 0, maxpos);
        oldScore += evaluator.evaluate(line, !player1Perspective, position, 0, maxpos);

        line.setCharAt( position, symb );
        int newScore = evaluator.evaluate(line, player1Perspective, position, 0, maxpos);
        newScore += evaluator.evaluate(line, !player1Perspective, position, 0, maxpos);

        return (newScore - oldScore);
    }

    /**
     * debugging aid
     */
    public void worthDebug( String dir, int pos, int diff ) {
        GameContext.log( 0,  dir + " "  + line + "  Pos: " + pos + "  difference:" + diff );
    }

    /** provide access to the evaluator for unit tests. */
    LineEvaluator getLineEvaluator() {
        return evaluator;
    }

    @Override
    public String toString() {
        return line.toString();
    }
}
