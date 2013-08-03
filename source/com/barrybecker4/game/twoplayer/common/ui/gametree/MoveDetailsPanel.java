/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;
import com.barrybecker4.ui.util.ColorUtil;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Presents info to the user about the currently moused over node in the tree.
 *
 * @author Barry Becker
 */
class MoveDetailsPanel extends JPanel {

    private JLabel infoLabel_;
    private JLabel leafDetailLabel_;


    public MoveDetailsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                             BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        infoLabel_ = new JLabel();
        leafDetailLabel_ = new JLabel();
        add(panelWrap(infoLabel_), BorderLayout.CENTER);
        add(panelWrap(leafDetailLabel_), BorderLayout.EAST) ;
    }

    private JPanel panelWrap(JLabel label) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(label);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    /**
     *  Display all the relevant info for the moused over move.
     */
    public void setText(AbstractTwoPlayerBoardViewer viewer, TwoPlayerMove m, SearchTreeNode lastNode) {
        TwoPlayerPieceRenderer renderer = (TwoPlayerPieceRenderer)viewer.getPieceRenderer();
        TwoPlayerController controller = (TwoPlayerController)viewer.getController();
        String passSuffix = m.isPassingMove() ? " (Pass)" : "";
        String entity = "Human's move";
        int numKids = lastNode.getChildMoves()==null? 0 : lastNode.getChildMoves().length;

        Color c = renderer.getPlayer2Color();
        if ( m.isPlayer1() )
            c = renderer.getPlayer1Color();
        PlayerList players = controller.getPlayers();
        if ( (m.isPlayer1() && !players.getPlayer1().isHuman()) ||
             (!m.isPlayer1() && !players.getPlayer2().isHuman()) )
            entity = "Computer's move";

        StringBuilder sBuf = new StringBuilder("<html>");
        sBuf.append("<font size=\"+1\" color=\"").append(ColorUtil.getHTMLColorFromColor(c)).
                append("\" bgcolor=\"#99AA99>\">").append(entity).append(passSuffix).append("</font><br>");
        sBuf.append("Static value = ").append(FormatUtil.formatNumber(m.getValue())).append("<br>");

        sBuf.append(lastNode.toString());

        sBuf.append("<br>Number of descendants = ").append(numKids).append("<br>");
        if (m.isUrgent())
            sBuf.append( "<font color=\"#FF6611\">Urgent move!</font>");
        sBuf.append("</html>");
        infoLabel_.setText(sBuf.toString());

        setLeafDetail(m, numKids==0);
    }

    private void setLeafDetail(TwoPlayerMove m, boolean isLeaf) {
        StringBuilder buf = new StringBuilder("");
        if (isLeaf) {
            if (m.getScoreDescription() != null)  {
                buf.append(m.getScoreDescription());
            }
        }  else {
            buf.append("Not a leaf");
        }
        leafDetailLabel_.setText(buf.toString());
    }

}
