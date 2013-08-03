/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerViewModel;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Responsible for handling events related to the display and interaction with the visual game tree.
 *
 * @author Barry Becker
 */
public final class GameTreeMotionListener implements MouseMotionListener {

    /** the controller that is actually being played in the normal view. */
    private TwoPlayerController mainController_;

    private volatile GameTreeViewer treeViewer_;
    private MoveInfoPanel moveInfoPanel_;

    /** the viewer in the debug window. */
    private TwoPlayerViewModel boardViewer_;

    private int oldChainLength_;

    private static final boolean SHOW_SUCCESSIVE_MOVES  = true;



    /**
     * constructor - create the tree dialog.
     */
    public GameTreeMotionListener(GameTreeViewer treeViewer,
                                  TwoPlayerViewModel boardViewer,
                                  MoveInfoPanel detailsPanel) {
        treeViewer_ = treeViewer;
        moveInfoPanel_ = detailsPanel;
        boardViewer_ = boardViewer;
        oldChainLength_ = 0;
    }

    public void setMainController(TwoPlayerController mainController) {
        mainController_ = mainController;
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        selectCallback( e );
    }

    public void resetOldChainLength() {
       oldChainLength_ = 0;
    }

    /**
     * called when a particular move in the game tree has been selected by the user (by clicking on it or mouse-over).
     */
    private void selectCallback( MouseEvent e ) {

        JTree tree = (JTree) e.getSource();

        if (mainController_.isProcessing())  {
            // avoid concurrency problems
            return;
        }

        int row = tree.getRowForLocation( e.getX(), e.getY() );
        if ( row == -1 ) return;

        TreePath path = tree.getPathForRow( row );
        treeViewer_.highlightPath( path );

        int chainLength = path.getPathCount();
        Object[] nodes = path.getPath();
        SearchTreeNode lastNode = (SearchTreeNode)nodes[chainLength-1];
        List<TwoPlayerMove> moveList = new LinkedList<TwoPlayerMove>();
        TwoPlayerMove m = null;
        for ( int i = 0; i < chainLength; i++ ) {
            SearchTreeNode node = (SearchTreeNode) nodes[i];
            m = (TwoPlayerMove) node.getUserObject();
            if ( m == null )
                return; // no node here
            moveList.add( m );
        }

        AbstractTwoPlayerBoardViewer viewer = (AbstractTwoPlayerBoardViewer)boardViewer_;
        if (SHOW_SUCCESSIVE_MOVES) {
            // add expected successive moves to show likely outcome.
            moveList = addSuccessiveMoves(moveList, lastNode);
        }
        GameContext.log(3, "chainlen before="+chainLength+" after="+moveList.size());
        chainLength = moveList.size();
        viewer.showMoveSequence( moveList, oldChainLength_, lastNode.getChildMoves() );

        // remember the old chain magnitude so we know how much to back up next time
        oldChainLength_ = chainLength;

        // we should throw an event instead of have this dependency.
        moveInfoPanel_.setText(viewer, m, lastNode);
    }

    /**
     * Add to the list all the moves that we expect are most likely to occur given the current game state.
     * This is how the computer expects the game to play out.
     * @return the list of successive moves.
     */
    private static List<TwoPlayerMove> addSuccessiveMoves(List<TwoPlayerMove> moveList, SearchTreeNode finalNode) {

        SearchTreeNode nextNode = finalNode.getExpectedNextNode();
        while (nextNode !=  null)  {
            TwoPlayerMove m = (TwoPlayerMove)((Move)nextNode.getUserObject()).copy();
            m.setFuture(true);
            moveList.add(m);
            nextNode = nextNode.getExpectedNextNode();
        }
        return moveList;
    }
}

