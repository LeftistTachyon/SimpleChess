package offlinechess;

import java.awt.Dimension;
import javax.swing.JFrame;

/**
 * A Frame for the application
 * @author Jed Wang
 */
public class ChessFrame extends JFrame {
    
    /**
     * The content panel/chess panel
     */
    private ChessPanel cp;
    
    /**
     * Default constructor
     */
    public ChessFrame() {
        super("Simple Server Chess");
        cp = new ChessPanel();
        setSize(new Dimension(535, 560));
        super.getContentPane().add(cp);
        super.setResizable(false);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setVisible(true);
        cp.start();
    }
    
    /**
     * Starts up this Chess Frame
     */
    public void start() {}
    
    /**
     * Stops redrawing the chess board
     */
    public void stop() {
        System.out.println("STOP!");
        cp.stop();
    }

    /**
     * Returns this instance's ChessPanel
     * @return this instance's ChessPanel
     */
    public ChessPanel getChessPanel() {
        return cp;
    }
}