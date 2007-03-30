package descent.ui.metrics.export.html;

import descent.ui.metrics.MetricsPlugin;

class PageHeaderWriter {
    private HtmlPageBuilder pageBuilder;
    private String[] metricIds;
    
    public void write(HtmlPageBuilder pageBuilder, String[] metricIds, String imageFileName) {
        this.pageBuilder = pageBuilder;
        this.metricIds = metricIds;
        pageBuilder.openElement("TABLE").openElement("TR").openElementAndNewline("TD");
        pageBuilder.createImageFileLink(imageFileName);
        pageBuilder.closeElement("TD").openElementAndNewline("TD");
        writeAcronymList();
        pageBuilder.closeElement("TD").closeElement("TR").closeElementAndNewline("TABLE");
    }
    
    private void writeAcronymList() {
        openMetricsAcronymListTable();
        writeMetricsAcronymListTableRows();
        closeMetricsAcronymListTable();
    }

    private void writeMetricsAcronymListTableRows() {
        for (int i = 0; i < metricIds.length; i++) {
            pageBuilder.openElement("TR").createElement("TD", MetricsPlugin.getMetricShortPresentationName(metricIds[i])).createElement("TD", pageBuilder.getLink(MetricsPlugin.getMetricDescriptionFile(metricIds[i]), MetricsPlugin.getMetricPresentationName(metricIds[i]))).closeElementAndNewline("TR");
        }
    }

    private void closeMetricsAcronymListTable() {
        pageBuilder.closeElementAndNewline("TABLE").openElementAndNewline("P");
    }

    private void openMetricsAcronymListTable() {
        pageBuilder.openElementAndNewline("TABLE border=\"1\" cellpadding=\"2\"").openElement("TR").createElement("TH", "Short Name").createElement("TH", "Full Name").closeElementAndNewline("TR");
    }
}
