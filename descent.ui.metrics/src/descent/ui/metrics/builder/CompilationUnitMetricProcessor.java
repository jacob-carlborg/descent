package descent.ui.metrics.builder;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import descent.core.ICompilationUnit;

import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.calculators.CalculatorListener;
import descent.ui.metrics.location.MetricLocation;

public abstract class CompilationUnitMetricProcessor implements CalculatorListener {
    public void setUpForCompilationUnit(ICompilationUnit compilationUnit) throws CoreException {}
    public void complete(IProgressMonitor monitor) throws IOException {}
    
    public void noteTypeValue(String metricKey, MetricLocation location, int value) {
        if (MetricsPlugin.getDefault().isMetricEnabled(metricKey)) {
            noteEnabledTypeValue(metricKey, location, value);
        }
    }
    
    public void noteMethodValue(String metricKey, MetricLocation location, int value) {
        if (MetricsPlugin.getDefault().isMetricEnabled(metricKey)) {
            noteEnabledMethodValue(metricKey, location, value);
        }
    }
    
    protected abstract void noteEnabledTypeValue(String metricKey, MetricLocation location, int value);
    protected abstract void noteEnabledMethodValue(String metricKey, MetricLocation location, int value);

}

