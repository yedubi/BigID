package com.bigId.fileReader;

import com.bigId.aggregator.Aggregator;
import com.bigId.fileReader.FileChunkReader;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    public static final int CHUNK_SIZE = 1000;
    public static final int FIXED_THREADS_POOL_SIZE = 4;

    public static void main(String[] args) throws InterruptedException {

        var start = System.nanoTime();

        var fileName = "big.txt";

        var words = "James,James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey, Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger";

        var wordsToMatch = getWordsSet(words);

        var threadPool = Executors.newFixedThreadPool(FIXED_THREADS_POOL_SIZE);

        //read file and send chunks asynchronous for matching
        var completableFuturesWordsLocations =
                new FileChunkReader(fileName)
                        .readFileByChunksAndMatchWordsLocationsAsync(CHUNK_SIZE, threadPool, wordsToMatch);

        //collect to list of maps for each chunk when all completableFutures complete
        var wordsLocations = completableFuturesWordsLocations
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        threadPool.shutdownNow();
        threadPool.awaitTermination(1, TimeUnit.SECONDS);

        // aggregate results from all chunks and print
        var result = Aggregator.aggregateMatches(wordsLocations);

        var stop = System.nanoTime();
        System.out.println("Time: " + (stop - start) / 1000000.0 + " msec");

    }

    private static HashSet<String> getWordsSet(String text) {
        return new HashSet<>(Arrays.asList(text.split(",")));
    }


}

