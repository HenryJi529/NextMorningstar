package com.morningstar.practice.type;

import org.junit.jupiter.api.Test;

import java.util.Stack;

public class StackTest {
    @Test
    public void test() {
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        System.out.println(stack);
        System.out.println(stack.pop());
        System.out.println(stack);
        System.out.println(stack.peek());
    }
}
