package descent.ui.metrics.calculators;

import descent.core.dom.ASTNode;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.VariableDeclarationFragment;

public final class NumberOfFieldsCalculator extends AbstractASTVisitorCalculator {
    private static class Memento extends BaseMemento {
        private int numberOfFields;
    }

    public static final String METRIC_ID = CalculatorUtils.createTypeMetricId(NumberOfFieldsCalculator.class);

    private static final String[] METRIC_IDS = new String[]{METRIC_ID};

    private int numberOfFields;

    public String[] getMetricIds() {
        return NumberOfFieldsCalculator.METRIC_IDS;
    }

    protected BaseMemento createMemento() {
        Memento memento = new Memento();
        memento.numberOfFields = numberOfFields;
        return memento;
    }

    protected void restoreState(BaseMemento memento) {
        numberOfFields = ((Memento) memento).numberOfFields;
        super.restoreState(memento);
    }

    protected void handleEndOfType() {
        noteTypeValue(NumberOfFieldsCalculator.METRIC_ID, numberOfFields);
        super.handleEndOfType();
    }

    protected void handleStartOfType() {
        numberOfFields = 0;
        super.handleStartOfType();
    }

    public boolean visit(VariableDeclarationFragment arg0) {
    	VariableDeclaration var = (VariableDeclaration) arg0.getParent();
    	
    	ASTNode parent = var.getParent();
    	while(parent != null && parent.getNodeType() == ASTNode.MODIFIER_DECLARATION) {
    		parent = parent.getParent();
    	}
    	
    	if (parent != null && parent.getNodeType() == ASTNode.AGGREGATE_DECLARATION) {
    		if ((var.getModifiers() & Modifier.STATIC) == 0) {
                numberOfFields++;
            }
    	}

        return true;
    }
}
