// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.pattern;

/**
 * Responsible for Converting pattern strings to an integer.
 *
 * @author Barry Becker
 */
class PatternToIntConverter {

    public PatternToIntConverter() { }

    /**
     * each pattern can be represented as a unique integer.
     * this integer can be used like a hash for a quick lookup of the weight
     * in the weightIndexTable
     * @return integer identifier for pattern.
     */
    int convertPatternToInt( String pattern ) {
        StringBuilder buf = new StringBuilder( pattern );
        return convertPatternToInt( buf, 0, pattern.length()-1 );
    }

    /**
     * Each pattern can be represented as a unique integer.
     * This integer can be used like a hash for a quick lookup of the weight
     * in the weightIndexTable.
     * @return integer representation of pattern
     */
    int convertPatternToInt( CharSequence pattern, int minpos, int maxpos ) {

        int power = 1;
        int sum = 0;

        for ( int i = maxpos; i >= minpos; i-- ) {
            if ( pattern.charAt( i ) != Patterns.UNOCCUPIED )   {
                sum += power;
            }
            // power doubles every step through the loop.
            power <<= 1;
        }
        return sum + power;
    }

    /**
     * Each pattern can be represented as a unique integer.
     * This integer can be used like a hash for a quick lookup of the weight
     * in the weightIndexTable.
     * This version makes sure we have only a single kind of nonempty character.
     * @return integer representation of pattern
     */
    int convertPatternToIntSafe( CharSequence pattern, int minpos, int maxpos ) {

        int power = 1;
        int sum = 0;
        int i = minpos;
        if (pattern.length() == 0) return 1;
        while (pattern.charAt( i ) == Patterns.UNOCCUPIED && i < maxpos)  i++;
        char occupiedSymb = pattern.charAt( i );

        for ( i = maxpos; i >= minpos; i-- ) {
            char c = pattern.charAt( i );
            if ( c != Patterns.UNOCCUPIED )   {
                assert c == occupiedSymb;
                sum += power;
            }
            // power doubles every step through the loop.
            power <<= 1;
        }
        return sum + power;
    }
}
