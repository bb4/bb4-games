/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;
import com.barrybecker4.game.twoplayer.blockade.board.path.Path;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathLengths;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;
import com.barrybecker4.game.twoplayer.blockade.board.path.PlayerPathLengths;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Barry Becker
 */
public class BlockadeBoardTest extends BlockadeTestCase {

    @Test
    public void testPositionStates() {

        BlockadeBoardPosition p = new BlockadeBoardPosition(1, 1);

        assertTrue("no piece or walls", p.getStateIndex() == 0);
        p.setPiece(new GamePiece(true));

        LinkedHashSet<BlockadeBoardPosition> wallPositions =
                new LinkedHashSet<>(2);

        assertTrue("p1 piece and no walls", p.getStateIndex() == 1);

        wallPositions.add(new BlockadeBoardPosition(10, 11));
        wallPositions.add(new BlockadeBoardPosition(10, 12));
        p.setEastWall(new BlockadeWall(wallPositions));
        assertTrue("p1 piece and east wall", p.getStateIndex() == 3);

        wallPositions.clear();
        wallPositions.add(new BlockadeBoardPosition(10, 12));
        wallPositions.add(new BlockadeBoardPosition(11, 12));
        p.setSouthWall(new BlockadeWall(wallPositions));
        assertTrue("p1 piece and both walls", p.getStateIndex() == 7);

        p.setPiece(new GamePiece(false));
        assertTrue("p2 piece and both walls", p.getStateIndex() == 11);
    }

    /**
     * Expected results for possible next move list.
     * For certain locations we expect other than the default.
     * We make a hashMap for these special locations
     * map from location to expected number of moves.
     */
    private static final Map<Location, Integer> P1_NUM_MAP = new HashMap<Location, Integer>() {
        {
             put(new ByteLocation(3, 4), 9);
             put(new ByteLocation(3, 4), 9);
             put(new ByteLocation(3, 8), 9);
             put(new ByteLocation(4, 3), 9);
             put(new ByteLocation(4, 5), 9);
             put(new ByteLocation(4, 9), 9);
             put(new ByteLocation(5, 4), 9);
             put(new ByteLocation(5, 3), 9);
             put(new ByteLocation(5, 7), 6);
             put(new ByteLocation(5, 8), 7);
             put(new ByteLocation(6, 7), 6);
             put(new ByteLocation(6, 8), 6);
             put(new ByteLocation(5, 5), 9);
             put(new ByteLocation(7, 3), 9);
             put(new ByteLocation(7, 5), 9);
             put(new ByteLocation(7, 8), 7);
             put(new ByteLocation(7, 9), 9);
             put(new ByteLocation(8, 3), 9);
             put(new ByteLocation(8, 4), 7);
             put(new ByteLocation(8, 5), 7);
             put(new ByteLocation(8, 6), 6);
             put(new ByteLocation(8, 7), 7);
             put(new ByteLocation(8, 8), 7);
             put(new ByteLocation(8, 9), 9);
             put(new ByteLocation(9, 4), 6);
             put(new ByteLocation(9, 5), 5);
             put(new ByteLocation(9, 6), 6);
             put(new ByteLocation(9, 7), 5);
             put(new ByteLocation(9, 8), 6);
             put(new ByteLocation(10, 3), 8);
             put(new ByteLocation(10, 4), 6);
             put(new ByteLocation(10, 5), 6);
             put(new ByteLocation(10, 7), 7);
             put(new ByteLocation(10, 8), 6);
             put(new ByteLocation(10, 9), 9);
             put(new ByteLocation(10, 10), 7);
             put(new ByteLocation(11, 5), 7);
             put(new ByteLocation(11, 7), 6);
             put(new ByteLocation(11, 8), 6);
             put(new ByteLocation(12, 7), 7);
             put(new ByteLocation(12, 8), 7);
             put(new ByteLocation(13, 10), 6);
         }
    };


