package assignment;
import java.awt.*;
public final class TetrisBoard implements Board {
    private Action lastAction;
    private Result lastResult;
    private int gridWidth;
    private int gridHeight;
    private Piece.PieceType[][] grid;
    private int[] rowWidths;
    private int[] colHeights;
    private int maxHeight;
    private int rowsCleared;
    private TetrisPiece currentPiece;

    // JTetris will use this generic constructor
    public TetrisBoard(int width, int height) {
        gridWidth = width;
        gridHeight = height;
        grid = new Piece.PieceType[height][width];
        currentPiece = null;
        maxHeight = 0;
        rowWidths = new int[height];
        colHeights = new int[width];
        rowsCleared = 0;
        lastAction = null;
        lastResult = null;
    }

    // main function to move a current piece based on a given Action
    public Result move(Action act) {
        lastAction = act;
        // edge case: checks to see whether current piece is valid and on the board
        if(currentPiece == null) {
            lastResult = Result.NO_PIECE;
            return Result.NO_PIECE;
        }
        Point pos = currentPiece.getCurrentPosition();
        switch(act) {
            // moves a current piece left one
            case LEFT:
                // loops through each piece on the vertical column in current piece
                for(int i = pos.y; i < pos.y + currentPiece.getHeight(); ++i){
                    // uses the LSkirt to avoid when a row of the current piece is Integer.MAX_VALUE
                    if(currentPiece.getLSkirt()[i - pos.y] != Integer.MAX_VALUE){
                        // finds the index that is one left of the current point
                        int leftOfPiece = pos.x + currentPiece.getLSkirt()[i - pos.y];
                        // edge case: evaluates whether a piece travels out of bounds
                        if(leftOfPiece <= 0){
                            lastResult = Result.OUT_BOUNDS;
                            return Result.OUT_BOUNDS;
                        }
                        // edge case: evaluates whether a piece hits another piece
                        else{
                            if(grid[i][leftOfPiece - 1] != null){
                                lastResult = Result.OUT_BOUNDS;
                                return Result.OUT_BOUNDS;
                            }
                        }
                    }
                }
                // if all conditions satisfied, then set new position to one left
                currentPiece.setCurrentPosition(new Point(pos.x - 1, pos.y));
                lastResult = Result.SUCCESS;
                return Result.SUCCESS;

            // moves a current piece right one
            case RIGHT:
                // loops through each piece on the vertical column in current piece
                for(int i = pos.y; i < pos.y + currentPiece.getHeight(); ++i){
                    // uses the LSkirt to avoid when a row of the current piece is Integer.MAX_VALUE
                    if(currentPiece.getRSkirt()[i - pos.y] != Integer.MIN_VALUE){
                        // finds the index that is one right of the current point
                        int rightOfPiece = pos.x + currentPiece.getRSkirt()[i - pos.y];
                        // edge case: evaluates whether a piece travels out of bounds
                        if(rightOfPiece >= gridWidth - 1){
                            lastResult = Result.OUT_BOUNDS;
                            return Result.OUT_BOUNDS;
                        }
                        // edge case: evaluates whether a piece hits another piece
                        else{
                            if(grid[i][rightOfPiece + 1] != null){
                                lastResult = Result.OUT_BOUNDS;
                                return Result.OUT_BOUNDS;
                            }
                        }
                    }
                }
                // if all conditions satisfied, then set new position to one right
                currentPiece.setCurrentPosition(new Point(pos.x + 1, pos.y));
                lastResult = Result.SUCCESS;
                return Result.SUCCESS;

            // moves current piece to down one
            case DOWN:
                // loops through each piece on the horizontal row in current piece
                for(int i = pos.x; i < pos.x + currentPiece.getWidth(); ++i){
                    // uses the regular Skirt to avoid when a column of the current piece is Integer.MAX_VALUE
                    if(currentPiece.getSkirt()[i - pos.x] != Integer.MAX_VALUE){
                        // finds the index that is one below the current point
                        int botOfPiece = pos.y + currentPiece.getSkirt()[i - pos.x];
                        // edge case: evaluates whether a piece travels out of bounds
                        if(botOfPiece <= 0){
                            setPiece();
                            lastResult = Result.PLACE;
                            return Result.PLACE;
                        }
                        // edge case: evaluates whether a piece hits another piece
                        else{
                            if(grid[botOfPiece - 1][i] != null){
                                setPiece();
                                lastResult = Result.PLACE;
                                return Result.PLACE;
                            }
                        }
                    }
                }
                // if all conditions satisfied, then set new position to one down
                currentPiece.setCurrentPosition(new Point(pos.x,pos.y - 1));
                lastResult = Result.SUCCESS;
                return Result.SUCCESS;

            // drops a piece to the max bottom of the board
            case DROP:
                int minY = Integer.MAX_VALUE;
                // checks to see minimum possible y value to drop piece based on dropHeight
                for(int i = pos.x; i < pos.x + currentPiece.getWidth(); ++i){
                    minY = Integer.min(pos.y - dropHeight(currentPiece, i), minY);
                }
                // sets posiiton of new piece to the minimal drop position
                currentPiece.setCurrentPosition(new Point(pos.x, pos.y - minY));
                setPiece();
                lastResult = Result.PLACE;
                return Result.PLACE;

            // rotates a piece one clockwise
            case CLOCKWISE:
                int sourceIndex1 = currentPiece.getRotationIndex();
                // rotates piece and creates new TetrisPiece with rotated piece
                currentPiece.getPossibleRotations().next();
                TetrisPiece newPiece1 = new TetrisPiece(currentPiece.getPossibleRotations().get(), currentPiece);
                // checks to see if piece is equal to PieceType STICK
                if(newPiece1.getType() == Piece.PieceType.STICK){
                    // sees if rotated body has interference
                    if(interference(newPiece1.getBody())){
                        // loops through clockwise wall kicks for STICK and see which translation of
                        // rotated body is successful using the rotator helper function
                        for(int i = 0; i < Piece.I_CLOCKWISE_WALL_KICKS[sourceIndex1].length; ++i){
                            Point p = Piece.I_CLOCKWISE_WALL_KICKS[sourceIndex1][i];
                            if (rotator(pos, newPiece1, p)) return Result.SUCCESS;
                        }
                        // if none are valid translations, then invalid rotation
                        lastResult = Result.OUT_BOUNDS;
                        return Result.OUT_BOUNDS;
                    }
                    // rotation was successful without wall kicks
                    else{
                        currentPiece = newPiece1;
                        lastResult = Result.SUCCESS;
                        return Result.SUCCESS;
                    }
                }
                // otherwise, implements wall kicks for all other TetrisPiece
                else{
                    // checks if rotated piece has interference
                    if(interference(newPiece1.getBody())){
                        // loops through clockwise wall kicks for all other pieces and see which translation of
                        // rotated body is successful using the rotator helper function
                        for(int i = 0; i < Piece.NORMAL_CLOCKWISE_WALL_KICKS[sourceIndex1].length; ++i){
                            Point p = Piece.NORMAL_CLOCKWISE_WALL_KICKS[sourceIndex1][i];
                            if (rotator(pos, newPiece1, p)) return Result.SUCCESS;
                        }
                        // if none are valid translations, then failed rotation
                        lastResult = Result.OUT_BOUNDS;
                        return Result.OUT_BOUNDS;
                    }
                    // rotation was successful without wall kicks
                    else{
                        currentPiece = newPiece1;
                        lastResult = Result.SUCCESS;
                        return Result.SUCCESS;
                    }
                }

            // rotates a piece one counterclockwise
            case COUNTERCLOCKWISE:
                int sourceIndex2 = currentPiece.getRotationIndex();
                // rotates piece counterclockwise and creates new TetrisPiece with rotated piece
                currentPiece.getPossibleRotations().last();
                TetrisPiece newPiece2 = new TetrisPiece(currentPiece.getPossibleRotations().get(), currentPiece);
                // checks if it is a STICK type
                if(newPiece2.getType() == Piece.PieceType.STICK){
                    // checks if rotated STICK has interference
                    if(interference(newPiece2.getBody())){
                        // loops through counterclockwise wall kicks for STICK and see which translation of
                        // rotated body is successful using the rotator helper function
                        for(int i = 0; i < Piece.I_COUNTERCLOCKWISE_WALL_KICKS[sourceIndex2].length; ++i){
                            Point p = Piece.I_COUNTERCLOCKWISE_WALL_KICKS[sourceIndex2][i];
                            if (rotator(pos, newPiece2, p)) return Result.SUCCESS;
                        }
                        // if none are valid translations, then failed rotation
                        lastResult = Result.OUT_BOUNDS;
                        return Result.OUT_BOUNDS;
                    }
                    // rotation was successful without wall kicks
                    else{
                        currentPiece = newPiece2;
                        lastResult = Result.SUCCESS;
                        return Result.SUCCESS;
                    }
                }
                else{
                    // checks if rotated piece has interference
                    if(interference(newPiece2.getBody())){
                        // loops through counterclockwise wall kicks for all other pieces and see which translation of
                        // rotated body is successful using the rotator helper function
                        for(int i = 0; i < Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[sourceIndex2].length; ++i){
                            Point p = Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[sourceIndex2][i];
                            if (rotator(pos, newPiece2, p)) return Result.SUCCESS;
                        }
                        // if none are valid translations, then failed rotation
                        lastResult = Result.OUT_BOUNDS;
                        return Result.OUT_BOUNDS;
                    }
                    // rotation was successful without wall kicks
                    else{
                        currentPiece = newPiece2;
                        lastResult = Result.SUCCESS;
                        return Result.SUCCESS;
                    }
                }
        }
        lastResult = Result.SUCCESS;
        return Result.SUCCESS;
    }

