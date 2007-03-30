package descent.ui.metrics.export.html;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.collator.MetricsCollator;

final class IndexPageWriter {
    private HtmlExporter exporter;
    private final HtmlPageBuilder pageBuilder;

    public IndexPageWriter(HtmlPageBuilder pageBuilder, HtmlExporter exporter) {
        this.pageBuilder = pageBuilder;
        this.exporter = exporter;
    }

    public void write(IProgressMonitor monitor) throws IOException {
        monitor.subTask("Creating index page");
        pageBuilder.openPage("index", "Metrics for " + getTitle());
        createFragments();
        pageBuilder.closePage();
        monitor.worked(1);
    }

    private void createFragments() {
        createDashboardIndexPageFragment();
        createIndexPageFragment(getPackageMetricsCollator(), HtmlExporter.PACKAGE_METRIC_LEVEL);
        createIndexPageFragment(getTypeMetricsCollator(), HtmlExporter.TYPE_METRIC_LEVEL);
        createIndexPageFragment(getMethodMetricsCollator(), HtmlExporter.METHOD_METRIC_LEVEL);
    }

    private void createDashboardIndexPageFragment() {
        if (isProducingDashboard()) {
            pageBuilder.createElementAndNewline("H2", "Dashboard").openElement("P").openElementAndNewline("UL").openElement("LI").printLinkAndNewline(HtmlExporter.DASHBOARD_FILE_NAME, "Visual Summary").closeElement("UL");
        }
    }

    private void createIndexPageFragment(MetricsCollator collator, String level) {
        pageBuilder.createElementAndNewline("H2", level + " Metrics").openElement("P").println("Order by:").openElementAndNewline("UL");
        createIndexPagePackageAndTypeNameFragment(level);
        createIndexPageMetricsFragment(collator, level);
        pageBuilder.closeElementAndNewline("UL");
    }

    private void createIndexPagePackageAndTypeNameFragment(String level) {
        if (HtmlExporter.PACKAGE_METRIC_LEVEL.equals(level)) {
            pageBuilder.openElement("LI").printLinkAndNewline("Package.1", "Package");
        } else {
            pageBuilder.openElement("LI").printLinkAndNewline(HtmlExporter.UNQUALIFIED_TYPE_NAME_PAGE_NAME_START + level + ".1", "Unqualified type name");
            pageBuilder.openElement("LI").printLinkAndNewline(HtmlExporter.QUALIFIED_TYPE_NAME_PAGE_NAME_START + level + ".1", "Qualified type name");
        }
    }

    private void createIndexPageMetricsFragment(MetricsCollator collator, String level) {
        String[] metricIds = collator.getSortedMetricIds();
        for (int i = 0; i < metricIds.length; i++) {
            pageBuilder.openElement("LI").printLinkAndNewline(getFileName(i, level) + ".1", MetricsPlugin.getMetricPresentationName(metricIds[i]));
        }
    }

    private String getTitle() {
        return exporter.getTitle();
    }

    private MetricsCollator getPackageMetricsCollator() {
        return exporter.getPackageMetricsCollator();
    }

    private MetricsCollator getTypeMetricsCollator() {
        return exporter.getTypeMetricsCollator();
    }

    private MetricsCollator getMethodMetricsCollator() {
        return exporter.getMethodMetricsCollator();
    }

    private boolean isProducingDashboard() {
        return exporter.isProducingImages();
    }

    private String getFileName(int index, String level) {
        return HtmlExporter.getFileName(index, level);
    }
}