    /**
     * Test the list of candidate next moves.
     */
    @Test
    public void testPossibleMoveList() throws Exception {

         restore("whitebox/moveList1");
         BlockadeBoard board = (BlockadeBoard)controller_.getBoard();

         // for each position on the board. determine the possible move list for each player
         int numRows = board.getNumRows();
         int numCols = board.getNumCols();
         for ( int row = 1; row <= numRows; row++ ) {
             for ( int col = 1; col <= numCols; col++ ) {
                  BlockadeBoardPosition position = board.getPosition(row, col);
                  List list1 = board.getPossibleMoveList(position, false);
                  //List list2 = board.getPossibleMoveList( position, true);

                  if (board.isOnEdge(position)) {

                      if (board.isInCorner(position)) {
                           // if in corner, we expect 3 moves
                          verifyMoves(position, list1,  3, P1_NUM_MAP);
                      }
                      else if (row == 2 || col == 2 || (row == numRows-1) || (col == numCols-1)) {
                          // if on edge and one space to corner, we expect 4 moves
                          verifyMoves(position, list1, 4, P1_NUM_MAP);
                      }
                      else {
                           // if the pos is at the edge we expect 6 moves
                          verifyMoves(position, list1,  5, P1_NUM_MAP);
                      }
                  }
                  else if (row == 2 || col == 2 || (row == numRows-1) || (col == numCols-1)) {
                      if (row == col || (row == 2 && col == numCols-1) || (col == 2 && row == numRows-1)) {
                           // if one space out from order we expect 6 moves
                          verifyMoves(position, list1, 6, P1_NUM_MAP);
                      }
                      else {
                           // if the pos is one space from the edge (but not in corner) we expect 7 moves,
                           verifyMoves(position, list1, 7, P1_NUM_MAP);
                      }
                  }
                  else {
                      verifyMoves(position, list1, 8, P1_NUM_MAP);
                  }
              }
         }
    }

    /**
     * confirm expected moves against actuals.
     */
    private static void verifyMoves(BoardPosition position, List player1Moves,
                                    int expectedNumMoves, Map<Location, Integer> p1NumMap) {

        if (p1NumMap.containsKey(position.getLocation())) {
             expectedNumMoves = p1NumMap.get(position.getLocation());
        }
        //if (player1Moves.size() != expectedNumMoves) {
        //    GameContext.log(1, "Expected "+expectedNumMoves+" moves for player1, but got "+player1Moves.size() +":" + player1Moves);
        //}
        assertTrue("Expected " + expectedNumMoves + " moves for player1, but got " + player1Moves.size()
                + ":" + player1Moves, player1Moves.size() == expectedNumMoves);
    }

    private static final BlockadeMove[][] moves1 =  {
        {
            createMove(6,4, 8,4, new GamePiece(false)),
            createMove(8,4, 10,4,  null),
            createMove(10,4,  11,5, null),
            createMove(11,5,  11,7, null),
            createMove(11,7,  11,8, null),
        },
        {
            createMove(6,4,  8,4,  new GamePiece(false)),
            createMove(8,4,  10,4, null),
            createMove(10,4,  10,2, null),
            createMove(10,2,  11,3, null),
            createMove(11,3,  11,4, null),
        },
        {
            createMove(8,8,  10,8,  new GamePiece(false)),
            createMove(10,8,  11,9, null),
            createMove(11,9,  11,8, null),
        },
        {
            createMove(8,8,  10,8, new GamePiece(false)),
            createMove(10,8,  10,6, null),
            createMove(10,6,  12,6, null),
            createMove(12,6,  13,5, null),
            createMove(13,5,  12,4, null),
            createMove(12,4,  11,4, null),
        },
    };


