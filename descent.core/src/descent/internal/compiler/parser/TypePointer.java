package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

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
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		buf.prependstring("*");
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars(context));
		}
		next.toCBuffer2(buf, ident, hgs, context);
	}

}
