package assignment;
import java.util.*;
public class TetrisAI implements Brain {
    private List<List<Board>> options;
    private List<List<Board.Action>> firstMoves;
    private Board.Action[] rotations;
    public Board.Action nextMove(Board currentBoard) {
        // Fill the our options array with versions of the new Board
        options = new ArrayList<>();
        firstMoves = new ArrayList<>();
        rotations = new Board.Action[2];
        checkRotations(currentBoard);

        int best = 0, bestIndexI = 0, bestIndexJ = 0;

        // Check all of the options and get the one with the highest score
        for (int i = 0; i < options.size(); i++) {
            for(int j = 0; j != options.get(i).size(); ++j){
                int score = scoreBoard(options.get(i).get(j));
                if (score > best) {
                    best = score;
                    bestIndexI = i;
                    bestIndexJ = j;
                }
            }
        }
        switch(bestIndexI){
            case 0:
                rotations[0] = Board.Action.NOTHING;
                rotations[1] = Board.Action.NOTHING;
                break;
            case 1:
                rotations[0] = Board.Action.CLOCKWISE;
                rotations[1] = Board.Action.NOTHING;
                break;
            case 2:
                rotations[0] = Board.Action.CLOCKWISE;
                rotations[1] = Board.Action.CLOCKWISE;
                break;
            case 3:
                rotations[0] = Board.Action.COUNTERCLOCKWISE;
                rotations[1] = Board.Action.NOTHING;
                break;
        }
        // We want to return the first move on the way to the best Board
        return firstMoves.get(bestIndexI).get(bestIndexJ);
    }
    private void checkRotations(Board currentBoard) {

        Board rotatedBoard = currentBoard.testMove(Board.Action.NOTHING);
        boolean stop = false;
        for(int i = 0; i != 4; ++i){
            if(!stop)
                rotatedBoard.move(Board.Action.CLOCKWISE);
            if(rotatedBoard.getLastResult() == Board.Result.SUCCESS){
                options.add(new ArrayList<>());
                firstMoves.add(new ArrayList<>());
                checkOptions(rotatedBoard,i);
            }
            stop = true;
        }
    }

    private void checkOptions(Board rotatedBoard, int i) {
        // We can always drop our current Piece
        options.get(i).add(rotatedBoard.testMove(Board.Action.DROP));
        firstMoves.get(i).add(Board.Action.DROP);

        // Now we'll add all the places to the left we can DROP
        Board left = rotatedBoard.testMove(Board.Action.LEFT);
        while (left.getLastResult() == Board.Result.SUCCESS) {
            options.get(i).add(left.testMove(Board.Action.DROP));
            firstMoves.get(i).add(Board.Action.LEFT);
            left.move(Board.Action.LEFT);
        }

        // And then the same thing to the right
        Board right = rotatedBoard.testMove(Board.Action.RIGHT);
        while (right.getLastResult() == Board.Result.SUCCESS) {
            options.get(i).add(right.testMove(Board.Action.DROP));
            firstMoves.get(i).add(Board.Action.RIGHT);
            right.move(Board.Action.RIGHT);
        }


    }

    private int scoreBoard(Board newBoard) {
        return 100 - (newBoard.getMaxHeight() * 5);
    }

    public Board.Action[] getRotations(){
        return rotations;
    }
}
