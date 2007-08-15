package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class TypePointer extends Type {
	
	public TypePointer(Type next) {
		super(TY.Tpointer, next);
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, next);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
	    Type n = next.semantic(loc, sc, context);
	    if (n != next) {
	    	deco = null;
	    }
	    next = n;
	    return merge(context);
	}
	
	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
	    e = new NullExp(Loc.ZERO);
	    e.type = this;
	    return e;
	}
	
	@Override
	public int getNodeType() {
		return TYPE_POINTER;
	}
	
	@Override
	public String toString() {
		return next + "*";
	}

}
