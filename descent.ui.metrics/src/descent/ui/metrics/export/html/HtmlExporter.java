package descent.ui.metrics.export.html;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.ui.metrics.collator.MetricsCollator;
import descent.ui.metrics.export.Exporter;

public final class HtmlExporter implements Exporter {
    public static final String QUALIFIED_TYPE_NAME_PAGE_NAME_START = "QualifiedTypeName.";
    public static final String UNQUALIFIED_TYPE_NAME_PAGE_NAME_START = "UnqualifiedTypeName.";
    public static final String DASHBOARD_FILE_NAME = "dashboard";

    public static final String METHOD_METRIC_LEVEL = "Method";
    public static final String TYPE_METRIC_LEVEL = "Type";
    public static final String PACKAGE_METRIC_LEVEL = "Package";

    private MetricsCollator packageMetricsCollator;
    private MetricsCollator typeMetricsCollator;
    private MetricsCollator methodMetricsCollator;
    private File directory;
    private String title;
    private int rowsPerPage;
    private boolean produceImages;
    private HtmlPageBuilder pageBuilder;
    private IProgressMonitor monitor;
    private ImageAndDashboardWriter imageAndDashboardWriter;

    public HtmlExporter(File directory, String title, int rowsPerPage, boolean produceImages) {
        this.directory = directory;
        this.title = title;
        this.rowsPerPage = rowsPerPage;
        this.produceImages = produceImages;

        pageBuilder = new HtmlPageBuilder(directory);
    }

    public void export(MetricsCollator typeMetrics, MetricsCollator methodMetrics, IProgressMonitor progressMonitor) throws IOException {
        packageMetricsCollator = new MetricsCollator();
        this.typeMetricsCollator = typeMetrics;
        this.methodMetricsCollator = methodMetrics;
        this.monitor = progressMonitor;
        
        imageAndDashboardWriter = new ImageAndDashboardWriter(methodMetricsCollator, typeMetricsCollator, produceImages);

        progressMonitor.beginTask("Exporting Metrics HTML", getMonitorSteps());

        export();
    }

    private void export() throws IOException {
        imageAndDashboardWriter.write(pageBuilder, directory, monitor);
        aggregateMetrics();
        
        MetricsPagesWriter metricsPagesWriter = new MetricsPagesWriter(pageBuilder, rowsPerPage);
        metricsPagesWriter.writeTypeAndMethodMetricsPages(methodMetricsCollator, HtmlExporter.METHOD_METRIC_LEVEL, imageAndDashboardWriter, monitor);
        metricsPagesWriter.writeTypeAndMethodMetricsPages(typeMetricsCollator, HtmlExporter.TYPE_METRIC_LEVEL, imageAndDashboardWriter, monitor);
        metricsPagesWriter.writePackageMetricsPages(packageMetricsCollator, imageAndDashboardWriter, monitor);
        
        new IndexPageWriter(pageBuilder, this).write(monitor);
        new MetricsDescriptionWriter(directory).write(monitor);
    }

    private int getMonitorSteps() {
        int methodMetrics = methodMetricsCollator.getNumberOfMetrics();
        int typeMetrics = typeMetricsCollator.getNumberOfMetrics();
        return 2 // aggregate calculations
                + imageAndDashboardWriter.getNumberOfSteps()
                + methodMetrics * 3 + typeMetrics * 2 // ordered by metrics pages for method, type and packages
                + 5 // ordered by Package, qualified and unqualified type names
                + 1; // copy metric descriptions
    }
    
    private void aggregateMetrics() {
        final MetricAggregator metricAggregator = new MetricAggregator();
        metricAggregator.aggregate(typeMetricsCollator, methodMetricsCollator, monitor);
        metricAggregator.aggregate(packageMetricsCollator, typeMetricsCollator, monitor);
    }

    static String getFileName(int index, String level) {
        return level + index;
    }

    boolean isProducingImages() {
        return produceImages;
    }

    String getTitle() {
        return title;
    }

    MetricsCollator getPackageMetricsCollator() {
        return packageMetricsCollator;
    }

    MetricsCollator getTypeMetricsCollator() {
        return typeMetricsCollator;
    }

    MetricsCollator getMethodMetricsCollator() {
        return methodMetricsCollator;
    }
}
