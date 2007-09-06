package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeSlice extends Type {

	public Expression lwr;
	public Expression upr;

	public TypeSlice(Type next, Expression lwr, Expression upr) {
		super(TY.Tslice, next);
		this.lwr = lwr;
		this.upr = upr;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, lwr);
			TreeVisitor.acceptChildren(visitor, upr);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return TYPE_SLICE;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		OutBuffer buf2 = new OutBuffer();

		buf2.writestring("[");
		buf2.writestring(lwr.toChars(context));
		buf2.writestring(" .. ");
		buf2.writestring(upr.toChars(context));
		buf2.writestring("]");

		buf.prependstring(buf2.toChars());
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
		next.toCBuffer2(buf, null, hgs, context);
	}

}
