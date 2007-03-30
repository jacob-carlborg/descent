package descent.ui.metrics.collator;

import descent.ui.metrics.location.MetricLocation;

public final class Metric {
    private MetricLocation location;
    private String id;
    private int value;
    private int aggregationSteps;

    public Metric(String id, MetricLocation location, int value) {
        this.location = location;
        this.id = id;
        this.value = value;
    }

    public Metric(MetricLocation location, Metric derivedFrom) {
        this(derivedFrom.getId(), location, derivedFrom.value);
        aggregationSteps = derivedFrom.aggregationSteps + 1;
    }

    public String getId() {
        return id;
    }

    public MetricLocation getLocation() {
        return location;
    }

    public int getValue() {
        return value;
    }

    public int getAggregationSteps() {
        return aggregationSteps;
    }

    public String toString() {
        return id + ", location=(" + location.toString() + "), value=" + value;
    }
}
