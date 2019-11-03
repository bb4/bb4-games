/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.ui;

import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineChangeListener;
import com.barrybecker4.game.common.online.server.IServerConnection;
import com.barrybecker4.ui.components.ScrollingTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Allows IM chatting with other online players.
 *
 * @author Barry Becker
 */
public class ChatPanel extends JPanel implements OnlineChangeListener, KeyListener {

    private IServerConnection connection_;
    private final ScrollingTextArea textArea_;
    private JTextField messageField_;

    public ChatPanel(IServerConnection connection) {
        setLayout(new BorderLayout());
        connection_ = connection;
        connection_.addOnlineChangeListener(this);

        textArea_ = new ScrollingTextArea();
        textArea_.setBackground(getBackground());
        textArea_.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        messageField_ = new JTextField();
        messageField_.addKeyListener(this);
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(new JLabel("Chat:"), BorderLayout.WEST);
        messagePanel.add(messageField_, BorderLayout.CENTER);

        add(textArea_, BorderLayout.CENTER);
        add(messagePanel, BorderLayout.SOUTH);
    }

    /**
     * Post messages from other players.
     */
    @Override
    public boolean handleServerUpdate(GameCommand cmd) {
        if (cmd.getName() == GameCommand.Name.CHAT_MESSAGE)  {
            textArea_.append(cmd.getArgument().toString());
            textArea_.append("\n");
            return true;
        }
        return false;
    }

    /**
     * Send the message when you press enter.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == '\n') {
            String txt = messageField_.getText();
            txt = txt.trim();
            if (txt.length() > 0) {
                messageField_.setText("");
                connection_.sendCommand(new GameCommand(GameCommand.Name.CHAT_MESSAGE, txt));
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}
