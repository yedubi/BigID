package com.bigId.aggregator.impl;

import com.bigId.aggregator.Aggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AggregatorImpl implements Aggregator {

    private final ConcurrentHashMap<String, List<Map<String, Integer>>> entries = new ConcurrentHashMap<>();

    public void aggregate(Map<String, List<Map<String, Integer>>> map) {
        if (Objects.nonNull(map)) {
            map.forEach((key, value) -> entries.compute(key, (k, v) -> {
                List<Map<String, Integer>> vals = v;
                if (vals == null)
                    vals = new ArrayList<>();
                vals.addAll(value);
                return vals;
            }));
        }
    }

    public Map<String, List<Map<String, Integer>>> getResult() {
        entries.forEach((key, value) -> System.out.println(key + "-->" + value));
        return entries;
    }

    public Map<String, List<Map<String, Integer>>> aggregateMatches(List<Map<String, List<Map<String, Integer>>>> wordsLocationsMatching) {
        var start = System.nanoTime();

        Map<String, List<Map<String, Integer>>> aggregatedResult = wordsLocationsMatching
                .stream()
                .flatMap(map -> map.entrySet().stream().filter(e -> !e.getValue().isEmpty()))
                .collect(Collectors.toMap(Map.Entry::getKey, e1 -> new ArrayList<>(e1.getValue()),
                        (left, right) -> {
                            left.addAll(right);
                            return left;
                        }
                ));
//        aggregatedResult.forEach((key, value) -> System.out.println(key + "-->" + value));
        var stop = System.nanoTime();
        System.out.println("Time: " + (stop - start) / 1000000.0 + " msec");

        return aggregatedResult;
    }

}
