// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.common.online.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * For entering and managing the multi-players name.
 * It must be unique among the players at the table.
 *
 * @author Barry Becker
 */
class NamePanel extends JPanel
                implements MouseListener {

    private static final String DEFAULT_NAME = "<name>";

    private JTextField localPlayerName_;

    /**
     * Constructor
     */
    protected NamePanel() {
        super(new BorderLayout());
        create();
    }

    @Override
    public void addKeyListener(KeyListener listener) {
       localPlayerName_.addKeyListener(listener);
    }

    public boolean nameChecksOut() {
        boolean checksOut = !getCurrentName().equals(DEFAULT_NAME);
         if (!checksOut) {
              JOptionPane.showMessageDialog(this, "You must enter your name at the top first.", "Warning",
                                            JOptionPane.INFORMATION_MESSAGE);
        }
        return checksOut;
    }

    public String getCurrentName() {
        return localPlayerName_.getText();
    }

    // Implement mouseListener interface
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
        giveFocus();
    }
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}


    /** Call when the user first clicks in the field */
    private void giveFocus() {
        if (localPlayerName_.getText().equals(DEFAULT_NAME))
            localPlayerName_.setText("");
    }


    private void create() {
        JLabel nameLabel = new JLabel("Your Name: ");
        localPlayerName_ = new JTextField(DEFAULT_NAME);
        localPlayerName_.addMouseListener(this);

        add(nameLabel, BorderLayout.WEST);
        add(localPlayerName_, BorderLayout.CENTER);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(180, 20));
        add(fill, BorderLayout.EAST);
    }
}
