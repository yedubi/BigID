package com.bigId.aggregator.impl;

import com.bigId.aggregator.Aggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AggregatorImpl implements Aggregator {

    private final ConcurrentHashMap<String, List<Map<String, Integer>>> entries = new ConcurrentHashMap<>();

    public void aggregate(Map<String, List<Map<String, Integer>>> map) {
        map.forEach((key, value) -> entries.compute(key, (k, v) -> {
            List<Map<String, Integer>> vals = v;
            if (vals == null)
                vals = new ArrayList<>();
            vals.addAll(value);
            return vals;
        }));
    }

    public Map<String, List<Map<String, Integer>>> getResult() {
        entries.forEach((key, value) -> System.out.println(key + "-->" + value));
        return entries;
    }

}
