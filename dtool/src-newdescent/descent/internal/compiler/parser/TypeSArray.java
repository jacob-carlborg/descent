package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class TypeSArray extends Type {
	
	public Expression dim;

	public TypeSArray(Type next, Expression dim) {
		super(TY.Tsarray, next);
		this.dim = dim;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, next);
			TreeVisitor.acceptChildren(visitor, dim);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Expression defaultInit(SemanticContext context) {
		return next.defaultInit(context);
	}
	
	@Override
	public Expression toExpression() {
		Expression e = next.toExpression();
		if (e != null) {
			List<Expression> arguments = new ArrayList<Expression>(1);
			arguments.add(dim);
			e = new ArrayExp(dim.loc, e, arguments);
			e.setSourceRange(start, length);
		}
		return e;
	}
	
	@Override
	public int getNodeType() {
		return TYPE_S_ARRAY;
	}

}
