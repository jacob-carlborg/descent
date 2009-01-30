package descent.ui.metrics.export.html;

import java.io.IOException;

import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.collator.MetricsCollator;

class DashboardWriter {
    private final HtmlPageBuilder pageBuilder;
    private final ImageFileNameFactory imageFileNameFactory;

    public DashboardWriter(HtmlPageBuilder pageBuilder, ImageFileNameFactory imageFileNameFactory) {
        this.pageBuilder = pageBuilder;
        this.imageFileNameFactory = imageFileNameFactory;
    }
    
    public void write(MetricsCollator methodMetricsCollator, MetricsCollator typeMetricsCollator) throws IOException {
        pageBuilder.openPage(HtmlExporter.DASHBOARD_FILE_NAME, "Dashboard").openElement("P").printLinkAndNewline("index", "Index").openElementAndNewline("P");
        writeDashboardFragment(methodMetricsCollator);
        writeDashboardFragment(typeMetricsCollator);
        pageBuilder.closePage();
    }

    private void writeDashboardFragment(MetricsCollator collator) {
        String[] ids = collator.getSortedMetricIds();
        for (int i = 0; i < ids.length; i++) {
            pageBuilder.createElement("H2", pageBuilder.getLink(MetricsPlugin.getMetricDescriptionFile(ids[i]), MetricsPlugin.getMetricPresentationName(ids[i])));
            pageBuilder.createImageFileLink(imageFileNameFactory.getImageFileName(ids[i]));
        }
    }
}
