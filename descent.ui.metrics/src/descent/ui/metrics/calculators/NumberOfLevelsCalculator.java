package descent.ui.metrics.calculators;

import descent.core.dom.AsmBlock;
import descent.core.dom.DebugStatement;
import descent.core.dom.DoStatement;
import descent.core.dom.ForStatement;
import descent.core.dom.ForeachRangeStatement;
import descent.core.dom.ForeachStatement;
import descent.core.dom.IfStatement;
import descent.core.dom.IftypeStatement;
import descent.core.dom.ScopeStatement;
import descent.core.dom.StaticIfStatement;
import descent.core.dom.SwitchStatement;
import descent.core.dom.TryStatement;
import descent.core.dom.VersionStatement;
import descent.core.dom.WhileStatement;
import descent.core.dom.WithStatement;

public final class NumberOfLevelsCalculator extends AbstractASTVisitorCalculator {
    private static class Memento extends BaseMemento {
        private int levels;
        private int maxLevelsInMethod;
        private int maxLevelsInClass;
    }

    public static final String METHOD_METRIC_ID = CalculatorUtils.createMethodMetricId(NumberOfLevelsCalculator.class);
    public static final String TYPE_METRIC_ID = CalculatorUtils.createTypeMetricId(NumberOfLevelsCalculator.class);

    private static final String[] METRIC_IDS = new String[]{METHOD_METRIC_ID, TYPE_METRIC_ID};

    private int levels;
    private int maxLevelsInMethod;
    private int maxLevelsInClass;

    public String[] getMetricIds() {
        return NumberOfLevelsCalculator.METRIC_IDS;
    }

    protected void handleNestedClass(BaseMemento inner) {
        increaseLevels(((Memento) inner).maxLevelsInClass);
        decreaseLevels(((Memento) inner).maxLevelsInClass);
    }

    public AbstractASTVisitorCalculator.BaseMemento createMemento() {
        Memento memento = new Memento();
        memento.levels = levels;
        memento.maxLevelsInMethod = maxLevelsInMethod;
        memento.maxLevelsInClass = maxLevelsInClass;

        return memento;
    }

    protected void restoreState(BaseMemento memento) {
        Memento myMemento = (Memento) memento;
        levels = myMemento.levels;
        maxLevelsInClass = myMemento.maxLevelsInClass;
        maxLevelsInMethod = myMemento.maxLevelsInMethod;

        super.restoreState(memento);
    }

    protected void handleStartOfType() {
        maxLevelsInClass = 1;
        super.handleStartOfType();
    }

    protected void handleEndOfType() {
        noteTypeValue(NumberOfLevelsCalculator.TYPE_METRIC_ID, maxLevelsInClass);
        super.handleEndOfType();
    }
    
    protected void handleStartOfMethod() {
        maxLevelsInMethod = 1;
        super.handleStartOfMethod();
    }

    protected void handleEndOfMethod() {
        noteMethodValue(NumberOfLevelsCalculator.METHOD_METRIC_ID, maxLevelsInMethod);
        super.handleEndOfMethod();
    }

    /*
    public boolean visit(FunctionDeclaration arg0) {
        levels = 1;
        maxLevelsInMethod = 1;
        return super.visit(arg0);
    }

    public void endVisit(FunctionDeclaration arg0) {
        noteMethodValue(NumberOfLevelsCalculator.METHOD_METRIC_ID, maxLevelsInMethod);
        super.endVisit(arg0);
    }
    */
    
    @Override
    public void endVisit(AsmBlock node) {
    	decreaseLevels(1);
    	super.endVisit(node);
    }
    
    @Override
    public void endVisit(DebugStatement node) {
    	decreaseLevels(1);
    	super.endVisit(node);
    }

    public void endVisit(DoStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }

    public void endVisit(ForStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }
    
    public void endVisit(ForeachStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }
    
    public void endVisit(ForeachRangeStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }
    
    @Override
    public void endVisit(IftypeStatement node) {
    	decreaseLevels(1);
    	super.endVisit(node);
    }
    
    public void endVisit(IfStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }
    
    public void endVisit(ScopeStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }
    
    public void endVisit(StaticIfStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }

    public void endVisit(SwitchStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }

    public void endVisit(TryStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }
    
    public void endVisit(VersionStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }

    public void endVisit(WhileStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }
    
    public void endVisit(WithStatement arg0) {
        decreaseLevels(1);
        super.endVisit(arg0);
    }
    
    public boolean visit(AsmBlock arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }
    
    public boolean visit(DebugStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }

    public boolean visit(DoStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }

    public boolean visit(ForStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }
    
    public boolean visit(ForeachStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }
    
    public boolean visit(ForeachRangeStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }

    public boolean visit(IfStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }
    
    public boolean visit(IftypeStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }
    
    public boolean visit(ScopeStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }
    
    public boolean visit(StaticIfStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }

    public boolean visit(SwitchStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }

    public boolean visit(TryStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }
    
    public boolean visit(VersionStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }

    public boolean visit(WhileStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }
    
    public boolean visit(WithStatement arg0) {
        increaseLevels(1);
        return super.visit(arg0);
    }

    private void increaseLevels(int delta) {
        levels += delta;
        maxLevelsInMethod = Math.max(levels, maxLevelsInMethod);
        maxLevelsInClass = Math.max(levels, maxLevelsInClass);
    }

    private void decreaseLevels(int delta) {
        levels -= delta;
    }
}