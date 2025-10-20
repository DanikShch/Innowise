package com.innowise.customlinkedlist;

import java.util.NoSuchElementException;

public class CustomLinkedList<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;
        Node<T> prev;

        public Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    public CustomLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public void addFirst(T element) {
        Node<T> newNode = new Node<>(element);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    public void addLast(T element) {
        Node<T> newNode = new Node<>(element);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    public void add(int index, T element) {//todo
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        if (index == 0) {
            addFirst(element);
        } else if (index == size) {
            addLast(element);
        } else {
            Node<T> current = getNode(index);
            Node<T> newNode = new Node<>(element);
            newNode.prev = current.prev;
            newNode.next = current;
            current.prev.next = newNode;
            current.prev = newNode;
            size++;
        }
    }

    public T getFirst() {
        if (size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return head.data;
    }

    public T getLast() {
        if (size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return tail.data;
    }

    private Node<T> getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        if (index < size / 2) {
            Node<T> current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current;
        } else {
            Node<T> current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
            return current;
        }
    }

    public T get(int index) {
        return getNode(index).data;
    }

    public T removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        T data = head.data;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
        size--;
        return data;
    }

    public T removeLast() {
        if (size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        T data = tail.data;
        if (head == tail) {
            tail = null;
            head = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        size--;
        return data;
    }

    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        if (index == 0) {
            return removeFirst();
        } else if (index == size - 1) {
            return removeLast();
        } else {
            Node<T> nodeToRemove = getNode(index);
            T data = nodeToRemove.data;
            nodeToRemove.prev.next = nodeToRemove.next;
            nodeToRemove.next.prev = nodeToRemove.prev;
            size--;
            return data;
        }
    }
}
