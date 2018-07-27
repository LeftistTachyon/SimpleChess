package offlinechess;

import java.util.ArrayList;

/**
 * A class that records the moves of a game
 * @author Jed Wang
 */
public class MoveRecorder {
    
    /**
     * Represents a pawn
     */
    public static final int PAWN = 0;
    
    /**
     * Represents a knight
     */
    public static final int KNIGHT = 1;
    
    /**
     * Represents a bishop
     */
    public static final int BISHOP = 2;
    
    /**
     * Represents a rook
     */
    public static final int ROOK = 3;
    
    /**
     * Represents a queen
     */
    public static final int QUEEN = 4;
    
    /**
     * Represents a king
     */
    public static final int KING = 5;
    
    /**
     * The collection of moves made in the game
     */
    private ArrayList<String> moves;
    
    /**
     * The outcome of the game
     */
    private String outcome = "";
    
    /**
     * Default constructor
     */
    public MoveRecorder() {
        moves = new ArrayList<>();
    }
    
    /**
     * Creates a duplicate of the given MoveRecorder
     * @param mr the MoveRecorder to duplicate
     */
    public MoveRecorder(MoveRecorder mr) {
        moves = new ArrayList<>(mr.moves);
    }

    /**
     * Returns the moves made
     * @return the moves made
     */
    public ArrayList<String> getMoves() {
        return moves;
    }
    
    /**
     * Determines a String that denotes a move <br>
     * Can be used for en passant captures as well
     * @param fromWhere from where the piece is moved
     * @param toWhere to where the piece moved
     * @param whichPiece which piece moved
     * @param capture whether this piece is capturing something
     * @return the String that denotes the move
     */
    public String toMoveString(int fromWhere, int toWhere, int whichPiece, boolean capture) {
        String captureSymbol = (capture)?"x":"";
        switch(whichPiece) {
            case BISHOP:
                return "B" + captureSymbol + toStringSquare(toWhere);
            case KING:
                return "K" + captureSymbol + toStringSquare(toWhere);
            case KNIGHT:
                return "N" + captureSymbol + toStringSquare(toWhere);
            case PAWN:
                return (capture)?toStringSquare(fromWhere).substring(0, 1) + 
                                "x" + toStringSquare(toWhere)
                        :toStringSquare(toWhere);
            case QUEEN:
                return "Q" + captureSymbol + toStringSquare(toWhere);
            case ROOK:
                return "R" + captureSymbol + toStringSquare(toWhere);
            default:
                return "";
        }
    }
    
    /**
     * Determines a String that denotes a move <br>
     * Used when a move is ambiguous and the file or rank is needed for clarification
     * @param toWhere to where the piece moved
     * @param column which file this piece is moved from
     * @param whichPiece which piece moved
     * @param capture whether this piece is capturing something
     * @return the String that denotes the move
     */
    public String toMoveStringF(int toWhere, int column, int whichPiece, boolean capture) {
        String captureSymbol = (capture)?"x":"";
        String file = "" + (char)(column + 'a');
        switch(whichPiece) {
            case BISHOP:
                return "B" + file + captureSymbol + toStringSquare(toWhere);
            case KING:
                return "K" + file + captureSymbol + toStringSquare(toWhere);
            case KNIGHT:
                return "N" + file + captureSymbol + toStringSquare(toWhere);
            case PAWN:
                return (capture)?file + "x" + toStringSquare(toWhere):
                        toStringSquare(toWhere);
            case QUEEN:
                return "Q" + file + captureSymbol + toStringSquare(toWhere);
            case ROOK:
                return "R" + file + captureSymbol + toStringSquare(toWhere);
            default:
                return "";
        }
    }
    
    /**
     * Determines a String that denotes a move <br>
     * Used when a move is ambiguous and the rank is needed for clarification
     * @param toWhere to where the piece moved
     * @param row which rank this piece is moved from
     * @param whichPiece which piece moved
     * @param capture whether this piece is capturing something
     * @return the String that denotes the move
     */
    public String toMoveStringR(int toWhere, int row, int whichPiece, boolean capture) {
        String captureSymbol = (capture)?"x":"";
        row = 8 - row;
        switch(whichPiece) {
            case BISHOP:
                return "B" + row + captureSymbol + toStringSquare(toWhere);
            case KING:
                return "K" + row + captureSymbol + toStringSquare(toWhere);
            case KNIGHT:
                return "N" + row + captureSymbol + toStringSquare(toWhere);
            case QUEEN:
                return "Q" + row + captureSymbol + toStringSquare(toWhere);
            case ROOK:
                return "R" + row + captureSymbol + toStringSquare(toWhere);
            default:
                return "";
        }
    }
    
