/** Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.server;

import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.game.twoplayer.go.GoController;
import gtp.GtpServer;
import utils.ErrorMessage;
import utils.Options;
import utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This class wraps the GoController and provides an interface to a GTP based controller (front end ui).
 * GoGui is typically the controller I have used, but it could be any GTP based UI.
 *
 * This wraps my Tesujisoft go engine so that a client program can use it.
 * If you want to have the Tesujisoft go engine play on KGS use GtpKgsTesujisoftGoClient.
 *
 * Inherits the ability to connect a Go program supporting GTP to a socket from GtpServer.
 *
 *  @author Barry Becker
 */
public class GtpTesujisoftGoServer extends GtpServer{

    private Thread thread_;
    private CommandHandler cmdHandler;

    /**
     * Constructor
     */
    public GtpTesujisoftGoServer(InputStream in, OutputStream out, PrintStream logger) throws Exception {

        super(in, out, logger);

        cmdHandler = new CommandHandler();
        thread_ = Thread.currentThread();
    }

    @Override
    public boolean handleCommand(String cmdLine, StringBuffer response) {

        return cmdHandler.handleCommand(cmdLine, response);
    }

    @Override
    public void interruptCommand() {
        thread_.interrupt();
    }

    /**
     * Handle the command line arguments
     */
    private static PrintStream handleArgs(String[] args) throws ErrorMessage, FileNotFoundException {
        String[] options = {
            "config:",
            "help",
            "log",
            "version"
        };
        Options opt = Options.parse(args, options);
        if (opt.isSet("help")) {
            String helpText =
                "Usage: java -classpath "+
                    FileUtil.PROJECT_HOME +
                    "/classes com.barrybecker4.game.twoplayer.go.server.GtpTesujisoftGoServer [options]\n" +
                    '\n' +
                "-config       config file\n" +
                "-help         display this help and exit\n" +
                "-log file     log GTP stream to file\n" +
                "-version      print version and exit\n";
            System.out.print(helpText);
            return null;
        }
        if (opt.isSet("version")) {
            System.out.println("GtpTesujisoft " + GoController.VERSION);
            return null;
        }
        PrintStream log;
        if (opt.isSet("log")) {
            File file = new File(opt.getString("log"));
            log = new PrintStream(new FileOutputStream(file));
        }
        else {
            String logFile = FileUtil.getHomeDir() + "/temp/" + "log.txt";
            File file = new File(logFile);
            log = new PrintStream(new FileOutputStream(file));
        }
        return log;
    }

    //-----------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        try {
            PrintStream log = handleArgs(args);

            GtpTesujisoftGoServer gtpTSGoServer = new GtpTesujisoftGoServer(System.in, System.out, log);
            gtpTSGoServer.mainLoop();
            log.close();
        }
        catch (Throwable t)  {
            StringUtils.printException(t);
            System.exit(-1);
        }
    }
}

