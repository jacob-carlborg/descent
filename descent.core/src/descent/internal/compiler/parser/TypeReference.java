package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Treference;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeReference extends Type {

	public TypeReference(Type t) {
		super(Treference, t);
		if (t.ty == Tbit) {
			error("cannot make reference to a bit");
		}
	}

	@Override
	public Type syntaxCopy() {
		Type t = next.syntaxCopy();
		if (t == next)
			t = this;
		else
			t = new TypeReference(t);
		return t;
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		return PTRSIZE;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		buf.prependstring("&");
		if (ident != null) {
			buf.writestring(ident.toChars());
		}
		next.toCBuffer2(buf, null, hgs, context);
	}
	
	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident, SemanticContext context) {
		// References just forward things along
	    return next.dotExp(sc, e, ident, context);
	}
	
	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
	    e = new NullExp(Loc.ZERO);
	    e.type = this;
	    return e;
	}
	
	@Override
	public boolean isZeroInit(SemanticContext context) {
		return true;
	}

	@Override
	public int getNodeType() {
		return TYPE_REFERENCE;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
	}

}