    private static final BlockadeMove[][] moves2 =  {
        {
            createMove(8,3,  6,3, new GamePiece(true)),
            createMove(6,3,  5,4, null),
            createMove(5,4,  4,4, null),
        },
        {
            createMove(8,3,  6,3, new GamePiece(true)),
            createMove(6,3,  5,4, null),
            createMove(5,4,  4,4, null),
            createMove(4,4,  4,6, null),
            createMove(4,6,  4,8, null),
        },
        {
            createMove(9,8,  8,9, new GamePiece(true)),
            createMove(8,9,  6,9, null),
            createMove(6,9,  4,9, null),
            createMove(4,9,  4,8, null),
        },
        {
            createMove(9,8,  7,8, new GamePiece(true)),
            createMove(7,8,  7,6, null),
            createMove(7,6,  7,4, null),
            createMove(7,4,  5,4, null),
            createMove(5,4,  4,4, null),
        }
    };

    private static BlockadeMove createMove(int r1, int c1, int r2, int c2, GamePiece piece) {
        return new BlockadeMove(new ByteLocation(r1, c1), new ByteLocation(r2, c2), 0, piece, null);
    }

    private static final Path[] EXPECTED_P1_PATHS = {
       new Path(moves1[0]), new Path(moves1[1]), new Path(moves1[2]), new Path(moves1[3])
    };

    private static final Path[] EXPECTED_P2_PATHS = {
       new Path(moves2[0]), new Path(moves2[1]), new Path(moves2[2]), new Path(moves2[3])
    };


    /**
     * Test that we can accurately determine all the opponent shortest paths.
     */
    @Test
    public void testFindOpponentShortestPaths() throws Exception {
         restore("whitebox/shortestPaths1");
         BlockadeBoard board = (BlockadeBoard)controller_.getBoard();

         PathList p1Paths = board.findAllOpponentShortestPaths(true);
         PathList p2Paths = board.findAllOpponentShortestPaths(false);

         // use to get expected results.
         //printPathCreator(p1Paths);
         //printPathCreator(p2Paths);

         // first check that we have the right overall number of paths
         assertEquals("Unexpected number of paths", 4, p1Paths.size());
         assertEquals("Unexpected number of paths", 4, p2Paths.size());

         // now check that we have exactly the expected paths
         String p1PathDiff = pathListDiff(EXPECTED_P1_PATHS, p1Paths);
         String p2PathDiff = pathListDiff(EXPECTED_P2_PATHS, p2Paths);
         assertTrue("p1Path difference=\n" + p1PathDiff, p1PathDiff.length() == 0);
         assertTrue("p2Path difference=\n" + p2PathDiff, p2PathDiff.length() == 0);
    }

    private void printPathCreator(PathList paths) {
        System.out.println("{");
        for (Path p : paths) {
            System.out.println("{");
            int len = p.getLength();
            for (int i=0; i<len; i++) {
                BlockadeMove move = p.get(i);
                System.out.println("    " + move.getConstructorString());
                //if (i < len -1)
                //    System.out.println(", ");
            }
            System.out.println("},");
        }
        System.out.println("}");
    }

    /**
     * @return "" if no difference or the list of differences.
     * (there will be *** after those that do not match)
     */
    private String pathListDiff(Path[] expectedPaths, PathList actualPaths) {
        boolean pathListsDifferent = false;
        StringBuilder diffs= new StringBuilder("Paths compared: \n");

        for (int i = 0; i < expectedPaths.length; i++) {
            Path ep = expectedPaths[i];
            Path ap = actualPaths.get(i);

            String diffMarker = ep.equals(ap) ? "" : "***";
            diffs.append("expected:  ").append(ep).append("actual  :  ").append(ap);
            if (!ep.equals(ap)) {
                diffs.append("   ").append(diffMarker).append(" \n");
                pathListsDifferent = true;
            }
        }
        return pathListsDifferent ? diffs.toString() : "";
    }

