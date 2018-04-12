package simplechessclient;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import offlinechess.ChessBoard;
import offlinechess.ChessFrame;

/**
 * A client to server communication
 * @author Jed Wang
 */
public class ServerCommunication {
    /**
     * The socket connection into this
     */
    private BufferedReader in;
    
    /**
     * The socket connection out of this
     */
    private PrintWriter out;
    
    /**
     * This instance's chess frame
     */
    private ChessFrame cf;
    
    /**
     * This instance's chess board
     */
    private ChessBoard cb;
    
    /**
     * This instance's time control
     */
    private TimeControl tc;
    
    /**
     * Standard constructor.
     * @throws java.io.IOException if something goes wrong
     */
    public ServerCommunication() throws IOException {
        try {
            cf = new ChessFrame();
            cb = cf.getChessPanel().getChessBoard();
            cb.addActionListener((ActionEvent e) -> {
                String message = e.getActionCommand();
                if (message.startsWith("MOVE") || message.startsWith("PROMOTE")) {
                    out.println(e.getActionCommand());
                    System.out.println(e.getActionCommand());
                    if (tc != null) {
                        tc.hit();
                    }
                }
            });
            cb.lock();
            run();
        } catch (IllegalStateException ise) {}
    }
    
    /**
     * Determines whether the connection is still open<br>
     * (if the application is still open)
     * @return whether the connection is open
     */
    public boolean isOpen() {
        return cf.isVisible();
    }
    
    /**
     * Stops communications with the server and stops this application.
     */
    public void close() {
        cf.stop();
    }
    
    /**
     * Connects to the server then enters the processing loop.
     * @throws java.io.IOException if something goes wrong
     */
    private void run() throws IOException {
        // Make connection and initialize streams
        String serverAddress;
        Socket socket = null;
        do {
            serverAddress = getServerAddress();
            try {
                socket = new Socket(serverAddress, 9001);
            } catch (ConnectException | UnknownHostException ex) {
                //JOptionPane.showMessageDialog(cf, ex.getMessage(),
                //        "Connection Error", JOptionPane.ERROR_MESSAGE);
                Object[] options = {"Reenter IP Adress", "Exit"};
                int returned = JOptionPane.showOptionDialog(cf, ex.getMessage(), 
                        "Connection Error", JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                if(returned != JOptionPane.OK_OPTION) {
                    cf.dispose();
                    System.exit(0);
                    return;
                }
            }
        } while(socket == null);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        
        LinkedList<JFrame> gameFrames = new LinkedList<>();
        ScheduledExecutorService service = null;
        String _name = null;
        
        while(true) {
            String line = in.readLine();
            if(line == null) {
                return;
            }
            if(line.startsWith("SUBMITNAME")) {
                _name = getName();
                out.println(_name);
                System.out.println(_name);
            } else if(line.startsWith("NAMEACCEPTED")) {
                // init stuff
                cf.start();
                try {
                    Thread.sleep(7500);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(cf, ex.getMessage(), 
                            "Thread Interrupted", JOptionPane.ERROR_MESSAGE);
                    cf.dispose();
                    System.exit(0);
                    return;
                }
                out.println("NEWOPPONENT");
                System.out.println("NEWOPPONENT");
                cf.getChessPanel().resetChessBoard();
                cb = cf.getChessPanel().getChessBoard();
                cb.addActionListener((ActionEvent e) -> {
                    String message = e.getActionCommand();
                    if(message.startsWith("MOVE") || message.startsWith("PROMOTE")) {
                        out.println(e.getActionCommand());
                        System.out.println(e.getActionCommand());
                        if(tc != null) tc.hit();
                    }
                });
                cb.lock();
            } else if(line.startsWith("STARTGAME")) {
                // STARTGAMEside name /*timecontrolMin timecontrolSec*/ gameID
                String[] data = line.substring(9).split(" ");
                
                tc = new TimeControl();
                service = Executors.newScheduledThreadPool(1);
                service.scheduleAtFixedRate(tc, 0, 100, TimeUnit.MILLISECONDS);
                
                Point cfLocation = cf.getLocation();
                JFrame tempFrame = GameWindows.showTimeWindow(tc);
                tempFrame.setLocation(cfLocation.x + tempFrame.getWidth() / 2, 
                        cfLocation.y + cf.getHeight() + 20);
                gameFrames.add(tempFrame);
                
                cb.setPerspective(Boolean.parseBoolean(data[0]));
                // data[1] will be other person's name
                tempFrame = GameWindows.showNameWindow(_name, true);
                tempFrame.setLocation(cfLocation.x - 20 - tempFrame.getWidth(), 
                        cfLocation.y);
                gameFrames.add(tempFrame);
                tempFrame = GameWindows.showNameWindow(data[1], false);
                tempFrame.setLocation(cfLocation.x - 20 - tempFrame.getWidth(), 
                        cfLocation.y + cf.getHeight() - tempFrame.getHeight());
                gameFrames.add(tempFrame);
                cb.recalculateMoves();
                cb.unlock();
            } else if(line.startsWith("ENDGAME")) {
                for(Iterator<JFrame> it = gameFrames.iterator(); it.hasNext();) {
                    JFrame next = it.next();
                    next.dispose();
                }
                gameFrames.removeAll(gameFrames);
                if(service != null) service.shutdown();
                // ENDGAMEresult why
                String[] data = line.substring(7).split(" ");
                notifyResult(data[0], data[1]);
                cf.getChessPanel().resetChessBoard();
                cb = cf.getChessPanel().getChessBoard();
                cb.addActionListener((ActionEvent e) -> {
                    String message = e.getActionCommand();
                    if(message.startsWith("MOVE") || message.startsWith("PROMOTE")) {
                        out.println(e.getActionCommand());
                        System.out.println(e.getActionCommand());
                        if(tc != null) tc.hit();
                    }
                });
                cb.lock();
                out.println("NEWOPPONENT");
                System.out.println("NEWOPPONENT");
            } else if(line.startsWith("MOVE")) {
                tc.hit();
                // MOVEfrom to
                String[] data = line.substring(4).split(" ");
                cb.movePiece(data[0], data[1]);
            } else if(line.startsWith("PROMOTE")) {
                tc.hit();
                // PROMOTEfrom to whatTo
                String[] data = line.substring(7).split(" ");
                cb.promotePiece(data[0], data[1], Integer.parseInt(data[2]));
            }
        }
    }
    
    /**
     * Prompt for and return the address of the server.
     */
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            cf,
            "Enter IP Address of the Server:",
            "Welcome to Simple Chess",
            JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Prompt for and return the desired screen name.
     */
    private String getName() {
        String s = null;
        do {
            s = JOptionPane.showInputDialog(
                cf,
                "Choose a screen name (no spaces):",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE);
            if(s == null) System.exit(0);
        } while(s.contains(" ") || "".equals(s));
        return s;
    }
    
    /**
     * Prompt to notify the user that the game has ended
     * @param result who won
     * @param why why it happened
     */
    private void notifyResult(String result, String why) {
        String message;
        switch(result) {
            case "0":
                message = "Drew by ";
                break;
            case "1":
                message = "White won by ";
                break;
            case "-1":
                message = "Black won by ";
                break;
            default:
                return;
        }
        JOptionPane.showMessageDialog(cf, message + why, 
                "Game has ended", JOptionPane.INFORMATION_MESSAGE);
    }
}