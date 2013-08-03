// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model;

import com.barrybecker4.common.format.FormatUtil;

import java.awt.*;
import java.io.IOException;

/**
 * Right now this just contains the name of the config and the
 * search options, but we may add the game weights too at some point.
 *
 * @author Barry Becker
 */
public class PerformanceResultsPair {

    /** true if player1 won */
    private PerformanceResults p1FirstResults;

    private PerformanceResults p2FirstResults;

    /** default constructor */
    public PerformanceResultsPair() {
        this.p1FirstResults = new PerformanceResults();
        this.p2FirstResults = new PerformanceResults();
        this.normalize(new ResultMaxTotals(1.0, 1));
    }

    /** constructor */
    public PerformanceResultsPair(PerformanceResults p1FirstResults, PerformanceResults p2FirstResults) {
        this.p1FirstResults = p1FirstResults;
        this.p2FirstResults = p2FirstResults;
    }

    public PerformanceResults getP1FirstResults() {
        return p1FirstResults;
    }
    public PerformanceResults getP2FirstResults() {
            return p2FirstResults;
        }

    public Outcome[] getOutcomes() {
        return new Outcome[] {p1FirstResults.getOutcome(), p2FirstResults.getOutcome()};
    }

    public Image[] getFinalImages() {
        return new Image[] {p1FirstResults.getFinalImage(), p2FirstResults.getFinalImage()};
    }

    public double getTotalNumSeconds() {
        return p1FirstResults.getNumSeconds() + p2FirstResults.getNumSeconds();
    }

    public int getTotalNumMoves() {
        return p1FirstResults.getNumMoves() + p2FirstResults.getNumMoves();
    }

    public void normalize(ResultMaxTotals maxTotals) {
        p1FirstResults.normalize(maxTotals);
        p2FirstResults.normalize(maxTotals);
    }

    public double[] getNormalizedTimes() {
        return new double[] {
            p1FirstResults.getNormalizedNumSeconds(),
            p2FirstResults.getNormalizedNumSeconds()
        };
    }

    public String[] getTimesFormatted() {
        return new String[]  {
            p1FirstResults.getTimeFormatted(),
            p2FirstResults.getTimeFormatted()
        };
    }

    public double[] getNormalizedNumMoves() {
        return new double[] {
            p1FirstResults.getNormalizedNumMoves(),
            p2FirstResults.getNormalizedNumMoves()
        };
    }

    public String[] getNumMoves() {
        return new String[] {
            FormatUtil.formatNumber(p1FirstResults.getNumMoves()),
            FormatUtil.formatNumber(p2FirstResults.getNumMoves())
        };
    }

    public void saveTo(String path) throws IOException {
        p1FirstResults.saveTo(path);
        p2FirstResults.saveTo(path);
    }

    public String getWinnerText() {
        String text1 = "Player1 " + (p1FirstResults.getOutcomeVerb(true)) + " when he played first.\n";
        String text2 = "Player2 " + (p2FirstResults.getOutcomeVerb(false)) + " when he played first.";
        return text1 + text2;
    }

    private String getOutcomeVerb(PerformanceResults results, boolean player1Perpective)  {
        String verb = "";
        switch (results.getOutcome()) {
            case PLAYER1_WON : verb = player1Perpective ? "won" : "lost"; break;
            case PLAYER2_WON : verb = player1Perpective ? "lost" : "won"; break;
            case TIE: verb = "tied"; break;
        }
        return verb;
    }

    public String toHtmlString() {
            StringBuilder bldr = new StringBuilder("<html>");

            bldr.append(" Player1 first: ").append(p1FirstResults.toHtmlString(true)).append("<br>");
            bldr.append("Player2 first: ").append(p2FirstResults.toHtmlString(true));
            bldr.append("</html>");
            return bldr.toString();
        }

    public String toString() {
        StringBuilder bldr = new StringBuilder("Results pair");

        bldr.append(" Player1 first: ").append(p1FirstResults.toString()).append("\n");
        bldr.append(" Player2 first: ").append(p2FirstResults.toString());

        return bldr.toString();
    }
}
