import com.bigId.aggregator.impl.AggregatorImpl;
import com.bigId.matcher.TextMatcher;

module bigId.main {

    requires bigId.aggregator;
    requires bigId.matcher;

    uses AggregatorImpl;
    uses TextMatcher;
}