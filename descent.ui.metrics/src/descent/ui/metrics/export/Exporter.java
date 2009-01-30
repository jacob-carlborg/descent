package descent.ui.metrics.export;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.ui.metrics.collator.MetricsCollator;

public interface Exporter {
    void export(MetricsCollator typeMetricsCollator, MetricsCollator methodMetricsCollator, IProgressMonitor monitor) throws IOException;
}
