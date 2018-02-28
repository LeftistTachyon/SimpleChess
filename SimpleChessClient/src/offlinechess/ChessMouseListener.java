package offlinechess;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * A MouseListener for the chess application
 * @author Jed Wang
 */
public class ChessMouseListener implements MouseListener {
    
    /**
     * Represents mouse pressed
     */
    public static final int MOUSE_PRESSED = 0;
    
    /**
     * Represents mouse released
     */
    public static final int MOUSE_RELEASED = 1;
    
    /**
     * Represents mouse clicked
     */
    public static final int MOUSE_CLICKED = 2;
    
    /**
     * The ChessPanel to notify
     */
    private ChessPanel cp;
    
    /**
     * Constructor method.
     */
    public ChessMouseListener() {
        super();
    }
    
    /**
     * Constructor method.
     * @param cp the ChessPanel to notify
     */
    public ChessMouseListener(ChessPanel cp) {
        super();
        this.cp = cp;
    }
     
    @Override
    public void mousePressed(MouseEvent e) {
        cp.notify(e, MOUSE_PRESSED);
    }
     
    @Override
    public void mouseReleased(MouseEvent e) {
        cp.notify(e, MOUSE_RELEASED);
    }
     
    @Override
    public void mouseClicked(MouseEvent e) {
        cp.notify(e, MOUSE_CLICKED);
    }
     
    @Override
    public void mouseEntered(MouseEvent e) {
    }
     
    @Override
    public void mouseExited(MouseEvent e) {
    }
}