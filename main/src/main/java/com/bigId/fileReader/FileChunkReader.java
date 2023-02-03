package com.bigId.fileReader;

import com.bigId.matcher.TextMatcher;

import java.io.BufferedReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class FileChunkReader {
    public static final int CHUNK_SIZE = 1000;
    private final String fileName;
    private final TextMatcher nameMatcher;

    public FileChunkReader(String fileName, TextMatcher nameMatcher) {
        this.fileName = fileName;
        this.nameMatcher = nameMatcher;
    }

    public ArrayList<CompletableFuture<Map<String, List<Map<String, Integer>>>>> readFileByChunksAndMatchWordsLocationsAsync
            (ExecutorService threadPool, Set<String> wordsToMatch) {

        var completableFutures = new ArrayList<CompletableFuture<Map<String, List<Map<String, Integer>>>>>();

        var chunkLinesList = new String[CHUNK_SIZE];

        String line;
        var totalLineCount = 0;
        var chunkId = 0;
        var chunkLineId = 0;

        try (var bufferedReader = new BufferedReader(new java.io.FileReader(fileName))) {

            while (Objects.nonNull(line = bufferedReader.readLine())) {
                chunkLinesList[chunkLineId] = getLineString(line);
                totalLineCount++;
                chunkLineId++;
                if (isChunkReadyForMatching(totalLineCount)) {
                    var completableFuture =
                            getCompletableFutureChunkMatchingResultMap(threadPool, wordsToMatch, chunkLinesList, chunkId);
                    completableFutures.add(completableFuture);
                    chunkId++;
                    chunkLinesList = new String[CHUNK_SIZE];
                    chunkLineId = 0;
                }
            }

            var completableFuture =
                    getCompletableFutureChunkMatchingResultMap(threadPool, wordsToMatch, chunkLinesList, chunkId);
            completableFutures.add(completableFuture);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return completableFutures;
    }

    private String getLineString(String line) {
        return line.isEmpty() ? System.lineSeparator() : line + System.lineSeparator();
    }

    private boolean isChunkReadyForMatching(int lineCount) {
        return lineCount % CHUNK_SIZE == 0;
    }

    private CompletableFuture<Map<String, List<Map<String, Integer>>>> getCompletableFutureChunkMatchingResultMap
            (ExecutorService threadPool, Set<String> wordsToMatch, String[] chunkLines, int chunkId) {
        var chunkLineOffset = getChunkLineOffset(chunkId);
        return CompletableFuture
                .supplyAsync(() -> nameMatcher.matchLocations(wordsToMatch, chunkLines, chunkLineOffset), threadPool)
                .exceptionally(ex -> {
                    System.out.println("Something went wrong : " + ex.getMessage());
                    return null;
                });
    }

    private int getChunkLineOffset(int chunk) {
        var firstLineOffset = 1;
        return firstLineOffset + (chunk * CHUNK_SIZE);
    }

}
