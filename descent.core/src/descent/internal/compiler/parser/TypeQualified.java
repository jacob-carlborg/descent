package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.DYNCAST.DYNCAST_DSYMBOL;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;


public abstract class TypeQualified extends Type {

	public int lineNumber;
	public char[] filename;
	public Identifiers idents = new Identifiers(2);

	public TypeQualified(char[] filename, int lineNumber, TY ty) {
		super(ty);
		this.filename = filename;
		this.lineNumber = lineNumber;
	}

	public void addIdent(IdentifierExp ident) {
		idents.add(ident);
	}

	public void resolveHelper(char[] filename, int lineNumber, Scope sc, Dsymbol s,
			Dsymbol scopesym, Expression[] pe, Type[] pt, Dsymbol[] ps, SemanticContext context) {
		VarDeclaration v;
		EnumMember em;
		// TupleDeclaration td;
		Type t = null;
		Expression e = null;

		pe[0] = null;
		pt[0] = null;
		ps[0] = null;
		if (s != null) {
			s.checkDeprecated(sc, context, this); // TODO check "this" for reference
			s = s.toAlias(context);

			if (idents != null) {
				for (IdentifierExp id : idents) {
					Dsymbol sm;

					if (id.dyncast() != DYNCAST.DYNCAST_IDENTIFIER) {
						// It's a template instance
						TemplateDeclaration td;
						TemplateInstance ti = ((TemplateInstanceWrapper) id).tempinst;
						id = ti.name;
						sm = s.search(filename, lineNumber, id, 0, context);
						
						// Descent: for binding resolution
						context.setResolvedSymbol(id, sm);
						
						if (sm == null) {
							if (context.acceptsErrors()) {
								context.acceptProblem(Problem.newSemanticTypeError(
										IProblem.NotAMember, id, new String[] { new String(
												id.ident) }));
							}
							return;
						}
						sm = sm.toAlias(context);
						td = sm.isTemplateDeclaration();
						if (td == null) {
							if (context.acceptsErrors()) {
								context.acceptProblem(Problem.newSemanticTypeError(
										IProblem.SymbolIsNotATemplate, this, new String[] { id.toChars() }));
							}
							return;
						}
						ti.tempdecl = td;
						if (0 == ti.semanticdone) {
							ti.semantic(sc, context);
						}
						sm = ti.toAlias(context);
					} else {
						// Added for Descent
						if (s == null && context.global.errors > 0) {
							return;
						}
						
						sm = s.search(filename, lineNumber, id, 0, context);
						
						// Descent: for binding resolution
						context.setResolvedSymbol(id, sm);
					}
					
					if (sm == null) {
						v = s.isVarDeclaration();
						if (v != null
								&& equals(id, Id.length)) {
							if (v.isConst()
									&& v.getExpInitializer(context) != null) {
								e = v.getExpInitializer(context).exp;
							} else {
								e = new VarExp(filename, lineNumber, v);
							}
							t = e.type;
							if (t == null) {
								// goto Lerror;
								resolveHelper_Lerror(id, context);
								return;
							}
							resolveHelper_L3(sc, pe, e, context);
							return;
						}
						t = s.getType(context);
						
						if (t == null && s.isDeclaration() != null) {
							t = s.isDeclaration().type;
						}
						if (t != null) {
							sm = t.toDsymbol(sc, context);
							if (sm != null) {
								sm = sm.search(filename, lineNumber, id, 0, context);
								if (sm != null) {
									// goto L2;
									s = sm.toAlias(context);
									continue;
								}
							}
							// e = t.getProperty(filename, lineNumber, id, context);
						    e = new TypeExp(filename, lineNumber, t);
						    e = t.dotExp(sc, e, id, context);
							resolveHelper_L3(sc, pe, e, context);
						} else {
							// Lerror:
							resolveHelper_Lerror(id, context);
							return;
						}
						return;
					}
					// L2:
					s = sm.toAlias(context);
				}
			}

			v = s.isVarDeclaration();
			if (v != null) {
				// It's not a type, it's an expression
				if (v.isConst() && v.getExpInitializer(context) != null) {
					ExpInitializer ei = v.getExpInitializer(context);
					Assert.isNotNull(ei);
					pe[0] = ei.exp.copy(); // make copy so we can change loc
				} else {
					pe[0] = new VarExp(filename, lineNumber, v);
				}
				return;
			}
			em = s.isEnumMember();
			if (em != null) {
				// It's not a type, it's an expression
				pe[0] = em.value().copy();
				return;
			}

			resolveHelper_L1_plus_end(sc, s, scopesym, pe, pt, ps, e, t,
					context);
			return;
		}
		if (s == null) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.UndefinedIdentifier, this,
						new String[] { this.toString() }));
			}
		}
	}

	public void resolveHelper_L1_plus_end(Scope sc, Dsymbol s,
			Dsymbol scopesym, Expression[] pe, Type[] pt, Dsymbol[] ps,
			Expression e, Type t, SemanticContext context) {
		t = s.getType(context);
		if (t == null) {
			// If the symbol is an import, try looking inside the import
			Import si;

			si = s.isImport();
			if (si != null) {
				s = si.search(filename, lineNumber, s.ident, 0, context);
				if (s != null && s != si) {
					// goto L1
					resolveHelper_L1_plus_end(sc, s, scopesym, pe, pt, ps, e,
							t, context);
					return;
				}
				s = si;
			}
			ps[0] = s;
			return;
		}
		if (t.ty == TY.Tinstance && t != this && t.deco == null) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.ForwardReferenceToSymbol, this, new String[] { t.toChars(context) }));
			}
			return;
		}

		if (!same(t, this, context)) {
			if (t.reliesOnTident() != null) {
				Scope scx;

				for (scx = sc; true; scx = scx.enclosing) {
					if (scx == null) {
						if (context.acceptsErrors()) {
							context.acceptProblem(Problem.newSemanticTypeError(
									IProblem.ForwardReferenceToSymbol, this, new String[] { t.toChars(context) }));
						}
						return;
					}
					if (scx.scopesym == scopesym) {
						break;
					}
				}
				t = t.semantic(filename, lineNumber, scx, context);
				// ((TypeIdentifier )t).resolve(filename, lineNumber, scx, pe, &t, ps);
			}
		}
		if (t.ty == TY.Ttuple) {
			pt[0] = t;
		} else {
			pt[0] = t.merge(context);
		}
		if (s == null) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.UndefinedIdentifier, this, new String[] { toChars(context) }));
			}
		}
	}

	public void resolveHelper_L3(Scope sc, Expression[] pe, Expression e,
			SemanticContext context) {
		for (IdentifierExp id : idents) {
			e = e.type.dotExp(sc, e, id, context);
		}
		pe[0] = e;
	}

	public void resolveHelper_Lerror(IdentifierExp id, SemanticContext context) {
		if (context.acceptsErrors()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.IdentifierOfSymbolIsNotDefined, this, new String[] { id.toChars(), toChars(context) }));
		}
	}

	@Override
	public int size(char[] filename, int lineNumber, SemanticContext context) {
		if (context.acceptsErrors()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.SizeOfTypeIsNotKnown, this, new String[] { toChars(context) }));
		}
		return 1;
	}

	public void syntaxCopyHelper(TypeQualified t, SemanticContext context) {
		if (idents != null && t.idents != null) {
			idents.setDim(t.idents.size());
			for (int i = 0; i < idents.size(); i++) {
				IdentifierExp id = t.idents.get(i);
				if (id.dyncast() == DYNCAST_DSYMBOL) {
					TemplateInstance ti = ((TemplateInstanceWrapper) id).tempinst;
	
					ti = (TemplateInstance) ti.syntaxCopy(null, context);
					id = new TemplateInstanceWrapper(null, 0, ti);
				}
				idents.set(i, id);
			}
		}
	}

	@Override
	public void toCBuffer2(OutBuffer buf, HdrGenState hgs, int mod, SemanticContext context) {
		int i;

		for (i = 0; i < idents.size(); i++) {
			IdentifierExp id = idents.get(i);

			buf.writeByte('.');

			if (id.dyncast() == DYNCAST.DYNCAST_DSYMBOL) {
				TemplateInstanceWrapper ti = (TemplateInstanceWrapper) id;
				ti.tempinst.toCBuffer(buf, hgs, context);
			} else {
				buf.writestring(id.toChars());
			}
		}
	}

	public void toCBuffer2Helper(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		int i;

		if (idents != null) {
			for (i = 0; i < idents.size(); i++) {
				IdentifierExp id = idents.get(i);
	
				buf.writeByte('.');
	
				if (id.dyncast() == DYNCAST_DSYMBOL) {
					TemplateInstance ti = ((TemplateInstanceWrapper) id).tempinst;
					ti.toCBuffer(buf, hgs, context);
				} else {
					buf.writestring(id.toChars());
				}
			}
		}
	}
	
	@Override
	public int getLineNumber() {
		return lineNumber;
	}
	
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

}
