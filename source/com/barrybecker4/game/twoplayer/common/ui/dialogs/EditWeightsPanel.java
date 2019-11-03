/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameWeights;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.parameter.types.Parameter;

import javax.swing.*;
import java.awt.*;

/**
 * Allow for editing the polynomial weights used in the static
 * evaluation function
 *
 * @author Barry Becker
 */
class EditWeightsPanel extends JPanel {

    private final ParameterArray weights_;
    private final GameWeights gameWeights_;
    private JTextField[] weightFields_;

    private JPanel weightsPanel_;

    private static final Dimension LABEL_DIM = new Dimension( 200, 20 );
    private static final Dimension FIELD_DIM = new Dimension( 100, 20 );
    private static final Dimension WEIGHT_PANEL_DIM = new Dimension( 900, 25 );

    /** constructor */
    EditWeightsPanel(ParameterArray weights, GameWeights gameWeights) {

        // make a copy of the weights so we can cancel if desired
        weights_ = weights;  // this does not make a copy.
        gameWeights_ = gameWeights;
        initialize();
    }


    protected void initialize() {

        setLayout( new BoxLayout(this, BoxLayout.Y_AXIS ) );

        JLabel instructLabel = new JLabel( GameContext.getLabel("EDIT_WTS_BELOW") );
        weightsPanel_= createWeightsPanel();
        initWeightsPanel();
        JScrollPane scrollPane_=new JScrollPane( weightsPanel_ );

        add( instructLabel );
        add( scrollPane_ );
    }

    private JPanel createWeightsPanel() {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setBorder( BorderFactory.createEtchedBorder() );
        return p;
    }

    private void initWeightsPanel() {
        int len = weights_.size();
        weightFields_ = new JTextField[len];

        final FlowLayout fl = new FlowLayout();
        for ( int i = 0; i < len; i++ ) {
            JPanel weightPanel = new JPanel( fl );
            weightPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
            weightPanel.setMaximumSize( WEIGHT_PANEL_DIM );
            JLabel lab = new JLabel( gameWeights_.getName( i )+" [0.0 - "+gameWeights_.getMaxWeight(i)+']');
            lab.setToolTipText( gameWeights_.getDescription( i ) );
            lab.setAlignmentX( Component.LEFT_ALIGNMENT );
            lab.setPreferredSize( LABEL_DIM );
            weightFields_[i] = new JTextField( Double.toString( weights_.get(i).getValue() ) );
            weightFields_[i].setAlignmentX( Component.LEFT_ALIGNMENT );
            weightFields_[i].setPreferredSize( FIELD_DIM );
            weightPanel.add( lab );
            weightPanel.add( weightFields_[i] );
            //weightPanel.setBorder(BorderFactory.createRaisedBevelBorder());
            weightsPanel_.add( weightPanel );
        }
        weightsPanel_.add( Box.createVerticalGlue() ); // fill extra space at the bottom
    }

    public void ok() {
        int len = weights_.size();
        String sErrors = "";
        for ( int i = 0; i < len; i++ ) {
            double v = Double.parseDouble( weightFields_[i].getText() );
            Parameter p = weights_.get(i);
            if ( v<p.minValue())  {
                sErrors += v+" is too small for "+p.name()+". The min vlaue of "+p.minValue()+" will be used.\n";
            }
            else if (v>p.maxValue()) {
                sErrors += v+" is too big for "+p.name()+". The max value of "+p.maxValue()+" will be used.\n";
            }
            else {
                weights_.get(i).setValue(v);
            }
        }
        if (sErrors.length() >1)
            JOptionPane.showMessageDialog(this, sErrors, "Parameter is out of Range", JOptionPane.WARNING_MESSAGE);
    }
}

