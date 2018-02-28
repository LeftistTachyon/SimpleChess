package offlinechess;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 * A class that represents the king
 * @author Jed Wang
 */
public class King extends AbstractPiece {
    
    /**
     * Whether this king has moved before - used to check castling
     */
    private boolean moved = false;
    
    /**
     * Whether this king is in check - used to do castling
     */
    private boolean inCheck = false;

    public King(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public LinkedList<String> allLegalMoves(ChessBoard cb, String currentPosition) {
        if(!ChessBoard.isValidSquare(currentPosition)) throw new IllegalArgumentException("Invalid square");
        if(!(cb.getPiece(currentPosition).getCharRepresentation().equals("K"))) throw new IllegalArgumentException("This isn\'t a king!");
        LinkedList<String> output = new LinkedList<>();
        String temp;
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(i == 0 && j == 0) continue;
                if(ChessBoard.isValidShift(currentPosition, i, j)) {
                    temp = ChessBoard.shiftSquare(currentPosition, i, j);
                    if(ChessBoard.isValidSquare(temp)) {
                        if(cb.isEmptySquare(temp)) {
                            output.add(temp);
                        } else if(cb.getPiece(temp).isWhite ^ isWhite) {
                            output.add(temp);
                        }
                    }
                }
            }
        }
        
