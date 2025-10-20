package com.innowise.customlinkedlist;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class CustomLinkedListTest {
    private CustomLinkedList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new CustomLinkedList<>();
    }

    @Test
    void testEmptyList() {
        assertEquals(0, list.size());
        assertThrows(NoSuchElementException.class, () -> list.getFirst());
        assertThrows(NoSuchElementException.class, () -> list.getLast());
        assertThrows(NoSuchElementException.class, () -> list.removeFirst());
        assertThrows(NoSuchElementException.class, () -> list.removeLast());
    }

    @Test
    void testAddFirst() {
        list.addFirst(1);
        assertEquals(1, list.size());
        assertEquals(1, list.getFirst());
        assertEquals(1, list.getLast());

        list.addFirst(2);
        assertEquals(2, list.size());
        assertEquals(2, list.getFirst());
        assertEquals(1, list.getLast());
    }

    @Test
    void testAddLast() {
        list.addLast(1);
        assertEquals(1, list.size());
        assertEquals(1, list.getFirst());
        assertEquals(1, list.getLast());

        list.addLast(2);
        assertEquals(2, list.size());
        assertEquals(1, list.getFirst());
        assertEquals(2, list.getLast());
    }

    @Test
    void testAddAtIndex() {
        list.add(0, 1);
        assertEquals(1, list.get(0));
        list.add(1, 3);
        assertEquals(3, list.get(1));
        list.add(1, 2);
        assertEquals(2, list.get(1));
        assertEquals(3, list.size());

        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(4, 5));
    }

    @Test
    void testGet() {
        list.addLast(0);
        list.addLast(1);
        list.addLast(2);
        assertEquals(0, list.get(0));
        assertEquals(1, list.get(1));
        assertEquals(2, list.get(2));

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(5));
    }

    @Test
    void testRemoveFirst() {
        list.addLast(0);
        list.addLast(1);
        list.addLast(2);

        assertEquals(0, list.removeFirst());
        assertEquals(2, list.size());
        assertEquals(1, list.getFirst());

        assertEquals(1, list.removeFirst());
        assertEquals(1, list.size());
        assertEquals(2, list.getFirst());

        assertEquals(2, list.removeFirst());
        assertEquals(0, list.size());
    }

    @Test
    void testRemoveLast() {
        list.addLast(0);
        list.addLast(1);
        list.addLast(2);

        assertEquals(2, list.removeLast());
        assertEquals(2, list.size());
        assertEquals(1, list.getLast());

        assertEquals(1, list.removeLast());
        assertEquals(1, list.size());
        assertEquals(0, list.getLast());

        assertEquals(0, list.removeLast());
        assertEquals(0, list.size());
    }

    @Test
    void testRemoveAtIndex() {
        list.addLast(0);
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        assertEquals(1, list.remove(1));
        assertEquals(3, list.size());
        assertEquals(0, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));

        assertEquals(0, list.remove(0));
        assertEquals(2, list.size());
        assertEquals(2, list.getFirst());

        assertEquals(3, list.remove(1));
        assertEquals(1, list.size());
        assertEquals(2, list.getLast());

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
    }

}