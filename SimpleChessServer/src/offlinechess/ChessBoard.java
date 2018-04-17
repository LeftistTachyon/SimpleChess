package offlinechess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A class that represents a chess board
 * @author Jed Wang
 */
public class ChessBoard {
    /**
     * The chess board
     */
    private AbstractPiece[][] board;
    
    /**
     * Whether the player this board is facing is white
     */
    private boolean playerIsWhite = true; // set it during the server application
    
    /**
     * The MoveRecorder
     */
    private MoveRecorder mr;
    
    /**
     * The squares open for en passant.<br>
     * null stands for no open squares<br>
     * Controls en passant
     */
    private String enPassant = null;
    
    /**
     * A Map of all of the legal moves possible
     */
    private HashMap<String, LinkedList<String>> allLegalMoves;
    
    /**
     * A Map of the king's position
     */
    private HashMap<Boolean, String> kingPos;
    
    /**
     * Counts how many times a position repeats<br>
     * Controls threefold repetition
     */
    private HashMap<String, Integer> positions;
    
    /**
     * Default constructor.
     */
    public ChessBoard() {
        board = new AbstractPiece[8][8];
        kingPos = new HashMap<>();
        addPieces();
        mr = new MoveRecorder();
        allLegalMoves = new HashMap<>();
        positions = new HashMap<>();
    }
    
    /**
     * Adds the starting pieces to a chessboard.
     */
    private void addPieces() {
        for(int i = 0;i<8;i++) {
            board[i][1] = new Pawn(false);
            board[i][6] = new Pawn(true);
        }
        
        board[0][0] = new Rook(false);
        board[1][0] = new Knight(false);
        board[2][0] = new Bishop(false);
        board[3][0] = new Queen(false);
        board[4][0] = new King(false);
        board[5][0] = new Bishop(false);
        board[6][0] = new Knight(false);
        board[7][0] = new Rook(false);
        
        board[0][7] = new Rook(true);
        board[1][7] = new Knight(true);
        board[2][7] = new Bishop(true);
        board[3][7] = new Queen(true);
        board[4][7] = new King(true);
        board[5][7] = new Bishop(true);
        board[6][7] = new Knight(true);
        board[7][7] = new Rook(true);
        
        kingPos.put(true, "e1");
        kingPos.put(false, "e8");
    }
    
    /**
     * Constructor from a previous ChessBoard
     * @param cb the ChessBoard to duplicate
     */
    public ChessBoard(ChessBoard cb) {
        this();
        for(int i = 0; i < cb.board.length; i++) {
            System.arraycopy(cb.board[i], 0, board[i], 0, cb.board[i].length);
        }
        this.enPassant = cb.enPassant;
    }
    
    /**
     * Determines which piece occupies a square
     * @param square a square
     * @return the piece on that square, and if none, null
     */
    public AbstractPiece getPiece(String square) {
        if(isValidSquare(square)) {
            return board[getColumn(square)][getRow(square)];
        } else throw new IllegalArgumentException("Invalid square");
    }
    
    /**
     * Determines which piece occupies a space represented by ABSOLUTE coordinates<br>
     * i.e. (0, 0) represents the top left corner
     * @param col the ABSOLUTE column
     * @param row the ABSOLUTE row
     * @return the piece on that square, and if none, null
     */
    public AbstractPiece getPiece(int col, int row) {
        if(isValidSquare(col, row)) {
            return board[col][row];
        } else throw new IllegalArgumentException("Invalid square");
    }
    
    /**
     * Determines whether a square is empty
     * @param square a square
     * @return whether that square is empty
     */
    public boolean isEmptySquare(String square) {
        return getPiece(square) == null;
    }
    
    /**
     * Determines whether a space represented by ABSOLUTE coordinates is empty
     * @param col the ABSOLUTE column
     * @param row the ABSOLUTE row
     * @return whether that square is empty
     */
    public boolean isEmptySquare(int col, int row) {
        return getPiece(col, row) == null;
    }
    
    /**
     * Determines the validity of the square
     * @param s a square
     * @return whether the square is valid
     */
    public static boolean isValidSquare(String s) {
        if(s.length() == 2) {
            int col = s.charAt(0)-'a', 
                    row = 8 - Integer.parseInt(s.charAt(1) + "");
            return Character.isLowerCase(s.charAt(0)) && 
                    Character.isDigit(s.charAt(1)) && isValidSquare(col, row);
        } else return false;
    }
    
