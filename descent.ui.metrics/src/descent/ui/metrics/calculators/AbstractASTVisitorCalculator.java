package descent.ui.metrics.calculators;

import java.util.List;
import java.util.Stack;

import descent.core.ICompilationUnit;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTVisitor;
import descent.core.dom.AbstractFunctionDeclaration;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.Argument;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ConstructorDeclaration;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.Statement;
import descent.ui.metrics.location.MetricLocation;
import descent.ui.metrics.location.NamedLineNumber;

public abstract class AbstractASTVisitorCalculator extends ASTVisitor implements Calculator {
    protected static class BaseMemento {
        private ASTNode typeOrMethodNode;
    }

    private CompilationUnit parsedCompilationUnit;
    private CalculatorListener listener;
    private Stack enclosingTypeOrMethodStack;

    private String packageName;
    private ASTNode typeOrMethodNode;
    
    public AbstractASTVisitorCalculator() {
        enclosingTypeOrMethodStack = new Stack();
    }

    public void measure(ICompilationUnit unparsed, CompilationUnit parsed) {
        parsedCompilationUnit = parsed;
        parsedCompilationUnit.accept(this);
        parsedCompilationUnit = null;
    }

    public void setListener(CalculatorListener newListener) {
        listener = newListener;
    }
    
    private boolean isMethod(ASTNode node) {
    	if (node == null) {
    		return false;
    	}
    	return node.getNodeType() == ASTNode.FUNCTION_DECLARATION 
    		|| node.getNodeType() == ASTNode.CONSTRUCTOR_DECLARATION;    	
    }

    protected boolean isInMethod() {
        return isMethod(typeOrMethodNode);
    }
    
    protected boolean isInTypeOrMethod() {
    	return typeOrMethodNode != null;
    }

    private AbstractFunctionDeclaration getMethodNode() {
        if (!isInMethod()) {
            throw new IllegalStateException("Not in a method");
        }

        return (AbstractFunctionDeclaration) typeOrMethodNode;
    }

    private int getMethodLineNumber() {
        return getStartLineNumber(getMethodNode());
    }

    private ASTNode getTypeOrMethodNode() {
       	return typeOrMethodNode;
    }

    /*
    protected ITypeBinding getTypeNodeBinding() {
        ASTNode node = getTypeNode();
        return (node.getNodeType() == ASTNode.TYPE_DECLARATION) ? ((TypeDeclaration) node).resolveBinding() : ((AnonymousClassDeclaration) node).resolveBinding();
    }
    */

    private int getTypeOrMethodLineNumber() {
        return getStartLineNumber(getTypeOrMethodNode());
    }

    protected void handleNestedClass(BaseMemento inner) {
    }
    
    protected void handleNestedMethod(BaseMemento inner) {
    }

    protected void noteMethodValue(String metricId, int value) {
        MetricLocation location = MetricLocation.findOrCreate(packageName, new NamedLineNumber(getTypeOrMethodName(), getTypeOrMethodLineNumber()), new NamedLineNumber(getMethodName(), getMethodLineNumber()));
        listener.noteMethodValue(metricId, location, value);
    }

    protected void noteTypeValue(String metricId, int value) {
        MetricLocation location = MetricLocation.findOrCreate(packageName, new NamedLineNumber(getTypeOrMethodName(), getTypeOrMethodLineNumber()));
        listener.noteTypeValue(metricId, location, value);
    }
    
    private String getConstructorName(ConstructorDeclaration c) {
    	switch(c.getKind()) {
    	case CONSTRUCTOR: return "this";
    	case DELETE: return "delete";
    	case DESTRUCTOR: return "~this";
    	case NEW: return "new";
    	case STATIC_CONSTRUCTOR: return "static this";
    	case STATIC_DESTRUCTOR: return "static ~this";
    	}
    	return null;
    }
    
    private String getAggregateName(AggregateDeclaration a) {
    	if (a.getName() == null) {
    		return "(anonymous)";
    	} else {
    		return a.getName().getIdentifier();
    	}
    }

