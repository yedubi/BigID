package com.bigId.matcher;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TextMatcher {

    private final static String REGEX = "%s([^A-Za-z]|$)";
    public static final String LINE_OFFSET_KEY = "lineOffset";
    public static final String CHAR_OFFSET_KEY = "charOffset";

    public Map<String, List<Map<String, Integer>>> matchWordsLocations(Set<String> words, String chunkText, int chunkLineOffset) {
        var lines = chunkText.split("\n");
        return IntStream.range(0, lines.length)
                .boxed()
                .flatMap(lineIdx -> words
                        .stream()
                        .filter(word -> lines[lineIdx].contains(word))
                        .map(word -> getWordLocationsMapping(chunkLineOffset, lines, lineIdx, word)))
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors
                        .groupingBy(AbstractMap.SimpleEntry::getKey,
                                Collectors.mapping(AbstractMap.SimpleEntry::getValue,
                                        Collectors.toList())));
    }

    private List<AbstractMap.SimpleEntry<String, Map<String, Integer>>> getWordLocationsMapping(int chunkLineOffset, String[] lines, Integer lineIdx, String word) {
        Matcher matcher = getMatcher(word, lines[lineIdx]);
        List<AbstractMap.SimpleEntry<String, Map<String, Integer>>> wordLocationsList = new ArrayList<>();
        while (matcher.find()) {
            Map<String, Integer> locationsMap = new HashMap<>();
            locationsMap.put(LINE_OFFSET_KEY, lineIdx + chunkLineOffset);
            locationsMap.put(CHAR_OFFSET_KEY, matcher.start() + 1);
            var wordLocationsMap = new AbstractMap.SimpleEntry<>(word, locationsMap);
            wordLocationsList.add(wordLocationsMap);
        }
        return wordLocationsList;
    }

    private Matcher getMatcher(String word, String lines) {

        var regex = String.format(REGEX, word);
        var pattern = Pattern.compile(regex);
        return pattern.matcher(lines);
    }

}
