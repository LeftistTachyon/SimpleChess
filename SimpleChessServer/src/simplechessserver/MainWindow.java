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
import offlinechess.AbstractPiece;
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
     * This instance's DrawPanel.
     */
    private DrawPanel dp;
    
    /**
     * Creates a new MainWindow and shows it.
     */
    public MainWindow() {
        super("SimpleChessServer Console");
        handlers = new ArrayList<>();
        dp = new DrawPanel();
        
        super.setPreferredSize(new Dimension(750, 500));
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
        
        dp.addEntry();
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
         * An ArrayList of Integers which keeps track of which one 
         * is open for inspection.
         */
        private ArrayList<Integer> animations;
        
        /**
         * The scroll bar for this Panel
         */
        private JScrollBar scrollBar;
        
        /**
         * The total number of animation frames.
         * Must be a whole number
         */
        private static final double ANIMATION_FRAMES = 10;
        
        /**
         * An ArrayList of Booleans that keeps track which direction 
         * a bar is opening.
         */
        private ArrayList<Boolean> directions;
        
        /**
         * The height of the quick info bar for each user
         */
        public static final int QUICK_INFO_HEIGHT = 80;
        
        /**
         * The height of the full info bar for the opened user
         */
        public static final int FULL_INFO_HEIGHT = 200;
        
        /**
         * The amount a bar expands per frame
         */
        public static final int EXPANSION = 
                (int) (FULL_INFO_HEIGHT / ANIMATION_FRAMES);

        /**
         * Creates a new DrawPanel.
         */
        public DrawPanel() {
            animations = new ArrayList<>();
            directions = new ArrayList<>();
            scrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 10, 0, 100);
            
            super.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Point p = e.getPoint();
                    for(int i = 0; i < handlers.size(); i++) {
                        int animation = animations.get(i);
                        int y0 = calculateSpace(i);
                        if (animation == 10 && p.x >= 15 && p.x <= 140) {
                            int baseY = y0 + QUICK_INFO_HEIGHT + 
                                    FULL_INFO_HEIGHT - 35;
                            if (p.y >= baseY && p.y <= baseY + 25) {
                                LogWindow.run(handlers.get(i));
                                return;
                            }
                        }
                        if (p.x >= getWidth() - 40 && p.x <= getWidth() - 10) {
                            if(p.y > y0 + QUICK_INFO_HEIGHT)
                                continue;
                            // thus, p.y <= y0 + QUICK_INFO_HEIGHT
                            int mod = p.y - y0;
                            if (mod >= 25 && mod <= 55) {
                                toggleOpen(i);
                                return;
                            }
                        }
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
            
            // Update scroll bar
            scrollBar.setValues(scrollBar.getValue(), getHeight(), 0, 
                    calculateMax());
            
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
            final Font[] fonts = {new Font("Consolas", 0, 12), 
                new Font("Consolas", 0, 20), new Font("Consolas", 0, 30)};
            
            BasicStroke defaultStroke = new BasicStroke(2, 
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            
            for(int i = 0; i < handlers.size(); i++) {
                Handler h = handlers.get(i);
                int animation = animations.get(i);
                int y0 = calculateSpace(i), extraSpace = animation * EXPANSION;
                
                g2D.setStroke(defaultStroke);
                
                g2D.drawLine(0, y0 + QUICK_INFO_HEIGHT, getWidth(), 
                        y0 + QUICK_INFO_HEIGHT);
                
                g2D.setFont(fonts[2]);
                String clientName = h.getClientName();
                g2D.drawString((clientName == null)?"":clientName, 10, y0 + 40);
                
                g2D.setFont(fonts[1]);
                g2D.drawString(h.socket.getInetAddress().getHostAddress(), 10, 
                        y0 + 70);
                
                final int[] x = {getWidth() - 33, getWidth() - 25, 
                    getWidth() - 17};
                
                switch(h.getSide()) {
                    case 1:
                        g2D.setColor(Color.WHITE);
                        g2D.fillRoundRect(getWidth() - 110, y0 + 10, 
                                60, 60, 10, 10);
                        g2D.setColor(Color.BLACK);
                        g2D.drawRoundRect(getWidth() - 110, y0 + 10, 
                                60, 60, 10, 10);
                        break;
                    case 0:
                        // g2D.setColor(Color.BLACK);
                        g2D.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, 
                                BasicStroke.JOIN_ROUND, 1.0f, 
                                new float[]{7.5f}, 0.0f));
                        g2D.drawRoundRect(getWidth() - 110, y0 + 10, 
                                60, 60, 10, 10);
                        g2D.setStroke(defaultStroke);
                        break;
                    case -1:
                        // g2D.setColor(Color.BLACK);
                        g2D.fillRoundRect(getWidth() - 110, y0 + 10, 
                                60, 60, 10, 10);
                        break;
                }
                
                /*
                Hitbox: getWidth() - 40, QUICK_INFO_HEIGHT*i + 25, 30, 30
                */
                final int[] y_down = {y0 + 33, y0 + 47, y0 + 33}, 
                        y_up = {y0 + 47, y0 + 33, y0 + 47};
                
                if(animations.get(i) != 0) {
                    double multUp = animation / ANIMATION_FRAMES, 
                            multDown = 1 - multUp;
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
                        g2D.setFont(fonts[0]);
                        
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
                        g2D.drawString(s1, 15, y0+QUICK_INFO_HEIGHT+20);
                        
                        String s2 = "";
                        byte[] address = h.socket.getInetAddress().getAddress();
                        for(int j = 0; j < address.length; j++) {
                            s2 += address[j];
                            if(j != address.length - 1) {
                                s2 += ".";
                            }
                        }
                        g2D.drawString("IP: " + s2 + " - " + h.socket.getInetAddress().getHostAddress(), 
                                15, y0+QUICK_INFO_HEIGHT+40);
                        
                        Point tl = new Point(15, 
                                y0+QUICK_INFO_HEIGHT+extraSpace-35), 
                                br = new Point(tl.x+125, tl.y+25);
                        g2D.setPaint(new GradientPaint(tl, Color.LIGHT_GRAY, br, Color.GRAY));
                        g2D.fillRect(tl.x, tl.y, 125, 25);
                        g2D.setColor(Color.BLACK);
                        g2D.drawString("Open log window", 22.5f, 
                                y0+QUICK_INFO_HEIGHT+extraSpace-17.5f);
                        
                        if(h.getSide() != 0) {
                            final int SQ = 20;
                            int baseY = y0+QUICK_INFO_HEIGHT+10, 
                                    baseX = getWidth() - 8*SQ - 10;
                            
                            g2D.setColor(new Color(181, 136, 99));
                            g2D.fillRect(baseX, baseY, 8*SQ, 8*SQ);
                            g2D.setColor(new Color(240, 217, 181));
                            for(int j = baseX;j<8*SQ+baseX;j+=SQ*2) {
                                for(int k = baseY;k<8*SQ+baseY;k+=SQ*2) {
                                    g2D.fillRect(j, k, SQ, SQ);
                                    g2D.fillRect(j+SQ, k+SQ, SQ, SQ);
                                }
                            }
                            
                            AbstractPiece[][] board = h.getChessBoard().getBoard();
                            for(int r = 0; r < board.length; r++) {
                                for(int c = 0; c < board[r].length; c++) {
                                    if(board[r][c] == null) continue;
                                    board[r][c].draw(g2D, 20*r+baseX, 
                                            20*c+baseY, 20, 20);
                                }
                            }
                            
                            TimeControl tc = h.getTimeControl();
                            g2D.setFont(fonts[1]);
                            g2D.setColor(Color.BLACK);
                            g2D.drawString(tc.toString(), baseX, baseY+8*SQ+25);
                        }
                    }
                    
                    g2D.setColor(Color.BLACK);
                    g2D.drawLine(0, y0+QUICK_INFO_HEIGHT+extraSpace, 
                            getWidth(), y0+QUICK_INFO_HEIGHT+extraSpace);
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
            updateVariables();
        }
        
        /**
         * Toggles opening a window describing the client
         * @param client which client to toggle
         */
        public void toggleOpen(int client) {
            int animation = animations.get(client);
            if(animation == 0) {
                animations.set(client, animation + 1);
            } else if(animation == 10) {
                animations.set(client, animation - 1);
            }
            directions.set(client, !directions.get(client));
        }
        
        /**
         * Updates all variables
         */
        private void updateVariables() {
            for(int i = 0; i < handlers.size(); i++) {
                int animation = animations.get(i);
                if(animation > 0 && animation < 10) {
                    if(directions.get(i)) animations.set(i, animation + 1);
                    else animations.set(i, animation - 1);
                }
            }
        }
        
        /**
         * Adds an additional entry to the table.
         */
        public void addEntry() {
            animations.add(0);
            directions.add(false);
        }
        
        /**
         * Calculates how much space is needed above the specified bar
         * @param bar the bar to calculate for
         * @return the amount of space needed
         */
        private int calculateSpace(int bar) {
            int output = QUICK_INFO_HEIGHT * bar;
            for(int i = 0; i < bar; i++) {
                output += animations.get(i) * EXPANSION;
            }
            return output;
        }
        
        /**
         * Calculates the maximum of the scroll bar
         * @return the maximum of the scroll bar
         */
        private int calculateMax() {
            return calculateSpace(handlers.size()) + 3;
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
                    Thread.sleep(40);
                } catch (InterruptedException ex) {
                    System.err.println("Thread was interrupted");
                }
            }
        }
    }
}