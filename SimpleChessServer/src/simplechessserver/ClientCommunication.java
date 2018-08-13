package simplechessserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import offlinechess.ChessBoard;

/**
 * A server to client communication
 * @author Jed Wang
 */
public class ClientCommunication {
    /**
     * The port to communicate over
     */
    public static final int PORT = 9001;
    
    /**
     * The set of all names of clients in the chat room.  Maintained
     * so that we can check that new clients are not registering name
     * already in use.
     */
    private static HashSet<String> names = new HashSet<>();
    
    /**
     * The queue of all clients who are unmatched. Maintained so that 
     * new clients can be easily paired.
     */
    private static Queue<Handler> unmatched = new LinkedList<>();
    
    /**
     * A map of all of the matches that are currently being played 
     * out. The int is for indexing the matches.
     */
    private static HashMap<Integer, Handler> matchedHandlers = new HashMap<>();
    
    /**
     * The lock for synchronization for matchmaking.
     */
    private static final Object MATCH_LOCK = new Object();
    
    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for a dealing with a single client
     * and broadcasting its messages.
     */
    public static class Handler extends Thread implements Comparable<Handler> {
        /**
         * This client's name
         */
        private String name;
        
        /**
         * This client's socket
         */
        public final Socket socket;
        
        /**
         * Messaging to here
         */
        private BufferedReader in;
        
        /**
         * Message from here
         */
        private PrintWriter out;
        
        /**
         * This instance's chess board.
         */
        private ChessBoard cb;
        
        /**
         * This instance's time control.
         */
        private TimeControl tc;
        
        /**
         * The opponent's ID
         */
        private int opponentID = -1;
        
        /**
         * Which side this client is on in a game
         */
        private int side = 0;
        
        /**
         * An ArrayList of EventListeners listening in on this object
         */
        private ActionListener listener = null;
        
        /**
         * The MainWindow that displays all of the Handlers.
         */
        private static MainWindow mainWindow = null;
        
        /**
         * Constructs a handler thread, squirreling away the socket.
         * All the interesting work is done in the run method.
         * @param socket the socket that receives info from the client
         */
        public Handler(Socket socket) {
            this.socket = socket;
            cb = new ChessBoard();
            cb.recalculateMoves();
        }
        
        /**
         * Services this thread's client by repeatedly requesting a
         * screen name until a unique one has been submitted, then
         * acknowledges the name and registers the output stream for
         * the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        @Override
        public void run() {
            try {
                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Request a name from this client.  Keep requesting until
                // a name is submitted that is not already used.  Note that
                // checking for the existence of a name and adding the name
                // must be done while locking the set of names.
                while(true) {
                    out.println("SUBMITNAME");
                    notify("SUBMITNAME", false);
                    name = in.readLine();
                    notify(name, true);
                    if(name == null) return;
                    if("".equals(name) || "null".equals(name)) continue;
                    synchronized(names) {
                        if(!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }

                // Now that a successful name has been chosen, add the
                // socket's print writer to the set of all writers so
                // this client can receive broadcast messages.
                out.println("NAMEACCEPTED");
                notify("NAMEACCEPTED", false);

                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
                while(true) {
                    String line = in.readLine();
                    notify(line, true);
                    if(line == null) {
                        return;
                    }
                    // handle input
                    String message = null;
                    if(line.startsWith("NEWOPPONENT")) {
                        if(unmatched.contains(this)) continue;
                        unmatched.add(this);
                        if(unmatched.size() >= 2) {
                            synchronized(MATCH_LOCK) {
                                Handler one = unmatched.remove(), two = unmatched.remove();
                                int iD = matchedHandlers.size();
                                matchedHandlers.put(iD, one);
                                one.opponentID = iD + 1;
                                matchedHandlers.put(iD+1, two);
                                two.opponentID = iD;
                                // STARTGAMEside name
                                one.tc = new TimeControl();
                                two.tc = new TimeControl();
                                one.tc.addActionListener((ActionEvent e) -> {
                                    String message1 = e.getActionCommand();
                                    boolean lost = Boolean.parseBoolean(message1.substring(7));
                                    String sideWon = lost?"-1":"1";
                                    one.endGame("ENDGAME" + sideWon + " time");
                                });
                                two.tc.addActionListener((ActionEvent e) -> {
                                    String message1 = e.getActionCommand();
                                    boolean lost = Boolean.parseBoolean(message1.substring(7));
                                    String sideWon = lost?"-1":"1";
                                    two.endGame("ENDGAME" + sideWon + " time");
                                });
                                if(Math.random() < 0.5) {
                                    one.out.println("STARTGAMEtrue " + two.name);
                                    one.notify("STARTGAMEtrue " + two.name, false);
                                    one.side = 1;
                                    two.out.println("STARTGAMEfalse " + one.name);
                                    two.notify("STARTGAMEfalse " + one.name, false);
                                    two.side = -1;
                                } else {
                                    one.out.println("STARTGAMEfalse " + two.name);
                                    one.notify("STARTGAMEfalse " + two.name, false);
                                    one.side = -1;
                                    two.out.println("STARTGAMEtrue " + one.name);
                                    two.notify("STARTGAMEtrue " + one.name, false);
                                    two.side = 1;
                                }
                                one.tc.start();
                                two.tc.start();
                                one.cb.recalculateMoves();
                                two.cb.recalculateMoves();
                            }
                        }
                    } else if(line.startsWith("MOVE") && opponentID != -1) {
                        String[] data = line.substring(4).split(" ");
                        cb.movePiece(Integer.parseInt(data[0]), 
                                Integer.parseInt(data[1]));
                        Handler opponent = matchedHandlers.get(opponentID);
                        tc.hit();
                        opponent.tc.hit();
                        opponent.cb.movePiece(Integer.parseInt(data[0]), 
                                Integer.parseInt(data[1]));
                        opponent.out.println(line);
                        opponent.notify(line, false);
                    } else if(line.startsWith("PROMOTE") && opponentID != -1) {
                        String[] data = line.substring(7).split(" ");
                        cb.promotePiece(Integer.parseInt(data[0]), 
                                Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                        Handler opponent = matchedHandlers.get(opponentID);
                        tc.hit();
                        opponent.tc.hit();
                        opponent.cb.promotePiece(Integer.parseInt(data[0]), 
                                Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                        opponent.out.println(line);
                        opponent.notify(line, false);
                    } else if(line.startsWith("PING")) {
                        out.println("PING");
                        notify("PING", false);
                    }
                    //message = "ENDGAME0 aborted"
                    if(cb.insufficientMaterial()){
                        message = "ENDGAME0 insufficient_material";
                    } else if(cb.is50MoveDraw()) {
                        message = "ENDGAME0 50_move_draw";
                    } else if(cb.stalemated(cb.currentPlayer())) {
                        message = "ENDGAME0 stalemate";
                        System.out.println(cb.stalemated(!cb.currentPlayer()));
                        System.out.println(cb.stalemated(cb.currentPlayer()));
                    } else if(cb.threeFoldRep()) {
                        message = "ENDGAME0 3-fold_repetition";
                    } else if(cb.checkMated(true)) {
                        message = "ENDGAME-1 checkmate";
                    } else if(cb.checkMated(false)) {
                        message = "ENDGAME1 checkmate";
                    }
                    if(message != null && opponentID != -1) {
                        endGame(message);
                    }
                }
            } catch (IOException e) {
                println(e.toString());
            } finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
                String message = "ENDGAME" + -side + " resignation";
                Handler opponent = matchedHandlers.get(opponentID);
                if(opponent != null) {
                    opponent.out.println(message);
                    opponent.notify(message, false);
                    opponent.tc.stop();
                    opponent.reset();
                }
                if(mainWindow != null) {
                    mainWindow.removeHandler(this);
                }
                if(tc != null) {
                    tc.stop();
                }
                if(name != null) {
                    names.remove(name);
                }
                if(unmatched.contains(this)) {
                    unmatched.remove(this);
                }
                if(matchedHandlers.containsValue(this)) {
                    List<Handler> temp = new ArrayList<>(matchedHandlers.values());
                    Collections.sort(temp);
                    matchedHandlers.remove(Collections.binarySearch(temp, this));
                }
                out.close();
                try {
                    in.close();
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        @Override
        public String toString() {
            return name;
        }
        
        /**
         * Prints something with a carriage return afterwards
         * @param s a string to println
         */
        public void println(String s) {
            System.out.println(name + ": " + s);
        }

