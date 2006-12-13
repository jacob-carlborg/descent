package descent.internal.core.dom;

import descent.core.dom.IElement;
import descent.core.dom.IName;
import descent.core.dom.IScopeExpression;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class ScopeExp extends Expression implements IScopeExpression {

	private final TemplateInstance tempinst;

	public ScopeExp(TemplateInstance tempinst) {
		this.tempinst = tempinst;
	}
	
	public int getElementType() {
		return ElementTypes.SCOPE_EXPRESSION;
	}
	
	public IName getName() {
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
