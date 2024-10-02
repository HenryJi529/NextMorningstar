package com.morningstar.practice.type;

import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.LinkedList;

public class DequeTest {
    @Test
    public void test() {
        Deque<Integer> deque = new LinkedList<>();
        deque.addFirst(1);
        deque.addLast(2);
        deque.addLast(3);
        deque.addLast(4);
        System.out.println(deque);
        System.out.println(deque.poll());
        System.out.println(deque.pollLast());
        System.out.println(deque);
    }
}
