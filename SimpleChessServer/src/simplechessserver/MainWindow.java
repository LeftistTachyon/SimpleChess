package simplechessserver;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.LayoutStyle;
import simplechessserver.ClientCommunication.Handler;

/**
 * A window that constitutes the main part of the SimpleChessServer GUI
 * @author Jed Wang
 */
public class MainWindow extends JFrame {
    /**
     * An ArrayList of all of the handlers the server is servicing right now.
     * Keep this in alphabetical order!
     */
    private ArrayList<Handler> handlers;
    
    /**
     * Creates a new MainWindow and shows it.
     */
    public MainWindow() {
        super("SimpleChessServer Console");
        handlers = new ArrayList<>();
        DrawPanel dp = new DrawPanel();
        
        super.setPreferredSize(new Dimension(500, 600));
        super.setResizable(true);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dp.scrollBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(dp.scrollBar, GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                    .addComponent(dp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
        
        super.setVisible(true);
        new Thread(dp).start();
    }
    
    /**
     * Adds a Handler to the visible list on the console.
     * @param h the Handler to add
     */
    public void addHandler(Handler h) {
        String name = h.getName();
        int i;
        for(i = 0; i < handlers.size() && 
                handlers.get(i).getName().compareToIgnoreCase(name) <= 0; i++) {
        }
        handlers.add(i, h);
    }
    
    /**
     * Removes a Handler from the visible list on the console
     * @param h the Handler to remove
     */
    public void removeHandler(Handler h) {
        int i = Collections.binarySearch(handlers, h, 
                (Handler h1, Handler h2) -> h1.getName().compareTo(h2.getName()));
        handlers.remove(i);
    }
    
    /**
     * A JPanel that handles all of the drawing on the MainWindow.
     */
    private class DrawPanel extends JPanel implements Runnable {
        /**
         * An int which keeps track of which one is open for inspection
         */
        private int open = -1;
        
        /**
         * The scroll bar for this Panel
         */
        private JScrollBar scrollBar;
        
        /**
         * Which frame of the animation this item is on
         */
        private int animation = 0;
        
        /**
         * The total number of animation frames.
         * Must be a whole number
         */
        private final double animationFrames = 10;
        
        /**
         * Whether a bar is opening
         */
        private boolean opening = true;
        
        /**
         * The height of the quick info bar for each user
         */
        public static final int QUICK_INFO_HEIGHT = 80;
        
        /**
         * The height of the full info bar for the opened user
         */
        public static final int FULL_INFO_HEIGHT = 200;

        /**
         * Creates a new DrawPanel.
         */
        public DrawPanel() {
            scrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 10, 0, 100);
            
            super.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Point p = e.getPoint();
                    if(p.y >= getWidth() - 40 && p.y <= getWidth() - 10) {
                        
                    }
                }
            });
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
            // Some assertions
            if(animation > animationFrames) 
                assert false : "The animation counter is overflowing!";
            
            // Update scroll bar
            int max = (int) ((FULL_INFO_HEIGHT / animationFrames) * animation) + 
                    QUICK_INFO_HEIGHT * handlers.size();
            scrollBar.setValues(scrollBar.getValue(), getHeight(), 0, max);
            
            Graphics2D g2D = (Graphics2D) g;
            
            drawBackground(g2D, 
                    new GradientPaint(0, 0, new Color(215, 215, 215), 0, 
                            getHeight(), new Color(238, 238, 238))
            );
            
            g2D.translate(0, -scrollBar.getValue());
            
            g2D.setColor(Color.BLACK);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            final Font[] fonts = {new Font("Consolas", 0, 10), 
                new Font("Consolas", 0, 20), new Font("Consolas", 0, 30)};
            
