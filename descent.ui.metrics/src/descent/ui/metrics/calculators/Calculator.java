package descent.ui.metrics.calculators;

import descent.core.ICompilationUnit;
import descent.core.JavaModelException;
import descent.core.dom.CompilationUnit;

public interface Calculator {
    void measure(ICompilationUnit unparsedCompilationUnit, CompilationUnit parsedCompilationUnit) throws JavaModelException;
    void setListener(CalculatorListener newListener);
    String[] getMetricIds();
}