    /**
     * Determines the validity of the square
     * @param col the ABSOLUTE column
     * @param row the ABSOLUTE row
     * @return whether the square is valid
     */
    public static boolean isValidSquare(int col, int row) {
        return col >= 0 && col <= 7 && row >= 0 && row <= 7;
    }
    
    /**
     * Determines which column a square is referring to<br>
     * <br>
     * The columns are ordered as such:<br>
     * |_|_|_|_|_|_|_|_|<br>
     * |0 1 2 3 4 5 6 7<br>
     * |a b c d e f g h
     * @param s a square
     * @return which column the String is referring to
     */
    public static int getColumn(String s) {
        if(isValidSquare(s)) {
            return s.charAt(0)-'a';
        } else {
            System.out.println("Invalid: " + s);
            throw new IllegalArgumentException("Invalid square");
        }
    }
    
    /**
     * Determines which row a square is referring to<br>
     * <br>
     * The rows are ordered as such:<br>
     * ____<br>
     * 0 |_<br>
     * 1 |_ <br>
     * 2 |_<br>
     * 3 |_<br>
     * 4 |_<br>
     * 5 |_<br>
     * 6 |_<br>
     * 7 |_<br>
     * ___W
     * @param s the square
     * @return the column / file
     */
    public static int getRow(String s) {
        if(isValidSquare(s)) {
            return 8 - Integer.parseInt(s.charAt(1) + "");
        } else throw new IllegalArgumentException("Invalid square");
    }
    
    /**
     * Determines where a square is after a shift (a.k.a. moving it left and right, up and down)
     * @param col current column
     * @param row current row
     * @param colShift how much to shift the columns
     * @param rowShift how much to shift the rows
     * @return the shifted square
     */
    public static String shiftSquare(int col, int row, int colShift, int rowShift) {
        if(isValidSquare(col, row)) {
            int shiftedCol = col + colShift, shiftedRow = row + rowShift;
            if(isValidSquare(shiftedCol, shiftedRow)) {
                return toSquare(shiftedCol, shiftedRow);
            } else throw new IllegalArgumentException("Invalid shift");
        } else throw new IllegalArgumentException("Invalid square");
    }
    
    /**
     * Determines where a square is after a shift (a.k.a. moving it left and right, up and down)
     * @param s the current square
     * @param colShift how much to shift the columns
     * @param rowShift how much to shift the rows
     * @return the shifted square
     */
    public static String shiftSquare(String s, int colShift, int rowShift) {
        if(isValidSquare(s)) {
            int col = getColumn(s), row = getRow(s);
            int shiftedCol = col + colShift, shiftedRow = row + rowShift;
            if(isValidSquare(shiftedCol, shiftedRow)) {
                return toSquare(shiftedCol, shiftedRow);
            } else throw new IllegalArgumentException("Invalid shift");
        } else throw new IllegalArgumentException("Invalid square");
    }
    
    /**
     * Checks if a shift is valid
     * @param col current column
     * @param row current row
     * @param colShift how much to shift the columns
     * @param rowShift how much to shift the rows
     * @return whether the shift is valid
     */
    public static boolean isValidShift(int col, int row, int colShift, int rowShift) {
        if(isValidSquare(col, row)) {
            int shiftedCol = col + colShift, shiftedRow = row + rowShift;
            return isValidSquare(shiftedCol, shiftedRow);
        } else return false;
    }
    
    /**
     * Checks if this shift is valid
     * @param s current square
     * @param colShift how much to shift the columns
     * @param rowShift how much to shift the rows
     * @return whether the shift is valid
     */
    public static boolean isValidShift(String s, int colShift, int rowShift) {
        return isValidShift(
                ChessBoard.getColumn(s), ChessBoard.getRow(s), 
                colShift, rowShift
        );
    }
    
    /**
     * Determines the square represented by the row and column
     * @param column the ABSOLUTE column
     * @param row the ABSOLUTE row
     * @return the square that is represented by the row and column
     */
    public static String toSquare(int column, int row) {
        return "" + (char)('a' + column) + (8 - row);
    }
    