            BasicStroke defaultStroke = new BasicStroke(2, 
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            
            // extra space needed for open things
            int extraSpace = (int) ((FULL_INFO_HEIGHT / animationFrames) * animation); 
            
            for(int i = 0; i < handlers.size(); i++) {
                Handler h = handlers.get(i);
                
                g2D.setStroke(defaultStroke);
                
                int add = (open != -1 && i > open)?extraSpace:0;
                
                g2D.drawLine(0, QUICK_INFO_HEIGHT*i+QUICK_INFO_HEIGHT + add, 
                        getWidth(), QUICK_INFO_HEIGHT*i + QUICK_INFO_HEIGHT + add);
                
                g2D.setFont(fonts[2]);
                g2D.drawString(h.getClientName(), 10, QUICK_INFO_HEIGHT*i+40+add);
                
                g2D.setFont(fonts[1]);
                g2D.drawString(h.socket.getInetAddress().toString(), 10, 
                        QUICK_INFO_HEIGHT*i+70+add);
                
                final int[] x = {getWidth() - 33, getWidth() - 25, 
                    getWidth() - 17};
                
                switch(h.getSide()) {
                    case 1:
                        g2D.setColor(Color.WHITE);
                        g2D.fillRoundRect(getWidth() - 110, 
                                QUICK_INFO_HEIGHT*i + 10 + add, 60, 60, 10, 10);
                        g2D.setColor(Color.BLACK);
                        g2D.drawRoundRect(getWidth() - 110, 
                                QUICK_INFO_HEIGHT*i + 10 + add, 60, 60, 10, 10);
                        break;
                    case 0:
                        // g2D.setColor(Color.BLACK);
                        g2D.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, 
                                BasicStroke.JOIN_ROUND, 1.0f, 
                                new float[]{7.5f}, 0.0f));
                        g2D.drawRoundRect(getWidth() - 110, 
                                QUICK_INFO_HEIGHT*i + 10 + add, 60, 60, 10, 10);
                        g2D.setStroke(defaultStroke);
                        break;
                    case -1:
                        // g2D.setColor(Color.BLACK);
                        g2D.fillRoundRect(getWidth() - 110, 
                                QUICK_INFO_HEIGHT*i + 10 + add, 60, 60, 10, 10);
                        break;
                }
                
                /*
                Hitbox: getWidth() - 40, QUICK_INFO_HEIGHT*i + 25, 30, 30
                */
                final int[] y_down = {QUICK_INFO_HEIGHT*i+33+add, 
                            QUICK_INFO_HEIGHT*i+47+add, 
                            QUICK_INFO_HEIGHT*i+33+add}, 
                        y_up = {QUICK_INFO_HEIGHT*i+47+add, 
                            QUICK_INFO_HEIGHT*i+33+add, 
                            QUICK_INFO_HEIGHT*i+47+add};
                
                if(i == open) {
                    double multDown = animation / animationFrames, 
                            multUp = 1 - multDown;
                    int[] y = new int[3];
                    for(int j = 0; j < y.length; j++) {
                        y[j] = (int) (y_down[j] * multDown + y_up[j] * multUp);
                    }
                    
                    Polygon tri = new Polygon(x, y, 3);
                    g2D.setColor(Color.GRAY);
                    g2D.fillPolygon(tri);
                    g2D.setColor(Color.BLACK);
                    g2D.drawPolygon(tri);
                    if(animation == 10) {
                        // Write out info
                        String s1;
                        switch(h.getSide()) {
                            case -1:
                                s1 = "Currently in a game as black against " + 
                                        h.getOpponentName();
                                break;
                            case 0:
                                s1 = "Currently not in a game";
                                break;
                            case 1:
                                s1 = "Currently in a game as white against " + 
                                        h.getOpponentName();
                                break;
                            default:
                                throw new IllegalStateException("Handler.side should be either 1, 0, or -1.");
                        }
                        g2D.drawString(s1, QUICK_INFO_HEIGHT, i);
                    }
                } else {
                    // standard triangle
                    Polygon tri = new Polygon(x, y_down, 3);
                    g2D.setColor(Color.GRAY);
                    g2D.fillPolygon(tri);
                    g2D.setColor(Color.BLACK);
                    g2D.drawPolygon(tri);
                }
            }
            
            // Update variables
            if(animation > 0 && animation < 10) {
                if(opening) animation++;
                else animation--;
            }
            if(animation == 0) {
                open = -1;
            }
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

        @Override
        public void run() {
            while(true) {
                repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.err.println("Thread was interrupted");
                }
            }
        }
    }
}