    // used to check if a given board is equal to the set board for a given action (for AI purpose)
    public Board testMove(Action act){
        TetrisBoard testBoard = new TetrisBoard(gridWidth, gridHeight);
        testBoard.setGrid(grid);
        testBoard.setCurrentPiece(currentPiece);
        testBoard.setMaxColHeight(maxHeight);
        testBoard.setRowWidths(rowWidths);
        testBoard.setColHeights(colHeights);
        testBoard.setRowsCleared(rowsCleared);
        testBoard.move(act);
        return testBoard;
    }

    // checks to see if a body of points interfere outside of bounds or hit another piece on the board
    private boolean interference(Point[] body) {
        Point pos = getCurrentPiecePosition();
        for(Point p : body) {
            //first checks if out of bounds
            if(p.x + pos.x < 0 || p.x + pos.x >= getWidth()
                    || p.y + pos.y < 0 || p.y + pos.y >= getHeight()) {
                return true;
            }
            //next checks if translation hits another item
            if(grid[p.y + pos.y][p.x + pos.x] != null) {
                return true;
            }
        }
        return false;
    }

    // checks to see whether a given rotated body is valid for a specific wall kick
    private boolean rotator(Point pos, TetrisPiece newPiece1, Point p) {
        Point[] bodyClone = newPiece1.getBody().clone();
        // updates points based on wall kick
        for(int j = 0; j != bodyClone.length; ++j){
            bodyClone[j] = new Point(bodyClone[j].x + p.x, bodyClone[j].y + p.y);
        }
        // checks if there is no interference for the translated and rotated body
        if(!interference(bodyClone)){
            // sets new piece's position
            newPiece1.setCurrentPosition(new Point(pos.x + p.x, pos.y + p.y));
            currentPiece = newPiece1;
            lastResult = Result.SUCCESS;
            return true;
        }
        return false;
    }

