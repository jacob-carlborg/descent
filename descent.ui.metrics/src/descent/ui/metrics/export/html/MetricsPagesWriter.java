package descent.ui.metrics.export.html;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.collator.MetricsCollator;
import descent.ui.metrics.location.LocationComparator;
import descent.ui.metrics.location.MetricOrderingLocationComparator;
import descent.ui.metrics.location.PackageNameComparator;
import descent.ui.metrics.location.QualifiedTypeNameComparator;
import descent.ui.metrics.location.UnqualifiedTypeNameComparator;

class MetricsPagesWriter {
    private final HtmlPageBuilder pageBuilder;
    private final int rowsPerPage;
    private String level;

    public MetricsPagesWriter(HtmlPageBuilder pageBuilder, int rowsPerPage) {
        this.pageBuilder = pageBuilder;
        this.rowsPerPage = rowsPerPage;
    }

    public void writeTypeAndMethodMetricsPages(MetricsCollator collator, String level, ImageFileNameFactory imageFileNameFactory, IProgressMonitor monitor) {
        this.level = level;
        monitor.subTask("Creating " + level + " metrics pages");
        createUnqualifiedTypeNameOrderedMetricsPage(collator);
        monitor.worked(1);

        createQualifiedTypeNameOrderedMetricsPage(collator);
        monitor.worked(1);

        createOrderedByMetricsPages(collator, imageFileNameFactory, monitor);
    }
    
    public void writePackageMetricsPages(MetricsCollator collator, ImageFileNameFactory imageFileNameFactory, IProgressMonitor monitor) {
        this.level = HtmlExporter.PACKAGE_METRIC_LEVEL;
        
        monitor.subTask("Creating " + HtmlExporter.PACKAGE_METRIC_LEVEL + " metrics pages");
        createPackageOrderedMetricsPage(collator);
        monitor.worked(1);
        
        createOrderedByMetricsPages(collator, imageFileNameFactory, monitor);
    }
    
    private void createPackageOrderedMetricsPage(MetricsCollator collator) {
        new OrderedPageWriter(pageBuilder, collator, "Package", null, "Package", HtmlExporter.PACKAGE_METRIC_LEVEL, rowsPerPage).write(new PackageNameComparator());
    }
    
    private void createUnqualifiedTypeNameOrderedMetricsPage(MetricsCollator collator) {
        createOrderedMetricsPage(collator, HtmlExporter.UNQUALIFIED_TYPE_NAME_PAGE_NAME_START, "Unqualified Type Name", new UnqualifiedTypeNameComparator());
    }

    private void createQualifiedTypeNameOrderedMetricsPage(MetricsCollator collator) {
        createOrderedMetricsPage(collator, HtmlExporter.QUALIFIED_TYPE_NAME_PAGE_NAME_START, "Qualified Type Name", new QualifiedTypeNameComparator());
    }
    
    private void createOrderedMetricsPage(MetricsCollator collator, final String pageNameStart, final String orderItem, final LocationComparator locationComparator) {
        new OrderedPageWriter(pageBuilder, collator, pageNameStart + level, null, orderItem, level, rowsPerPage).write(locationComparator);
    }
    
    private void createOrderedByMetricsPages(MetricsCollator collator, ImageFileNameFactory imageFileNameFactory, IProgressMonitor monitor) {
        String[] metricIds = collator.getSortedMetricIds();
        for (int i = 0; i < metricIds.length; i++) {
            createOrderedByMetricPage(collator, imageFileNameFactory, metricIds[i], HtmlExporter.getFileName(i, level));
            monitor.worked(1);
        }
    }
    
    private void createOrderedByMetricPage(MetricsCollator collator, ImageFileNameFactory imageFileNameFactory, final String metricId, String fileName) {
        new OrderedPageWriter(pageBuilder, collator, fileName, imageFileNameFactory.getImageFileName(metricId), MetricsPlugin.getMetricPresentationName(metricId), level, rowsPerPage).write(new MetricOrderingLocationComparator(collator, metricId));
    }
}
