package descent.internal.compiler.parser;

public class DeclarationExp extends Expression {

	public Dsymbol declaration;

	public DeclarationExp(Dsymbol declaration) {
		super(TOK.TOKdeclaration);
		this.declaration = declaration;
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		return 1;
	}

	@Override
	public int getNodeType() {
		return DECLARATION_EXP;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type != null) {
			return this;
		}

		/* This is here to support extern(linkage) declaration,
		 * where the extern(linkage) winds up being an AttribDeclaration
		 * wrapper.
		 */
		Dsymbol s = declaration;

		AttribDeclaration ad = declaration.isAttribDeclaration();
		if (ad != null) {
			if (ad.decl != null && ad.decl.size() == 1) {
				s = ad.decl.get(0);
			}
		}

		if (s.isVarDeclaration() != null) { // Do semantic() on initializer first, so:
			//	int a = a;
			// will be illegal.
			declaration.semantic(sc, context);
			s.parent = sc.parent;
		}

		// Insert into both local scope and function scope.
		// Must be unique in both.
		if (s.ident != null) {
			if (sc.insert(s) == null) {
				error("declaration %s is already defined", s.toPrettyChars());
			} else if (sc.func != null) {
				//VarDeclaration v = s.isVarDeclaration();
				if ((s.isFuncDeclaration() != null /*|| v && v.storage_class & STCstatic*/)
						&& sc.func.localsymtab.insert(s) == null) {
					error(
							"declaration %s is already defined in another scope in %s",
							s.toPrettyChars(), sc.func.toChars());
				} else if (!context.global.params.useDeprecated) { // Disallow shadowing

					for (Scope scx = sc.enclosing; scx != null
							&& scx.func == sc.func; scx = scx.enclosing) {
						Dsymbol s2;

						if (scx.scopesym != null
								&& scx.scopesym.symtab != null
								&& (s2 = scx.scopesym.symtab.lookup(s.ident)) != null
								&& s != s2) {
							error("shadowing declaration %s is deprecated", s
									.toPrettyChars());
						}
					}
				}
			}
		}
		if (s.isVarDeclaration() == null) {
			declaration.semantic(sc, context);
			s.parent = sc.parent;
		}
		if (context.global.errors == 0) {
			declaration.semantic2(sc, context);
			if (context.global.errors == 0) {
				declaration.semantic3(sc, context);

				if (context.global.errors == 0
						&& context.global.params.useInline) {
					declaration.inlineScan(context);
				}
			}
		}

		type = Type.tvoid;
		return this;
	}
	
	@Override
	public Expression syntaxCopy() {
		return new DeclarationExp(declaration.syntaxCopy(null));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		declaration.toCBuffer(buf, hgs, context);
	}

}
