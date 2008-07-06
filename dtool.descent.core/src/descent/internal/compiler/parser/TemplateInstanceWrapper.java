package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TemplateInstanceWrapper extends IdentifierExp {

	public TemplateInstance tempinst;

	public TemplateInstanceWrapper(Loc loc, TemplateInstance tempinst) {
		super(loc);
		this.tempinst = tempinst;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, tempinst);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public DYNCAST dyncast() {
		return DYNCAST.DYNCAST_OBJECT;
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_INSTANCE_WRAPPER;
	}

}
