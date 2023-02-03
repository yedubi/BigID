package com.bigId.matcher.impl;

import com.bigId.matcher.TextMatcher;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NameMatcher implements TextMatcher {

    private final static String NAME_REGEX = "%s([^A-Za-z]|$)";
    public static final String LINE_OFFSET_KEY = "lineOffset";
    public static final String CHAR_OFFSET_KEY = "charOffset";

    public Map<String, List<Map<String, Integer>>> matchLocations(Set<String> names, String[] chunkLines, int chunkLineOffset) {
        // iterate over each line and each name
        return IntStream.range(0, chunkLines.length)
                .boxed()
                .flatMap(lineIdx -> names
                        .stream()
                        .filter(name -> {
                            try {
                                return chunkLines[lineIdx].contains(name);
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .map(name -> getWordLocationsMapping(chunkLineOffset, chunkLines, lineIdx, name)))
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                        Collectors.mapping(AbstractMap.SimpleEntry::getValue,
                                Collectors.toList())));
    }


    private List<AbstractMap.SimpleEntry<String, Map<String, Integer>>> getWordLocationsMapping(
            int chunkLineOffset, String[] lines, Integer lineIdx, String name) {
        List<AbstractMap.SimpleEntry<String, Map<String, Integer>>> nameLocationsList = new ArrayList<>();
        try {
            Matcher matcher = getMatcher(name, lines[lineIdx]);
            while (matcher.find()) {
                Map<String, Integer> locationsMap = new HashMap<>();
                locationsMap.put(LINE_OFFSET_KEY, lineIdx + chunkLineOffset);
                locationsMap.put(CHAR_OFFSET_KEY, matcher.start() + 1);
                var nameLocationsMap = new AbstractMap.SimpleEntry<>(name, locationsMap);
                nameLocationsList.add(nameLocationsMap);
            }
        } catch (NullPointerException e) {
            System.out.println("");
        }
        return nameLocationsList;
    }

    private Matcher getMatcher(String word, String lines) {
        var regex = String.format(NAME_REGEX, word);
        var pattern = Pattern.compile(regex);
        return pattern.matcher(lines);
    }

}
