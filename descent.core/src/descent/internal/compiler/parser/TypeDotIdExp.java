package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class TypeDotIdExp extends Expression {

	public IdentifierExp ident;
	public Type resolvedType;

	public TypeDotIdExp(Loc loc, Type type, IdentifierExp ident) {
		super(loc, TOK.TOKtypedot);
		this.type = this.sourceType = type;
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
		
		// Save the expression, so that we can retrieve it's type for further use
		DotIdExp savedE = (DotIdExp) e;
		
		e = e.semantic(sc, context);
		
		resolvedType = savedE.e1.type;
		
		return e;
	}

	@Override
	public Expression syntaxCopy(SemanticContext context) {
		TypeDotIdExp te = new TypeDotIdExp(loc, type.syntaxCopy(context), ident);
		return te;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writeByte('(');
		type.toCBuffer(buf, null, hgs, context);
		buf.writeByte(')');
		buf.writeByte('.');
		buf.writestring(ident.toChars());
	}

}
