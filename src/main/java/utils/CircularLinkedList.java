package utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CircularLinkedList {
    private Node head = null;
    private Node tail = null;

    public void add(final int value) {
        Node newNode = new Node(value);

        if (head == null) {
            head = newNode;
        } else {
            tail.nextNode = newNode;
        }

        tail = newNode;
        tail.nextNode = head;
    }

    public boolean contains(final int searchValue) {
        Node currentNode = head;

        if (head == null) {
            return false;
        } else {
            do {
                if (currentNode.value == searchValue) {
                    return true;
                }
                currentNode = currentNode.nextNode;
            } while (currentNode != head);
            return false;
        }
    }

    public void delete(final int value) {
        Node currentNode = head;

        if (head == null) {
            return;
        }

        do {
            Node nextNode = currentNode.nextNode;
            if (nextNode.value == value) {
                if (tail == head) {
                    head = null;
                    tail = null;
                } else {
                    currentNode.nextNode = nextNode.nextNode;
                    if (head == nextNode) {
                        head = head.nextNode;
                    }
                    if (tail == nextNode) {
                        tail = currentNode;
                    }
                }
                break;
            } currentNode = nextNode;
        }  while (currentNode != head);
    }

    public void traverse() {
        Node currentNode = head;

        if (head != null) {
            do {
                log.info(currentNode.value + " ");
                currentNode = currentNode.nextNode;
            } while (currentNode != head);
        }
    }
}

class Node {
    int value;
    Node nextNode;

    public Node(final int value) {
        this.value = value;
    }

}