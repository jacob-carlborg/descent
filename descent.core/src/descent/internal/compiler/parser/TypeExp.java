package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TypeExp extends Expression {

	public TypeExp(Loc loc, Type type) {
		super(loc, TOK.TOKtype);
		this.type = type;
		this.sourceType = type;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public int getNodeType() {
		return TYPE_EXP;
	}

	
	@Override
	public Expression optimize(int result, SemanticContext context) {
		return this;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		type = type.semantic(loc, sc, context);
	    return this;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		type.toCBuffer(buf, null, hgs, context);
	}
	
	@Override
	public void appendBinding(StringBuilder sb) {
		type.appendBinding(sb);
	}

}
