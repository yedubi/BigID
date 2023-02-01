package com.bigId.fileReader;

import com.bigId.aggregator.Aggregator;
import com.bigId.matcher.TextMatcher;

import java.io.BufferedReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FileChunkReader {
    public static final int CHUNK_SIZE = 1000;
    public static final int FIXED_THREADS_POOL_SIZE = 4;
    private final String fileName;
    private final TextMatcher nameMatcher;
    private final Aggregator aggregator;

    public FileChunkReader(String fileName, TextMatcher nameMatcher, Aggregator aggregator) {
        this.fileName = fileName;
        this.nameMatcher = nameMatcher;
        this.aggregator = aggregator;
    }


    public void readFileByChunksAndMatchWordsLocationsAsync(Set<String> wordsToMatch) {

        var threadPool = Executors.newFixedThreadPool(FIXED_THREADS_POOL_SIZE);

        var completableFuturesWordsLocations =
                readFileByChunksAndMatchWordsLocationsAsync(threadPool, wordsToMatch);

        //collect to list of maps for each chunk when all completableFutures complete
        completableFuturesWordsLocations
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        threadPool.shutdownNow();
    }

    public Map<String, List<Map<String, Integer>>> getResult() {
        return aggregator.getResult();
    }


    public ArrayList<CompletableFuture<Map<String, List<Map<String, Integer>>>>> readFileByChunksAndMatchWordsLocationsAsync
            (ExecutorService threadPool, Set<String> wordsToMatch) {

        var completableFutures = new ArrayList<CompletableFuture<Map<String, List<Map<String, Integer>>>>>();

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(fileName))) {
            var chunkStringBuilder = new StringBuilder();
            String line;
            var lineCount = 0;
            var chunkId = 0;

            while (Objects.nonNull(line = br.readLine())) {
                chunkStringBuilder.append(line.isEmpty() ? System.lineSeparator() : line + System.lineSeparator());
                lineCount++;
                if (lineCount % CHUNK_SIZE == 0) {
                    var chunkLength = chunkStringBuilder.length();
                    var completableFuture =
                            getCompletableFutureChunkMatchingResultMap(threadPool, wordsToMatch, chunkStringBuilder, chunkId);
                    completableFutures.add(completableFuture);
                    chunkId++;
                    chunkStringBuilder = new StringBuilder();
                }
            }
            if (chunkStringBuilder.length() > 0) {
                var completableFuture =
                        getCompletableFutureChunkMatchingResultMap(threadPool, wordsToMatch, chunkStringBuilder, chunkId);
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
                }).thenApplyAsync(map -> {
                    aggregator.aggregate(map);
                    return map;
                });
    }

    private int getChunkLineOffset(int chunk) {
        var firstLineOffset = 1;
        return firstLineOffset + (chunk * CHUNK_SIZE);
    }


}
