/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.persistence.tokens;

import com.barrybecker4.ca.dj.jigo.sgf.Point;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.TwoPlayerMoveToken;

import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * A generic blockade move.
 * The superclass forPlayer1MoveToken and Player2MoveToken.
 */
public abstract class BlockadeMoveToken extends TwoPlayerMoveToken
{
    private Point fromPoint = new Point();

    private Point wallPoint1 = null;
    private Point wallPoint2 = null;

    /**
     * A token the describes where a players pawn started and where it ended after moving.
     */
    BlockadeMoveToken() { }

    /**
     * Parse in the wall locations.
     */
    @Override
   protected boolean parseContent( StreamTokenizer st )  throws IOException
   {
       boolean parsed = parsePoint( st, fromPoint );
       if( st.nextToken() != (int)'[' )
           return false;
       parsed = (parsed && parsePoint(st, toPoint));
       if( st.nextToken() !=  StreamTokenizer.TT_WORD)
           return false;
       if (st.sval.equals("wall"))
          parseWall(st);
       return parsed;
   }

  /**
   * Parses a blockade wall, sets the walls 2 points accordingly.
   * <P>
   * The first opening '[' must have already been read; thus leaving two
   * letters and a closing ']'.  This method reads everything up to and
   * including ']'.  If a ']' follows immediately after the '[', then this
   * move is considered a pass in FF[4].
   * <P>
   * The letters are from 'a' through 'Z', inclusive, to represent row
   * (or column) 1 through 52, respectfully.
   *
   * Returns:
   *   true - The wall was perfectly parsed.
   *   false - The wall wasn't perfectly parsed.
   */
  boolean parseWall( StreamTokenizer st)  throws IOException
  {
        if( st.nextToken() != (int)'[' )
            return false;
        int token = st.nextToken();

        wallPoint1 = new Point();
        wallPoint1.x =  ( coordFromChar( st.sval.charAt( 0 ) ) );
        wallPoint1.y = ( coordFromChar( st.sval.charAt( 1 ) ) );

        boolean parsed =  (st.nextToken() == (int)']' && st.nextToken() == (int)'[');

        token = st.nextToken();

        wallPoint2 = new Point();
        wallPoint2.x =  ( coordFromChar( st.sval.charAt( 0 ) ) );
        wallPoint2.y = ( coordFromChar( st.sval.charAt( 1 ) ) );

        parsed = parsed && (st.nextToken() == (int)']');
        return parsed;
  }


  /**
   * Only subclasses (and classes in this package) may get at this class's
   * Point variable.  Everybody else must use get*X() and get*Y().
   */
  protected Point getFromPoint() { return fromPoint; }


  /**
   * Returns:
   *   The X coordinate of the from position.
   */
    public int getFromX() { return fromPoint.x; }

  /**
   * Returns:
   *   The Y coordinate of the prom position.
   */
    public int getFromY() { return fromPoint.y; }


    public boolean hasWall() {
        return wallPoint1 != null;
    }

    public Point getWallPoint1() {
        return wallPoint1;
    }

    public Point getWallPoint2() {
        return wallPoint2;
    }

}



