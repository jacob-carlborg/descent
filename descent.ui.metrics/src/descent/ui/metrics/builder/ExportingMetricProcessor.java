package descent.ui.metrics.builder;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.ui.metrics.collator.Metric;
import descent.ui.metrics.collator.MetricsCollator;
import descent.ui.metrics.export.Exporter;
import descent.ui.metrics.location.MetricLocation;

public class ExportingMetricProcessor extends CompilationUnitMetricProcessor {
    private MetricsCollator typeMetricsCollator;
    private MetricsCollator methodMetricsCollator;
    private final Exporter[] exporters;
    
    public ExportingMetricProcessor(Exporter[] exporters) {
        this.exporters = exporters;
        typeMetricsCollator = new MetricsCollator();
        methodMetricsCollator = new MetricsCollator();
    }
    
    public void complete(IProgressMonitor monitor) throws IOException {
        for (int i = 0; i < exporters.length; i++) {
            exporters[i].export(typeMetricsCollator, methodMetricsCollator, monitor);
        }
    }
    
    public void noteEnabledTypeValue(String metricKey, MetricLocation location, int value) {
        typeMetricsCollator.addMetric(new Metric(metricKey, location, value));
    }

    public void noteEnabledMethodValue(String metricKey, MetricLocation location, int value) {
        methodMetricsCollator.addMetric(new Metric(metricKey, location, value));
    }
}
