import com.bigId.aggregator.Aggregator;
import com.bigId.matcher.TextMatcher;

module bigId.main {

    requires bigId.aggregator;
    requires bigId.matcher;

    uses Aggregator;
    uses TextMatcher;
}