        // CASTLING
        if(!moved && !inCheck) {
            // white on 7, black on 0
            int row = (isWhite)?7:0;
            
            boolean canQ = true, canK = true;
            for(int i = 0; i < cb.getBoard().length; i++) {
                for(int j = 0; j < cb.getBoard()[i].length; j++) {
                    AbstractPiece ap = cb.getPiece(i, j);
                    if(ap == null) continue;
                    if((ap.isWhite == isWhite) || (ap.getCharRepresentation().equals("K"))) continue;
                    String fromWhere = ChessBoard.toSquare(i, j);
                    if(ap.isAllLegalMove(cb, fromWhere, ChessBoard.toSquare(1, row)) || 
                            ap.isAllLegalMove(cb, fromWhere, ChessBoard.toSquare(2, row)) ||
                            ap.isAllLegalMove(cb, fromWhere, ChessBoard.toSquare(3, row))) {
                        canQ = false;
                    }
                    if(ap.isAllLegalMove(cb, fromWhere, ChessBoard.toSquare(5, row)) ||
                            ap.isAllLegalMove(cb, fromWhere, ChessBoard.toSquare(6, row))) {
                        canK = false;
                    }
                }
            }
            // 1, 2, 3, Queenside
            if(cb.isEmptySquare(1, row) && cb.isEmptySquare(2, row) && cb.isEmptySquare(3, row) && canQ) {
                output.add(ChessBoard.shiftSquare(currentPosition, -2, 0));
            }
            // 5, 6, Kingside
            if(cb.isEmptySquare(5, row) && cb.isEmptySquare(6, row) && canK) {
                output.add(ChessBoard.shiftSquare(currentPosition, 2, 0));
            }
        }
        return output;
    }

    @Override
    public LinkedList<String> legalCaptures(ChessBoard cb, String currentPosition) {
        return allLegalMoves(cb, currentPosition);
    }
    
    /**
     * Notifies this king that it is in check.
     */
    public void notifyCheck() {
        inCheck = true;
    }
    
    /**
     * Determines what moves are necessary
     * @param cb the current state of the game
     * @param currentPosition where the piece currently is
     * @param includeOtherKing whether to discount the other's king
     * @return all King moves discounting the other's king or not
     * @deprecated not needed anymore
     */
    @Deprecated
    private LinkedList<String> moves(ChessBoard cb, String currentPosition) {
        /*
        if(!ChessBoard.isValidSquare(currentPosition)) throw new IllegalArgumentException("Invalid square");
        if(!(cb.getPiece(currentPosition).getCharRepresentation().equals("K"))) throw new IllegalArgumentException("This isn\'t a king!");
        LinkedList<String> output = new LinkedList<>();
        String temp;
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(i == 0 && j == 0) continue;
                if(ChessBoard.isValidShift(currentPosition, i, j)) {
                    temp = ChessBoard.shiftSquare(currentPosition, i, j);
                    if(ChessBoard.isValidSquare(temp)) {
                        if(cb.isEmptySquare(temp)) {
                            output.add(temp);
                        } else if(cb.getPiece(temp).isWhite ^ isWhite) {
                            output.add(temp);
                        }
                    }
                }
            }
        }
        
        // CASTLING
        if(!moved) {
            // white on 7, black on 0
            int row = (isWhite)?7:0;
            
            boolean canQ = true, canK = true;
            for(int i = 0; i < cb.getBoard().length; i++) {
                for(int j = 0; j < cb.getBoard()[i].length; j++) {
                    AbstractPiece ap = cb.getPiece(i, j);
                    if(ap == null) continue;
                    if((ap.isWhite == isWhite) || (ap.getCharRepresentation().equals("K"))) continue;
                    String fromWhere = ChessBoard.toSquare(i, j);
                    if(ap.isAllLegalMove(cb, fromWhere, ChessBoard.toSquare(1, row)) || 
                            ap.isAllLegalMove(cb, fromWhere, ChessBoard.toSquare(2, row)) ||
                            ap.isAllLegalMove(cb, fromWhere, ChessBoard.toSquare(3, row))) {
                        canQ = false;
                    }
                    if(ap.isAllLegalMove(cb, fromWhere, ChessBoard.toSquare(5, row)) ||
                            ap.isAllLegalMove(cb, fromWhere, ChessBoard.toSquare(6, row))) {
                        canK = false;
                    }
                }
            }
            // 1, 2, 3, Queenside
            if(cb.isEmptySquare(1, row) && cb.isEmptySquare(2, row) && cb.isEmptySquare(3, row) && canQ) {
                output.add(ChessBoard.shiftSquare(currentPosition, -2, 0));
            }
            // 5, 6, Kingside
            if(cb.isEmptySquare(5, row) && cb.isEmptySquare(6, row) && canK) {
                output.add(ChessBoard.shiftSquare(currentPosition, 2, 0));
            }
        }
        return output;
        */
        return null;
    }
    
    /**
     * Copies from one LinkedList to another, without duplicates.
     * @param <V> the class of the objects contained in the LinkedLists
     * @param from the LinkedList the elements are copied from
     * @param to the LinkedList the elements are copied to
     * @return a LinkedList with the elements copied
     * @deprecated no longer needed
     */
    @Deprecated
    private <V> LinkedList<V> addAllWODuplicates(LinkedList<V> from, LinkedList<V> to) {
        for(V v:from) {
            if(!to.contains(v)) to.add(v);
        }
        return to;
    }
    
    /**
     * Removes elements in one LinkedList that are present in the other.<br>
     *  = b - a.
     * @param <V> the class of the objects contained in the LinkedLists
     * @param a the LinkedList to subtract
     * @param b the LinkedList to subtract from
     * @return the difference of the LinkedLists
     * @deprecated no longer needed
     */
    @Deprecated
    private <V> LinkedList<V> difference(LinkedList<V> a, LinkedList<V> b) {
        LinkedList<V> bCopy = new LinkedList<>(b);
        LinkedList<V> aCopy = new LinkedList<>(a);
        for(V v:aCopy) {
            if(bCopy.contains(v)) bCopy.remove(v);
        }
        return bCopy;
    }
    
    /**
     * The images for the black and white pieces
     */
    private static BufferedImage black, white;
    
    /**
     * Loads the images for this piece
     * @param b the black image
     * @param w the white image
     * @throws IOException if something goes wrong
     */
    public static void loadImages(URL b, URL w) throws IOException {
        white = ImageIO.read(w);
        black = ImageIO.read(b);
        whiteGhost = ghostify(white);
        blackGhost = ghostify(black);
    }
    
    /**
     * Draws this piece
     * @param g the Graphics to draw on
     * @param x the X coordinate of the image
     * @param y the Y coordinate of the image
     * @param width the width of the picture
     * @param height the height of the picture
     */
    @Override
    public void draw(Graphics g, int x, int y, int width, int height) {
        if(isWhite) {
            g.drawImage(white, x, y, width, height, null);
        } else {
            g.drawImage(black, x, y, width, height, null);
        }
    }
    /**
     * The images for the black and white ghosts
     */
    private static BufferedImage blackGhost, whiteGhost;
    
    /**
     * Draws a ghost of this image
     * @param g the Graphics to draw on
     * @param x the X coordinate of the image
     * @param y the Y coordinate of the image
     * @param width the width of the picture
     * @param height the height of the picture
     */
    @Override
    public void drawGhost(Graphics g, int x, int y, int width, int height) {
        if(isWhite) {
            g.drawImage(whiteGhost, x, y, width, height, null);
        } else {
            g.drawImage(blackGhost, x, y, width, height, null);
        }
    }
    
    /**
     * Gets this piece's image that is white or black
     * @param isWhite whether the image should be white or black
     * @return the image that represents this piece
     */
    public static BufferedImage getImage(boolean isWhite) {
        return (isWhite)?white:black;
    }

    @Override
    public String getCharRepresentation() {
        return "K";
    }
    
    /**
     * Notifies the King that it has moved
     */
    public void notifyOfMove() {
        moved = true;
    }
    
    /**
     * Notifies the King that it is no longer in check
     */
    public void notifyNoCheck() {
        inCheck = false;
    }

    /**
     * Returns whether this King has moved
     * @return whether this King has moved
     */
    public boolean isMoved() {
        return moved;
    }
}