package com.bigId.aggregator;

import java.util.List;
import java.util.Map;

public interface Aggregator {
    void aggregate(Map<String, List<Map<String, Integer>>> map);
    Map<String, List<Map<String, Integer>>> getResult();
}
