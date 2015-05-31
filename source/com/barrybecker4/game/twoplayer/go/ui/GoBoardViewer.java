/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;
import com.barrybecker4.game.twoplayer.go.GoController;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import com.barrybecker4.game.twoplayer.go.ui.rendering.GoBoardRenderer;

import java.awt.event.MouseEvent;

/**
 *  Takes a GoController as input and displays the
 *  current state of the Go Game. The GoController contains a GoBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public final class GoBoardViewer extends AbstractTwoPlayerBoardViewer<GoMove, GoBoard> {

    /**
     * Construct the viewer given the controller.
     */
    GoBoardViewer() {}

    @Override
    protected ViewerMouseListener<GoMove, GoBoard> createViewerMouseListener() {
        return new GoViewerMouseListener(this);
    }
    /**
     * start over with a new game using the current options.
     */
    @Override
    public void startNewGame()  {
        super.startNewGame();
        getBoardRenderer().setDraggedShowPiece(null);
    }

    @Override
    protected GameController<GoMove, GoBoard> createController()  {
        return new GoController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return GoBoardRenderer.getRenderer();
    }

    /**
     * perform a pass for the current player.
     */
    public void pass() {
        GameContext.log( 1, "passing" );   // NON-NLS
        GoMove m = GoMove.createPassMove( 0, get2PlayerController().isPlayer1sTurn() );
        continuePlay( m );
    }

    /**
     * Current player resigns from the game.
     */
    public void resign() {
        GameContext.log( 1, "player resigns" );  // NON_NLS
        GoMove m = GoMove.createResignationMove(get2PlayerController().isPlayer1sTurn());
        continuePlay( m );
    }

    /**
     * @return   the message to display at the completion of the game.
     */
    @Override
    protected String getGameOverMessage() {

        // show the dead stones marked as such.
        refresh();

        return new GoGameOverMessage((GoController)controller_).getText();
    }

    /**
     * @return the tooltip for the panel given a mouse event.
     */
    @Override
    public String getToolTipText( MouseEvent e ) {
        if (get2PlayerController().isProcessing())
            return "";  // avoids concurrent modification exception

        Location loc = getBoardRenderer().createLocation(e);
        StringBuilder sb = new StringBuilder( "<html><font=-3>" );  // NON_NLS

        GoBoardPosition space = (GoBoardPosition) controller_.getBoard().getPosition( loc );
        if ( space != null && GameContext.getDebugMode() > 0 ) {
            String spaceText = space.getDescription();
            sb.append( spaceText);
            IGoString string = space.getString();
            IGoEye eye = space.getEye();
            if ( string != null ) {
                appendStringText(sb, spaceText, string);
            }
            // it might belong to both an eye and a string
            if (eye != null) {
                appendEyeText(sb, spaceText, eye);
            }
        }
        else {
            sb.append( loc );
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }

    private void appendEyeText(StringBuilder sb, String spaceText, IGoEye eye) {
        String eyeText = eye.toString();
        sb.append( "<br>" );
        eyeText = eyeText.replaceAll(spaceText, "<b><font color=#991100>" + spaceText + "</font></b>");
        sb.append(eyeText);
        // to debug show the group that contains this eye
        sb.append( "<br>" );
        sb.append("The group that contains this eye is ").append(eye.getGroup());
    }

    private void appendStringText(StringBuilder sb, String spaceText, IGoString string) {
        sb.append( "<br>" );
        sb.append("string liberties = ").append(string.getNumLiberties(controller_.getBoard()));
        String stringText = string.toString();
        if ( string.getGroup() != null ) {
            sb.append( "<br>" );
            String groupText = string.getGroup().toHtml();

            groupText = groupText.replaceAll(stringText, "<font color=#440000>" + stringText + "</font>" );
            groupText = groupText.replaceAll(spaceText, "<b><font color=#991100>" + spaceText + "</font></b>");
            sb.append( groupText );
        }
    }
}
