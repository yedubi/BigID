import com.bigId.matcher.TextMatcher;
import com.bigId.matcher.impl.NameMatcher;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameMatcherTest {

    @Test
    void testLocationMatcher() {
        var textChunk = "The Project Gutenberg EBook of The Adventures of Sherlock Holmes\n" +
                "by Sir Arthur Conan[ Arthur\n" +
                "(#15 in our series by Sir Arthur Conan  Doyle)\n";
        Set<String> names = new HashSet<>(Arrays.asList("Conan,Arthur,Doyle".split(",")));

        var listArthur = new ArrayList<Map<String, Integer>>();
        var listConan = new ArrayList<Map<String, Integer>>();
        var listDoyle = new ArrayList<Map<String, Integer>>();

        var mapArthur1 = new HashMap<String, Integer>();
        mapArthur1.put("lineOffset", 2);
        mapArthur1.put("charOffset", 8);
        var mapArthur2 = new HashMap<String, Integer>();
        mapArthur2.put("lineOffset", 2);
        mapArthur2.put("charOffset", 22);
        var mapArthur3 = new HashMap<String, Integer>();
        mapArthur3.put("lineOffset", 3);
        mapArthur3.put("charOffset", 27);
        listArthur.add(mapArthur1);
        listArthur.add(mapArthur2);
        listArthur.add(mapArthur3);

        var mapConan1 = new HashMap<String, Integer>();
        mapConan1.put("lineOffset", 2);
        mapConan1.put("charOffset", 15);
        var mapConan2 = new HashMap<String, Integer>();
        mapConan2.put("lineOffset", 3);
        mapConan2.put("charOffset", 34);
        listConan.add(mapConan1);
        listConan.add(mapConan2);

        var mapDoyle1 = new HashMap<String, Integer>();
        mapDoyle1.put("lineOffset", 3);
        mapDoyle1.put("charOffset", 41);
        listDoyle.add(mapDoyle1);

        var expectedResult = new HashMap<>();
        expectedResult.put("Arthur", listArthur);
        expectedResult.put("Conan", listConan);
        expectedResult.put("Doyle", listDoyle);

        TextMatcher matcher = new NameMatcher();

        var actualResult = matcher.matchLocations(names, textChunk, 1);
        assertEquals(actualResult, expectedResult);
    }
}
