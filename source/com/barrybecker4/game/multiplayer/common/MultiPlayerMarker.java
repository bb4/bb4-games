/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.GamePiece;

import java.awt.*;

/**
 * Represents a trivial player in the viewer.
 * For the player we draw their value.
 *
 * @author Barry Becker
 */
public class MultiPlayerMarker extends GamePiece {

    private static final long serialVersionUID = 1;

    private MultiGamePlayer owner_;
    private Location location_;
    private boolean highlighted_;


    public MultiPlayerMarker(MultiGamePlayer owner) {
        setAnnotation(""+owner.getName());
        owner_ = owner;
    }

    public MultiGamePlayer getOwner() {
        return owner_;
    }

    public boolean isHighlighted() {
        return highlighted_;
    }

    public void setHighlighted(boolean highlighted) {
        highlighted_ = highlighted;
    }


    public Color getColor() {
        return getOwner().getColor();
    }

    public Location getLocation() {
        return location_;
    }

    /**
     * ordinarily this does not change
     */
    public void setLocation(Location loc) {
        location_ = loc;
    }

    /**
      * Get the textual representation of the marker.
      * @return string form
      */
     public String toString() {
         return toString("\n");
     }

     /**
      * get the html representation of the marker.
      * @return html form
      */
     public String toHtml()  {
         return toString( "<br>" );
     }


    String toString(String newLine)  {
        StringBuilder sb = new StringBuilder("");

        if (getOwner()!=null)
            sb.append("Owner: ").append(this.getOwner().getName()).append(newLine);
        return sb.toString();
    }

}



