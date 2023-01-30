package com.bigId.matcher;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TextMatcher {
    Map<String, List<Map<String, Integer>>> matchLocations(Set<String> words, String chunkText, int chunkLineOffset);

}
