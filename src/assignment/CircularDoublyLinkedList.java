package assignment;
import java.awt.*;
public class CircularDoublyLinkedList {
    private Node head;
    private int iter = 0;
    private int size = 4;

    // doubly linked list node definition (next and prev to iterate forwards and backwards)
    public class Node{
        Point[] data;
        Node next;
        Node prev;
        public Node(Point[] data){
            this.data = data;
        }
    }

    // function to insert node in the list
    public void add(Point[] rotationPoints) {
        // list is empty so create a single node first
        if (head == null) {
            Node new_node = new Node(rotationPoints);
            new_node.next = new_node.prev = new_node;
            head = new_node;
            return;
        }

        // find last node in the list if list is not empty
        Node last = head.prev;

        // instantiate new node and its data
        Node new_node = new Node(rotationPoints);

        // update connection between new node and first node
        new_node.next = head;
        head.prev = new_node;

        // update connection between last node and new node
        new_node.prev = last;
        last.next = new_node;
    }

    // iterates linked list backwards and updates size
    public void last() {
        iter--;
        if(iter == -1)
            iter = size - 1;
        head = head.prev;
    }

    // iterates linked list forwards and updates size
    public void next() {
        iter++;
        if(iter == size)
            iter = 0;
        head = head.next;
    }

    // getter functions

    public Point[] get() {
        return head.data;
    }

    public int getIndex() {
        return iter;
    }
}