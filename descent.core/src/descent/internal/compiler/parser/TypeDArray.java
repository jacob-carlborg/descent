package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeDArray extends TypeArray {
	
	public TypeDArray(Type next) {
		super(TY.Tarray, next);
	}
	
	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
	    e = new NullExp(Loc.ZERO);
	    e.type = this;
	    return e;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, next);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public int getNodeType() {
		return TYPE_D_ARRAY;
	}
	
	@Override
	public void toPrettyBracket(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("[]");
	}

}
