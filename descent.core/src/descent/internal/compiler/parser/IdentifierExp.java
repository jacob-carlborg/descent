package descent.internal.compiler.parser;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class IdentifierExp extends Expression {
	
	public static int count;
	public static char[] generateId(char[] id) {
		StringBuilder s = new StringBuilder();
		s.append(id);
		s.append(count);
		count++;
		if (count < 0) {
			count = 0;
		}
		return s.toString().toCharArray();
	}

	public char[] ident;

	public IdentifierExp(Loc loc) {
		super(loc, TOK.TOKidentifier);
	}
	
	public IdentifierExp(char[] ident) {
		this(Loc.ZERO, ident);
	}

	public IdentifierExp(Loc loc, char[] ident) {
		this(loc);
		this.ident = ident;
	}

	public IdentifierExp(Loc loc, IdentifierExp ident) {
		this(loc);
		this.ident = ident.ident;
		this.start = ident.start;
		this.length = ident.length;
	}

	public IdentifierExp(Loc loc, Token token) {
		this(loc);
		this.ident = token.sourceString;
		this.start = token.ptr;
		this.length = token.sourceLen;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public DYNCAST dyncast() {
		return DYNCAST.DYNCAST_IDENTIFIER;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IdentifierExp)) {
			return false;
		}

		IdentifierExp i = (IdentifierExp) o;
		return CharOperation.equals(ident, i.ident);
	}

	@Override
	public int getNodeType() {
		return IDENTIFIER_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Dsymbol s;
		Dsymbol[] scopesym = { null };

		s = sc.search(loc, this, scopesym, context);
		if (s != null) {
			Expression e;
			WithScopeSymbol withsym;

			// See if it was a with class
			withsym = scopesym[0].isWithScopeSymbol();
			if (withsym != null) {
				s = s.toAlias(context);

				// Same as wthis.ident
				if (s.needThis() || s.isTemplateDeclaration() != null) {
					e = new VarExp(loc, withsym.withstate.wthis);
					e = new DotIdExp(loc, e, this);
				} else {
					Type t = withsym.withstate.wthis.type;
					if (t.ty == TY.Tpointer) {
						t = t.next;
					}
					e = new TypeDotIdExp(loc, t, this);
				}
			} else {
				if (s.parent == null
						&& scopesym[0].isArrayScopeSymbol() != null) { // Kludge
					// to
					// run
					// semantic()
					// here
					// because
					// ArrayScopeSymbol::search() doesn't have access to sc.
					s.semantic(sc, context);
				}
				// Look to see if f is really a function template
				FuncDeclaration f = s.isFuncDeclaration();
				if (f != null && f.parent != null) {
					TemplateInstance ti = f.parent.isTemplateInstance();

					if (ti != null
							&& ti.isTemplateMixin() == null
							&& (ti.name.equals(f.ident) || 
									ti.toAlias(context).ident == f.ident)
							&& ti.tempdecl != null
							&& ti.tempdecl.onemember != null) {
						TemplateDeclaration tempdecl = ti.tempdecl;
						if (tempdecl.overroot != null) { // if not start of
							// overloaded list of
							// TemplateDeclaration's
							tempdecl = tempdecl.overroot; // then get the
							// start
						}
						e = new TemplateExp(loc, tempdecl);
						e = e.semantic(sc, context);
						return e;
					}
				}
				e = new DsymbolExp(loc, s);
				e.start = start;
				e.length = length;
			}
			return e.semantic(sc, context);
		}
		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.UndefinedIdentifier, 0, start, length,
				new String[] { new String(ident) }));
		type = Type.terror;
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (hgs.hdrgen) {
			buf.writestring(toHChars2());
		} else {
			buf.writestring(ident);
		}
	}
	
	private final static char[] notThis = { '~', 't', 'h', 'i', 's' };
	private final static char[] invariant = { 'i', 'n', 'v', 'a', 'r', 'i', 'a', 'n', 't' };
	private final static char[] unittest = { 'u', 'n', 'i', 't', 't', 'e', 's', 't' };
	private final static char[] staticThis = { 's', 't', 'a', 't', 'i', 'c', ' ', 't', 'h', 'i', 's' };
	private final static char[] staticNotThis = { 's', 't', 'a', 't', 'i', 'c', ' ', '~', 't', 'h', 'i', 's' };
	private final static char[] dollar = { '$' };
	private final static char[] with = { 'w', 'i', 't', 'h' };
	private final static char[] result = { 'r', 'e', 's', 'u', 'l', 't' };
	private final static char[] Return = { 'r', 'e', 't', 'u', 'r', 'n' };

	public char[] toHChars2() {
		char[] p = null;

	    if (CharOperation.equals(ident, Id.ctor)) {
			p = Id.This;
		} else if (CharOperation.equals(ident, Id.dtor)) {
			p = notThis;
		} else if (CharOperation.equals(ident, Id.classInvariant)) {
			p = invariant;
		} else if (CharOperation.equals(ident, Id.unitTest)) {
			p = unittest;
		} else if (CharOperation.equals(ident, Id.staticCtor)) {
			p = staticThis;
		} else if (CharOperation.equals(ident, Id.staticDtor)) {
			p = staticNotThis;
		} else if (CharOperation.equals(ident, Id.dollar)) {
			p = dollar;
		} else if (CharOperation.equals(ident, Id.withSym)) {
			p = with;
		} else if (CharOperation.equals(ident, Id.result)) {
			p = result;
		} else if (CharOperation.equals(ident, Id.returnLabel)) {
			p = Return;
		} else {
			p = ident;
		}

	    return p;
	}

	@Override
	public char[] toCharArray() {
		return ident;
	}
	
	public String toChars() {
		return new String(ident).intern();
	}

	@Override
	public String toChars(SemanticContext context) {
		return toChars();
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		return this;
	}

	@Override
	public String toString() {
		return new String(ident);
	}

}
