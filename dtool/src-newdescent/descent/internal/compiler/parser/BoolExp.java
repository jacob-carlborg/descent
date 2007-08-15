package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class BoolExp extends UnaExp {

	public BoolExp(Loc loc, Expression e, Type t) {
		super(loc, TOK.TOKtobool, e);
		this.type = t;
	}

	@Override
	public int getNodeType() {
		return 0;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public boolean isBit() {
		return true;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
	    e1 = resolveProperties(sc, e1, context);
	    e1 = e1.checkToBoolean(context);
	    type = Type.tboolean;
	    return this;
	}

}
