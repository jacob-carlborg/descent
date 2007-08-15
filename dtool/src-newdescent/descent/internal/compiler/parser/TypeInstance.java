package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class TypeInstance extends TypeQualified {

	public TemplateInstance tempinst;
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, tempinst);
		}
		visitor.endVisit(this);
	}

	public TypeInstance(Loc loc, TemplateInstance tempinst) {
		super(loc, TY.Tinstance);
		this.tempinst = tempinst;
	}
	
	@Override
	public int getNodeType() {
		return TYPE_INSTANCE;
	}

}
