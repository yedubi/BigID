package com.bigId.aggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Aggregator {

    public static Map<String, List<Map<String, Integer>>> aggregateMatches(List<Map<String, List<Map<String, Integer>>>> wordsLocationsMatching) {
        Map<String, List<Map<String, Integer>>> aggregatedResult = wordsLocationsMatching
                .stream()
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
