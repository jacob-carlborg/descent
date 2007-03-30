package descent.ui.metrics.calculators;

import descent.core.dom.ASTNode;
import descent.core.dom.ConstructorDeclaration;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.ModifierDeclaration;

public final class NumberOfParametersCalculator extends AbstractASTVisitorCalculator {
    public static final String METRIC_ID = CalculatorUtils.createMethodMetricId(NumberOfParametersCalculator.class);

    private static final String[] METRIC_IDS = new String[]{METRIC_ID};

    public String[] getMetricIds() {
        return NumberOfParametersCalculator.METRIC_IDS;
    }

    public void endVisit(FunctionDeclaration arg0) {
    	if ((arg0.getModifiers() & Modifier.EXTERN) == 0) {
    		ASTNode parent = arg0.getParent();
    		while(parent != null && (parent.getNodeType() == ASTNode.MODIFIER_DECLARATION || parent.getNodeType() == ASTNode.EXTERN_DECLARATION)) {
    			if (parent.getNodeType() == ASTNode.EXTERN_DECLARATION) {
    				super.endVisit(arg0);
    				return;
    			}
    			
    			ModifierDeclaration modifierDeclaration = (ModifierDeclaration) parent;
    			Modifier modifier = modifierDeclaration.getModifier();
    			if (modifier.getModifierKeyword() == Modifier.ModifierKeyword.EXTERN_KEYWORD
    					|| (modifierDeclaration.getModifiers() & Modifier.EXTERN) != 0) {
    				super.endVisit(arg0);
    				return;
    			}
    			parent = parent.getParent();
    		}
    		
    		noteMethodValue(NumberOfParametersCalculator.METRIC_ID, arg0.arguments().size());
    	}    	
        
        super.endVisit(arg0);
    }
    
    public void endVisit(ConstructorDeclaration arg0) {
        noteMethodValue(NumberOfParametersCalculator.METRIC_ID, arg0.arguments().size());
        super.endVisit(arg0);
    }

    protected boolean isInterfaceCalculator() {
        return true;
    }
}
