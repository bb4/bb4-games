/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic;

import com.barrybecker4.game.common.player.PlayerAction;

import java.util.List;

/**
 *
 * @author Barry Becker
 */
public class GalacticAction extends PlayerAction {

     /** a list of outstanding Orders */
     private List<Order> orders_;

     /** Constructor */
     public GalacticAction(String playerName, List<Order> orders) {
         super(playerName);
         orders_ = orders;
     }


     public List<Order> getOrders() {
         return orders_;
     }
}
