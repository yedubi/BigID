package com.bigId.fileReader;

import com.bigId.matcher.TextMatcher;
import com.bigId.matcher.impl.NameMatcher;

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
            (int chunkSize, ExecutorService threadPool, Set<String> wordsToMatch) {

        var completableFutures = new ArrayList<CompletableFuture<Map<String, List<Map<String, Integer>>>>>();

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(fileName))) {
            var chunkStringBuilder = new StringBuilder();
            String line;
            var lineCount = 0;
            var chunkId = 0;

            while (Objects.nonNull(line = br.readLine())) {
                chunkStringBuilder.append(line.isEmpty() ? System.lineSeparator() : line + System.lineSeparator());
                lineCount++;
                if (lineCount % chunkSize == 0) {
                    var chunkLength = chunkStringBuilder.length();
                    var completableFuture = getCompletableFutureChunkMatchingResultMap(threadPool, wordsToMatch, chunkStringBuilder, chunkId);
                    completableFutures.add(completableFuture);
                    chunkId++;
                    chunkStringBuilder = new StringBuilder();
                }
            }
            if (chunkStringBuilder.length() > 0) {
                var completableFuture = getCompletableFutureChunkMatchingResultMap(threadPool, wordsToMatch, chunkStringBuilder, chunkId);
                completableFutures.add(completableFuture);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return completableFutures;
    }

    private CompletableFuture<Map<String, List<Map<String, Integer>>>> getCompletableFutureChunkMatchingResultMap
            (ExecutorService threadPool, Set<String> wordsToMatch, StringBuilder sb, int chunkId) {
        var chunkLineOffset = getChunkLineOffset(chunkId);
        return CompletableFuture
                .supplyAsync(() -> nameMatcher.matchLocations(wordsToMatch, sb.toString(), chunkLineOffset), threadPool)
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
