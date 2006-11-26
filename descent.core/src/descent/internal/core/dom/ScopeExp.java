package descent.internal.core.dom;

import descent.core.dom.IElement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IName;
import descent.core.dom.IScopeExpression;

public class ScopeExp extends Expression implements IScopeExpression {

	private final TemplateInstance tempinst;

	public ScopeExp(Loc loc, TemplateInstance tempinst) {
		this.tempinst = tempinst;
	}
	
	public int getElementType() {
		return SCOPE_EXPRESSION;
	}
	
	public IName getName() {
		return tempinst;
	}
	
	public IElement[] getArguments() {
		return tempinst.tiargs.toArray(new IElement[tempinst.tiargs.size()]);
	}
	
	@Override
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, tempinst);
			acceptChildren(visitor, tempinst.tiargs);
		}
		visitor.endVisit(this);
	}

}
