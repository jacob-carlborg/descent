package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IDescentElement;
import descent.core.dom.IName;
import descent.core.dom.IScopeExpression;
import descent.core.domX.IASTVisitor;

public class ScopeExp extends Expression implements IScopeExpression {

	public final TemplateInstance tempinst;

	public ScopeExp(TemplateInstance tempinst) {
		this.tempinst = tempinst;
	}
	
	public int getElementType() {
		return ElementTypes.SCOPE_EXPRESSION;
	}
	
	public IName getName() {
		return tempinst;
	}
	
	public IDescentElement[] getArguments() {
		return tempinst.tiargs.toArray(new IDescentElement[tempinst.tiargs.size()]);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, tempinst);
			//TreeVisitor.acceptChildren(visitor, tempinst.tiargs);
		}
		visitor.endVisit(this);
	}

}
