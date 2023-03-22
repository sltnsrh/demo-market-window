package com.salatin;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class TestMap {
    public static void main(String[] args) {
        TreeMap<Double, Integer> map = new TreeMap<>(Collections.reverseOrder());

        map.put(0.1, 2);
        map.put(0.2, 2);
        map.put(0.3, 2);
        map.put(0.4, 2);
        map.put(0.5, 2);
        map.put(0.6, 2);
        map.put(0.67, 2);

        System.out.println(map);
    }
}
