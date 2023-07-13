package assignment;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class JBrainTetris extends JTetris{
    public static void main(String[] args){
        createGUI(new JBrainTetris());
    }
    public JBrainTetris() {
        setPreferredSize(new Dimension(WIDTH*PIXELS, (HEIGHT+TOP_SPACE)*PIXELS));
        gameOn = false;
        TetrisAI brain = new TetrisAI();
        board = new TetrisBoard(WIDTH, HEIGHT + TOP_SPACE);
        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Board.Action next = brain.nextMove(board);
                Board.Action[] rotations = brain.getRotations();
                tick(rotations[0]);
                tick(rotations[1]);
                tick(next);
                tick(Board.Action.DOWN);
            }
        });
    }
}