    /**
     * Recalculates all of the moves on a square
     */
    public void recalculateMoves() {
        allLegalMoves = new HashMap<>();
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if(board[i][j] == null) continue;
                if(board[i][j].isWhite == playerIsWhite) {
                    String current = ChessBoard.toSquare(i, j);
                    LinkedList<String> moves = board[i][j].legalMoves(this, current);
                    allLegalMoves.put(current, moves);
                }
            }
        }
    }
    
    /**
     * Moves a piece from fromWhere to toWhere
     * @param fromWhere from where a piece is moved
     * @param toWhere where to move a piece
     */
    public void movePiece(String fromWhere, String toWhere) {
        movePiece(
                ChessBoard.getColumn(fromWhere), 
                ChessBoard.getRow(fromWhere), 
                ChessBoard.getColumn(toWhere), 
                ChessBoard.getRow(toWhere)
        );
    }
    
    /**
     * Moves a piece from fromWhere(X, Y) to toWhere(X, Y)
     * @param fromWhereX from where a piece is moved
     * @param fromWhereY from where a piece is moved
     * @param toWhereX where to move a piece
     * @param toWhereY where to move a piece
     */
    public void movePiece(int fromWhereX, int fromWhereY, int toWhereX, int toWhereY) {
        if(board[fromWhereX][fromWhereY] == null) return;
        ChessBoard thisCopy = new ChessBoard(this);
        maybeMove(fromWhereX, fromWhereY, toWhereX, toWhereY);
        if(board[toWhereX][toWhereY].getCharRepresentation().equals("K")) {
            ((King)(board[toWhereX][toWhereY])).notifyOfMove();
        }
        enPassant = null;
        if(board[toWhereX][toWhereY].getCharRepresentation().equals("P")) {
            if(Math.abs(fromWhereY-toWhereY) == 2) {
                String file = (char) ('a' + fromWhereX) + "", rank = String.valueOf(8-((fromWhereY+toWhereY)/2));
                enPassant = file + rank;
            }
        }
        String kingAt = kingPos.get(playerIsWhite);
        ((King)(board[getColumn(kingAt)][getRow(kingAt)])).notifyNoCheck();
        System.out.println("Moved: " + playerIsWhite);
        playerIsWhite = !playerIsWhite;
        recalculateMoves();
        updatePos(miniFEN());
        mr.moved(thisCopy, this, ChessBoard.toSquare(fromWhereX, fromWhereY), ChessBoard.toSquare(toWhereX, toWhereY));
        if(checkMated(playerIsWhite)) System.out.println("Checkmate!\n");
        else if(inCheck(playerIsWhite)) {
            ((King)(getPiece(kingPos.get(playerIsWhite)))).notifyCheck();
            System.out.println("Check!\n");
        } else if(isDraw(playerIsWhite)) System.out.println("Draw.\n");
    }
    
    /**
     * Used to check whether this move is legal
     * @param fromWhere from where to move a piece
     * @param toWhere to where to move a piece
     */
    public void maybeMove(String fromWhere, String toWhere) {
        maybeMove(
                getColumn(fromWhere), getRow(fromWhere), 
                getColumn(toWhere), getRow(toWhere)
        );
    }
    
    /**
     * Used to check whether this move is legal
     * @param fromWhereX from which column to move a piece
     * @param fromWhereY from which row to move a piece
     * @param toWhereX to which column to move a piece
     * @param toWhereY to which row to move a piece
     */
    public void maybeMove(int fromWhereX, int fromWhereY, int toWhereX, int toWhereY) {
        if(board[fromWhereX][fromWhereY].getCharRepresentation().equals("K")) {
            if(Math.abs(fromWhereX-toWhereX) == 2 && fromWhereY == toWhereY) {
                // Castling
                if(fromWhereX < toWhereX) {
                    // Castling Kingside
                    board[toWhereX-1][toWhereY] = board[7][fromWhereY];
                    board[7][fromWhereY] = null;
                } else {
                    // Castling Queenside
                    board[toWhereX+1][toWhereY] = board[0][fromWhereY];
                    board[0][fromWhereY] = null;
                }
            }
        } else if(toSquare(toWhereX, toWhereY).equals(enPassant)) {
            board[getColumn(enPassant)][getRow(enPassant)+(fromWhereY-toWhereY)] = null;
        }
        
        board[toWhereX][toWhereY] = board[fromWhereX][fromWhereY];
        board[fromWhereX][fromWhereY] = null;
        
        try {
            if (board[toWhereX][toWhereY]
                    .getCharRepresentation()
                    .equals("K")) {
                kingPos.put(
                        playerIsWhite,
                        toSquare(toWhereX, toWhereY)
                );
            }
        } catch (NullPointerException npe) {
            System.out.println("\n\nNULLPOINTEREXCEPTION");
            printBoard();
            System.out.println(board[toWhereX][toWhereY]);
        }
    }
    
    /**
     * Promotes a pawn
     * @param fromWhere from where to promote
     * @param toWhere to where to promote
     * @param toWhatPiece to what piece to promote to
     */
    public void promotePiece(String fromWhere, String toWhere, int toWhatPiece) {
        if(!getPiece(fromWhere).getCharRepresentation().equals("P")) 
            assert false : "Cannot promote a non-pawn";
        boolean isWhite = getPiece(fromWhere).isWhite;
        ChessBoard thisCopy = new ChessBoard(this);
        int fromWhereX = getColumn(fromWhere), fromWhereY = getRow(fromWhere);
        int toWhereX = getColumn(toWhere), toWhereY = getRow(toWhere);
        board[fromWhereX][fromWhereY] = null;
        // board[toWhereX][toWhereY];
        switch(toWhatPiece) {
            case MoveRecorder.BISHOP:
                board[toWhereX][toWhereY] = new Bishop(isWhite);
                break;
            case MoveRecorder.KNIGHT:
                board[toWhereX][toWhereY] = new Knight(isWhite);
                break;
            case MoveRecorder.QUEEN:
                board[toWhereX][toWhereY] = new Queen(isWhite);
                break;
            case MoveRecorder.ROOK:
                board[toWhereX][toWhereY] = new Rook(isWhite);
                break;
            default:
                throw new IllegalArgumentException("Unknown piece" + toWhatPiece);
        }
        playerIsWhite = !playerIsWhite;
        mr.moved(thisCopy, this, fromWhere, toWhere);
        System.out.println("Promoted from " + fromWhere + " to " + toWhere + " to a " + toWhatPiece);
        recalculateMoves();
        if(checkMated(playerIsWhite)) System.out.println("Checkmate!\n");
        else if(inCheck(playerIsWhite)) System.out.println("Check!\n");
        else if(stalemated(playerIsWhite)) System.out.println("Stalemate.\n");
    }
    
    /**
     * DO NOT USE OFTEN <br>
     * Places a piece somewhere
     * @param ap a piece to place
     * @param where where to place the piece
     * @deprecated since it is not needed
     */
    @Deprecated
    public void placePiece(AbstractPiece ap, String where) {
        placePiece(ap, getColumn(where), getRow(where));
    }
    
    /**
     * DO NOT USE OFTEN <br>
     * Places a piece somewhere
     * @param ap a piece to place
     * @param col the column to place the piece in
     * @param row the row to place the piece in
     * @deprecated since it is not needed
     */
    @Deprecated
    public void placePiece(AbstractPiece ap, int col, int row) {
        board[col][row] = ap;
    }
    
    /**
     * Determines whether one side's king is in check
     * @param isWhite whether the side to check is white (PUN INTENDED)
     * @return whether the side is in check
     */
    public boolean inCheck(boolean isWhite) {
        for(int i = 0;i<8;i++) {
            for(int j = 0;j<8;j++) {
                AbstractPiece ap = getPiece(i, j);//lit dude lit
                if(ap != null) {
                    if(ap.isWhite ^ isWhite) {
                        //if(ap.legalCaptures(this, ChessBoard.toSquare(i, j)).contains(kingPos))
                        // if the current opposite-colored piece can eat the king on the next move
                        if(ap.isAllLegalMove(this, ChessBoard.toSquare(i, j), kingPos.get(isWhite))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Determines whether the king is checkmated
     * @param isWhite whether the side to check is white (PUN INTENDED)
     * @return whether the side is checkmated
     */
    public boolean checkMated(boolean isWhite) {
        if(inCheck(isWhite)) {
            for(String s : allLegalMoves.keySet()) {
                if(!allLegalMoves.get(s).isEmpty()) return false;
            }
            return true;
        } else return false;
    }
    
    /**
     * Determines whether one side is stalemated
     * @param isWhite whether the side to check is white
     * @return whether one side is stalemated
     */
    public boolean stalemated(boolean isWhite) {
        for(LinkedList<String> allLegalMove : allLegalMoves.values()) {
            if(!allLegalMove.isEmpty()) return false;
        }
        return !inCheck(isWhite);
    }
    
    /**
     * Determines whether either side has insufficient material to checkmate
     * @return whether either side has insufficient material to checkmate
     */
    public boolean insufficientMaterial() {
        HashMap<String, Integer> pieces = new HashMap<>();
        pieces.put("BW", 0);
        pieces.put("BB", 0);
        pieces.put("N", 0);
        pieces.put("bw", 0);
        pieces.put("bb", 0);
        pieces.put("n", 0);
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if(board[i][j] == null) continue;
                if(board[i][j].getCharRepresentation().equals("K")) continue;
                if("QRP".contains(board[i][j].getCharRepresentation())) return false;
                String rep = board[i][j].getCharRepresentation();
                if(board[i][j].getCharRepresentation().equals("B")) {
                    if(isSquareWhite(i, j))
                        rep += "W";
                    else
                        rep += "B";
                }
                if(board[i][j].getCharRepresentation().equals("N")) {
                    String nRep = (board[i][j].isWhite)?"N":"n";
                    if(pieces.get(nRep) == 1)
                        return false;
                }
                if(!board[i][j].isWhite)
                    rep = rep.toLowerCase();
                try {
                    pieces.put(rep, pieces.get(rep)+1);
                } catch(NullPointerException npe) {
                    throw new NullPointerException(npe.getMessage() + ": " + rep);
                }
            }
        }
        final boolean noBW = pieces.get("BW") == 0, 
                noBB = pieces.get("BB") == 0, noN = pieces.get("N") == 0;
        final boolean nobw = pieces.get("bw") == 0, 
                nobb = pieces.get("bb") == 0, non = pieces.get("n") == 0;
        final boolean whiteBare = noBW && noBB && noN;
        final boolean blackBare = nobw && nobb && non;
        return (whiteBare && blackBare) || 
                (noN && non && ((noBB && nobb) || (noBW && nobw))) ||
                (blackBare && noN && (noBW || noBB)) || 
                (blackBare && noBB && noBW && pieces.get("N") == 1) || 
                (whiteBare && non && (nobw || nobb)) || 
                (whiteBare && nobb && nobw && pieces.get("n") == 1);
    }
    
    /**
     * Determines whether the current state of the game is a draw
     * @param isWhite the side to check for stalemates
     * @return whether the game is a draw
     */
    public boolean isDraw(boolean isWhite) {
        return insufficientMaterial() || stalemated(isWhite) || mr.is50MoveDraw() || threeFoldRep();
    }
    
    /**
     * Determines whether there is threefold repetition
     * @return whether there is threefold repetition
     */
    public boolean threeFoldRep() {
        for(int value : positions.values()) {
            if(value >= 3)
                return true;
        }
        return false;
    }
    
    /**
     * Determines whether the game has been drawn by 50 move draw
     * @return whether the game has been drawn by 50 move draw
     */
    public boolean is50MoveDraw() {
        return mr.is50MoveDraw();
    }
    
    /**
     * Updates positions
     * @param pos the position to update with
     */
    private void updatePos(String pos) {
        if(positions.containsKey(pos)) {
            positions.put(pos, positions.get(pos)+1);
        } else {
            positions.put(pos, 1);
        }
    }
    
    /**
     * Determines whether a square is white
     * @param square the square to check
     * @return whether the square is white
     */
    public static boolean isSquareWhite(String square) {
        return isSquareWhite(getColumn(square), getRow(square));
    }
    
    /**
     * Determines whether a square is white
     * @param col the column of the square to check
     * @param row the row of the square to check
     * @return whether the square is white
     */
    public static boolean isSquareWhite(int col, int row) {
        return (col+row)%2==0;
    }
    
    /**
     * Determines where all of the pieces which fit the criteria
     * @param whichPiece which piece, determined by the number
     * @param isWhite whether the piece is white
     * @return where all of the pieces are
     */
    public ArrayList<String> findAll(int whichPiece, boolean isWhite) {
        String representation;
        switch(whichPiece) {
            case MoveRecorder.BISHOP:
                representation = "B";
                break;
            case MoveRecorder.KING:
                representation = "K";
                break;
            case MoveRecorder.KNIGHT:
                representation = "N";
                break;
            case MoveRecorder.PAWN:
                representation = "P";
                break;
            case MoveRecorder.QUEEN:
                representation = "Q";
                break;
            case MoveRecorder.ROOK:
                representation = "R";
                break;
            default:
                throw new IllegalArgumentException("Unknown piece type: " + whichPiece);
        }
        ArrayList<String> output = new ArrayList<>();
        for(int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if(board[i][j] == null) continue;
                if(isWhite == board[i][j].isWhite && board[i][j].getCharRepresentation().equals(representation)) {
                    output.add(toSquare(i, j));
                }
            }
        }
        return output;
    }
    
    /**
     * Refinds both kings.
     */
    public void resetKingPos() {
        String bKing = null, wKing = null;
        OUTER: for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if(board[i][j] == null) continue;
                if(board[i][j].getCharRepresentation().equals("K")) {
                    if(board[i][j].isWhite) {
                        if(wKing == null) {
                            wKing = toSquare(i, j);
                        } else {
                            assert false : "There are two white kings?!";
                        }
                    } else {
                        if(bKing == null) {
                            bKing = toSquare(i, j);
                        } else {
                            assert false : "There are two black kings?!";
                        }
                    }
                    if(wKing != null && bKing != null) break OUTER;
                }
            }
        }
        if(wKing == null) assert false : "Cannot find white king";
        if(bKing == null) assert false : "Cannot find black king";
        kingPos.put(true, wKing);
        kingPos.put(false, bKing);
    }
    
    /**
     * Refinds only one king.
     * @param isWhite whether the king to find again is white
     */
    public void resetKingPos(boolean isWhite) {
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if(board[i][j] == null) continue;
                if(board[i][j].getCharRepresentation().equals("K") && (board[i][j].isWhite == isWhite)) {
                    kingPos.put(isWhite, toSquare(i, j));
                    return;
                }
            }
        }
    }

    /**
     * Determines which square is open for en passant
     * @return which square is open for en passant
     */
    public String getEnPassant() {
        return enPassant;
    }

    /**
     * Returns the current player
     * @return the current player
     */
    public boolean currentPlayer() {
        return playerIsWhite;
    }

    /**
     * Returns the board of AbstractPieces
     * @return the board of AbstractPieces
     */
    public AbstractPiece[][] getBoard() {
        return board;
    }
    
    /**
     * Rotates a square 180 degrees.
     * @param s the square to rotate
     * @return the resulting square
     */
    public String rotateSquare180(String s) {
        return rotateSquare180(getColumn(s), getRow(s));
    }
    
    /**
     * Rotates a square 180 degrees.
     * @param x the x position of the square to rotate
     * @param y the y position of the square to rotate
     * @return the resulting square
     */
    public String rotateSquare180(int x, int y) {
        String output = toSquare(7-x, 7-y);
        return (isValidSquare(output))? output : null;
    }

    /**
     * DO NOT USE OFTEN <br>
     * Sets this board to a new state
     * @param board the board to set to
     */
    public void setBoard(AbstractPiece[][] board) {
        this.board = new AbstractPiece[board.length][board[0].length];
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                this.board[i][j] = board[i][j];
            }
        }
    }
    
    /**
     * Prints the current state of the chess board.
     */
    public void printBoard() {
        for(int i = 0;i<board[0].length;i++) {
            for(int j = 0;j<board.length;j++) {
                AbstractPiece ap = board[j][i];
                if(ap == null) {
                    System.out.print(" ");
                } else if(ap.isWhite) {
                    System.out.print(ap.getCharRepresentation());
                } else {
                    System.out.print(ap.getCharRepresentation().toLowerCase());
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Prints all of the current moves.
     */
    public void printMoves() {
        System.out.println(mr.toString());
    }
    
    /**
     * Returns a miniature of this chess board
     * @return a miniature of this chess board
     */
    public String miniFEN() {
        String output = "";
        for(AbstractPiece[] col : board) {
            int blanks = 0;
            for(AbstractPiece piece : col) {
                if(piece == null) {
                    blanks++;
                } else {
                    if(blanks != 0) {
                        output += blanks;
                    }
                    blanks = 0;
                    String rep = piece.getCharRepresentation();
                    if(piece.isWhite) 
                        output += rep.toUpperCase(); 
                    else 
                        output += rep.toLowerCase();
                }
            }
            output += "/";
        }
        return output;
    }
}