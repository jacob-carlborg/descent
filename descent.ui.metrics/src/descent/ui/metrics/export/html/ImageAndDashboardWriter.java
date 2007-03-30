package descent.ui.metrics.export.html;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.ui.metrics.collator.MetricsCollator;

class ImageAndDashboardWriter implements ImageFileNameFactory {
    private final boolean produceImages;
    private final MetricsCollator methodMetrics;
    private final MetricsCollator typeMetrics;
    
    public ImageAndDashboardWriter(MetricsCollator methodMetricsCollator, MetricsCollator typeMetricsCollator, boolean produceImages) {
        this.methodMetrics = methodMetricsCollator;
        this.typeMetrics = typeMetricsCollator;
        this.produceImages = produceImages;
    }
    
    public void write(HtmlPageBuilder pageBuilder, File directory, IProgressMonitor monitor) throws IOException {
        if (produceImages) {
            createImages(directory, monitor);
            createDashboard(pageBuilder, monitor);
        }
    }
    
    public int getNumberOfSteps() {
        return produceImages ? 1 + methodMetrics.getNumberOfMetrics() + typeMetrics.getNumberOfMetrics() : 0;
    }
    
    public String getImageFileName(String metricId) {
        if (produceImages) {
            return metricId + ".jpg";
        } else {
            return null;
        }
    }
    
    private void createDashboard(HtmlPageBuilder pageBuilder, IProgressMonitor monitor) throws IOException {
        monitor.subTask("Creating dashboard");
        new DashboardWriter(pageBuilder, this).write(methodMetrics, typeMetrics);
        monitor.worked(1);
    }
    
    private void createImages(File directory, IProgressMonitor monitor) throws IOException {
        monitor.subTask("Creating images");
        new HistogramWriter(directory, this).write(methodMetrics, typeMetrics, monitor);
    }
}
