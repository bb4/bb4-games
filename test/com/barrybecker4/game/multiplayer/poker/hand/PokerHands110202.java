/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Card;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Programming challenge to test which poker hands are better
 * see  http://www.programming-challenges.com/pg.php?page=downloadproblem&probid=110202&format=html
 * for details
 *
 * Note: when I submitted this it did not compile because they require 1.2 or before, but I am using 1.5....
 *
 * When running, enter 2 poker hands to be compared. There are exactly 5 cards in a hand.
 * Each card has the form <rank><suit>, so for example enter
 *   QC 3H 4D 5H 6H 3S 4C JC 6C 7C
 * The first 5 cards are for the first hand, then last five for the second.
 *
 * author Barry Becker
 */
class PokerHands110202 {

    private static final int MAX_LG = 255;

    /**
     * For reading from stdin for the programming contests
     *
     * expects input to be something like   2H 3H 4H 5H 6H 3C 4C 5C 6C 7C
     * @return the line that was read.
     */
    static String readLine(InputStream stream)  // utility function to read from stdin
    {
        byte lin[] = new byte [MAX_LG];
        int lg = 0, car = -1;

        try
        {
            while (lg < MAX_LG)
            {
                car = stream.read();
                if ((car < 0) || (car == '\n')) break;
                lin [lg++] += car;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        if ((car < 0) && (lg == 0)) {
            return null;  // eof
        }
        return (new String (lin, 0, lg));
    }


    private void evaluateLine(String line) {
         if (line == null || line.length() <2) {
            return;
        }

        List<Card> blackCards = new ArrayList<Card>(5);
            List<Card> whiteCards = new ArrayList<Card>(5);

        StringTokenizer tokenizer = new StringTokenizer(line, " ");

        // the first five entries for for black the second five are for white
        int ct = 0;
        while (tokenizer.hasMoreElements()) {
            String cardToken = (String) tokenizer.nextElement();

            if (ct < 5)  {
                blackCards.add(new Card(cardToken));
            } else if (ct < 10)  {
                whiteCards.add(new Card(cardToken));
            }
            ct++;
        }

        Hand blackHand = new Hand(blackCards);
        Hand whiteHand = new Hand(whiteCards);

        PokerHandScorer scorer = new PokerHandScorer();
        int blackWin = scorer.getScore(blackHand).compareTo(scorer.getScore(whiteHand));

        if (blackWin > 0) {
            System.out.println("Black wins.");
        } else if (blackWin < 0)  {
            System.out.println("White wins.");
        } else {
            System.out.println("Tie.");
        }
    }


    public void evaluate(InputStream stream) throws IOException {

        String line;

        while ((line = readLine(stream)) != null) {
            evaluateLine(line);
        }
    }


    /**
     * Entry point.
     */
    public static void main(String args[])  {

        PokerHands110202 app = new PokerHands110202();

        try {
            app.evaluate(System.in);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
