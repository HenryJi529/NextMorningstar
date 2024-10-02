package com.morningstar.practice.type;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ListTest {
    @Test
    public void test() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        Lists.partition(list, 3).forEach(System.out::println);
    }
}
