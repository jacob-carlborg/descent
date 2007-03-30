package descent.ui.metrics.export.html;

import java.io.IOException;
import java.util.Comparator;

import descent.ui.metrics.collator.MetricsCollator;
import descent.ui.metrics.location.MetricLocation;

final class OrderedPageWriter {
    private HtmlPageBuilder pageBuilder;
    private MetricsCollator collator;
    private String fileName;
    private final String imageFileName;
    private String orderItem;
    private String level;
    private String[] metricIds;
    private int rowsPerPage;

    public OrderedPageWriter(HtmlPageBuilder pageBuilder, MetricsCollator collator, String fileName, String imageFileName, String orderItem, String level, int rowsPerPage) {
        this.pageBuilder = pageBuilder;
        this.collator = collator;
        this.fileName = fileName;
        this.imageFileName = imageFileName;
        this.orderItem = orderItem;
        this.level = level;
        this.rowsPerPage = rowsPerPage;
        metricIds = collator.getSortedMetricIds();
    }

    public void write(Comparator comparator) {
        final int[] row = new int[1];
        final MetricsTableRowWriter rowWriter = new MetricsTableRowWriter(pageBuilder);
        collator.forEach(comparator, new MetricLocation.Closure() {;
            public void execute(MetricLocation location) {
                startNewPageIfRequired(row[0]);
                rowWriter.write(location, metricIds, collator);
                row[0]++;
            }
        });
        if (row[0] > 0) {
            closeTableFragment(collator.size() - 1);
        }
    }

    private void startNewPageIfRequired(int row) {
        if (row % rowsPerPage == 0) {
            try {
                startTableFragment(row);
            } catch (IOException ioex) {
                throw new RuntimeException("Failed to start a table fragment: " + ioex.getMessage());
            }
        }
    }

    private void startTableFragment(int row) throws IOException {
        if (row > 0) {
            closeTableFragment(row - 1);
        }

        int fragmentNumber = row / rowsPerPage + 1;
        pageBuilder.openPage(fileName + "." + fragmentNumber, "Order by " + orderItem).openElement("P").printLinkAndNewline("index", "Index").openElementAndNewline("P");

        createHistogramAndAcronymListTable();
        createTableFragmentLinks(row);
        openMetricsTable(fragmentNumber);
    }

    private void createHistogramAndAcronymListTable() {
        new PageHeaderWriter().write(pageBuilder, metricIds, imageFileName);
    }

    private void closeTableFragment(int row) {
        pageBuilder.closeElementAndNewline("TABLE");
        createTableFragmentLinks(row);
        pageBuilder.closePage();
    }

    private void createTableFragmentLinks(int row) {
        if (collator.size() <= rowsPerPage) {
            return;
        }

        pageBuilder.openElement("P").println("Pages:");
        createTableFragmentPageLinks(row);
        pageBuilder.openElementAndNewline("P");
    }

    private void createTableFragmentPageLinks(int row) {
        for (int i = 1; i <= collator.size() / rowsPerPage + 1; i++) {
            if (i == row / rowsPerPage + 1) {
                pageBuilder.println(Integer.toString(i));
            } else {
                pageBuilder.printLinkAndNewline(fileName + "." + i, Integer.toString(i));
            }
        }
    }

    private void openMetricsTable(int fragmentNumber) {
        pageBuilder.openElementAndNewline("TABLE border=\"1\" cellpadding=\"2\"").openElement("TR");
        new MetricsTableHeaderWriter().write(pageBuilder, fragmentNumber, level, metricIds, collator);
        pageBuilder.closeElementAndNewline("TR");
    }
}
