package offlinechess;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 * A class to represent any chess piece
 * @author Jed Wang
 */
public abstract class AbstractPiece {
    /**
     * Whether or not the piece is white
     */
    protected final boolean isWhite;
    
    /**
     * Creates a new AbstractPiece
     * @param isWhite whether or not the piece is white
     */
    public AbstractPiece(boolean isWhite) {
        this.isWhite = isWhite;
    }
    
    /**
     * Determines whether a move is legal
     * @param cb the current state of the chess game
     * @param fromWhere the current place of the piece
     * @param toWhere to where the piece would be moved
     * @return whether the move would be legal
     */
    public boolean isLegalMove(ChessBoard cb, String fromWhere, String toWhere) {
        return legalMoves(cb, fromWhere).contains(toWhere);
    }
    
    /**
     * Returns all of the legal moves this piece could make
     * @param cb the current state of the chess game
     * @param currentPosition the current place of the piece
     * @return all legal moves
     */
    public abstract LinkedList<String> allLegalMoves(ChessBoard cb, String currentPosition);
    
    /**
      * Determines whether a move is legal <br>
      * However, this method does not check for checks
      * @param cb
      * @param fromWhere
      * @param toWhere
      * @return 
      */
    public boolean isAllLegalMove(ChessBoard cb, String fromWhere, String toWhere) {
        return allLegalMoves(cb, fromWhere).contains(toWhere);
    }
    
    /**
     * Returns all of the legal moves this piece could make, taking into account check
     * @param cb the current state of the chess game
     * @param currentPosition the current place of the piece
     * @return the legal moves this piece can make
     */
    public LinkedList<String> legalMoves(ChessBoard cb, String currentPosition) {
        LinkedList<String> allLegal = allLegalMoves(cb, currentPosition);
        LinkedList<String> output = new LinkedList<>();
        AbstractPiece[][] initLayout = new AbstractPiece[cb.getBoard().length][cb.getBoard()[0].length];
        for(int i = 0; i < cb.getBoard().length; i++) {
            for(int j = 0; j < cb.getBoard()[i].length; j++) {
                initLayout[i][j] = cb.getBoard()[i][j];
            }
        }
        for(String square:allLegal) {
            cb.maybeMove(currentPosition, square);
            if(getCharRepresentation().equals("P") && 
                    (ChessBoard.getRow(square) == 0 
                    || ChessBoard.getRow(square) == 7)) 
                cb.placePiece(new Queen(isWhite), square);
            if(!cb.inCheck(isWhite)) output.add(square);
            cb.setBoard(initLayout);
            if(getCharRepresentation().equals("K")) cb.resetKingPos(isWhite);
        }
        return output;
    }
    
    /**
     * Returns all of the legal captures this piece could make
     * @param cb the current state of the chess game
     * @param currentPosition the current place of the piece
     * @return all legal captures
     */
    public abstract LinkedList<String> legalCaptures(ChessBoard cb, String currentPosition);
    
    /**
     * The ghostifier
     */
    private static RescaleOp rop;
    
    /**
     * static init
     */
    static {
        float[] scales = { 1f, 1f, 1f, 0.3f };
        float[] offsets = new float[4];
        rop = new RescaleOp(scales, offsets, null);
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
     * Turns the alpha of the image to 30%
     * @param bi the BufferedImage to change
     * @return the changed image
     */
    public static BufferedImage ghostify(BufferedImage bi) {
        return rop.filter(bi, null);
    }
    
    /**
     * Draws a ghost of this image
     * @param g the Graphics to draw on
     * @param x the X coordinate of the image
     * @param y the Y coordinate of the image
     * @param width the width of the picture
     * @param height the height of the picture
     */
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
    
    /**
     * Returns the character that represents this piece
     * @return the character that represents this piece
     */
    public abstract String getCharRepresentation();
}