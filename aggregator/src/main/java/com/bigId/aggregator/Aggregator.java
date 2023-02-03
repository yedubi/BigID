package com.bigId.aggregator;

import java.util.List;
import java.util.Map;

public interface Aggregator {
    Map<String, List<Map<String, Integer>>> aggregateMatches(List<Map<String, List<Map<String, Integer>>>> wordsLocationsMatching);
}
