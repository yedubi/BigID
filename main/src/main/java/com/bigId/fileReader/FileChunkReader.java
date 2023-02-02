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

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(fileName))) {
            var chunkStringArray = new String[CHUNK_SIZE];
            String line;
            var totalLineCount = 0;
            var chunkLineCount = 0;
            var chunkId = 0;

            while (Objects.nonNull(line = br.readLine())) {
                chunkStringArray[chunkLineCount] = line.isEmpty() ? System.lineSeparator() : line + System.lineSeparator();
                totalLineCount++;
                chunkLineCount++;
                if (isChunkReadyForMatching(totalLineCount)) {
                    var completableFuture =
                            getCompletableFutureChunkMatchingResultMap(threadPool, wordsToMatch, chunkStringArray, chunkId);
                    completableFutures.add(completableFuture);
                    chunkId++;
                    chunkStringArray = new String[CHUNK_SIZE];
                    chunkLineCount = 0;
                }
            }
            var completableFuture =
                    getCompletableFutureChunkMatchingResultMap(threadPool, wordsToMatch, chunkStringArray, chunkId);
            completableFutures.add(completableFuture);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return completableFutures;
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