    // calls the next piece for the game after current piece is set
    public void nextPiece(Piece p, Point spawn) {
        rowsCleared = 0;
        // edge case: checks whether a piece is valid by checking whether it can fit on board's maxHeight
        if(maxHeight >= gridHeight - 3)
            throw new IllegalArgumentException();
        // edge case: checks whether passed Piece is valid
        if(p != null){
            // edge case: checks if a piece is not already in the given spawn position
            for(int x = spawn.x; x < spawn.x + p.getWidth(); ++x){
                for(int y = spawn.y; y < spawn.y + p.getHeight(); ++y){
                    if(grid[y][x] != null)
                        throw new IllegalArgumentException();
                }
            }
            // creates new TetrisPiece and sets the new position for the currentPiece
            currentPiece = (TetrisPiece) p;
            currentPiece.setCurrentPosition(spawn);
        }
    }

    // helps set the current piece onto the board after dropping or setting
    public void setPiece() {
        Point pos = currentPiece.getCurrentPosition();
        Point[] body = currentPiece.getBody();
        // sets point of body to grid and updates the specific row's width by one
        for(Point p: body){
            grid[p.y + pos.y][p.x + pos.x] = currentPiece.getType();
            ++rowWidths[p.y + pos.y];
        }
        // goes through the whole grid to see whether a piece is hit at that column
        // if a piece is hit, then colHeights is updated to that specific row value
        for(int x = 0; x < gridWidth; ++x){
            for(int y = gridHeight - 1; y >= 0; --y){
                if(grid[y][x] != null) {
                    colHeights[x] = y + 1;
                    break;
                }
            }
        }
        // finds the maxHeight out of all column heights
        maxHeight = Integer.MIN_VALUE;
        for(int h: colHeights)
            maxHeight = Integer.max(h, maxHeight);
        // calls clear row on any row(s) that are filled in a given turn
        for(int i = 0; i < rowWidths.length; ++i){
            if(rowWidths[i] == gridWidth){
                clearRow(i);
                rowsCleared++;
                --i;
            }
        }
        setRowsCleared(rowsCleared);
    }

