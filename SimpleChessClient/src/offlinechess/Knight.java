package offlinechess;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 * A class that represents a knight
 * @author Jed Wang
 */
public class Knight extends AbstractPiece {

    public Knight(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public LinkedList<String> allLegalMoves(ChessBoard cb, String currentPosition) {
        if(!ChessBoard.isValidSquare(currentPosition)) throw new IllegalArgumentException("Invalid square");
        if(cb.getPiece(currentPosition) == null) throw new IllegalArgumentException("This is a null piece");
        if(!(cb.getPiece(currentPosition).getCharRepresentation().equals("N"))) throw new IllegalArgumentException("This isn\'t a knight! It\'s a " + AbstractPiece.getClassName(cb.getPiece(currentPosition)));
        LinkedList<String> output = new LinkedList<>();
        String temp;
        if(ChessBoard.isValidShift(currentPosition, -2, -1)) {
            temp = ChessBoard.shiftSquare(currentPosition, -2, -1);
            if(ChessBoard.isValidSquare(temp)) {
                if(cb.isEmptySquare(temp)) {
                    output.add(temp);
                } else if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        if(ChessBoard.isValidShift(currentPosition, -2, 1)) {
            temp = ChessBoard.shiftSquare(currentPosition, -2, 1);
            if(ChessBoard.isValidSquare(temp)) {
                if(cb.isEmptySquare(temp)) {
                    output.add(temp);
                } else if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        if(ChessBoard.isValidShift(currentPosition, 2, -1)) {
            temp = ChessBoard.shiftSquare(currentPosition, 2, -1);
            if(ChessBoard.isValidSquare(temp)) {
                if(cb.isEmptySquare(temp)) {
                    output.add(temp);
                } else if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        if(ChessBoard.isValidShift(currentPosition, 2, 1)) {
            temp = ChessBoard.shiftSquare(currentPosition, 2, 1);
            if(ChessBoard.isValidSquare(temp)) {
                if(cb.isEmptySquare(temp)) {
                    output.add(temp);
                } else if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        if(ChessBoard.isValidShift(currentPosition, 1, -2)) {
            temp = ChessBoard.shiftSquare(currentPosition, 1, -2);
            if(ChessBoard.isValidSquare(temp)) {
                if(cb.isEmptySquare(temp)) {
                    output.add(temp);
                } else if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        if(ChessBoard.isValidShift(currentPosition, -1, -2)) {
            temp = ChessBoard.shiftSquare(currentPosition, -1, -2);
            if(ChessBoard.isValidSquare(temp)) {
                if(cb.isEmptySquare(temp)) {
                    output.add(temp);
                } else if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        if(ChessBoard.isValidShift(currentPosition, 1, 2)) {
            temp = ChessBoard.shiftSquare(currentPosition, 1, 2);
            if(ChessBoard.isValidSquare(temp)) {
                if(cb.isEmptySquare(temp)) {
                    output.add(temp);
                } else if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        if(ChessBoard.isValidShift(currentPosition, -1, 2)) {
            temp = ChessBoard.shiftSquare(currentPosition, -1, 2);
            if(ChessBoard.isValidSquare(temp)) {
                if(cb.isEmptySquare(temp)) {
                    output.add(temp);
                } else if(cb.getPiece(temp).isWhite ^ isWhite) {
                    output.add(temp);
                }
            }
        }
        return output;
    }

    @Override
    public LinkedList<String> legalCaptures(ChessBoard cb, String currentPosition) {
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
        return "N";
    }
}