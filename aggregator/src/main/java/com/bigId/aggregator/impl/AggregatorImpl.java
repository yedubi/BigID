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

    public Map<String, List<Map<String, Integer>>> aggregateMatches(List<Map<String, List<Map<String, Integer>>>> wordsLocationsMatching) {
        Map<String, List<Map<String, Integer>>> aggregatedResult = wordsLocationsMatching
                .stream()
                .filter(Objects::nonNull)
                .flatMap(map -> map.entrySet().stream().filter(e -> !e.getValue().isEmpty()))
                .collect(Collectors.toMap(Map.Entry::getKey, e1 -> new ArrayList<>(e1.getValue()),
                        (left, right) -> {
                            left.addAll(right);
                            return left;
                        }
                ));
        aggregatedResult.forEach((key, value) -> System.out.println(key + "-->" + value));
        return aggregatedResult;
    }

}