    private String getMethodName() {
        if (!isInMethod()) {
            throw new IllegalStateException("Not in a method");
        }
        StringBuffer sb = new StringBuffer(64);
        AbstractFunctionDeclaration abstractFunc = getMethodNode();
        if (abstractFunc.isConstructor()) {
        	ConstructorDeclaration c = (ConstructorDeclaration) abstractFunc;
        	sb.append(getConstructorName(c)).append("(");
        } else {
        	FunctionDeclaration f = (FunctionDeclaration) abstractFunc;
        	sb.append(f.getName().getFullyQualifiedName()).append("(");
        }
        appendMethodParameterList(sb);
        sb.append(')');

        return sb.toString();
    }

    private void appendMethodParameterList(StringBuffer sb) {
        List parameters = getMethodNode().arguments();
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(getQualifiedTypeName((Argument) parameters.get(i)));
        }
    }

    private String getQualifiedTypeName(Argument arg) {
    	return arg.getType().toString();
    	/*
        ITypeBinding type = declaration.getType().resolveBinding();
        if (type == null) {
            return "?";
        } else {
            return type.getQualifiedName();
        }
    	*/
    }

    private String getTypeOrMethodName() {
        return getTypeOrMethodName(getTypeOrMethodNode());
    }

    private String getTypeOrMethodName(ASTNode fromTypeNode) {
        if (fromTypeNode.getNodeType() == ASTNode.AGGREGATE_DECLARATION) {
            return getTypeOrMethodNameAppendDot(fromTypeNode.getParent()) + getAggregateName(((AggregateDeclaration) fromTypeNode));
        } else if (fromTypeNode.getNodeType() == ASTNode.FUNCTION_DECLARATION) {
            return getTypeOrMethodNameAppendDot(fromTypeNode.getParent()) + ((FunctionDeclaration) fromTypeNode).getName().toString();
        } else if (fromTypeNode.getNodeType() == ASTNode.CONSTRUCTOR_DECLARATION) {
            return getTypeOrMethodNameAppendDot(fromTypeNode.getParent()) + getConstructorName(((ConstructorDeclaration) fromTypeNode));
        } else {
            return getTypeOrMethodName(fromTypeNode.getParent());
        }
    }

    private String getTypeOrMethodNameAppendDot(ASTNode fromTypeNode) {
        if (fromTypeNode == null) {
            return "";
        } else if (fromTypeNode.getNodeType() == ASTNode.AGGREGATE_DECLARATION) {
            return getTypeOrMethodNameAppendDot(fromTypeNode.getParent()) + getAggregateName(((AggregateDeclaration) fromTypeNode)) + ".";
        } else if (fromTypeNode.getNodeType() == ASTNode.FUNCTION_DECLARATION) {
            return getTypeOrMethodNameAppendDot(fromTypeNode.getParent()) + ((FunctionDeclaration) fromTypeNode).getName().toString() + ".";
        } else if (fromTypeNode.getNodeType() == ASTNode.CONSTRUCTOR_DECLARATION) {
            return getTypeOrMethodNameAppendDot(fromTypeNode.getParent()) + getConstructorName(((ConstructorDeclaration) fromTypeNode)) + ".";
        } else {
            return getTypeOrMethodNameAppendDot(fromTypeNode.getParent());
        }
    }

    protected int getStartLineNumber(ASTNode node) {
    	// To exclude ddocs
    	if (node instanceof AbstractFunctionDeclaration) {
    		AbstractFunctionDeclaration a = (AbstractFunctionDeclaration) node;
    		if (a.getNodeType() == ASTNode.FUNCTION_DECLARATION) {
    			FunctionDeclaration func = (FunctionDeclaration) a;
    			if (func.getName() != null && func.getName().getStartPosition() != -1) {
    				return parsedCompilationUnit.getLineNumber(func.getName().getStartPosition());
    			}
    		}
    		Statement body = a.getBody();
    		if (body.getStartPosition() != - 1) {
    			return parsedCompilationUnit.getLineNumber(a.getBody().getStartPosition());
    		}
    	}
    	
        return parsedCompilationUnit.getLineNumber(node.getStartPosition());
    }

    protected int getEndLineNumber(ASTNode node) {
        return parsedCompilationUnit.getLineNumber(node.getStartPosition() + node.getLength());
    }

    private void pushState() {
        enclosingTypeOrMethodStack.push(getMemento());
    }

    private BaseMemento popState() {
        return (BaseMemento) enclosingTypeOrMethodStack.pop();
    }

    private BaseMemento getMemento() {
        BaseMemento memento = createMemento();
        memento.typeOrMethodNode = typeOrMethodNode;

        return memento;
    }

    protected BaseMemento createMemento() {
        return new BaseMemento();
    }

    private void clearState() {
        typeOrMethodNode = null;
    }

    protected void restoreState(BaseMemento memento) {
        typeOrMethodNode = memento.typeOrMethodNode;
    }

    public boolean visit(CompilationUnit node) {
        packageName = "(default)";
        return super.visit(node);
    }

    public boolean visit(ModuleDeclaration node) {
        packageName = node.getName().getFullyQualifiedName();
        return super.visit(node);
    }

    public boolean visit(AggregateDeclaration node) {
        if (node.getKind() == AggregateDeclaration.Kind.INTERFACE && !isInterfaceCalculator()) {
            return false;
        }

        handleStartOfInnerType();

        typeOrMethodNode = node;
        handleStartOfType();
        return super.visit(node);
    }

    private void handleStartOfInnerType() {
        if (isInTypeOrMethod()) {
            pushState();
            clearState();
        }
    }

    public void endVisit(AggregateDeclaration node) {
        if (node.getKind() == AggregateDeclaration.Kind.INTERFACE && !isInterfaceCalculator()) {
            return;
        }

        handleEndOfType();
        if (!enclosingTypeOrMethodStack.isEmpty()) {
            handleEndOfInnerType();
        } else {
            typeOrMethodNode = null;
        }
        super.endVisit(node);
    }

    private void handleEndOfInnerType() {
        BaseMemento currentState = createMemento();
        restoreState(popState());
        handleNestedClass(currentState);
    }

    protected boolean isInterfaceCalculator() {
        return false;
    }

    protected void handleStartOfType() {
    }

    protected void handleEndOfType() {
    }
    
    protected void handleStartOfMethod() {
    }

    protected void handleEndOfMethod() {
    }

    public boolean visit(FunctionDeclaration methodDeclarationNode) {
    	handleStartOfInnerMethod();

        typeOrMethodNode = methodDeclarationNode;
        handleStartOfMethod();
        return super.visit(methodDeclarationNode);
    }
    
    public boolean visit(ConstructorDeclaration methodDeclarationNode) {
    	handleStartOfInnerMethod();

        typeOrMethodNode = methodDeclarationNode;
        handleStartOfMethod();
        return super.visit(methodDeclarationNode);
    }
    
    private void handleStartOfInnerMethod() {
        if (isInTypeOrMethod()) {
            pushState();
            clearState();
        }
    }

    public void endVisit(FunctionDeclaration methodDeclarationNode) {
    	handleEndOfMethod();
        if (!enclosingTypeOrMethodStack.isEmpty()) {
            handleEndOfInnerMethod();
        } else {
            typeOrMethodNode = null;
        }
        super.endVisit(methodDeclarationNode);
    }

    public void endVisit(ConstructorDeclaration methodDeclarationNode) {
    	handleEndOfMethod();
        if (!enclosingTypeOrMethodStack.isEmpty()) {
            handleEndOfInnerMethod();
        } else {
            typeOrMethodNode = null;
        }
        super.endVisit(methodDeclarationNode);
    }
    
    private void handleEndOfInnerMethod() {
        BaseMemento currentState = createMemento();
        restoreState(popState());
        handleNestedMethod(currentState);
    }
    
}