    /**
     * Determines a String that denotes a move <br>
     * Used when a move is ambiguous and the rank and file is needed for clarification
     * @param fromWhere from where the piece is moved
     * @param toWhere to where the piece moved
     * @param whichPiece which piece moved
     * @param capture whether this piece is capturing something
     * @return the String that denotes the move
     */
    public String toMoveStringFR(int fromWhere, int toWhere, int whichPiece, boolean capture) {
        String captureSymbol = (capture)?"x":"";
        switch(whichPiece) {
            case BISHOP:
                return "B" + toStringSquare(fromWhere) + captureSymbol + 
                        toStringSquare(toWhere);
            case KING:
                return "K" + toStringSquare(fromWhere) + captureSymbol + 
                        toStringSquare(toWhere);
            case KNIGHT:
                return "N" + toStringSquare(fromWhere) + captureSymbol + 
                        toStringSquare(toWhere);
            case QUEEN:
                return "Q" + toStringSquare(fromWhere) + captureSymbol + 
                        toStringSquare(toWhere);
            case ROOK:
                return "R" + toStringSquare(fromWhere) + captureSymbol + 
                        toStringSquare(toWhere);
            default:
                return "";
        }
    }
    
    /**
     * Determines a String that denotes a castling move
     * @param isKingSide whether the castling move is to the king side
     * @return the String that denotes the move
     */
    public String castlingMoveString(boolean isKingSide) {
        return (isKingSide)?"O-O":"O-O-O";
    }
    
    /**
     * Determines a String that denotes a promotion
     * @param normalMove the move without the promotion
     * @param promotionPiece the piece to promote to
     * @return the String that denotes the move
     */
    public String promotionMoveString(String normalMove, int promotionPiece) {
        switch(promotionPiece) {
            case BISHOP:
                return normalMove + "=B";
            case KNIGHT:
                return normalMove + "=N";
            case QUEEN:
                return normalMove + "=Q";
            case ROOK:
                return normalMove + "=R";
            default:
                return "";
        }
    }
    
    /**
     * Notifies this object of a move and notes it
     * @param before the state of the game before the move
     * @param after the state of the game after the move
     * @param fromWhere from where the piece was moved
     * @param toWhere to where the piece was moved
     */
    public void moved(ChessBoard before, ChessBoard after, int fromWhere, int toWhere) {
        /*moved(before, 
                ChessBoard.getColumn(fromWhere), ChessBoard.getRow(fromWhere), 
                ChessBoard.getColumn(toWhere), ChessBoard.getRow(toWhere));*/
        AbstractPiece toMove = before.getPiece(fromWhere);
        if(toMove == null) throw new IllegalArgumentException("Null piece");
        //if(!toMove.isLegalMove(before, fromWhere, toWhere)) throw new IllegalArgumentException("Not a legal move!");
        
        switch(toMove.getCharRepresentation()) {
            case "P":
                if(toWhere%10 == 0 || toWhere%10 == 7) {
                    switch(after.getPiece(toWhere).getCharRepresentation()) {
                        case "N":
                            moves.add(addChecks(promotionMoveString(toMoveString(fromWhere, toWhere, PAWN, isCapture(before, toWhere)), KNIGHT), after, after.getPiece(toWhere).isWhite));
                            break;
                        case "B":
                            moves.add(addChecks(promotionMoveString(toMoveString(fromWhere, toWhere, PAWN, isCapture(before, toWhere)), BISHOP), after, after.getPiece(toWhere).isWhite));
                            break;
                        case "R":
                            moves.add(addChecks(promotionMoveString(toMoveString(fromWhere, toWhere, PAWN, isCapture(before, toWhere)), ROOK), after, after.getPiece(toWhere).isWhite));
                            break;
                        case "Q":
                            moves.add(addChecks(promotionMoveString(toMoveString(fromWhere, toWhere, PAWN, isCapture(before, toWhere)), QUEEN), after, after.getPiece(toWhere).isWhite));
                            break;
                    }
                } else {
                    moves.add(addChecks(toMoveString(fromWhere, toWhere, PAWN, isCapture(before, toWhere)), after, after.getPiece(toWhere).isWhite));
                }
                break;
            case "K":
                if(Math.abs(fromWhere/10-toWhere/10) == 2) {
                    moves.add(addChecks(castlingMoveString(fromWhere/10 < toWhere/10), after, after.getPiece(toWhere).isWhite));
                } else {
                    moves.add(addChecks(toMoveString(fromWhere, toWhere, KING, isCapture(before, toWhere)), after, after.getPiece(toWhere).isWhite));
                }
                break;
            case "N":
                moves.add(addChecks(moveString(before, fromWhere, toWhere, toMove, KNIGHT, isCapture(before, toWhere)), after, after.getPiece(toWhere).isWhite));
                break;
            case "B":
                moves.add(addChecks(moveString(before, fromWhere, toWhere, toMove, BISHOP, isCapture(before, toWhere)), after, after.getPiece(toWhere).isWhite));
                break;
            case "R":
                moves.add(addChecks(moveString(before, fromWhere, toWhere, toMove, ROOK, isCapture(before, toWhere)), after, after.getPiece(toWhere).isWhite));
                break;
            case "Q":
                moves.add(addChecks(moveString(before, fromWhere, toWhere, toMove, QUEEN, isCapture(before, toWhere)), after, after.getPiece(toWhere).isWhite));
                break;
            default:
                throw new IllegalArgumentException("Unknown piece");
        }
        if(after.checkMated(true)) {
            addOutcome(-1);
        } else if(after.checkMated(false)) {
            addOutcome(1);
        }
    }
    
