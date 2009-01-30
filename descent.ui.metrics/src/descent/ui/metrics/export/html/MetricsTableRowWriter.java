package descent.ui.metrics.export.html;

import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.collator.MetricsCollator;
import descent.ui.metrics.location.MetricLocation;

class MetricsTableRowWriter {
    private HtmlPageBuilder pageBuilder;
    private MetricLocation location;
    
    public MetricsTableRowWriter(HtmlPageBuilder builder) {
        this.pageBuilder = builder;
    }

    public void write(MetricLocation location, String[] metricIds, MetricsCollator collator) {
        this.location = location;
        
        pageBuilder.openElement("TR");
        writeMetrics(metricIds, collator);
        writeMethodInfo();
        writeTypeInfo();
        writePackageInfo();
        pageBuilder.closeElementAndNewline("TR");
    }
    
    private void writePackageInfo() {
        pageBuilder.createElement("TD", (location.getPackageName().length() > 0 ? location.getPackageName() : "(default)"));
    }

    private void writeTypeInfo() {
        if (location.hasType()) {
            if (!location.hasMethod()) {
                pageBuilder.createElement("TD", Integer.toString(location.getTypeInfo().getLineNumber()));
            }
            pageBuilder.createElement("TD", location.getTypeInfo().getName());
        }
    }

    private void writeMethodInfo() {
        if (location.hasMethod()) {
            pageBuilder.createElement("TD", Integer.toString(location.getMethodInfo().getLineNumber())).createElement("TD", location.getMethodInfo().getName());
        }
    }

    private void writeMetrics(String[] metricIds, MetricsCollator collator) {
        for (int i = 0; i < metricIds.length; i++) {
            writeMetricTableData(metricIds[i], collator);
        }
    }

    private void writeMetricTableData(String metricId, MetricsCollator collator) {
        pageBuilder.openElement("TD");
        if (collator.hasMetric(location, metricId)) {
            writeValidMetricTableData(metricId, collator);
        } else {
            pageBuilder.print('-');
        }
        pageBuilder.closeElement("TD");
    }

    private void writeValidMetricTableData(String metricId, MetricsCollator collator) {
        int value = collator.getMetric(location, metricId).getValue();
        boolean violation = (value > MetricsPlugin.getDefault().getMaximumPreferredValue(metricId));
        if (violation) {
            pageBuilder.print("<FONT color=\"red\">");
        }
        pageBuilder.print(Integer.toString(collator.getMetric(location, metricId).getValue()));
        if (violation) {
            pageBuilder.print("</FONT>");
        }
    }
}
