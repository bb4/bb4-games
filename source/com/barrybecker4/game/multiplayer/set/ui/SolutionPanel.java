/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui;

import com.barrybecker4.game.multiplayer.set.Card;
import com.barrybecker4.game.multiplayer.set.ui.render.CardRenderer;
import com.barrybecker4.ui.themes.BarryTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Barry Becker Date: Apr 2, 2006
 */
class SolutionPanel extends JPanel
                           implements MouseMotionListener{

    private List<Card> sets_;

    private List<Card> currentlyHighlightedSet_;

    private SetGameViewer viewer_;

    private static final Color BACKGROUND_COLOR = BarryTheme.UI_COLOR_SECONDARY3;

    /**
     * Constructor.
     */
    SolutionPanel(List<Card> sets, SetGameViewer viewer)
    {
        assert (sets != null);
        sets_ = sets;
        viewer_ = viewer;
        this.addMouseMotionListener(this);
    }


    private int getCanvasWidth() {
        return getWidth() - 2 * CardRenderer.LEFT_MARGIN;
    }

    private int getNumColumns() {

        return 3;
    }

    private Dimension calcCardDimension(int numCols) {
        int cardWidth = getCanvasWidth() / numCols;
        return new Dimension(cardWidth, (int) (cardWidth * CardRenderer.CARD_HEIGHT_RAT));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 800);
    }

    /**
     * @param i th set to return.
     * @return the ith set.
     */
    private List<Card> getSet(int i) {
        List<Card> set = new ArrayList<>(getNumColumns());
        int numCardsInSet = getNumColumns();
        int index = i * numCardsInSet;
        for (int j=0; j<numCardsInSet; j++) {
            set.add(sets_.get(index + j));
        }
        return set;
    }

    /**
     * @return  the set (row) that the mouse is currently over (at x, y coords)
     */
    private List<Card> findSetOver(int x, int y) {
        int numCols = getNumColumns();

        Dimension cardDim = calcCardDimension(numCols);
        int cardHeight = (int) cardDim.getHeight();

        int selectedIndex = -1;
        for (int row = 0; row < (sets_.size() / numCols); row++ ) {
            int rowPos = row * cardHeight + CardRenderer.TOP_MARGIN;
            if (y > rowPos && y <= rowPos + cardDim.getHeight()) {
                selectedIndex = row;
                break;
            }
        }

        if (selectedIndex == -1) {
            return null;
        }

        return getSet(selectedIndex);
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        List<Card> set = findSetOver(e.getX(), e.getY());

        if (currentlyHighlightedSet_ == null && set == null)  {
            return;
        }
        boolean changed =
            currentlyHighlightedSet_ == null || set == null || !sameSet(set, currentlyHighlightedSet_);

        if (!changed) return;

        if (currentlyHighlightedSet_ != null) {
            highlight(currentlyHighlightedSet_, false);
        }
        if (set != null) {
            currentlyHighlightedSet_ = set;
            highlight(currentlyHighlightedSet_, true);
        } else {
            currentlyHighlightedSet_ = null;
        }

        this.repaint();
    }

    private static boolean sameSet(List<Card> s1, List<Card> s2) {
        return s1.get(0) == s2.get(0)  && s1.get(1) == s2.get(1) && s1.get(2) == s2.get(2);
    }

    /**
     * @param set  to highlight.
     * @param highlight  whether to higlight or not.
     */
    private void highlight(List<Card> set, boolean highlight) {
        if (set == null) return;
        for (Card c : set) {
            c.setHighlighted(highlight);
            //viewer_.highlightCard(c, highlight);
        }
        viewer_.refresh();
    }

    /**
     *  call this when close so we can unhighlight whatever is highlighted.
     */
    public void closed()  {
        highlight(currentlyHighlightedSet_, false);
    }

    /**
     * This renders the current state of the puzzle to the screen.
     * Render each card in the deck.
     */
    @Override
    protected void paintComponent( Graphics g )
    {
        int i;

        super.paintComponents( g );
        // erase what's there and redraw.

        g.clearRect( 0, 0, getWidth(), getHeight() );
        g.setColor( BACKGROUND_COLOR );
        g.fillRect( 0, 0, getWidth(), getHeight() );

        int numCols = getNumColumns();

        Dimension cardDim = calcCardDimension(numCols);
        int cardWidth = (int) cardDim.getWidth();
        int cardHeight = (int) cardDim.getHeight();

        for (i = 0; i<sets_.size(); i++ ) {
            int row = i / numCols;
            int col = i % numCols;
            int colPos = col * cardWidth + CardRenderer.LEFT_MARGIN;
            int rowPos = row * cardHeight + CardRenderer.TOP_MARGIN;
            CardRenderer.render((Graphics2D) g, sets_.get(i),
                                new Point2D.Float(colPos, rowPos), cardWidth, cardHeight, false);
        }
    }

}
