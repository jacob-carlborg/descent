package descent.ui.metrics.export.html;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.ui.metrics.collator.Metric;
import descent.ui.metrics.collator.MetricsCollator;
import descent.ui.metrics.location.MetricLocation;

final class MetricAggregator {
    public void aggregate(final MetricsCollator to, final MetricsCollator from, IProgressMonitor monitor) {
        monitor.subTask("Aggregating metrics");
        
        final String[] metricIds = from.getMetricIds();
        from.forEach(new MetricLocation.Closure() {
            public void execute(MetricLocation location) {
                aggregateMetrics(to, from, metricIds, location);
            }
        });
        
        monitor.worked(1);
    }

    private void aggregateMetrics(MetricsCollator to, MetricsCollator from, String[] metricIds, MetricLocation location) {
        MetricLocation containingLocation = location.createContainingLocation();
        for (int i = 0; i < metricIds.length; i++) {
            if (from.hasMetric(location, metricIds[i])) {
                aggregateMetric(to, containingLocation, from.getMetric(location, metricIds[i]));
            }
        }
    }

    private void aggregateMetric(MetricsCollator to, MetricLocation containingLocation, Metric metric) {
        if (!to.hasMetric(containingLocation, metric.getId())) {
            to.addMetric(containingLocation, new Metric(containingLocation, metric));
        } else {
            Metric existingMethodMetric = to.getMetric(containingLocation, metric.getId());
            if (metric.getValue() > existingMethodMetric.getValue()) {
                to.addMetric(containingLocation, new Metric(containingLocation, metric));
            }
        }
    }
}
