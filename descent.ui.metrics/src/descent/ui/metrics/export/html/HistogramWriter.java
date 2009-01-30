package descent.ui.metrics.export.html;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.ui.metrics.collator.MetricsCollator;

public class HistogramWriter {
    private final File directory;
    private final ImageFileNameFactory imageFileNameFactory;

    public HistogramWriter(File directory, ImageFileNameFactory imageFileWriter) {
        this.directory = directory;
        this.imageFileNameFactory = imageFileWriter;
    }
    
    public void write(MetricsCollator methodMetricsCollator, MetricsCollator typeMetricsCollator, IProgressMonitor monitor) throws IOException {
        createImages(methodMetricsCollator, monitor);
        createImages(typeMetricsCollator, monitor);
    }

    private void createImages(MetricsCollator collator, IProgressMonitor monitor) throws IOException {
        JpegProducer producer = new JpegProducer();
        String[] metricIds = collator.getMetricIds();
        for (int i = 0; i < metricIds.length; i++) {
            producer.produce(new File(directory, imageFileNameFactory.getImageFileName(metricIds[i])), collator, metricIds[i]);
            monitor.worked(1);
        }
    }
}
