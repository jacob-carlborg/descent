package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class DeclarationExp extends Expression {

	public Dsymbol declaration, sourceDeclaration;

	public DeclarationExp(Loc loc, Dsymbol declaration) {
		super(loc, TOK.TOKdeclaration);
		this.declaration = declaration;
		this.sourceDeclaration = declaration;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		return 1;
	}

	@Override
	public Expression doInline(InlineDoState ids) {
		DeclarationExp de = (DeclarationExp) copy();
		VarDeclaration vd;

		//printf("DeclarationExp::doInline(%s)\n", toChars());
		vd = declaration.isVarDeclaration();
		if (vd != null) {
			if (vd.isStatic() || vd.isConst()) {
				;
			} else {
				ExpInitializer ie;
				ExpInitializer ieto;
				VarDeclaration vto;

				vto = new VarDeclaration(vd.loc, vd.type, vd.ident, vd.init);
				vto = vd;
				vto.parent = ids.parent;
				//		    vto.csym = null;
				//		    vto.isym = null;

				ids.from.add(vd);
				ids.to.add(vto);

				if (vd.init.isVoidInitializer() != null) {
					vto.init = new VoidInitializer(vd.init.loc);
				} else {
					ie = vd.init.isExpInitializer();
					if (ie == null) {
						throw new IllegalStateException("assert(ie);");
					}
					ieto = new ExpInitializer(ie.loc, ie.exp.doInline(ids));
					vto.init = ieto;
				}
				de.declaration = vto;
			}
		}
		/* This needs work, like DeclarationExp::toElem(), if we are
		 * to handle TemplateMixin's. For now, we just don't inline them.
		 */
		return de;
	}

	@Override
	public int getNodeType() {
		return DECLARATION_EXP;
	}

	@Override
	public int inlineCost(InlineCostState ics, SemanticContext context) {
		int cost = 0;
		VarDeclaration vd;

		vd = declaration.isVarDeclaration();
		if (vd != null) {
			TupleDeclaration td = vd.toAlias(context).isTupleDeclaration();
			if (td != null) {
				return COST_MAX; // finish DeclarationExp::doInline
			}
			if (!ics.hdrscan && vd.isDataseg(context)) {
				return COST_MAX;
			}
			cost += 1;

			// Scan initializer (vd.init)
			if (vd.init != null) {
				ExpInitializer ie = vd.init.isExpInitializer();

				if (ie != null) {
					cost += ie.exp.inlineCost(ics, context);
				}
			}
		}

		// These can contain functions, which when copied, get output twice.
		if (declaration.isStructDeclaration() != null
				|| declaration.isClassDeclaration() != null
				|| declaration.isFuncDeclaration() != null
				|| declaration.isTypedefDeclaration() != null
				|| declaration.isTemplateMixin() != null) {
			return COST_MAX;
		}

		//printf("DeclarationExp::inlineCost('%s')\n", toChars());
		return cost;
	}

	@Override
	public Expression inlineScan(InlineScanState iss, SemanticContext context) {
		scanVar(declaration, iss, context);
		return this;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		Expression e = EXP_CANT_INTERPRET;
		VarDeclaration v = declaration.isVarDeclaration();
		if (v != null) {
			Dsymbol s = v.toAlias(context);
			if (s == v && !v.isStatic() && v.init != null) {
				ExpInitializer ie = v.init.isExpInitializer();
				if (ie != null) {
					e = ie.exp.interpret(istate, context);
				} else if (v.init.isVoidInitializer() != null) {
					e = null;
				}
			} else if (s == v && v.isConst() && v.init != null) {
				e = v.init.toExpression(context);
				if (null == e) {
					e = EXP_CANT_INTERPRET;
				} else if (null == e.type) {
					e.type = v.type;
				}
			}
		}
		return e;
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context) {
		declaration.parent = sc.parent;
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
				context.acceptProblem(Problem.newSyntaxError(IProblem.DeclarationIsAlreadyDefined, 0, s.start, s.length, new String[] { s.toChars(context) } ));
			} else if (sc.func != null) {
				//VarDeclaration v = s.isVarDeclaration();
				if ((s.isFuncDeclaration() != null /*|| v && v.storage_class & STCstatic*/)
						&& sc.func.localsymtab.insert(s) == null) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.DeclarationIsAlreadyDefinedInAnotherScope, this, new String[] { s.toPrettyChars(context), sc.func.toChars(context) }));
				} else if (!context.global.params.useDeprecated) { // Disallow shadowing

					for (Scope scx = sc.enclosing; scx != null
							&& scx.func == sc.func; scx = scx.enclosing) {
						Dsymbol s2;

						if (scx.scopesym != null
								&& scx.scopesym.symtab != null
								&& (s2 = scx.scopesym.symtab.lookup(s.ident)) != null
								&& s != s2) {
							context.acceptProblem(Problem.newSemanticTypeError(IProblem.ShadowingDeclarationIsDeprecated, this, new String[] { s.toPrettyChars(context) }));
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
		return new DeclarationExp(loc, declaration.syntaxCopy(null));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		declaration.toCBuffer(buf, hgs, context);
	}
	
	@Override
	public String toString() {
		return declaration.toString();
	}

}
