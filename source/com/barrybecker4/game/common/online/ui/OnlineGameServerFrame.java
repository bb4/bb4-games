/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.ui;

import com.barrybecker4.common.app.CommandLineOptions;
import com.barrybecker4.game.common.online.server.OnlineGameServer;
import com.barrybecker4.game.common.plugin.PluginManager;
import com.barrybecker4.ui.components.ScrollingTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * UI Frame to show what the GameServer is doing.
 * There is nothing game specific about the server it just accepts commands and delegates to the controller.
 * Invoke with
 *   java OnlineGameServer -game &lt;game&gt;
 * for example:
 *   java OnlineGameServer -game poker
 *
 * @author Barry Becker
 */
public class OnlineGameServerFrame  extends JFrame {

    private ScrollingTextArea textArea;
    private OnlineGameServer server;


    /**
     * Create the online game server to serve all online clients.
     */
    private OnlineGameServerFrame(String gameName) {
        initUI(gameName);
        server = new OnlineGameServer(gameName, textArea);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.shutDown();
            }
        });
    }

    /**
     * initialize UI for server.
     */
    private void initUI(String gameName) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Commands received over the socket:");
        textArea = new ScrollingTextArea(20, 44);

        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);
        panel.add("North", label);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add("Center", scrollPane);
        String gameLabel = PluginManager.getInstance().getPlugin(gameName).getLabel();
        setTitle(gameLabel + " Server");

        getContentPane().add(panel);

        pack();
        setVisible(true);
    }

    /**
     * create and show the server.
     */
    public static void main(String[] args) {

        CommandLineOptions options = new CommandLineOptions(args);

        if (OnlineGameServer.verifyCmdLineOptions(options)) {
            String gameName = options.getValueForOption(OnlineGameServer.GAME_OPTION);
            OnlineGameServerFrame frame = new OnlineGameServerFrame(gameName);
            frame.setVisible(true);
        }
    }
}
