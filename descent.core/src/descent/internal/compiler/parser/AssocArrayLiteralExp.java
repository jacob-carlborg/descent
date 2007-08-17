package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// TODO semantic
public class AssocArrayLiteralExp extends Expression {
	
	public List<Expression> keys;
	public List<Expression> values;

	public AssocArrayLiteralExp(Loc loc, List<Expression> keys, List<Expression> values) {
		super(loc, TOK.TOKassocarrayliteral);
		this.keys = keys;
		this.values = values;
	}

	@Override
	public int getNodeType() {
		return ASSOC_ARRAY_LITERAL_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, keys);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

}
