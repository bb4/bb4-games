/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.card;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Barry Becker
 */
@SuppressWarnings({"StaticMethodOnlyUsedInOneClass","ClassWithTooManyFields"})
public enum Rank {

    DEUCE("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("A");


    private final String symbol;
    private static final Map<String,Rank> RANK_FROM_SYMBOL = new HashMap<String,Rank>();

    static {
        for (Rank r : values()) {
            RANK_FROM_SYMBOL.put(r.getSymbol(), r);
        }
    }

    Rank(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Rank getRankForSymbol(String symbol) {
        return RANK_FROM_SYMBOL.get(symbol);
    }

}
