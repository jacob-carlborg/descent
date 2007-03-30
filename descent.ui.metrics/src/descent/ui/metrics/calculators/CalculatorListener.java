package descent.ui.metrics.calculators;

import descent.ui.metrics.location.MetricLocation;


public interface CalculatorListener {
    void noteMethodValue(String metricKey, MetricLocation location, int value);
    void noteTypeValue(String metricKey, MetricLocation location, int value);
}
