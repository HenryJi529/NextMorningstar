package com.morningstar.practice.core;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class StreamTest {
    @Test
    public void testReduce() {
        List<Integer> list1 = List.of(1, 2, 3, 4);
        assert list1.stream().reduce(0, Integer::sum).equals(10);
        assert list1.parallelStream().reduce(0, Integer::sum, Integer::sum).equals(10);
    }

    @Test
    public void testCollect() {
        List<Integer> list = List.of(1, 2, 3, 4);
        assert list.stream().map(Object::toString).collect(Collectors.joining(",")).equals("1,2,3,4");
    }
}
