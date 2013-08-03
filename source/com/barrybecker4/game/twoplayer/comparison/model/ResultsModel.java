// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model;

import com.barrybecker4.common.util.FileUtil;

import java.io.IOException;

/**
 * @author Barry Becker
 */
public class ResultsModel {

    private int size;

    /** matrix of performance results based on the grid of options to compare. */
    PerformanceResultsPair[][] resultsGrid;


    public ResultsModel(int size) {
        this.size = size;
        resultsGrid = new PerformanceResultsPair[size][size];
    }

    public void setResults(int i, int j, PerformanceResultsPair results) {
        resultsGrid[i][j] = results;
    }

    public PerformanceResultsPair getResults(int i, int j) {
        return resultsGrid[i][j];
    }

    public int getSize() {
        return size;
    }

    /** Once all the results have been recorded, we should go through and normalize them. */
    public void normalize()  {
        double maxTotalTime = findMaxTotalTimeSeconds();
        int maxTotalMoves = findMaxTotalMoves();
        ResultMaxTotals maxTotals = new ResultMaxTotals(maxTotalTime, maxTotalMoves);

        updateNormalizedValues(maxTotals);
    }

    private void updateNormalizedValues(ResultMaxTotals maxTotals) {
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                resultsGrid[i][j].normalize(maxTotals);
            }
        }
    }

    private double findMaxTotalTimeSeconds() {
        double maxTime = 0;

        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                double time = resultsGrid[i][j].getTotalNumSeconds();
                if (time > maxTime)  {
                   maxTime = time;
                }
            }
        }
        return maxTime;
    }

    private int findMaxTotalMoves() {
        int maxNumMoves = 0;

        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                int numMoves = resultsGrid[i][j].getTotalNumMoves();
                if (numMoves > maxNumMoves)  {
                    maxNumMoves = numMoves;
                }
            }
        }
        return maxNumMoves;
    }

    /**
     * Save all the results in the mode to the specified path on the filesystem.
     * Consider first removing everything at the specified path.
     * @param path filesystem path to save results to.
     */
    public void saveModel(String path) {
        try
        {
            for (int i=0; i<size; i++) {
                for (int j=0; j<size; j++) {
                    PerformanceResultsPair resultsPair = resultsGrid[i][j];
                    String directoryName = (i > j)?
                            resultsPair.getP2FirstResults().getDescription():
                            resultsPair.getP1FirstResults().getDescription();
                    resultsPair.saveTo(path + FileUtil.FILE_SEPARATOR + directoryName);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not save the model", e);
        }
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                bldr.append(resultsGrid[i][j].toString());
                bldr.append("\n") ;
            }
            bldr.append("\n");
        }
        return bldr.toString();
    }
}
