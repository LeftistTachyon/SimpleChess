package simplechessclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;

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
    private DoubleProperty whiteTime;
    
    /**
     * The amount of time black has
     */
    private DoubleProperty blackTime;
    
    /**
     * Whose turn it is
     */
    private boolean turn;
    
    /**
     * The action listeners for moves and promotions
     */
    private ArrayList<ActionListener> actionListeners;
    
    /**
     * The change listeners for times
     */
    private ArrayList<ChangeListener> changeListeners;
    
    /**
     * A new Time Control: start + add
     * @param start the starting amount of seconds
     * @param add the increment
     */
    public TimeControl(int start, int add) {
        actionListeners = new ArrayList<>();
        changeListeners = new ArrayList<>();
        startingSeconds = start;
        increment = add;
        whiteTime = new SimpleDoubleProperty(startingSeconds); 
        blackTime = new SimpleDoubleProperty(startingSeconds);
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
            // whiteTime += increment;
            whiteTime.set(whiteTime.get() + increment);
        } else {
            // blackTime += increment;
            blackTime.set(blackTime.get() + increment);
        }
        turn = !turn;
    }
    
    /**
     * Starts the clock
     */
    public void start() {
        turn = true;
        new Thread(this).start();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if(turn) {
            // whiteTime -= 0.1;
            if(whiteTime.get() > 0) {
                whiteTime.set(whiteTime.get() - 0.1);
                notifyChangeListeners(true);
            } else {
                notifyActionListeners("TIMEOUTtrue");
            }
        } else {
            // blackTime -= 0.1;
            if(blackTime.get() > 0) {
                blackTime.set(blackTime.get() - 0.1);
                notifyChangeListeners(false);
            } else {
                notifyActionListeners("TIMEOUTfalse");
            }
        }
        if(turn && whiteTime.get()%1 == 0.0) {
            System.out.println("White time: " + whiteTime);
        }
        if(!turn && blackTime.get()%1 == 0.0) {
            System.out.println("Black time: " + blackTime);
        }
    }
    
    /**
     * Resets the clock
     */
    public void reset() {
        whiteTime = new SimpleDoubleProperty(startingSeconds);
        blackTime = new SimpleDoubleProperty(startingSeconds);
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
        double time = (whichSide)?whiteTime.get():blackTime.get();
        if(time <= 0) {
            return "0:00.0";
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
    private void notifyActionListeners(String message) {
        for(ActionListener listener : actionListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, message));
        }
    }
    
    /**
     * Notifies all change listeners listening to this object
     */
    private void notifyChangeListeners(boolean white) {
        double after = ((white)?whiteTime:blackTime).get();
        for(ChangeListener changeListener : changeListeners) {
            changeListener.changed((white)?whiteTime:blackTime, after - 0.1, after);
        }
    }
    
    /**
     * Adds an action listener
     * @param al the one to add
     */
    public void addActionListener(ActionListener al) {
        actionListeners.add(al);
    }
    
    /**
     * Adds a change listener
     * @param cl the one to add
     */
    public void addChangeListener(ChangeListener cl) {
        changeListeners.add(cl);
    }
}