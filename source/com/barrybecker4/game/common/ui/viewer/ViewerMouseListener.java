/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.viewer;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.board.Board;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *  Do nothing by default for all these. Subclasses must override some of them.
 *
 *  @author Barry Becker
 */
public class ViewerMouseListener<M extends Move, B extends Board<M>>
        implements MouseListener, MouseMotionListener {

    protected GameBoardViewer<M, B> viewer;

    /**
     * Constructor.
     */
    public ViewerMouseListener(GameBoardViewer<M, B> viewer) {
        this.viewer =  viewer;
    }

    protected GameBoardRenderer getRenderer() {
        return viewer.getBoardRenderer();
    }

    public void reset() {}
    /**
     * make the human move and show it on the screen,
     * then depending on the options, the computer may move.
     */
    @Override
    public void mouseClicked( MouseEvent e ) {}
    @Override
    public void mousePressed( MouseEvent e ) {}
    @Override
    public void mouseReleased( MouseEvent e ) {}
    @Override
    public void mouseEntered( MouseEvent e ) {}
    @Override
    public void mouseExited( MouseEvent e ) {}

    // implement MouseMotionListener interface
    @Override
    public void mouseDragged(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
}
