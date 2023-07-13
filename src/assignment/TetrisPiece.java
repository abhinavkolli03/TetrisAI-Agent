package assignment;
import java.awt.*;
import java.util.*;
import java.util.List;
public final class TetrisPiece implements Piece {
    private Point[] body;
    private Color color;
    private int width;
    private int height;
    private int[] skirt, lSkirt, rSkirt;
    private PieceType selfType;
    private CircularDoublyLinkedList possibleRotations;
    private int rotationIndex;
    private Point currentPosition;

    // generic constructor for JTetris
    public TetrisPiece(PieceType type) {
        body = type.getSpawnBody();
        color = type.getColor();
        width = type.getBoundingBox().width;
        height = type.getBoundingBox().height;
        currentPosition = null;
        selfType = setType(type);
        skirt = generateSkirt();
        lSkirt = generateLSkirt();
        rSkirt = generateRSkirt();
        possibleRotations = computePieceRotations();
        rotationIndex = possibleRotations.getIndex();
    }

    // creating new piece constructor when turning clockwise or counterclockwise
    public TetrisPiece(Point[] newBody, TetrisPiece original) {
        body = newBody;
        color = original.getColor();
        width = original.getWidth();
        height = original.getHeight();
        currentPosition = original.getCurrentPosition();
        selfType = original.getType();
        skirt = generateSkirt();
        lSkirt = generateLSkirt();
        rSkirt = generateRSkirt();
        possibleRotations = original.getPossibleRotations();
        rotationIndex = possibleRotations.getIndex();
    }

    // important methods for calculating certain aspects of each piece

    // helps generate a vertical skirt value for the heights of a piece on the board
    private int[] generateSkirt() {
        int[] skirt = new int[width];
        Arrays.fill(skirt, Integer.MAX_VALUE);
        for(Point point: body)
            skirt[point.x] = Integer.min(point.y, skirt[point.x]);
        return skirt;
    }

    // helps generate a left skirt value for the rows of the piece on the board
    private int[] generateLSkirt() {
        int[] skirt = new int[height];
        Arrays.fill(skirt, Integer.MAX_VALUE);
        for(Point point: body)
            skirt[point.y] = Integer.min(point.x,skirt[point.y]);
        return skirt;
    }

    // helps generate a right skirt value for the rows of the piece on the board
    private int[] generateRSkirt() {
        int[] skirt = new int[height];
        Arrays.fill(skirt, Integer.MIN_VALUE);
        for(Point point: body)
            skirt[point.y] = Integer.max(point.x,skirt[point.y]);
        return skirt;
    }

    // helps rotate piece clockwise and iterate through the linked list forward
    public TetrisPiece clockwisePiece() {
        possibleRotations.last();
        rotationIndex = possibleRotations.getIndex();
        return new TetrisPiece(possibleRotations.get(), this);
    }

    // helps rotate piece counterclockwise and iterate through the linked list backwards
    public TetrisPiece counterclockwisePiece() {
        possibleRotations.next();
        rotationIndex = possibleRotations.getIndex();
        return new TetrisPiece(possibleRotations.get(), this);
    }

    // computes all four possible rotations for a given Piece.piecetype
    private CircularDoublyLinkedList computePieceRotations() {
        List<Point[]> rotations = new ArrayList<>();
        rotations.add(body.clone());
        for(int rotate = 0; rotate < 3; rotate++) {
            Point[] nextRotation = new Point[body.length],
                    prev = rotations.get(rotate);
            //loops through body length, applies algorithm and adds new rotation body
            for(int i = 0; i < body.length; i++) {
                // algorithm: rotate the co-ordinates clockwise 90 degrees
                // and shift them up by (height - 1) to reset the vertical
                nextRotation[i] = new Point(prev[i].y, height - 1 - prev[i].x);
            }
            rotations.add(nextRotation);
        }
        // adds all rotations list to final linked list
        CircularDoublyLinkedList result = new CircularDoublyLinkedList();
        for(Point[] b: rotations){
            result.add(b);
        }
        return result;
    }

    // evaluates whether a tetris piece is equal to another piece
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces
        if( !(other instanceof TetrisPiece) )
            return false;

        Point[] otherBody = ((TetrisPiece)other).getBody();

        // if length isn't the same, then end comparison immediately
        if(body.length != otherBody.length) {
            return false;
        }

        // checks if rotation index is same
        if(((TetrisPiece) other).getRotationIndex() != this.getRotationIndex())
            return false;

        // checks if type is same
        if(((TetrisPiece) other).getType() != this.getType())
            return false;

        return true;
    }

    // series of important getter and setter methods
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Point[] getBody() {
        return body;
    }

    public int[] getSkirt() {
        return skirt;
    }

    public int[] getLSkirt(){
        return lSkirt;
    }

    public int[] getRSkirt(){
        return rSkirt;
    }

    public PieceType getType() {
        return selfType;
    }

    public int getRotationIndex() {
        return rotationIndex;
    }

    public Point getCurrentPosition(){
        return currentPosition;
    }

    public Color getColor(){
        return color;
    }

    public CircularDoublyLinkedList getPossibleRotations(){
        return possibleRotations;
    }

    public void setCurrentPosition(Point position){
        currentPosition = position;
    }

    private PieceType setType(PieceType type) {
        for(Piece.PieceType p: Piece.PieceType.values())
            if(type == p)
                return p;
        return null;
    }
}