        @Override
        public int compareTo(Handler h) {
            return name.compareTo(h.name);
        }
        
        /**
         * Ends the current game and releases this client to play another game.
         * @param message the message to broadcast to both players
         */
        public void endGame(String message) {
            Handler opponent = matchedHandlers.get(opponentID);
            if(opponent != null) {
                opponent.out.println(message);
                opponent.notify(message, false);
            }
            out.println(message);
            notify(message, false);
            if(opponent != null) opponent.reset();
            reset();
        }
        
        /**
         * Resets the variables to default values
         */
        public void reset() {
            tc.stop();
            matchedHandlers.remove(opponentID);
            opponentID = -1;
            side = 0;
            cb = new ChessBoard();
        }
        
        /**
         * Sets the listening ActionListener to the one given
         * @param al the ActionListener to use to listen
         */
        public void setActionListener(ActionListener al) {
            listener = al;
        }
        
        /**
         * Clears the listening ActionListener so it isn't listening anymore
         */
        public void clearActionListener() {
            listener = null;
        }
        
        /**
         * Notifies the listener that an event happened
         * @param s the command
         * @param isIn whether the message is inbound or outbound
         */
        public void notify(String s, boolean isIn) {
            if(listener != null) {
                String tag = (isIn)?name + ": ":"SERVER: ";
                listener.actionPerformed(new ActionEvent(this, 1, tag + s));
            }
        }

        /**
         * Sets the MainWindow
         * @param mainWindow which MainWindow to use to show every Handler
         */
        public static void setMainWindow(MainWindow mainWindow) {
            Handler.mainWindow = mainWindow;
        }

        /**
         * Returns the client's name
         * @return the client's name
         */
        public String getClientName() {
            return name;
        }

        /**
         * Returns which side of a game this client is in, if any.
         * @return the current game status of this client
         */
        public int getSide() {
            return side;
        }
        
        /**
         * Determines the opponent's name, if in a game.
         * @return the opponent's name, or null if this user is unpaired.
         */
        public String getOpponentName() {
            if(opponentID == -1) {
                return null;
            } else {
                return matchedHandlers.get(opponentID).name;
            }
        }

        /**
         * Returns this Handler's virtual chess board
         * @return this Handler's chess board
         */
        public ChessBoard getChessBoard() {
            return cb;
        }
        
        /**
         * Returns this Handler's virtual chess clock
         * @return this Handler's TimeControl
         */
        public TimeControl getTimeControl() {
            return tc;
        }
    }
}