package offlinechess;

import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * The Panel which you can draw on
 * @author Jed Wang
 */
public class ChessPanel extends JPanel {
    
    /**
     * The chessboard
     */
    private ChessBoard cb;
    
    /**
     * The mouse listener
     */
    private ChessMouseListener cml;
    
    /**
     * A reference to the most recent ChessPanel created
     */
    private static ChessPanel _this;
    
    /**
     * When to stop the game
     */
    private volatile boolean stop = false;
    
    /**
     * Default constructor
     */
    public ChessPanel() {
        cml = new ChessMouseListener(this);
        _this = this;
        cb = new ChessBoard();
        cb.recalculateMoves();
        addMouseListener(cml);
        super.setVisible(true);
    }

    /**
     * Updates the current rendering
     * @param g Graphics to draw on
     */
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Renders the image
     * @param g Graphics to draw on
     */
    @Override
    public void paint(Graphics g) {
        drawBackground((Graphics2D) g, 
                new GradientPaint(0, 0, new Color(215, 215, 215), 0, 
                        getHeight(), new Color(238, 238, 238))
        );
        cb.draw(g);
    }
    
    /**
     * Paints the background a solid color
     * @param g the Graphics to draw on
     * @param c The color to draw the background
     */
    private void drawBackground(Graphics g, Color c) {
        g.setColor(c);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    /**
     * Paints the background a solid color
     * @param g2D the Graphics2D to draw on
     * @param p The paint to paint the background
     */
    private void drawBackground(Graphics2D g2D, Paint p) {
        g2D.setPaint(p);
        g2D.fillRect(0, 0, getWidth(), getHeight());
    }
    
    /**
     * Notifies this of a MouseEvent
     * @param me the MouseEvent
     * @param i what fired this event (See: <code>ChessMouseListener.MOUSE_?</code>)
     */
    public void notify(MouseEvent me, int i) {
        String selected;
        switch(i) {
            case ChessMouseListener.MOUSE_CLICKED:
                selected = cb.toSquareFromPos(me.getX(), me.getY());
                if(ChessBoard.isValidSquare(selected)) cb.clicked(selected);
                break;
            case ChessMouseListener.MOUSE_PRESSED:
                selected = cb.toSquareFromPos(me.getX(), me.getY());
                if(ChessBoard.isValidSquare(selected)) cb.enableDragging(selected);
                break;
            case ChessMouseListener.MOUSE_RELEASED:
                cb.disableDragging();
                break;
        }
        repaint();
    }
    
    /**
     * Determines where the mouse currently is
     * @return A point representing the mouse's position
     */
    public static Point getMouseCoordinates() {
        return _this.getMousePosition();
    }
    
    /**
     * A method that starts the redrawing of the chess board constantly
     */
    public void start() {
        new Thread() {
            @Override
            public void run() {
                while(!stop) {
                    repaint();
                    /*try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }*/
                }
                System.out.println("Thread stopped!");
            }
        }.start();
    }
    
    /**
     * Stops this thread and redrawing the chess board.
     */
    public void stop() {
        stop = true;
        cb.printMoves();
    }

    /**
     * Returns this ChessPanel's ChessBoard
     * @return this ChessPanel's ChessBoard
     */
    public ChessBoard getChessBoard() {
        return cb;
    }
    
    /**
     * Resets the chess board
     */
    public void resetChessBoard() {
        cb = new ChessBoard();
    }
}