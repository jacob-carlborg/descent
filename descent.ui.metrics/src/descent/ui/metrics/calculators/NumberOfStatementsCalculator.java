package descent.ui.metrics.calculators;

import descent.core.dom.AsmBlock;
import descent.core.dom.AsmStatement;
import descent.core.dom.BreakStatement;
import descent.core.dom.CatchClause;
import descent.core.dom.ContinueStatement;
import descent.core.dom.DebugStatement;
import descent.core.dom.DeclarationStatement;
import descent.core.dom.DoStatement;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.ForStatement;
import descent.core.dom.ForeachStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.GotoCaseStatement;
import descent.core.dom.GotoDefaultStatement;
import descent.core.dom.GotoStatement;
import descent.core.dom.IfStatement;
import descent.core.dom.PragmaStatement;
import descent.core.dom.ReturnStatement;
import descent.core.dom.ScopeStatement;
import descent.core.dom.StaticAssertStatement;
import descent.core.dom.StaticIfStatement;
import descent.core.dom.SwitchCase;
import descent.core.dom.ThrowStatement;
import descent.core.dom.TryStatement;
import descent.core.dom.VersionStatement;
import descent.core.dom.WhileStatement;
import descent.core.dom.WithStatement;

public final class NumberOfStatementsCalculator extends AbstractASTVisitorCalculator {
    private class Memento extends BaseMemento {
        private int statementCount;
        private int classStatementCount;
    }

    public static final String METRIC_ID = CalculatorUtils.createMethodMetricId(NumberOfStatementsCalculator.class);

    private static final String[] METRIC_IDS = new String[]{METRIC_ID};

    private int statementCount;
    private int classStatementCount;

    public String[] getMetricIds() {
        return NumberOfStatementsCalculator.METRIC_IDS;
    }

    protected BaseMemento createMemento() {
        Memento memento = new Memento();
        memento.statementCount = statementCount;
        memento.classStatementCount = classStatementCount;

        return memento;
    }

    protected void restoreState(BaseMemento memento) {
        statementCount = ((Memento) memento).statementCount;
        classStatementCount = ((Memento) memento).classStatementCount;
        super.restoreState(memento);
    }

    protected void handleNestedClass(BaseMemento inner) {
        super.handleNestedClass(inner);
        increaseStatementCount(((Memento) inner).classStatementCount);
    }

    protected void handleStartOfType() {
        classStatementCount = 0;
        super.handleStartOfType();
    }

    public boolean visit(FunctionDeclaration arg0) {
        statementCount = 0;
        return super.visit(arg0);
    }

    public void endVisit(FunctionDeclaration arg0) {
        noteMethodValue(NumberOfStatementsCalculator.METRIC_ID, statementCount);
        super.endVisit(arg0);
    }

    private void increaseStatementCount(int amount) {
        statementCount += amount;
        classStatementCount += amount;
    }

    private void incrementStatementCount() {
        increaseStatementCount(1);
    }
    
    @Override
    public boolean visit(AsmStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }

    public boolean visit(BreakStatement arg0) {
        incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(DebugStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(StaticIfStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(VersionStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(AsmBlock arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }

    public boolean visit(CatchClause arg0) {
        incrementStatementCount();
        return super.visit(arg0);
    }

    public boolean visit(ContinueStatement arg0) {
        incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(DeclarationStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }

    public boolean visit(DoStatement arg0) {
        incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(ExpressionStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }

    public boolean visit(ForStatement arg0) {
        incrementStatementCount();
        
        // I don't want to get into the initializer
        if (arg0.getBody() != null) {
        	arg0.getBody().accept(this);
        }
        
        return false;
    }

    public boolean visit(ForeachStatement arg0) {
        incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(ScopeStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(StaticAssertStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(PragmaStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(GotoCaseStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(GotoDefaultStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(GotoStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }
    
    public boolean visit(IfStatement arg0) {
        incrementStatementCount();
        return super.visit(arg0);
    }

    public boolean visit(ReturnStatement arg0) {
        incrementStatementCount();
        return super.visit(arg0);
    }

    public boolean visit(SwitchCase arg0) {
        incrementStatementCount();
        return super.visit(arg0);
    }

    public boolean visit(ThrowStatement arg0) {
        incrementStatementCount();
        return super.visit(arg0);
    }

    public boolean visit(TryStatement arg0) {
        incrementStatementCount();
        if (arg0.getFinally() != null) {
            incrementStatementCount();
        }
        return super.visit(arg0);
    }
    
    @Override
    public boolean visit(WithStatement arg0) {
    	incrementStatementCount();
        return super.visit(arg0);
    }

    public boolean visit(WhileStatement arg0) {
        incrementStatementCount();
        return super.visit(arg0);
    }
}