    /**
     * Notifies this object of a move and notes it
     * @param before the state of the game before the move
     * @param after the state of the game after the move
     * @param fromWhereX from which X position the piece was moved
     * @param fromWhereY from which Y position the piece was moved
     * @param toWhereX to which X position the piece was moved
     * @param toWhereY to which Y position the piece was moved
     */
    public void moved(ChessBoard before, ChessBoard after, int fromWhereX, int fromWhereY, int toWhereX, int toWhereY) {
        moved(before, after,  
                ChessBoard.toSquare(fromWhereX, fromWhereY), 
                ChessBoard.toSquare(toWhereX, toWhereY));
    }
    
    /**
     * Determines whether a move is a capture
     * @param before the state of the game before the move
     * @param toWhere to where the piece was moved
     * @return whether a move is a capture
     */
    private boolean isCapture(ChessBoard before, int toWhere) {
        if(before.getEnPassant() == -1) {
            return !before.isEmptySquare(toWhere);
        } else {
            return !before.isEmptySquare(toWhere) || before.getEnPassant() == toWhere;
        }
    }
    
    /**
     * Determines the String representing a move
     * @param before the state of the game before the move
     * @param fromWhere from where the piece was moved
     * @param toWhere to where the piece was moved
     * @param piece which piece is to be moved
     * @return A String that represents the move
     */
    private String moveString(ChessBoard before, int fromWhere, int toWhere, AbstractPiece piece, int whichPiece, boolean capture) {
        if(!piece.isAllLegalMove(before, fromWhere, toWhere)) 
            throw new IllegalArgumentException("This isn\'t a legal move: from " + fromWhere + " to " + toWhere);
        ArrayList<Integer> allPiece = before.findAll(whichPiece, piece.isWhite);
        boolean needRank = false, needFile = false;
        for(int square:allPiece) {
            if(fromWhere != square) {
                if(before.getPiece(square).isLegalMove(before, square, toWhere)) {
                    if(square/10 == fromWhere/10) {
                        needRank = true;
                    } else needFile = true;
                }
            }
        }
        if(needRank) {
            if(needFile) {
                return toMoveStringFR(fromWhere, toWhere, whichPiece, capture);
            } else {
                return toMoveStringR(toWhere, fromWhere%10, whichPiece, capture);
            }
        } else{ 
            if(needFile) {
                return toMoveStringF(toWhere, fromWhere/10, whichPiece, capture);
            } else {
                return toMoveString(fromWhere, toWhere, whichPiece, capture);
            }
        }
    }
    
    /**
     * Adds the check notation if the side is checked
     * @param move the current move
     * @param after the state of the game after the move
     * @param isWhite whether the moved piece is white
     * @return the finished move notation
     */
    public String addChecks(String move, ChessBoard after, boolean isWhite) {
        if(after.checkMated(!isWhite)) {
            return move + "#";
        } else if(after.inCheck(!isWhite)) {
            return move + "+";
        } else return move;
    }
    
    /**
     * Adds the outcome of the game to the moves
     * @param outcome the outcome
     */
    public void addOutcome(int outcome) {
        switch(outcome) {
            case -1:
                this.outcome = "0-1";
                break;
            case 0:
                this.outcome = "1/2-1/2";
                break;
            case 1:
                this.outcome = "1-0";
                break;
            default:
                throw new IllegalArgumentException("Unknown outcome: " + outcome);
        }
    }
    
    /**
     * Determines whether 50 moves have been made without a pawn moving.
     * @return whether it is a draw according to this rule.
     */
    public boolean is50MoveDraw() {
        if(moves.size() < 100) return false;
        for(int i = moves.size()-1; i <= moves.size()-100; i--) {
            if(Character.isLowerCase(moves.get(i).charAt(0))) return false;
        }
        return true;
    }
    
    /**
     * Returns how many moves have been played in the game
     * @return how many moves have been played in the game
     */
    public int moves() {
        return moves.size()/2;
    }
    
    /**
     * int square -> String square
     * @param square the String square to convert
     * @return String square
     */
    public static String toStringSquare(int square) {
        return "" + (char)('a' + square/10) + (8 - square%10);
    }
    
    /**
     * Creates a String that represents this MoveRecorder. <br>
     * Essentially creates a PGN of the game
     * @return a String that represents this MoveRecorder
     */
    @Override
    public String toString() {
        String output = "";
        for(int i = 0; i < moves.size(); i++) {
            if(i%2 == 0) {
                output += ((i/2) + 1) + ". ";
            }
            output += moves.get(i) + " ";
        }
        output += outcome;
        return output;
    }
}