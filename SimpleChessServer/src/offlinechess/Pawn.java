package offlinechess;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 * A class that represents a pawn
 * @author Jed Wang
 */
public class Pawn extends AbstractPiece {
    
    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public LinkedList<String> allLegalMoves(ChessBoard cb, String currentPosition) {
        if(!ChessBoard.isValidSquare(currentPosition)) throw new IllegalArgumentException("Invalid square");
        if(!(cb.getPiece(currentPosition).getCharRepresentation().equals("P"))) throw new IllegalArgumentException("This isn\'t a pawn!");
        LinkedList<String> output = new LinkedList<>();
        int row = ChessBoard.getRow(currentPosition), column = ChessBoard.getColumn(currentPosition);
        if(isWhite) {
            if(row == 0) assert false : "Pawns should have promoted already!";
            if(row == 7) assert false : "Dafuq white pawns shouldn\'t be on the first rank";
            if(ChessBoard.isValidSquare(column, row-1)) {
                if(cb.isEmptySquare(column, row - 1)) {
                    output.add(ChessBoard.shiftSquare(currentPosition, 0, -1));
                }
            }
            if(ChessBoard.isValidSquare(column, row - 1) && ChessBoard.isValidSquare(column, row - 2)) {
                if(row == 6 && cb.isEmptySquare(column, row - 1) && cb.isEmptySquare(column, row - 2)) {
                    output.add(ChessBoard.shiftSquare(currentPosition, 0, -2));
                }
            }
            if(ChessBoard.isValidSquare(column - 1, row - 1)) {
                if((!cb.isEmptySquare(column - 1, row - 1) && !cb.getPiece(column - 1, row - 1).isWhite) || 
                        (cb.getEnPassant() == null?ChessBoard.shiftSquare(currentPosition, -1, -1) == null
                        :cb.getEnPassant().equals(ChessBoard.shiftSquare(currentPosition, -1, -1)))) {
                    output.add(ChessBoard.shiftSquare(currentPosition, -1, -1));
                }
            }
            if (ChessBoard.isValidSquare(column + 1, row - 1)) {
                if(!cb.isEmptySquare(column + 1, row - 1) && !cb.getPiece(column + 1, row - 1).isWhite || 
                        (cb.getEnPassant() == null?ChessBoard.shiftSquare(currentPosition, 1, -1) == null
                        :cb.getEnPassant().equals(ChessBoard.shiftSquare(currentPosition, 1, -1)))) {
                    output.add(ChessBoard.shiftSquare(currentPosition, 1, -1));
                }
            }
        } else {
            if(row == 7) assert false : "Pawns should have promoted already!";
            if(row == 0) assert false : "Dafuq black pawns shouldn\'t be on the eighth rank";
            if(ChessBoard.isValidSquare(column, row+1)) {
                if(cb.isEmptySquare(column, row + 1)) {
                    output.add(ChessBoard.shiftSquare(currentPosition, 0, 1));
                }
            }
            if(ChessBoard.isValidSquare(column, row + 1) && ChessBoard.isValidSquare(column, row + 2)) {
                if(row == 1 && cb.isEmptySquare(column, row + 1) && cb.isEmptySquare(column, row + 2)) {
                    output.add(ChessBoard.shiftSquare(currentPosition, 0, 2));
                }
            }
            if(ChessBoard.isValidSquare(column - 1, row + 1)) {
                if(!cb.isEmptySquare(column - 1, row + 1) && cb.getPiece(column - 1, row + 1).isWhite 
                        || (cb.getEnPassant() == null?ChessBoard.shiftSquare(currentPosition, -1, 1) == null
                        :cb.getEnPassant().equals(ChessBoard.shiftSquare(currentPosition, -1, 1)))) {
                    output.add(ChessBoard.shiftSquare(currentPosition, -1, 1));
                }
            }
            if(ChessBoard.isValidSquare(column + 1, row + 1)) {
                if(!cb.isEmptySquare(column + 1, row + 1) && cb.getPiece(column + 1, row + 1).isWhite 
                        || (cb.getEnPassant() == null?ChessBoard.shiftSquare(currentPosition, 1, 1) == null
                        :cb.getEnPassant().equals(ChessBoard.shiftSquare(currentPosition, 1, 1)))) {
                    output.add(ChessBoard.shiftSquare(currentPosition, 1, 1));
                }
            }
        }
        return output;
    }

    @Override
    public LinkedList<String> legalCaptures(ChessBoard cb, String currentPosition) {
        if(!ChessBoard.isValidSquare(currentPosition)) throw new IllegalArgumentException("Invalid square");
        if(!(cb.getPiece(currentPosition).getCharRepresentation().equals("P"))) throw new IllegalArgumentException("This isn\'t a pawn!");
        LinkedList<String> output = new LinkedList<>();
        int row = ChessBoard.getRow(currentPosition), column = ChessBoard.getColumn(currentPosition);
        if(isWhite) {
            if(row == 0) assert false : "Pawns should have promoted already!";
            if(row == 7) assert false : "Dafuq white pawns shouldn\'t be on the first rank";
            if(ChessBoard.isValidSquare(column - 1, row - 1)) {
                if(cb.isEmptySquare(column - 1, row - 1) || !cb.getPiece(column - 1, row - 1).isWhite) {
                    output.add(ChessBoard.shiftSquare(currentPosition, -1, -1));
                }
            }
            if (ChessBoard.isValidSquare(column + 1, row - 1)) {
                if(cb.isEmptySquare(column + 1, row - 1) || !cb.getPiece(column + 1, row - 1).isWhite) {
                    output.add(ChessBoard.shiftSquare(currentPosition, 1, -1));
                }
            }
        } else {
            if(row == 7) assert false : "Pawns should have promoted already!";
            if(row == 0) assert false : "Dafuq black pawns shouldn\'t be on the eighth rank";
            if(ChessBoard.isValidSquare(column - 1, row + 1)) {
                if(cb.isEmptySquare(column - 1, row + 1) || cb.getPiece(column - 1, row + 1).isWhite) {
                    output.add(ChessBoard.shiftSquare(currentPosition, -1, 1));
                }
            }
            if(ChessBoard.isValidSquare(column + 1, row + 1)) {
                if(cb.isEmptySquare(column + 1, row + 1) || cb.getPiece(column + 1, row + 1).isWhite) {
                    output.add(ChessBoard.shiftSquare(currentPosition, 1, 1));
                }
            }
        }
        return output;
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
        return "P";
    }
}