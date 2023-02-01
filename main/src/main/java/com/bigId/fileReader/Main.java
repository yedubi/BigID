package com.bigId.fileReader;

import com.bigId.aggregator.impl.AggregatorImpl;
import com.bigId.matcher.impl.NameMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args)  {

        var start = System.nanoTime();

        var fileName = "big.txt";

        var words = "James,James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey, Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger";

        var wordsToMatch = getWordsSet(words);

        var aggregator = new AggregatorImpl();
        var matcher = new NameMatcher();
        var fileReader = new FileChunkReader(fileName, matcher, aggregator);

        //read file and send chunks asynchronous for matching
        fileReader.readFileByChunksAndMatchWordsLocationsAsync(wordsToMatch);
        var result = fileReader.getResult();

        var stop = System.nanoTime();
        System.out.println("Time: " + (stop - start) / 1000000.0 + " msec");

    }

    private static HashSet<String> getWordsSet(String text) {
        return new HashSet<>(Arrays.asList(text.split(",")));
    }


}

