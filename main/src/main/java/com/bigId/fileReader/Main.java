package com.bigId.fileReader;

import com.bigId.aggregator.impl.AggregatorImpl;
import com.bigId.matcher.impl.NameMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    public static final int FIXED_THREADS_POOL_SIZE = 4;

    public static void main(String[] args) throws InterruptedException {

        var start = System.nanoTime();

        var fileName = "big.txt";

        var words = "James,James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey, Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger";

        var wordsToMatch = getWordsSet(words);

        var threadPool = Executors.newFixedThreadPool(FIXED_THREADS_POOL_SIZE);

        var aggregator = new AggregatorImpl();
        var matcher = new NameMatcher();
        var fileReader = new FileChunkReader(fileName, matcher, aggregator);
        //read file and send chunks asynchronous for matching
        var completableFuturesWordsLocations =
                fileReader.readFileByChunksAndMatchWordsLocationsAsync(threadPool, wordsToMatch);

        //collect to list of maps for each chunk when all completableFutures complete
        completableFuturesWordsLocations
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        threadPool.shutdownNow();

        var result = aggregator.getResult();

        // aggregate results from all chunks and print
        //var result = Aggregator.aggregateMatches(wordsLocations);

        var stop = System.nanoTime();
        System.out.println("Time: " + (stop - start) / 1000000.0 + " msec");

    }

    private static HashSet<String> getWordsSet(String text) {
        return new HashSet<>(Arrays.asList(text.split(",")));
    }


}

