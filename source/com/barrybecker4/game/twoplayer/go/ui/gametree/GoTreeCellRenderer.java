/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui.gametree;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.game.twoplayer.common.ui.gametree.GameTreeCellRenderer;
import com.barrybecker4.game.twoplayer.go.ui.rendering.GoStoneRenderer;

import javax.swing.*;
import java.awt.*;

/**
 *  This class defines how to draw entrees in the textual game tree ui.
 *
 *  @author Barry Becker
 */
public final class GoTreeCellRenderer extends GameTreeCellRenderer {

    private static final Color ROW_BG_COLOR = new Color( 220, 210, 240 );

    private static final int STONE_IMG_SIZE = 11;
    private static final int TEXT_MARGIN = 4;
    private static final Font FONT = new Font("Sans Serif", Font.PLAIN, 10);

    /** the node we are going to render. */
    private SearchTreeNode node_;

    private static final ColorMap COLORMAP = new GoTreeColorMap();

    /**
     *  Default Constructor.
     */
    public GoTreeCellRenderer() {
        setColorMap(COLORMAP);
        this.setPreferredSize(new Dimension(1000, 16));
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value,
            boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus1 ) {

        super.getTreeCellRendererComponent( tree, value, sel,
                expanded, leaf, row, hasFocus1 );

        node_ = (SearchTreeNode) value;

        Color bg = getBGColor( value );

        this.setBackgroundNonSelectionColor( bg );
        this.setBackgroundSelectionColor( Color.orange );

        return this;
    }

    @Override
    protected Color getBGColor( Object value ) {
        return ROW_BG_COLOR;
    }


    /**
      * Paints the value.  The background is filled based on what was selected.
      */
    @Override
    public void paint(Graphics g) {
        drawCellBackground(g);

        Graphics2D g2 = (Graphics2D) g;
        TwoPlayerMove move = (TwoPlayerMove) node_.getUserObject();
        if (move == null) {
            return;
        }

        drawStoneIcon(move.isPlayer1(), g2);
        drawMoveText(move, g2);
    }

    /**
     * Draw a nice icon to show who's move it corresponds to
     */
    private void drawStoneIcon(boolean isPlayer1, Graphics2D g2) {
        if (isPlayer1)  {
            g2.drawImage(GoStoneRenderer.BLACK_STONE_IMG.getImage(), 1, 0, STONE_IMG_SIZE, STONE_IMG_SIZE, null);
        } else {
            g2.drawImage(GoStoneRenderer.WHITE_STONE_IMG.getImage(), 1, 0, STONE_IMG_SIZE, STONE_IMG_SIZE, null);
        }
    }

    /**
     * Draw the inherited value, the base value, and the move(with its attributes) to the right of the icon.
     */
    private void drawMoveText(TwoPlayerMove move, Graphics2D g2) {

        int inheritedValue = move.getInheritedValue();
        int value = move.getValue();

        g2.setFont(FONT);

        int end = TEXT_MARGIN + STONE_IMG_SIZE;
        end = drawStringValue("inhrtd=", inheritedValue, end, g2);
        end = drawStringValue("val=", value, end, g2);

        StringBuilder bldr = new StringBuilder();
        if (node_.isPruned()) {
            bldr.append(" *PRUNED");
        }  else {
            int numKids = node_.getChildMoves()==null? 0 : node_.getChildMoves().length;
            bldr.append(" kids=").append(numKids);
            bldr.append(node_.toString());
        }

        g2.drawString(bldr.toString(), end, 8);
    }

    private int drawStringValue(String prefix, int value, int offset, Graphics2D g2) {

        Color c = getColorMap().getColorForValue(value);
        String text = prefix + FormatUtil.formatNumber(value);
        g2.setColor(c);
        int width = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        g2.fillRect(offset, 1, width + 2 * TEXT_MARGIN, 10);
        g2.setColor(this.getForeground());
        g2.drawString(text, offset + TEXT_MARGIN, 9);
        return offset + width + 3 * TEXT_MARGIN;
    }

    private void drawCellBackground(Graphics g) {
        Color bColor;

        if(selected) {
            bColor = getBackgroundSelectionColor();
        } else {
            bColor = getBackgroundNonSelectionColor();
            if(bColor == null)
                bColor = getBackground();
        }

        int imageOffset;
        if (bColor != null) {
            //Icon currentI = getIcon();

            imageOffset = 0;
            g.setColor(bColor);
            // if I don't  add 500, then some of the text does not have a bg.
            if (getComponentOrientation().isLeftToRight()) {
                g.fillRect(imageOffset, 0, getWidth() - imageOffset, getHeight());
            } else {
                g.fillRect(0, 0, getWidth() - imageOffset, getHeight());
            }
        }
    }

}
