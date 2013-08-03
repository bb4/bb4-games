// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers;

import com.barrybecker4.game.twoplayer.comparison.model.Outcome;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Show the column headers with player twos color.
 * validate/revalidate overridden in cell renderer for performance reasons..
 *
 * @author Barry Becker
 */
public class PlayerHeadCellRenderer extends DefaultTableCellRenderer {

    protected static final Font FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.BOLD, 16 );

    /**
     * This method gets called each time a column header is rendered.
     * @param row always -1
     * @param isSelected always false.
     * @param hasFocus always false.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        // Inherit the colors and font from the header component
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(Outcome.PLAYER1_WON.getColor().darker());
                setBackground(Color.WHITE);
                setFont(header.getFont());
            }
        }

        setText((value == null) ? "" : value.toString());
        setToolTipText(getText());
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(JLabel.CENTER);
        this.setFont(FONT);
        return this;
    }
}
