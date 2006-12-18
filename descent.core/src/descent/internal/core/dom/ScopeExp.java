package descent.internal.core.dom;

import descent.core.dom.IElement;
import descent.core.dom.ASTVisitor;
import descent.core.dom.ISimpleName;
import descent.core.dom.IScopeExpression;

public class ScopeExp extends Expression implements IScopeExpression {

	private final TemplateInstance tempinst;

	public ScopeExp(AST ast, TemplateInstance tempinst) {
		super(ast);
		this.tempinst = tempinst;
	}
	
	public int getNodeType0() {
		return SCOPE_EXPRESSION;
	}
	
	public ISimpleName getName() {
		return tempinst;
	}
	
	public IElement[] getArguments() {
		return tempinst.tiargs.toArray(new IElement[tempinst.tiargs.size()]);
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, tempinst);
			acceptChildren(visitor, tempinst.tiargs);
		}
		visitor.endVisit(this);
	}

}
