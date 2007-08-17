package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class DotTypeExp extends UnaExp {
	
	public Dsymbol sym;

	public DotTypeExp(Loc loc, Expression e, Dsymbol s) {
		super(loc, TOK.TOKdottype, e);
		this.sym = s;
		this.type = s.getType();
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
	public Expression semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
	    return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
	    buf.writeByte('.');
	    buf.writestring(sym.toChars());
	}

}
