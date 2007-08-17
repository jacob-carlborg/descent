package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeExp extends Expression {

	public TypeExp(Loc loc, Type type) {
		super(loc, TOK.TOKtype);
		this.type = type;
	}
	
	@Override
	public int getNodeType() {
		return TYPE_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}

	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		type = type.semantic(loc, sc, context);
	    return this;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		type.toCBuffer(buf, null, hgs);
	}

}
