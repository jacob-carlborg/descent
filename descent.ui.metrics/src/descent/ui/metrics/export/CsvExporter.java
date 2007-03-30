package descent.ui.metrics.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.collator.MetricsCollator;
import descent.ui.metrics.location.LocationComparator;
import descent.ui.metrics.location.MetricLocation;
import descent.ui.metrics.location.NamedLineNumber;

public final class CsvExporter implements Exporter {
    private File directory;

    public CsvExporter(File exportDirectory) {
        directory = exportDirectory;
    }

    public void export(MetricsCollator typeMetricsCollator, MetricsCollator methodMetricsCollator, IProgressMonitor monitor) throws IOException {
        monitor.beginTask("Exporting CSV", 2);
        writeTypeMetrics(typeMetricsCollator, monitor);
        writeMethodMetrics(methodMetricsCollator, monitor);
    }

    private void writeTypeMetrics(MetricsCollator collator, IProgressMonitor monitor) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(new File(directory, "types.csv")));
        String[] metricIds = collator.getMetricIds();
        writeTypeMetricHeader(writer, metricIds);
        writeMetricsRows(writer, metricIds, collator);
        writer.close();

        monitor.worked(1);
    }

    private void writeMethodMetrics(MetricsCollator collator, IProgressMonitor monitor) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(new File(directory, "methods.csv")));
        String[] metricIds = collator.getMetricIds();
        writeMethodMetricHeader(writer, metricIds);
        writeMetricsRows(writer, metricIds, collator);
        writer.close();

        monitor.worked(1);
    }

    private void writeTypeMetricHeader(PrintWriter writer, String[] metricIds) {
        writer.print("PACKAGE,TYPE,LINE");
        writeMetricNamesHeaderFragment(writer, metricIds);
    }

    private void writeMethodMetricHeader(PrintWriter writer, String[] metricIds) {
        writer.print("PACKAGE,TYPE,METHOD,LINE");
        writeMetricNamesHeaderFragment(writer, metricIds);
    }

    private void writeMetricNamesHeaderFragment(PrintWriter writer, String[] metricIds) {
        for (int i = 0; i < metricIds.length; i++) {
            writer.print(',');
            writer.print(MetricsPlugin.getMetricShortPresentationName(metricIds[i]));
        }
        writer.println();
    }

    private void writeMetricsRows(final PrintWriter writer, final String[] metricIds, final MetricsCollator collator) {
        collator.forEach(new LocationComparator() {
            protected int compareParticular(MetricLocation l1, MetricLocation l2) {
                return 0;
            }
        }, new MetricLocation.Closure() {
            public void execute(MetricLocation location) {
                writeMetricsRow(writer, metricIds, collator, location);
            }
        });
    }

    private void writeMetricsRow(PrintWriter writer, String[] metricIds, MetricsCollator collator, MetricLocation location) {
        writeRowPreamble(writer, location);
        writeMetrics(writer, metricIds, collator, location);
        writer.println();
    }

    private void writeRowPreamble(PrintWriter writer, MetricLocation location) {
        writePackageAndType(writer, location);
        if (location.hasMethod()) {
            writeMethodInfo(writer, location.getMethodInfo());
        }
    }

    private void writeMethodInfo(PrintWriter writer, NamedLineNumber methodInfo) {
        writer.print(',');
        writer.print(methodInfo.getName().substring(0, methodInfo.getName().indexOf('(')));
        writer.print(',');
        writer.print(methodInfo.getLineNumber());
    }

    private void writePackageAndType(PrintWriter writer, MetricLocation location) {
        writer.print(location.getPackageName());
        writer.print(',');
        writer.print(location.getTypeInfo().getName());
        if (!location.hasMethod()) {
            writer.print(',');
            writer.print(location.getTypeInfo().getLineNumber());
        }
    }

    private void writeMetrics(PrintWriter writer, String[] metricIds, MetricsCollator collator, MetricLocation location) {
        for (int i = 0; i < metricIds.length; i++) {
            writer.print(',');
            if (collator.hasMetric(location, metricIds[i])) {
                writer.print(collator.getMetric(location, metricIds[i]).getValue());
            }
        }
    }
}
