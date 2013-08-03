/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.path;

/**
 * Holds the the different paths lengths for a given player.
 *
 * @author Barry Becker
 */
public class PathLengths {

    int shortestLength = Integer.MAX_VALUE;
    int secondShortestLength = Integer.MAX_VALUE;
    int furthestLength = 0;

    private boolean isValid = true;

    /**
     * Default constructor
     */
    public PathLengths() {}

    /**
     * Constructor for creating expected object for testing.
     */
    public PathLengths(int shortest, int secondShortest, int furthest) {
        shortestLength = shortest;
        secondShortestLength = secondShortest;
        furthestLength = furthest;
    }

    /**
     * Update the values of the shortest, secondShortest and furthest.
     * @param paths paths to update lengths of.
     */
    public void updatePathLengths(PathList paths) {
        // if we don't have NUM_HOMES paths then this set of path lengths is invalid.
        // probably the move and corresponding wall placement was not valid, or we landed on a home.
        if (paths.isValid()) {
            isValid = false;
            return;
        }
        for (final Path path : paths) {
            int len = path.getLength();
            if (len < shortestLength) {
                secondShortestLength = shortestLength;
                shortestLength = len;
            } else if (len < secondShortestLength) {
                secondShortestLength = len;
            }

            if (len > furthestLength) {
                furthestLength = len;
            }
        }
    }

    /**
     *There must be at least one path from every pawn to every opponent base.
     *@return false if there are not enough paths.
     */
    public boolean isValid() {
        return isValid;
    }

    @Override
    public boolean equals(Object o) {
        PathLengths compPathLengths = (PathLengths) o;
        return (this.shortestLength == compPathLengths.shortestLength
                && this.secondShortestLength == compPathLengths.secondShortestLength
                && this.furthestLength == compPathLengths.furthestLength);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.shortestLength;
        hash = 97 * hash + this.secondShortestLength;
        hash = 97 * hash + this.furthestLength;
        hash = 97 * hash + (this.isValid ? 1 : 0);
        return hash;
    }

    /**
     * Serialize.
     */
    @Override
    public String toString() {
       return "shortestLength=" + shortestLength+
               " secondShortestLength =" + secondShortestLength+
               " furthestLength=" + furthestLength;
    }
}
