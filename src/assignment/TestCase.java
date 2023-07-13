package assignment;

import java.awt.*;

public class TestCase {
    public static void main(String[] args) {
        /*The goal of this test case is to see whether a tetris figure can rotate into tight crevices with no issues.
        * Specifically, two rotated RIGHT_L's will be stacked on top of each other. This will cause a small crevice
        * where a tetris figure can go into if rotated. In this case, a stick will be tested.*/

        TetrisBoard board = new TetrisBoard(8, 16);
        //setting initial board
        board.nextPiece(new TetrisPiece(Piece.PieceType.RIGHT_L), new Point(0, 10));
        board.move(Board.Action.CLOCKWISE);
        board.move(Board.Action.CLOCKWISE);
        board.move(Board.Action.DROP);
        board.nextPiece(new TetrisPiece(Piece.PieceType.RIGHT_L), new Point(0, 10));
        board.move(Board.Action.CLOCKWISE);
        board.move(Board.Action.CLOCKWISE);
        board.move(Board.Action.DROP);

        //movement of the stick
        board.nextPiece(new TetrisPiece(Piece.PieceType.STICK), new Point(0, 10));
        board.move(Board.Action.COUNTERCLOCKWISE);
        board.move(Board.Action.RIGHT);
        board.move(Board.Action.RIGHT);
        while(board.getCurrentPiecePosition().y != 0)
            board.move(Board.Action.DOWN);
        board.move(Board.Action.CLOCKWISE);
        board.move(Board.Action.LEFT);

        //tester points to see if the final stick got to the exact position
        Point[] actualPoints = new Point[]{new Point(1, 2), new Point(2, 2), new Point(3, 2), new Point(4, 2)};

        //validate whether each position on the tetris board's finished movement is the same as the actual points
        boolean failed = false;
        for(Point pt : board.getCurrentPiece().getBody()) {
            boolean passedPoint = false;
            for(Point actualPt : actualPoints) {
                if(pt.x + board.getCurrentPiecePosition().x == actualPt.x && pt.y + board.getCurrentPiecePosition().y == actualPt.y) {
                    passedPoint = true;
                }
            }
            if(!passedPoint) {
                failed = true;
                break;
            }
        }
        if(!failed)
            System.out.println("Passed test case!");
        else
            System.out.println("Failed test case!");
    }
}
