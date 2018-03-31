package simplechessserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Time control for chess
 * @author Jed Wang
 */
public class TimeControl implements Runnable {
    /**
     * The starting amount of seconds
     */
    private final int startingSeconds; 
    
    /**
     * The increment
     */
    private final int increment;
    
    /**
     * The amount of time white has
     */
    private double whiteTime;
    
    /**
     * The amount of time black has
     */
    private double blackTime;
    
    /**
     * Whose turn it is
     */
    private boolean turn;
    
    /**
     * Whether the clock is in a game
     */
    private boolean inGame = false;
    
    /**
     * The action listeners for moves and promotions
     */
    private ArrayList<ActionListener> listeners;
    
    /**
     * A new Time Control: start + add
     * @param start the starting amount of seconds
     * @param add the increment
     */
    public TimeControl(int start, int add) {
        listeners = new ArrayList<>();
        startingSeconds = start;
        increment = add;
        whiteTime = startingSeconds; 
        blackTime = startingSeconds;
    }
    
    /**
     * Creates a default TimeControl instance with (1+0).
     */
    public TimeControl() {
        this(60, 0);
    }
    
    /**
     * Hit the clock.
     */
    public void hit() {
        if(turn) {
            whiteTime += increment;
        } else {
            blackTime += increment;
        }
        turn = !turn;
    }
    
    /**
     * Starts the clock
     */
    public void start() {
        inGame = true;
        turn = true;
        new Thread(this).start();
    }
    
    /**
     * Stops the clock
     */
    public void stop() {
        inGame = false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        while(inGame) {
            if(turn) {
                whiteTime -= 0.1;
                if(whiteTime == 0) {
                    notifyListeners("TIMEOUTtrue");
                }
            } else {
                blackTime -= 0.1;
                if(blackTime == 0) {
                    notifyListeners("TIMEOUTfalse");
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Interrupted: " + ex.toString());
                return;
            }
        }
    }
    
    /**
     * Resets the clock
     */
    public void reset() {
        whiteTime = startingSeconds;
        blackTime = startingSeconds;
    }

    /**
     * {@inheritDoc}
     * @return the current time state of the game
     */
    @Override
    public String toString() {
        return toString(true) + "|" + toString(false);
    }
    
    /**
     * Constructs a String that represents the current time state of the given player.
     * @param whichSide which side to get time
     * @return a String representation of their current time state
     */
    public String toString(boolean whichSide) {
        double time = (whichSide)?whiteTime:blackTime;
        if(time <= 0) {
            return "0:00.00";
        } else if(time <= 20) {
            return String.format("0:%.1f", time);
        } else if(time < 3600) {
            // return (int)(time / 60) + ":" + (int)(time % 60);
            return String.format("%d:%02d", (int)(time / 60), (int)(time % 60));
        } else {
            // return (int)(time / 3600) + ":" + (int)((time / 60) % 60);
            return String.format("%d:%02d", (int)(time / 3600), (int)((time / 60) % 60));
        }
    }
    
    /**
     * Notifies all action listeners listening to this object
     * @param message the message to give to all of the listeners
     */
    private void notifyListeners(String message) {
        for(ActionListener listener : listeners) {
            listener.actionPerformed(new ActionEvent(this, 0, message));
        }
    }
    
    /**
     * Adds an action listener
     * @param al the one to add
     */
    public void addActionListener(ActionListener al) {
        listeners.add(al);
    }
}