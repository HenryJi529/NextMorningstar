package com.morningstar.practice;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class StreamTest {
    @Test
    public void testReduce(){
        List<Integer> list = List.of(1,2,3,4);
        assert list.stream().reduce(0, Integer::sum).equals(10);
    }

    @Test
    public void testCollect(){
        List<Integer> list = List.of(1,2,3,4);
        assert list.stream().map(Object::toString).collect(Collectors.joining(",")).equals("1,2,3,4");
    }
}
