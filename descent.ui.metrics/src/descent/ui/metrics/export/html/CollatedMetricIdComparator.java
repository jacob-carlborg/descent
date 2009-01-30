package descent.ui.metrics.export.html;

import java.util.Comparator;

import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.collator.MetricsCollator;

public final class CollatedMetricIdComparator implements Comparator {
    private final MetricsCollator collator;

    public CollatedMetricIdComparator(MetricsCollator collator) {
        this.collator = collator;
    }

    public int compare(Object obj1, Object obj2) {
        return compareStrings((String) obj1, (String) obj2);
    }

    private int compareStrings(String s1, String s2) {
        int compare = collator.getAggregationSteps(s2) - collator.getAggregationSteps(s1);
        if (compare != 0) {
            return compare;
        } else {
            return MetricsPlugin.getMetricShortPresentationName(s1).compareTo(MetricsPlugin.getMetricShortPresentationName(s2));
        }
    }
}