    // helps clear a row from the board when a row is filled
    public void clearRow(int y){
        // grid removes filled row and updates all other rows above it by moving them down one
        // also updates the rowWidths to the previous row's rowWidths value
        for(int i = y; i < gridHeight - 1; ++i){
            grid[i] = grid[i + 1];
            rowWidths[i] = rowWidths[i + 1];
        }
        // sets the top row to an empty array since it can't be updated with a row above it
        grid[gridHeight - 1] = new Piece.PieceType[gridWidth];
        rowWidths[gridHeight - 1] = 0;
        // updates each column height by decrementing by one
        for(int i = 0; i < colHeights.length; ++i)
            --colHeights[i];
        --maxHeight;
    }

    // calculates the drop height that the tetris piece can travel to when drop is called
    public int dropHeight(Piece p, int x) {
        int Y = currentPiece.getCurrentPosition().y,
            X = currentPiece.getCurrentPosition().x;
        int[] skirt = currentPiece.getSkirt();
        // checks to see if there are a valid amount of rows to drop from
        if(x >= X && x < X + currentPiece.getWidth()){
            // edge case: if skirt value at that point is Integer.MAX_VALUE, then return random negative int
            if(skirt[x - X] == Integer.MAX_VALUE)
                return -1000000;
            // otherwise, return the column's height minus the skirt
            else
                Y = colHeights[x] - skirt[x - X];
        }
        return Y;
    }

    // gets individual grid piecetype value of the passed Point parameter
    public Piece.PieceType getGrid(int x, int y) {
        // edge case: if parameters return out of bounds
        if( (y < 0 || y >= gridHeight) || (x < 0 || x > gridWidth) )
            return null;
        // checks to see whether given position has a piecetype
        if(currentPiece != null){
            int X = currentPiece.getCurrentPosition().x,
                    Y = currentPiece.getCurrentPosition().y;
            if( (x >= X && x < X + currentPiece.getWidth()) && (y >= Y && y < Y + currentPiece.getHeight()) )
                return null;
        }
        return grid[y][x];
    }

    // checks to see whether a TetrisBoard is equal to another board
    public boolean equals(Object other) {
        if( !(other instanceof TetrisBoard) )
            return false;
        TetrisBoard otherBoard = (TetrisBoard) other;
        //compare width and height
        if(this.getWidth() != otherBoard.getWidth() || this.getHeight() != otherBoard.getHeight())
            return false;
        //compare rows cleared
        if(this.getRowsCleared() != otherBoard.getRowsCleared())
            return false;
        //compare individual values of each board
        for(int row = 0; row < this.getWidth(); row++) {
            for(int col = 0; col < this.getHeight(); col++) {
                if(otherBoard.getGrid(row, col) != this.getGrid(row, col))
                    return false;
            }
        }
        return true;
    }

    // series of important getter and setter methods

    public int getColumnHeight(int x) {
        return colHeights[x];
    }

    public int getRowWidth(int y) {
        return rowWidths[y];
    }

    public Result getLastResult() {
        return lastResult;
    }

    public Action getLastAction() {
        return lastAction;
    }

    public int getRowsCleared() {
        return rowsCleared;
    }

    public int getWidth() {
        return gridWidth;
    }

    public int getHeight() {
        return gridHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public Point getCurrentPiecePosition() {
        return currentPiece.getCurrentPosition();
    }

    public Piece getCurrentPiece() {
        return currentPiece;
    }

    public void setGrid(Piece.PieceType[][] otherGrid){
        for(int i = 0; i < gridHeight; ++i)
            System.arraycopy(otherGrid[i], 0, grid[i], 0, gridWidth);
    }

    public void setCurrentPiece(TetrisPiece curr){
        currentPiece = curr;
    }

    public void setMaxColHeight(int i){
        maxHeight = i;
    }

    public void setRowsCleared(int i){
        rowsCleared = i;
    }

    public void setRowWidths(int[] widths){
        System.arraycopy(widths, 0, rowWidths, 0, gridHeight);
    }

    public void setColHeights(int[] heights){
        System.arraycopy(heights, 0, colHeights, 0, gridWidth);
    }

    public Piece.PieceType[][] getGrid(){
        return grid;
    }
}