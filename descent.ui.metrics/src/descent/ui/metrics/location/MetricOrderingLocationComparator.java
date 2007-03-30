package descent.ui.metrics.location;

import descent.ui.metrics.collator.MetricsCollator;

public final class MetricOrderingLocationComparator extends LocationComparator {
    private final MetricsCollator collator;
    private final String metricId;

    public MetricOrderingLocationComparator(MetricsCollator collator, String metricId) {
        this.collator = collator;
        this.metricId = metricId;
    }

    protected int compareParticular(MetricLocation l1, MetricLocation l2) {
        if (collator.hasMetric(l1, metricId)) {
            if (collator.hasMetric(l2, metricId)) {
                return collator.getMetric(l2, metricId).getValue() - collator.getMetric(l1, metricId).getValue();
            } else {
                return -1;
            }
        } else {
            if (collator.hasMetric(l2, metricId)) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
