package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class ArrayExp extends UnaExp {

	public List<Expression> arguments;

	public ArrayExp(Loc loc, Expression e, List<Expression> arguments) {
		super(loc, TOK.TOKarray, e);
		this.arguments = arguments;
	}
	
	@Override
	public int getNodeType() {
		return ARRAY_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}

}
