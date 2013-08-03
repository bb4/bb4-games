// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.analysis;

import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;

/**
 * Extract a pattern from a line of symbols.
 * A pattern consists of a sequence of friendly and unoccupied positions.
 *
 * @author Barry Becker
 */
public class PatternExtractor {

    private static final int BACK = -1;
    private static final int FORWARD = 1;

    private CharSequence line;

    /**
     * Constructor
     * @param line the line to extract patterns from.
     */
    public PatternExtractor(CharSequence line) {
        this.line = line;
    }

    /**
     * Given the opponent symbol, find the pattern in the specified potion of the line.
     */
    protected CharSequence getPattern(char opponentSymb, int pos, int minpos, int maxpos) {

        if (line.charAt(pos) == opponentSymb) {
            // assert false :" line="+ line +" pos = " + pos;
            return "";
        }
        int start = getEndPosition(line, opponentSymb, pos, minpos, BACK);
        int stop = getEndPosition(line, opponentSymb, pos, maxpos, FORWARD);
        return line.subSequence(start, stop + 1);
    }

    /**
     * March in the direction specified until we hit 2 blanks, an opponent piece,
     * or the end of the line.
     * @return end position
     */
    protected int getEndPosition(CharSequence line, char opponentSymb, int pos, int extremePos, int direction) {
        int end;
        end = pos;
        if ( (line.charAt( pos ) == opponentSymb) && (pos == extremePos) )  {
            end -= direction;
        }
        else {
            while ( (direction * end < direction * extremePos) && (line.charAt( end + direction ) != opponentSymb)
                  && !next2Unoccupied(line, end, direction) ) {
                end += direction;
            }
        }
        return end;
    }

    private boolean next2Unoccupied(CharSequence line, int position, int dir) {
        return (line.charAt( position ) == Patterns.UNOCCUPIED
             && line.charAt( position + dir ) == Patterns.UNOCCUPIED);
    }
}
