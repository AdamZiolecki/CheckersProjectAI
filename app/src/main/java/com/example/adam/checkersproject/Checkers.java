package com.example.adam.checkersproject;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sac.game.AlphaBetaPruning;
import sac.game.GameSearchAlgorithm;
import sac.game.GameSearchConfigurator;
import sac.game.GameState;
import sac.game.GameStateImpl;

public class Checkers extends GameStateImpl implements Runnable {

    private static final int m = 8;
    private static final int n = 8;
    private static final int WHITE_TILE = -1;
    private static final int DARK_TILE_EMPTY = 0;
    private static final int WHITE_PIECE = 1;
    private static final int BLACK_PIECE = 2;
    private static final int WHITE_DAME = 3;
    private static final int BLACK_DAME = 4;

    public int whitePiecesAmount;
    public int blackPiecesAmount;

    private List<String> possibleCaptures;
    public List<String> bestCaptures;

    public int playerColor = 1;
    public int mode = 1;
    public int difficulty = 4;
    public String winningCommunicate = "";
    private int winPlayer = 0;

    GameSearchAlgorithm alg;
    GameSearchConfigurator conf;

    public byte[][] board = null;

    Checkers() {
        board = new byte[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i <= 2 && (i + j) % 2 == 1) {
                    board[i][j] = BLACK_PIECE;
                } else if (i >= 5 && (i + j) % 2 == 1) {
                    board[i][j] = WHITE_PIECE;
                } else if ((i + j) % 2 == 1) {
                    board[i][j] = DARK_TILE_EMPTY;
                } else {
                    board[i][j] = WHITE_TILE;
                }
            }
        }
        whitePiecesAmount = 12;
        blackPiecesAmount = 12;
        setMaximizingTurnNow(true);

        conf = new GameSearchConfigurator();
        conf.setParentsMemorizingChildren(true);
        conf.setRefutationTableOn(true);
        conf.setTranspositionTableOn(true);
        conf.setDepthLimit(2);

        alg = new AlphaBetaPruning(this, conf);
        Checkers.setHFunction(new Heuristic());
    }

    private Checkers(Checkers parent) {
        board = new byte[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = parent.board[i][j];
            }
        }
        whitePiecesAmount = parent.whitePiecesAmount;
        blackPiecesAmount = parent.blackPiecesAmount;
        setMaximizingTurnNow(parent.isMaximizingTurnNow());
    }

    @Override
    public void run() {
        System.out.println("In run() function; MaximazingTurn: " + isMaximizingTurnNow());
        if (isMaximizingTurnNow() == (playerColor == 2))
        {
            alg.execute();
            //System.out.println("BEST SCORES:        " + alg.getMovesScores());
            //GameSearchGraphvizer.go(alg , "D:/crap/files/output.dot" , true , true);
            //System.out.println("DEPTH:         " + alg.getDepthReached());
            String str = alg.getFirstBestMove();
            System.out.println("CLOSED: " + alg.getClosedStatesCount());
            System.out.println("TIME: " + alg.getDurationTime());
            //System.out.println("BEST MOVE:      " + alg.getFirstBestMove());
            if (str != null) {
                checkMove(Character.getNumericValue(str.charAt(0)), Character.getNumericValue(str.charAt(1)), Character.getNumericValue(str.charAt(2)), Character.getNumericValue(str.charAt(3)));
            }
        }
    }

    @Override
    public List<GameState> generateChildren() {
        List<GameState> children = new ArrayList<GameState>();
        this.checkBoard();
        int piece, dame, dir;
        if (isMaximizingTurnNow()) {
            piece = WHITE_PIECE;
            dame = WHITE_DAME;
            dir = -1;
        }
        else {
            piece = BLACK_PIECE;
            dame = BLACK_DAME;
            dir = 1;
        }

        if (this.bestCaptures.isEmpty()) {
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (board[i][j] == piece) {
                        if (j - 1 >= 0) {
                            Checkers child = new Checkers(this);
                            if (child.checkMove(i, j, i + dir, j - 1)) {
                                StringBuilder txt = new StringBuilder();
                                txt.append(i);
                                txt.append(j);
                                txt.append(i + dir);
                                txt.append(j - 1);
                                child.setMoveName(txt.toString());
                                children.add(child);
                            }
                        }
                        if (j + 1 < 8) {
                            Checkers child = new Checkers(this);
                            if (child.checkMove(i, j, i + dir, j + 1)) {
                                StringBuilder txt = new StringBuilder();
                                txt.append(i);
                                txt.append(j);
                                txt.append(i + dir);
                                txt.append(j + 1);
                                child.setMoveName(txt.toString());
                                children.add(child);
                            }
                        }
                    }
                    else if (board[i][j] == dame) {
                        for (int k = 1; k < 8; ++k) {
                            for (int l = 1; l < 8; ++l) {
                                Checkers child1 = new Checkers(this);
                                if (child1.checkMove(i, j, i + k, j + l)) {
                                    StringBuilder txt = new StringBuilder();
                                    txt.append(i);
                                    txt.append(j);
                                    txt.append(i + k);
                                    txt.append(j + l);
                                    child1.setMoveName(txt.toString());
                                    children.add(child1);
                                }
                                Checkers child2 = new Checkers(this);
                                if (child2.checkMove(i ,j, i + k, j - l)) {
                                    StringBuilder txt = new StringBuilder();
                                    txt.append(i);
                                    txt.append(j);
                                    txt.append(i + k);
                                    txt.append(j - l);
                                    child2.setMoveName(txt.toString());
                                    children.add(child2);
                                }
                                Checkers child3 = new Checkers(this);
                                if (child3.checkMove(i ,j, i - k, j + l)) {
                                    StringBuilder txt = new StringBuilder();
                                    txt.append(i);
                                    txt.append(j);
                                    txt.append(i - k);
                                    txt.append(j + l);
                                    child3.setMoveName(txt.toString());
                                    children.add(child3);
                                };
                                Checkers child4 = new Checkers(this);
                                if (child4.checkMove(i ,j, i - k, j - l)) {
                                    StringBuilder txt = new StringBuilder();
                                    txt.append(i);
                                    txt.append(j);
                                    txt.append(i - k);
                                    txt.append(j - l);
                                    child4.setMoveName(txt.toString());
                                    children.add(child4);
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            for (int k = 0; k < bestCaptures.size(); ++k) {
                Checkers child = new Checkers(this);
                List<Point> coords = toCoordinates(bestCaptures.get(k));
                if (child.checkMove(coords.get(0).x, coords.get(0).y, coords.get(coords.size() - 1).x, coords.get(coords.size() - 1).y)) {
                    StringBuilder txt = new StringBuilder();
                    txt.append(coords.get(0).x);
                    txt.append(coords.get(0).y);
                    txt.append(coords.get(coords.size() - 1).x);
                    txt.append(coords.get(coords.size() - 1).y);
                    child.setMoveName(txt.toString());
                    children.add(child);
                }
            }
        }
        return children;
    }

    void checkBoard() {
        possibleCaptures = new ArrayList<>();
        bestCaptures = new ArrayList<>();
        byte [][] boardCopy = cloneBoard(board);
        int piece;
        int dame;
        if (isMaximizingTurnNow()) {
            piece = WHITE_PIECE;
            dame = WHITE_DAME;
        }
        else {
            piece = BLACK_PIECE;
            dame = BLACK_DAME;
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (boardCopy[i][j] == piece) {
                    canCapture(cloneBoard(boardCopy), i, j, "");
                }
                else if (boardCopy[i][j] == dame) {
                    dameCanCapture(cloneBoard(boardCopy), i, j, "");
                }
            }
        }
        if (possibleCaptures.isEmpty()) {
            if (!canMove() && isMaximizingTurnNow()) {
                winPlayer = 2;
            }
            else if (!canMove() && !isMaximizingTurnNow()) {
                winPlayer = 1;
            }
        }
        else {
            selectBestCapture();
        }
    }

    private void dameCanCapture(byte [][] board, int x, int y, String capturePath) {
        int oppPiece;
        int oppDame;
        int ownPiece;
        int ownDame;
        if (isMaximizingTurnNow()) {
            oppPiece = BLACK_PIECE;
            oppDame = BLACK_DAME;
            ownPiece = WHITE_PIECE;
            ownDame = WHITE_DAME;
        }
        else {
            oppPiece = WHITE_PIECE;
            oppDame = WHITE_DAME;
            ownPiece = BLACK_PIECE;
            ownDame = BLACK_DAME;
        }

        try {
            for (int i = 1, j = 1; board[x - i][y + j] != ownPiece && board[x - i][y + j] != ownDame; i++, j++) {
                if ((board[x - i][y + j] == oppPiece || board[x - i][y + j] == oppDame) && board[x - (i + 1)][y + (j + 1)] == DARK_TILE_EMPTY) {
                    for (int k = 1, l = 1; board[x - (i + k)][y + (j + l)] == DARK_TILE_EMPTY; k++, l++) {
                        simDameExecuteCapture(board, x, y, x - i, y + j, x - (i + k), y + (j + l));
                        dameCanCapture(board, x - (i + k), y + (j + l), capturePath + x + y + (x - i) + (y + j) + (x - (i + k)) + (y + (j + l)));
                    }
                }
                else if ((board[x - i][y + j] == oppPiece || board[x - i][y + j] == oppDame) && (board[x - (i + 1)][y + (j + 1)] == oppPiece || board[x - (i + 1)][y + (j + 1)] == oppDame)) {
                    break;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        try {
            for (int i = 1, j = 1; board[x - i][y - j] != ownPiece && board[x - i][y - j] != ownDame; i++, j++) {
                if ((board[x - i][y - j] == oppPiece || board[x - i][y - j] == oppDame) && board[x - (i + 1)][y - (j + 1)] == DARK_TILE_EMPTY) {
                    for (int k = 1, l = 1; board[x - (i + k)][y - (j + l)] == DARK_TILE_EMPTY; k++, l++) {
                        simDameExecuteCapture(board, x, y, x - i, y - j, x - (i + k), y - (j + l));
                        dameCanCapture(board, x - (i + k), y - (j + l), capturePath + x + y + (x - i) + (y - j) + (x - (i + k)) + (y - (j + l)));
                    }
                }
                else if ((board[x - i][y - j] == oppPiece || board[x - i][y - j] == oppDame) && (board[x - (i + 1)][y - (j + 1)] == oppPiece || board[x - (i + 1)][y - (j + 1)] == oppDame)) {
                    break;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        try {
            for (int i = 1, j = 1; board[x + i][y - j] != ownPiece && board[x + i][y - j] != ownDame; i++, j++) {
                if ((board[x + i][y - j] == oppPiece || board[x + i][y - j] == oppDame) && board[x + (i + 1)][y - (j + 1)] == DARK_TILE_EMPTY) {
                    for (int k = 1, l = 1; board[x + (i + k)][y - (j + l)] == DARK_TILE_EMPTY; k++, l++) {
                        simDameExecuteCapture(board, x, y, x + i, y - j, x + (i + k), y - (j + l));
                        dameCanCapture(board, x + (i + k), y - (j + l), capturePath + x + y + (x + i) + (y - j) + (x + (i + k)) + (y - (j + l)));
                    }
                }
                else if ((board[x + i][y - j] == oppPiece || board[x + i][y - j] == oppDame) && (board[x + (i + 1)][y - (j + 1)] == oppPiece || board[x + (i + 1)][y - (j + 1)] == oppDame)) {
                    break;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        try {
            for (int i = 1, j = 1; board[x + i][y + j] != ownPiece && board[x + i][y + j] != ownDame; i++, j++) {
                if ((board[x + i][y + j] == oppPiece || board[x + i][y + j] == oppDame) && board[x + (i + 1)][y + (j + 1)] == DARK_TILE_EMPTY) {
                    for (int k = 1, l = 1; board[x + (i + k)][y + (j + l)] == DARK_TILE_EMPTY; k++, l++) {
                        simDameExecuteCapture(board, x, y, x + i, y + j, x + (i + k), y + (j + l));
                        dameCanCapture(board, x + (i + k), y + (j + l), capturePath + x + y + (x + i) + (y + j) + (x + (i + k)) + (y + (j + l)));
                    }
                }
                else if ((board[x + i][y + j] == oppPiece || board[x + i][y + j] == oppDame) && (board[x + (i + 1)][y + (j + 1)] == oppPiece || board[x + (i + 1)][y + (j + 1)] == oppDame)) {
                    break;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}

        if (!capturePath.equals("")) {
            possibleCaptures.add(capturePath);
        }
    }

    private void selectBestCapture() {
        if (!possibleCaptures.isEmpty()) {
            int max = 0;
            for (int i = 0; i < possibleCaptures.size(); ++i) {
                if (max < possibleCaptures.get(i).length()) {
                    max = possibleCaptures.get(i).length();
                    bestCaptures.clear();
                    bestCaptures.add(possibleCaptures.get(i));
                } else if (max == possibleCaptures.get(i).length()) {
                    bestCaptures.add(possibleCaptures.get(i));
                }
            }
        }
    }

    private byte [][] cloneBoard(byte [][] board) {
        byte [][] newBoard = new byte[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        return  newBoard;
    }

    private boolean canMove() {
        int piece;
        int dame;
        if (isMaximizingTurnNow()) {
            piece = WHITE_PIECE;
            dame = WHITE_DAME;
        }
        else {
            piece = BLACK_PIECE;
            dame = BLACK_DAME;
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == dame) {
                    return true;
                }
                else if (board[i][j] == piece) {
                    if (isMaximizingTurnNow()) {
                        try {
                            if (board[i - 1][j + 1] != DARK_TILE_EMPTY) {
                            } else {
                                return true;
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            if (board[i - 1][j - 1] != DARK_TILE_EMPTY) {
                            } else {
                                return true;
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    } else {
                        try {
                            if (board[i + 1][j + 1] != DARK_TILE_EMPTY) {
                            } else {
                                return true;
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            if (board[i + 1][j - 1] != DARK_TILE_EMPTY) {
                            } else {
                                return true;
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                }
            }
        }
        return false;
    }

    private void canCapture(byte [][] board, int x, int y, String capturePath) {
        int oppPiece;
        int oppDame;
        if (isMaximizingTurnNow()) {
            oppPiece = BLACK_PIECE;
            oppDame = BLACK_DAME;
        }
        else {
            oppPiece = WHITE_PIECE;
            oppDame = WHITE_DAME;
        }

        try {
            if ((board[x - 1][y + 1] == oppPiece || board[x - 1][y + 1] == oppDame) && board[x - 2][y + 2] == DARK_TILE_EMPTY) {
                simExecuteCapture(board, x, y, x - 1, y + 1, x - 2, y + 2);
                canCapture(board, x - 2, y + 2, capturePath + x + y + (x - 1) + (y + 1) + (x - 2) + (y + 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            if ((board[x - 1][y - 1] == oppPiece || board[x - 1][y - 1] == oppDame) && board[x - 2][y - 2] == DARK_TILE_EMPTY) {
                simExecuteCapture(board, x, y, x - 1, y - 1, x - 2, y - 2);
                canCapture(board, x - 2, y - 2, capturePath + x + y + (x - 1) + (y - 1) +(x - 2) + (y - 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            if ((board[x + 1][y - 1] == oppPiece || board[x + 1][y - 1] == oppDame) && board[x + 2][y - 2] == DARK_TILE_EMPTY) {
                simExecuteCapture(board, x, y, x + 1, y - 1, x + 2, y - 2);
                canCapture(board, x + 2, y - 2, capturePath + x + y + (x + 1) + (y - 1) + (x + 2) + (y - 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            if ((board[x + 1][y + 1] == oppPiece || board[x + 1][y + 1] == oppDame) && board[x + 2][y + 2] == DARK_TILE_EMPTY) {
                simExecuteCapture(board, x, y, x + 1, y + 1, x + 2, y + 2);
                canCapture(board, x + 2, y + 2, capturePath + x + y + (x + 1) + (y + 1) + (x + 2) + (y + 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("Skip");
        }

        if (!capturePath.equals("")) {
            possibleCaptures.add(capturePath);
        }
    }

    private void simDameExecuteCapture(byte [][] board, int x1, int y1, int x2, int y2, int x3, int y3) {
        if (isMaximizingTurnNow()) {
            board[x2][y2] = DARK_TILE_EMPTY;
            board[x3][y3] = WHITE_DAME;
        }
        else {
            board[x2][y2] = DARK_TILE_EMPTY;
            board[x3][y3] = BLACK_DAME;
        }
        board[x1][y1] = DARK_TILE_EMPTY;
    }

    public boolean checkMove(int x1, int y1, int x2, int y2) {
        checkBoard();
        try {
            if (!bestCaptures.isEmpty()) {
                for (int i = 0; i < bestCaptures.size(); ++i) {
                    List<Point> coords = toCoordinates(bestCaptures.get(i));
                    if (x1 == coords.get(0).x && y1 == coords.get(0).y) {
                        if (x2 == coords.get(coords.size() - 1).x && y2 == coords.get(coords.size() - 1).y) {
                            executeComplexCapture(coords);
                            return true;
                        }
                    }
                }
            } else {
                if (isMaximizingTurnNow() && board[x1][y1] == WHITE_PIECE && board[x2][y2] == DARK_TILE_EMPTY) {
                    if ((x1 - 1 == x2 && y1 - 1 == y2) || (x1 - 1 == x2 && y1 + 1 == y2)) {
                        makeMove(x1, y1, x2, y2);
                        return true;
                    }
                } else if (!isMaximizingTurnNow() && board[x1][y1] == BLACK_PIECE && board[x2][y2] == DARK_TILE_EMPTY) {
                    if ((x1 + 1 == x2 && y1 - 1 == y2) || (x1 + 1 == x2 && y1 + 1 == y2)) {
                        makeMove(x1, y1, x2, y2);
                        return true;
                    }
                } else if (isMaximizingTurnNow() && board[x1][y1] == WHITE_DAME && board[x2][y2] == DARK_TILE_EMPTY && Math.abs(x1 - x2) == Math.abs(y1 - y2)) {
                    int xInc, yInc;
                    if (x1 - x2 > 0) {
                        xInc = -1;
                    } else {
                        xInc = 1;
                    }
                    if (y1 - y2 > 0) {
                        yInc = -1;
                    } else {
                        yInc = 1;
                    }
                    int i = xInc;
                    int j = yInc;

                    while ((i + x1) != x2) {
                        if (board[x1 + i][y1 + j] != DARK_TILE_EMPTY) {
                            return false;
                        }

                        if (xInc > 0) {
                            i++;
                        } else {
                            i--;
                        }
                        if (yInc > 0) {
                            j++;
                        } else {
                            j--;
                        }
                    }
                    makeMove(x1, y1, x2, y2);
                    return true;
                } else if (!isMaximizingTurnNow() && board[x1][y1] == BLACK_DAME && board[x2][y2] == DARK_TILE_EMPTY && Math.abs(x1 - x2) == Math.abs(y1 - y2)) {
                    int xInc, yInc;
                    if (x1 - x2 > 0) {
                        xInc = -1;
                    } else {
                        xInc = 1;
                    }
                    if (y1 - y2 > 0) {
                        yInc = -1;
                    } else {
                        yInc = 1;
                    }
                    int i = xInc;
                    int j = yInc;

                    while ((i + x1) != x2) {
                        if (board[x1 + i][y1 + j] != DARK_TILE_EMPTY) {
                            return false;
                        }

                        if (xInc > 0) {
                            i++;
                        } else {
                            i--;
                        }
                        if (yInc > 0) {
                            j++;
                        } else {
                            j--;
                        }
                    }
                    makeMove(x1, y1, x2, y2);
                    return true;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {}

        return false;
    }

    private void makeMove(int x1, int y1, int x2, int y2) {
        byte piece = board[x1][y1];
        board[x1][y1] = DARK_TILE_EMPTY;
        board[x2][y2] = piece;
        if (isMaximizingTurnNow() && x2 == 0) {
            becameDame(x2, y2);
        }
        else if (!isMaximizingTurnNow() && x2 == 7) {
            becameDame(x2, y2);
        }
        setMaximizingTurnNow(!isMaximizingTurnNow());
    }

    private void becameDame(int x, int y) {
        if (isMaximizingTurnNow()) {
            board[x][y] = WHITE_DAME;
            whitePiecesAmount += 2;
        }
        else {
            board[x][y] = BLACK_DAME;
            blackPiecesAmount += 2;
        }
    }

    private List<Point> toCoordinates(String capturePath) {
        List<Point> coordinates = new ArrayList<Point>();
        for (int i = 0; i < capturePath.length() - 1; i += 2) {
            Point point = new Point(Character.getNumericValue(capturePath.charAt(i)), Character.getNumericValue(capturePath.charAt(i+1)));
            coordinates.add(point);
        }
        return coordinates;
    }

    private void executeComplexCapture(List<Point> coords) {
        byte pieceKind = board[coords.get(0).x][coords.get(0).y];
        board[coords.get(0).x][coords.get(0).y] = DARK_TILE_EMPTY;

        int x1, y1, x2, y2, x3, y3;
        for (int i = 0; i < coords.size() - 2; i += 3) {
            x1 = coords.get(i).x;
            y1 = coords.get(i).y;
            x2 = coords.get(i + 1).x;
            y2 = coords.get(i + 1).y;
            x3 = coords.get(i + 2).x;
            y3 = coords.get(i + 2).y;

            board[x1][y1] = DARK_TILE_EMPTY;

            if (board[x2][y2] == WHITE_PIECE)
                whitePiecesAmount--;
            else if (board[x2][y2] == WHITE_DAME)
                whitePiecesAmount -= 3;
            else if (board[x2][y2] == BLACK_PIECE)
                blackPiecesAmount--;
            else if (board[x2][y2] == BLACK_DAME)
                blackPiecesAmount -= 3;

            board[x2][y2] = DARK_TILE_EMPTY;
            board[x3][y3] = pieceKind;
        }

        if (board[coords.get(coords.size() - 1).x][coords.get(coords.size() - 1).y] == WHITE_PIECE || board[coords.get(coords.size() - 1).x][coords.get(coords.size() - 1).y] == BLACK_PIECE) {
            if (isMaximizingTurnNow() && coords.get(coords.size() - 1).x == 0) {
                becameDame(coords.get(coords.size() - 1).x, coords.get(coords.size() - 1).y);
            } else if (!isMaximizingTurnNow() && coords.get(coords.size() - 1).x == 7) {
                becameDame(coords.get(coords.size() - 1).x, coords.get(coords.size() - 1).y);
            }
        }
        setMaximizingTurnNow(!isMaximizingTurnNow());
    }

    private void simExecuteCapture(byte [][] board, int x1, int y1, int x2, int y2, int x3, int y3) {
        if (isMaximizingTurnNow()) {
            board[x2][y2] = DARK_TILE_EMPTY;
            board[x3][y3] = WHITE_PIECE;
        }
        else {
            board[x2][y2] = DARK_TILE_EMPTY;
            board[x3][y3] = BLACK_PIECE;
        }
        board[x1][y1] = DARK_TILE_EMPTY;
    }

    public void newGame() {
        board = new byte [m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i <= 2 && (i+j)%2 == 1) {
                    board[i][j] = BLACK_PIECE;
                }
                else if (i >= 5 && (i+j)%2 == 1) {
                    board[i][j] = WHITE_PIECE;
                }
                else if ((i+j)%2 == 1){
                    board[i][j] = DARK_TILE_EMPTY;
                }
                else {
                    board[i][j] = WHITE_TILE;
                }
            }
        }
        whitePiecesAmount = 12;
        blackPiecesAmount = 12;
        setMaximizingTurnNow(true);
        switch (difficulty) {
            case 1:                         // Easy
                Checkers.setHFunction(new HeuristicRandom());
                conf.setDepthLimit(0.5);
                break;
            case 2:                         // Medium
                Checkers.setHFunction(new Heuristic());
                conf.setDepthLimit(1);
                break;
            case 3:                         // Hard
                Checkers.setHFunction(new Heuristic());
                conf.setDepthLimit(1.5);
                break;
            case 4:                         // Very hard
                Checkers.setHFunction(new Heuristic());
                conf.setDepthLimit(2);
                break;
            default:
                System.err.println("Undefined game difficulty");

        }
        winPlayer = 0;
    }

    public int hashCode() {
        byte[] lin = new byte[m*n];
        int k = 0;
        for (int i = 0; i<n; i++)
            for (int j = 0; j<m; j++)
                lin[k++] = board[i][j];
        return Arrays.hashCode(lin);
    }

    public boolean isEnd() {
        if (whitePiecesAmount == 0) {
            winningCommunicate = "Black wins!";
            return true;
        }
        else if (blackPiecesAmount == 0) {
            winningCommunicate = "White wins!";
            return true;
        }
        else if (winPlayer == 1) {
            winningCommunicate = "White wins!";
            return true;
        }
        else if (winPlayer == 2) {
            winningCommunicate = "Black wins!";
            return true;
        }
        return false;
    }

}