    @Test
    public void testShortestPathLength() throws Exception {
         restore("whitebox/noMoves2");
         BlockadeBoard board = (BlockadeBoard)controller_.getBoard();

         PlayerPathLengths pLengths = board.findPlayerPathLengths();
         GameContext.log(1, pLengths.toString());

         PathLengths expectedP1Lengths = new PathLengths(4, 6, 12);
         PathLengths expectedP2Lengths = new PathLengths(3, 3, 10);

         PathLengths actualP1Lengths = pLengths.getPathLengthsForPlayer(true);
         PathLengths actualP2Lengths = pLengths.getPathLengthsForPlayer(false);

         assertTrue("Unexpected Player1 Path lengths - " + actualP1Lengths, expectedP1Lengths.equals(actualP1Lengths));
         assertTrue("Unexpected Player2 Path lengths - " + actualP2Lengths, expectedP2Lengths.equals(actualP2Lengths));
    }

    @Test
    public void testShortestPaths2() throws Exception {

         restore("whitebox/noMoves2");
         BlockadeBoard board = (BlockadeBoard)controller_.getBoard();

         GamePiece piece1 = new GamePiece(true); // player 1
         GamePiece piece2 = new GamePiece(false);  // player 2
         BlockadeWall wall1 = new BlockadeWall(board.getPosition(8, 10), board.getPosition(9, 10));
         BlockadeWall wall2 = new BlockadeWall(board.getPosition(12, 6), board.getPosition(12, 7));

         BlockadeMove move1 = BlockadeMove.createMove(new ByteLocation(8, 11), new ByteLocation(6, 11), 1 /*0.1*/, piece2, wall2);
         BlockadeMove move2 = BlockadeMove.createMove(new ByteLocation(12, 6), new ByteLocation(10, 6), 1 /*0.1*/, piece1, wall1);

         controller_.makeMove(move1);
         controller_.makeMove(move2);

         PlayerPathLengths pLengths = board.findPlayerPathLengths();
         GameContext.log(1, pLengths.toString());

         PathLengths expectedP1Lengths = new PathLengths(7, 9, 12);
         PathLengths expectedP2Lengths = new PathLengths(4, 6, 10);

         PathLengths actualP1Lengths = pLengths.getPathLengthsForPlayer(true);
         PathLengths actualP2Lengths = pLengths.getPathLengthsForPlayer(false);

         assertTrue("Unexpected Player1 Path lengths - " + actualP1Lengths, expectedP1Lengths.equals(actualP1Lengths));
         assertTrue("Unexpected Player2 Path lengths - " + actualP2Lengths, expectedP2Lengths.equals(actualP2Lengths));
    }


     private static final int[] EXPECTED_PATHS_LENGTHS = { 0,  11, 12};

    @Test
    public void testFindShortestPaths() throws Exception {
         restore("whitebox/shortestPathsCheck");
         BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
         //BlockadeMove lastMove = (BlockadeMove) controller.getMoveList().getLast();

         BlockadeBoardPosition pos1 = board.getPosition(2, 2);
         BlockadeBoardPosition pos2 = board.getPosition(5, 2);

         PathList pLengths1 = board.findShortestPaths(pos1);
         PathList pLengths2 = board.findShortestPaths(pos2);
         //GameContext.log(2, "paths for "+pos1+ " are = "+ pLengths1);
         //GameContext.log(2, "pLengths2 = "+ pLengths2);

         int size = EXPECTED_PATHS_LENGTHS.length;
         int[] lengths = new int[size];
         assertTrue("Unexpected number of pLengths1=" + pLengths1.size(), pLengths1.size() == 1);
         assertTrue("Unexpected number of pLengths2=" + pLengths2.size(), pLengths2.size() == 2);

         lengths[0] = pLengths1.get(0).getLength();
         lengths[1] = pLengths2.get(0).getLength();
         lengths[2] = pLengths2.get(1).getLength();

         for (int i=0; i<size; i++) {
             assertTrue("Expected len " + EXPECTED_PATHS_LENGTHS[i] + " but got  " + lengths[i], lengths[i] == EXPECTED_PATHS_LENGTHS[i]);
         }
    }
}
