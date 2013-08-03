/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.menu;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The standard file menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
public abstract class AbstractGameMenu extends JMenu
                                       implements ActionListener {

    protected JFrame frame_;
    protected String currentGameName;

    protected List<GameMenuListener> listeners =
            new ArrayList<GameMenuListener>();

    /**
     * Game file menu constructor
     * @param title user visible menu title.
     */
    public AbstractGameMenu(String title) {
        super(title);
        setBorder(BorderFactory.createEtchedBorder());
    }

    public void addListener(GameMenuListener listener) {
        listeners.add(listener);
        notifyOfChange(currentGameName);
    }

    protected void notifyOfChange(String gameName) {
        for (GameMenuListener listener : listeners) {
            listener.gameChanged(gameName);
        }
    }

    /**
     * Create a menu item.
     * @param name name of the menu item. The label.
     * @return the menu item to add.
     */
    protected JMenuItem createMenuItem(String name) {

        JMenuItem item = new JMenuItem(name);
        item.addActionListener(this);
        return item;
    }

}
