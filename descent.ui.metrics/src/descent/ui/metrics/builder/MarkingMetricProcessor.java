package descent.ui.metrics.builder;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import descent.core.ICompilationUnit;

import descent.ui.metrics.MetricsBuilder;
import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.location.MetricLocation;
import descent.ui.metrics.location.NamedLineNumber;

public class MarkingMetricProcessor extends CompilationUnitMetricProcessor {
    private ICompilationUnit compilationUnit;

    public void setUpForCompilationUnit(ICompilationUnit compilationUnit) throws CoreException {
        this.compilationUnit = compilationUnit;
        super.setUpForCompilationUnit(compilationUnit);
        compilationUnit.getCorrespondingResource().deleteMarkers(MetricsBuilder.MARKER_ID, true, IResource.DEPTH_ZERO);
    }
    
    protected void noteEnabledMethodValue(String metricKey, MetricLocation location, int value) {
        createMarkerIfRequired(metricKey, location.getMethodInfo(), value);
    }

    protected void noteEnabledTypeValue(String metricKey, MetricLocation location, int value) {
        createMarkerIfRequired(metricKey, location.getTypeInfo(), value);
    }

    private void createMarkerIfRequired(String metricKey, NamedLineNumber lineNumberInfo, int value) {
        int preferredUpperBound = MetricsPlugin.getDefault().getMaximumPreferredValue(metricKey);
        if (value > preferredUpperBound) {
            createMarker(metricKey, lineNumberInfo, value);
        }
    }

    private void createMarker(String metricKey, NamedLineNumber lineNumberInfo, int value) {
        String message = new MessageFormat(MetricsPlugin.getTaskListMetricViolationMessage(metricKey)).format(new Integer[]{new Integer(value)});
        createMarker(lineNumberInfo, message);
    }

    private void createMarker(NamedLineNumber lineNumberInfo, String comment) {
        try {
            IMarker marker = compilationUnit.getCorrespondingResource().createMarker(MetricsBuilder.MARKER_ID);
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumberInfo.getLineNumber());
            marker.setAttribute(IMarker.MESSAGE, comment);
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        } catch (CoreException cex) {
            MetricsPlugin.getDefault().getLog().log(cex.getStatus());
        }
    }
}
