package descent.internal.compiler.parser;

/*
 * Identifier + IdentifierExp
 */
public class IdentifierExp extends Expression {
	
	public Identifier ident;
	
	public IdentifierExp() {
		super(TOK.TOKidentifier);
	}
	
	public IdentifierExp(Identifier ident) {
		this();
		this.ident = ident;
	}
	
	public IdentifierExp(Token token) {
		this(token.ident);
		this.start = token.ptr;
		this.length = token.len;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Dsymbol s;
		Dsymbol[] scopesym = { null };

		s = sc.search(this, scopesym, context);
		if (s != null) {
			Expression e;
			WithScopeSymbol withsym;

			// See if it was a with class
			withsym = scopesym[0].isWithScopeSymbol();
			if (withsym != null) {
				s = s.toAlias(context);

				// Same as wthis.ident
				if (s.needThis() || s.isTemplateDeclaration() != null) {
					e = new VarExp(withsym.withstate.wthis);
					e = new DotIdExp(e, this);
				} else {
					Type t = withsym.withstate.wthis.type;
					if (t.ty == TY.Tpointer) {
						t = t.next;
					}
					e = new TypeDotIdExp(t, this);
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
						e = new TemplateExp(tempdecl);
						e = e.semantic(sc, context);
						return e;
					}
				}
				e = new DsymbolExp(s);
			}
			return e.semantic(sc, context);
		}
		error("undefined identifier %s", this.toChars());
		type = Type.terror;
		return this;
	}
	
	public boolean dyncast() {
		return Identifier.DYNCAST_IDENTIFIER;
	}
	
	@Override
	public int getNodeType() {
		return IDENTIFIER_EXP;
	}
	
	@Override
	public String toString() {
		return ident.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IdentifierExp)) {
			return false;
		}
		
		IdentifierExp i = (IdentifierExp) o;
		return ident.equals(i.ident);
	}

}
