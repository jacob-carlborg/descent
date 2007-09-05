package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TypeDotIdExp extends Expression {

	public IdentifierExp ident;

	public TypeDotIdExp(Loc loc, Type type, IdentifierExp ident) {
		super(loc, TOK.TOKtypedot);
		this.type = type;
		this.ident = ident;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return TYPE_DOT_ID_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;

		e = new DotIdExp(loc, new TypeExp(loc, type), ident);
		e = e.semantic(sc, context);
		return e;
	}

	@Override
	public Expression syntaxCopy() {
		TypeDotIdExp te = new TypeDotIdExp(loc, type.syntaxCopy(), ident);
		return te;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writeByte('(');
		type.toCBuffer(buf, null, hgs, context);
		buf.writeByte(')');
		buf.writeByte('.');
		buf.writestring(ident.toChars(context));
	}

}
