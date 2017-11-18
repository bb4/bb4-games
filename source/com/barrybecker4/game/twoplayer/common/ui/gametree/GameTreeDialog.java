/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree;

import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerViewModel;
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;
import com.barrybecker4.ui.dialogs.AbstractDialog;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import java.awt.*;
import java.util.List;

/**
 * Draw the entire game tree using a java tree control.
 * Contains 3 sub representations: a java text tree with nodes that can be expanded and collapsed,
 * game viewer, and the graphical GameTreeViewer at the bottom that renders a tree.
 *
 * @author Barry Becker
 */
public final class GameTreeDialog extends AbstractDialog
                                  implements GameChangedListener, TreeExpansionListener {

    /** the options get set directly on the game controller that is passed in. */
    private volatile TwoPlayerController controller_;

    private volatile GameTreeViewer treeViewer_;
    private volatile TextualGameTree textTree_;
    private volatile GameTreeButtons gameTreeButtons_;
    private volatile GameTreeViewable tree_;
    private MoveInfoPanel moveInfo_;

    /** the viewer in the debug window. */
    private volatile TwoPlayerViewModel boardViewer_;

    /** the controller that is actually being played in the normal view. */
    private TwoPlayerController mainController_;

    private volatile GameTreeCellRenderer cellRenderer_;
    private GameTreeMotionListener motionListener_;


    /**
     * constructor - create the tree dialog.
     * @param parent component to display relative to
     * @param boardViewer board viewer
     * @param cellRenderer how to render cells in text tree view.
     */
    public GameTreeDialog(Component parent, AbstractTwoPlayerBoardViewer boardViewer,
                          GameTreeCellRenderer cellRenderer) {
        super( parent );
        initialize(boardViewer, cellRenderer);
    }

    synchronized void initialize(TwoPlayerViewModel boardViewer, GameTreeCellRenderer cellRenderer) {
        tree_ = new GameTreeViewable(null);
        boardViewer_ = boardViewer;
        controller_ = (TwoPlayerController)boardViewer.getController();
        cellRenderer_ = cellRenderer;
        showContent();
        motionListener_ = new GameTreeMotionListener(treeViewer_, boardViewer_, moveInfo_);
    }

    /**
     * ui initialization of the tree control.
     */
    @Override
    public JComponent createDialogContent() {
        setTitle( "Game Tree" );
        textTree_ = createTextualTree();
        JPanel mainPanel = new JPanel(new BorderLayout() );

        TwoPlayerPieceRenderer pieceRenderer =
                (TwoPlayerPieceRenderer)((AbstractTwoPlayerBoardViewer)boardViewer_).getPieceRenderer();
        treeViewer_ =
                new GameTreeViewer(tree_.getRootNode(), cellRenderer_.getColorMap(), pieceRenderer);
        treeViewer_.setPreferredSize(new Dimension(500, 120));

        // the graphical tree goes below the top split pane.
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, createTopSplitPane(), treeViewer_ );

        mainPanel.add( splitPane, BorderLayout.CENTER );

        gameTreeButtons_ = new GameTreeButtons(this);
        mainPanel.add( gameTreeButtons_, BorderLayout.SOUTH );

        return mainPanel;
    }


    synchronized JSplitPane createTopSplitPane() {

        JPanel previewPanel = new JPanel(new BorderLayout());

        ((Component) boardViewer_).setPreferredSize( new Dimension( 200, 500 ) );

        JPanel viewerPanel = new JPanel();
        viewerPanel.setLayout(new BorderLayout());
        viewerPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        moveInfo_ = new MoveInfoPanel(cellRenderer_.getColorMap());

        // this goes to the right of the test tree view
        viewerPanel.add((Component) boardViewer_, BorderLayout.CENTER);
        viewerPanel.add( moveInfo_, BorderLayout.SOUTH);
        previewPanel.add( viewerPanel, BorderLayout.CENTER );

        return new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true, textTree_, previewPanel );
    }

    /**
     * start over from scratch.
     */
    public synchronized void reset() {
        if (textTree_ != null) {
            textTree_.removeMouseMotionListener(motionListener_);
        }
        tree_ = new GameTreeViewable(null);
        boardViewer_.reset();
        textTree_.reset(tree_.getRootNode());
        treeViewer_.setRoot(tree_.getRootNode());
    }

    @Override
    public synchronized void treeExpanded( TreeExpansionEvent e ) {
        refresh();
        treeViewer_.refresh();
    }

    @Override
    public synchronized void treeCollapsed( TreeExpansionEvent e ) {
        refresh();
        treeViewer_.refresh();
    }

    /**
     * called when the game has changed.
     * @param gce the event spawned when the game changed.
     */
    @Override
    public synchronized void gameChanged( GameChangedEvent gce ) {
        mainController_ = (TwoPlayerController)gce.getController();
        motionListener_.setMainController(mainController_);
        gameTreeButtons_.setMainController(mainController_);
        // it is possible that the size of the game has changed since the game tree controller
        // was initialized. Make sure that it is synched up.
        Board mainBoard = (Board)mainController_.getBoard();
        Board board = (Board)controller_.getBoard();
        if ( mainBoard.getNumRows() != board.getNumRows() || mainBoard.getNumCols() != board.getNumCols() ) {
            board.setSize( mainBoard.getNumRows(), mainBoard.getNumCols() );
        }

        // can't do it if we are in the middle of searching
        if (mainController_.isProcessing())  {
            return;
        }

        showCurrentGameTree();

        motionListener_.resetOldChainLength();
    }

    /**
     * show whatever portion of the game tree that has been searched so far.
     */
    public synchronized void showCurrentGameTree() {
        SearchTreeNode root = tree_.getTreeCopy();

        textTree_.reset(root);

        treeViewer_.setRoot(root);
        if (textTree_ == null) return;

        textTree_.expandRow( 0 );
        textTree_.addMouseMotionListener(motionListener_);

        // make the viewer shows the game so far
        setMoveList( mainController_.getMoveList().copy() );

        refresh();
    }

    public IGameTreeViewable getGameTreeViewable() {

        return tree_;
    }

    /**
     * Create the game tree representation
     * @return the the java tree control itself
     */
    private synchronized TextualGameTree createTextualTree() {
        if (tree_.getRootNode() == null)
            return null;
        TextualGameTree textTree = new TextualGameTree( tree_.getRootNode(), cellRenderer_ );
        textTree.addTreeExpansionListener(this);
        return textTree;
    }

    /**
     * Initialize the tree previewer to show the moves made so far.
     */
    private synchronized void setMoveList( List moveList ) {
        boardViewer_.reset();
        ((AbstractTwoPlayerBoardViewer)boardViewer_).showMoveSequence( moveList );
    }

    /**
     * refresh the game tree.
     */
    private synchronized void refresh() {
        paint( getGraphics() );
    }


    /**
     * called when the ok button is clicked.
     */
    @Override
    public void close() {
        // if we set the root to null, then it doesn't have to build the tree
        controller_.setGameTreeViewable( null );
        super.close();
    }
}

