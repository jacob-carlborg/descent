package descent.ui.metrics.export.html;

import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.collator.MetricsCollator;

class MetricsTableHeaderWriter {
    private HtmlPageBuilder pageBuilder;
    private int fragmentNumber;
    private String level;

    public void write(HtmlPageBuilder pageBuilder, int fragmentNumber, String level, String[] metricIds, MetricsCollator collator) {
        this.pageBuilder = pageBuilder;
        this.fragmentNumber = fragmentNumber;
        this.level = level;
        
        writeMetricsTableMetricsHeaders(metricIds, collator);
        writeMetricsTableLocationHeaders();
        writeMetricsTablePackageHeader();
    }

    private void writeMetricsTablePackageHeader() {
        if (HtmlExporter.PACKAGE_METRIC_LEVEL.equals(level)) {
            pageBuilder.createElement("TH", pageBuilder.getHtmlLink(HtmlExporter.PACKAGE_METRIC_LEVEL + "." + fragmentNumber, "Package"));
        } else {
            pageBuilder.createElement("TH", pageBuilder.getHtmlLink(HtmlExporter.QUALIFIED_TYPE_NAME_PAGE_NAME_START + level + "." + fragmentNumber, "Package"));
        }
    }

    private void writeMetricsTableLocationHeaders() {
        if (!HtmlExporter.PACKAGE_METRIC_LEVEL.equals(level)) {
            pageBuilder.createElement("TH", "Line");
            if (HtmlExporter.METHOD_METRIC_LEVEL.equals(level)) {
                pageBuilder.createElement("TH", "Method");
            }
            pageBuilder.createElement("TH", pageBuilder.getHtmlLink(HtmlExporter.UNQUALIFIED_TYPE_NAME_PAGE_NAME_START + level + "." + fragmentNumber, "Type"));
        }
    }

    private void writeMetricsTableMetricsHeaders(String[] metricIds, MetricsCollator collator) {
        for (int i = 0; i < metricIds.length; i++) {
            pageBuilder.openElement("TH").printLink(HtmlExporter.getFileName(i, level) + "." + fragmentNumber, MetricsPlugin.getMetricShortPresentationName(metricIds[i]));
            if (collator.getAggregationSteps(metricIds[i]) > 0) {
                pageBuilder.openElement("BR").print("(max)");
            }
            pageBuilder.closeElementAndNewline("TH");
        }
    }
}
