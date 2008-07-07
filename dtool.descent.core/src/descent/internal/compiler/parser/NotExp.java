package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class NotExp extends UnaExp {

	public NotExp(Loc loc, Expression e1) {
		super(loc, TOK.TOKnot, e1);
	}
	
	@Override
	public int getNodeType() {
		return NOT_EXP;
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
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretCommon(istate, op, context);
	}

}