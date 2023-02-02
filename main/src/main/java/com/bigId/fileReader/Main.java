package com.bigId.fileReader;

import com.bigId.aggregator.impl.AggregatorImpl;
import com.bigId.matcher.impl.NameMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class Main {
    public static final int FIXED_THREADS_POOL_SIZE = 4;

    public static void main(String[] args) {

        do {

            var start = System.nanoTime();

            var fileName = "big.txt";

            var words = "Yevhenii,James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey,Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger";

            var wordsToMatch = getWordsSet(words);

            var aggregator = new AggregatorImpl();
            var matcher = new NameMatcher();
            var fileReader = new FileChunkReader(fileName, matcher);


            var threadPool = Executors.newFixedThreadPool(FIXED_THREADS_POOL_SIZE);
            try {
                var completableFuturesWordsLocations =
                        fileReader.readFileByChunksAndMatchWordsLocationsAsync(threadPool, wordsToMatch);
                var chunksResultListMap = completableFuturesWordsLocations
                        .stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList());
                var result = aggregator.aggregateMatches(chunksResultListMap);
                System.out.println("");
            } finally {
                threadPool.shutdownNow();
            }

            var stop = System.nanoTime();
            System.out.println("Time: " + (stop - start) / 1000000.0 + " msec");
            System.out.println("Meg used=" + (Runtime.getRuntime().totalMemory() -
                    Runtime.getRuntime().freeMemory()) / (1000 * 1000) + "M");
        }
        while (true);
    }

    private static HashSet<String> getWordsSet(String text) {
        return new HashSet<>(Arrays.asList(text.split(",")));
    }


}

