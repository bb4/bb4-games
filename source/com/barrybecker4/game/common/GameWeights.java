/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common;


import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.parameter.redistribution.RedistributionFunction;
import scala.Option;

/**
 * The GameWeights define the coefficients to use by the
 * evaluation polynomial used by each computer player.
 * Optimizing these weights is the primary way that the game
 * learns to play better.
 *
 * @author Barry Becker
 */
public class GameWeights {

    /** scores computed from weights are assumed to be between [0 and ASSUMED_WINNING_VALUE] for player1 */
    protected static final double ASSUMED_WINNING_VALUE = 1024;

    /** for scala Option compatability */
    protected static final Option<RedistributionFunction> NONE = scala.Option.apply(null);

    /**
     * the weights are created assuming a winning value of ASSUMED_WINNING_VALUE.
     * If that changes we need to scale them
     */
    private static final double SCALE = SearchStrategy.WINNING_VALUE / ASSUMED_WINNING_VALUE;

    private int numWeights;

    private final ParameterArray defaultWeights;
    private ParameterArray p1Weights;
    private ParameterArray p2Weights;

    private final String[] names;
    private final String[] descriptions;

    /**
     * Constructor
     * @param defaultWeights default weights to use (will also be used for p1 and p2 weights).
     */
    public GameWeights( ParameterArray defaultWeights ) {
        // this will not change once set.
        numWeights = defaultWeights.size();

        this.defaultWeights = defaultWeights;
        names = new String[numWeights];
        descriptions = new String[numWeights];

        for (int i = 0; i < numWeights; i++ ) {
            names[i] = "Weight " + i;
            descriptions[i] = "The weighting coefficient for the " + i + "th term of the evaluation polynomial";
        }
        init();
    }

    /**
     * Constructor
     */
    public GameWeights( double[] defaultWeights, double[] minWeights, double[] maxWeights,
                        String[] names, String[] descriptions ) {
        numWeights = defaultWeights.length;
        double[] minVals = new double[numWeights];
        double [] defaultVals = new double[numWeights];
        double [] maxVals = new double[numWeights];

        for (int i = 0; i < numWeights; i++) {
            minVals[i] = SCALE * minWeights[i];
            defaultVals[i] = SCALE * defaultWeights[i];
            maxVals[i] = SCALE * maxWeights[i];
        }
        this.defaultWeights = new NumericParameterArray(defaultVals, minVals, maxVals, names, MathUtil.RANDOM());

        this.names = names;
        this.descriptions = descriptions;

        init();
    }

    private void init() {
        p1Weights = defaultWeights;
        p2Weights = defaultWeights;
    }

    /**
     * @return the weights for player1. It a reference, so changing them will change the weights in this structure
     */
    public final ParameterArray getPlayer1Weights() {
        return p1Weights;
    }

    /**
     * @return the weights for player1. It a reference, so changing them will change the weights in this structure
     */
    public final ParameterArray getPlayer2Weights() {
        return p2Weights;
    }

    public final void setPlayer1Weights( ParameterArray p1Weights) {
       verify(p1Weights);
       this.p1Weights = p1Weights;
    }

    public final void setPlayer2Weights( ParameterArray p2Weights) {
       verify(p2Weights);
       p1Weights = p2Weights;
    }

    private void verify( ParameterArray wts) {
       assert wts.size() == numWeights :
               "Incorrect number of weights: "+ wts.size()+" you need "+ numWeights;
    }

    /**
     * @return the default weights. It a reference, so changing them will change the weights in this structure
     */
    public final ParameterArray getDefaultWeights() {
        return defaultWeights;
    }

    /**
     * @return short description of weight i
     */
    public final String getName( int i ) {
        return names[i];
    }

    /**
     * @return description of weight i (good for putting in a tooltip)
     */
    public final String getDescription( int i ) {
        return descriptions[i];
    }

    /**
     * @return the maximum allowed value of weight i
     */
    public final double getMaxWeight( int i ) {
        return defaultWeights.get(i).maxValue();
    }

    /**
     * nicely print the weights
     */
    @Override
    public final String toString() {
        return "Player1's weights are:" + p1Weights + "\nPlayer2's weights are " + p2Weights;
    }
}

