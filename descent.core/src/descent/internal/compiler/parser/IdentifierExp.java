package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

/*
 * Identifier + IdentifierExp
 */
public class IdentifierExp extends Expression {

	public char[] ident;

	public IdentifierExp(Loc loc) {
		super(loc, TOK.TOKidentifier);
	}
	
	public IdentifierExp(Loc loc, IdentifierExp ident) {
		this(loc);
		this.ident = ident.ident;
		this.start = ident.start;
		this.length = ident.length;
	}
	
	public IdentifierExp(Loc loc, Identifier ident) {
		this(loc);
		this.ident = ident.string;
		this.start = ident.startPosition;
		this.length = ident.length;
	}

	public IdentifierExp(Loc loc, Token token) {
		this(loc);
		this.ident = token.string;
		this.start = token.ptr;
		this.length = token.len;
	}
	
	public IdentifierExp(Loc loc, char[] ident) {
		this(loc);
		this.ident = ident;
	}
	
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
		return ident.equals(i.ident);
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
							&& (ti.idents.get(ti.idents.size()).ident == f.ident.ident || ti
									.toAlias(context).ident == f.ident)
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
			}
			return e.semantic(sc, context);
		}
		context.acceptProblem(Problem.newSemanticTypeError(IProblem.UndefinedIdentifier, 0,
				start, length, new String[] { new String(ident) }));
		type = Type.terror;
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		/* TODO semantic
		if (hgs.hdrgen) {
			buf.writestring(ident.toHChars2());
		} else {
			buf.writestring(ident.toChars());
		}
		*/
	}

	@Override
	public String toChars() {
		return new String(ident).intern();
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		return this;
	}

	@Override
	public String toString() {
		return new String(ident);
	}
	
	@Override
	public char[] toCharArray() {
		return ident;
	}

}
