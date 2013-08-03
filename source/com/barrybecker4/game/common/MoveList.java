/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common;


import java.util.ArrayList;

/**
 * A list of game moves.
 * What kind of performance difference is there if this is a LinkedList instead of ArrayList?
 *
 * @author Barry Becker
 */
public class MoveList extends ArrayList<Move> {

    private final Object lock = new Object();

    /**
     * Construct set of players
     */
    public MoveList() {}

    /**
     * Copy constructor. Does not make a deep copy.
     * @param list list of moves to initialize with
     */
    public MoveList(MoveList list) {
        super(list);
    }

    /**
     * Copies the constituent moves as well.
     * @return a deep copy of the movelist.
     */
    public MoveList copy() {
        MoveList copiedList = new MoveList();
        synchronized (lock) {
            for (Move m : this) {
                copiedList.add(m.copy());
            }
        }
        return copiedList;
    }

    @Override
    public boolean add(Move m) {
        synchronized (lock) {
           return super.add(m);
        }
    }

    @Override
    public void add(int index, Move move) {
         synchronized (lock) {
           super.add(index, move);
        }
    }

    /**
     *  @return the player that goes first.
     */
    public Move getFirstMove() {
        return get(0);
    }

    public synchronized Move getLastMove() {
        if ( isEmpty() ) {
            return null;
        }
        return get(size()-1);
    }

    public Move removeLast() {
        synchronized (lock) {
           return remove(this.size()-1);
        }
    }

    /**
     * @return  number of active players.
     */
    public int getNumMoves() {
        return size();
    }

    @Override
    public MoveList subList(int first, int last) {
        MoveList subList = new MoveList();
        synchronized (lock) {
            subList.addAll(super.subList(first, last));
        }
        return subList;
    }

    /**
     * @return a random move from the list.
     */
    public Move getRandomMove() {

        return getRandomMove(size());
    }

    /**
     * Randomly get one of the top n moves and ignore the rest.
     * The moves are assumed ordered.
     * @param ofFirstN the first n to choose randomly from.
     * @return a random move from the list.
     */
    public Move getRandomMove(int ofFirstN) {

        int r = GameContext.random().nextInt(Math.min(ofFirstN, size()));
        return get( r );
    }

    /**
     * Randomly get one of the top n moves and ignore the rest.
     * The moves are assumed ordered.
     * @param percentLessThanBestThresh randomly get one of the moves who's score is
     * not more than this percent less that the first..
     * @return a random move from the list.
     */
    public Move getRandomMoveForThresh(int percentLessThanBestThresh) {

        // first find the index of the last move that is still above the thresh
        double thresh = getFirstMove().getValue() * (1.0 - (float)percentLessThanBestThresh/100.0);
        int ct = 1;
        Move currentMove = getFirstMove();
        int numMoves = size();
        while (currentMove.getValue() > thresh && ct < numMoves) {
            currentMove = get(ct++);
        }
        int r = GameContext.random().nextInt(ct);
        return get( r );
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        int ct = 1;
        for (Move m : this) {
            bldr.append(ct++).append(") ").append(m.toString()).append("\n");
        }
        return bldr.toString();
    }
}