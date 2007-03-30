package descent.ui.metrics.calculators;

import descent.core.dom.ConstructorDeclaration;
import descent.core.dom.FunctionDeclaration;

public final class LinesOfCodeCalculator extends AbstractASTVisitorCalculator {
    public static final String METHOD_METRIC_ID = CalculatorUtils.createMethodMetricId(LinesOfCodeCalculator.class);

    private static final String[] METRIC_IDS = new String[]{METHOD_METRIC_ID};

    public String[] getMetricIds() {
        return LinesOfCodeCalculator.METRIC_IDS;
    }

    public boolean visit(FunctionDeclaration arg0) {
        super.visit(arg0);
        if ((arg0.getModifiers()) == 0) {
            noteMethodValue(LinesOfCodeCalculator.METHOD_METRIC_ID, getEndLineNumber(arg0) - getStartLineNumber(arg0) + 1);
            return super.visit(arg0);
        } else {
            return false;
        }
    }
    
    public boolean visit(ConstructorDeclaration arg0) {
        super.visit(arg0);
        if ((arg0.getModifiers()) == 0) {
            noteMethodValue(LinesOfCodeCalculator.METHOD_METRIC_ID, getEndLineNumber(arg0) - getStartLineNumber(arg0) + 1);
            return super.visit(arg0);
        } else {
            return false;
        }
    }
    
}
