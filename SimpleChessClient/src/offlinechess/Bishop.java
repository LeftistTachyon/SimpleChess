package offlinechess;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 * A class that represents the bishop
 * @author Jed Wang
 */
public class Bishop extends AbstractPiece {
    /**
     * Creates a new Bishop
     * @param isWhite whether the bishop is white or black
     */
    public Bishop(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public LinkedList<Integer> allLegalMoves(ChessBoard cb, int currentPosition) {
        if(!ChessBoard.isValidSquare(currentPosition)) throw new IllegalArgumentException("Invalid square");
        if(cb.getPiece(currentPosition) == null) throw new IllegalArgumentException("This is a null piece");
        if(!(cb.getPiece(currentPosition).getCharRepresentation().equals("B"))) throw new IllegalArgumentException("This isn\'t a bishop! It\'s a " + AbstractPiece.getClassName(cb.getPiece(currentPosition)));
        LinkedList<Integer> output = new LinkedList<>();
        int temp;
        if(ChessBoard.isValidShift(currentPosition, 1, 1)) {
            temp = ChessBoard.shiftSquare(currentPosition, 1, 1);
            while(cb.isEmptySquare(temp)) {
                output.add(temp);
                try {
                    temp = ChessBoard.shiftSquare(temp, 1, 1);
                } catch(IllegalArgumentException iae) {
                    break;
                }
            }
            if(ChessBoard.isValidSquare(temp) && !cb.isEmptySquare(temp)) {
                if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        if(ChessBoard.isValidShift(currentPosition, -1, -1)) {
            temp = ChessBoard.shiftSquare(currentPosition, -1, -1);
            while(cb.isEmptySquare(temp)) {
                output.add(temp);
                try {
                    temp = ChessBoard.shiftSquare(temp, -1, -1);
                } catch(IllegalArgumentException iae) {
                    break;
                }
            }
            if(ChessBoard.isValidSquare(temp) && !cb.isEmptySquare(temp)) {
                if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        if(ChessBoard.isValidShift(currentPosition, -1, 1)) {
            temp = ChessBoard.shiftSquare(currentPosition, -1, 1);
            while(cb.isEmptySquare(temp)) {
                output.add(temp);
                try {
                    temp = ChessBoard.shiftSquare(temp, -1, 1);
                } catch(IllegalArgumentException iae) {
                    break;
                }
            }
            if(ChessBoard.isValidSquare(temp) && !cb.isEmptySquare(temp)) {
                if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        if(ChessBoard.isValidShift(currentPosition, 1, -1)) {
            temp = ChessBoard.shiftSquare(currentPosition, 1, -1);
            while(cb.isEmptySquare(temp)) {
                output.add(temp);
                try {
                    temp = ChessBoard.shiftSquare(temp, 1, -1);
                } catch(IllegalArgumentException iae) {
                    break;
                }
            }
            if(ChessBoard.isValidSquare(temp) && !cb.isEmptySquare(temp)) {
                if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        return output;
    }

    @Override
    public LinkedList<Integer> legalCaptures(ChessBoard cb, int currentPosition) {
        return allLegalMoves(cb, currentPosition);
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
    public static void loadImages(File b, File w) throws IOException {
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
        return "B";
    }
}