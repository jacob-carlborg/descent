package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;

public abstract class TypeQualified extends Type {
	
	public Loc loc;
	public List<IdentifierExp> idents;
	
	public TypeQualified(Loc loc, TY ty) {
		super(ty, null);
		this.loc = loc;
	}

	public void addIdent(IdentifierExp ident) {
		if (idents == null) {
			idents = new ArrayList<IdentifierExp>();
		}
		idents.add(ident);
	}
	
	public void resolveHelper(Scope sc, Dsymbol s, Dsymbol scopesym, Expression[] pe, Type[] pt, Dsymbol[] ps, SemanticContext context) {
		VarDeclaration v;
		EnumMember em;
		// TupleDeclaration td;
		Type t = null;
		Expression e = null;

		pe[0] = null;
		pt[0] = null;
		ps[0] = null;
		if (s != null) {
			s = s.toAlias(context);
			
			if (idents != null) {
				for (IdentifierExp id : idents) {
					Dsymbol sm;
	
					if (id.dyncast() != DYNCAST.DYNCAST_IDENTIFIER) {
						// It's a template instance
						// printf("\ttemplate instance id\n");
						TemplateDeclaration td;
						TemplateInstance ti = ((TemplateInstanceWrapper) id).tempinst;
						id = (IdentifierExp) ti.idents.get(0);
						sm = s.search(loc, id, 0, context);
						if (sm == null) {
							context.acceptProblem(Problem.newSemanticTypeError("Template identifier " + id + " is not a member of this module", IProblem.NotAMember, 0, id.start, id.length));
							return;
						}
						sm = sm.toAlias(context);
						td = sm.isTemplateDeclaration();
						if (td == null) {
							error("%s is not a template", id.toChars());
							return;
						}
						ti.tempdecl = td;
						if (!ti.semanticdone) {
							ti.semantic(sc, context);
						}
						sm = ti.toAlias(context);
					} else
						sm = s.search(loc, id, 0, context);
					// printf("\t3: s = '%s' %p, kind = '%s'\n",s.toChars(), s,
					// s.kind());
					// printf("getType = '%s'\n", s.getType().toChars());
					if (sm == null) {
						v = s.isVarDeclaration();
						if (v != null && id.ident == Id.length) {
							if (v.isConst() && v.getExpInitializer(context) != null) {
								e = v.getExpInitializer(context).exp;
							} else
								e = new VarExp(loc, v);
							t = e.type;
							if (t == null) {
								// goto Lerror;
								resolveHelper_Lerror(id);
								return;
							}
							resolveHelper_L3(sc, pe, e, context);
							return;
						}
						t = s.getType();
						if (t == null && s.isDeclaration() != null)
							t = s.isDeclaration().type;
						if (t != null) {
							sm = t.toDsymbol(sc, context);
							if (sm != null) {
								sm = sm.search(loc, id, 0, context);
								if (sm != null) {
									// goto L2;
									s = sm.toAlias(context);
									continue;
								}
							}
							e = t.getProperty(loc, id.ident, context);
							resolveHelper_L3(sc, pe, e, context);
						} else {
							// Lerror:
							resolveHelper_Lerror(id);
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
					pe[0] = new VarExp(loc, v);
				}
				return;
			}
			em = s.isEnumMember();
			if (em != null) {
				// It's not a type, it's an expression
				pe[0] = em.value.copy();
				return;
			}
			
			resolveHelper_L1_plus_end(sc, s, scopesym, pe, pt, ps, e, t, context);
			return;
		}
		if (s == null) {
			// TODO semantic remove if and leave body
			if (!toString().equals("Object")) {
				context.acceptProblem(Problem.newSemanticTypeError("Identifier '" + this + "' is not defined", IProblem.UndefinedIdentifier, 0, start, length));
			}
		}
	}
	
	public void resolveHelper_L1_plus_end(Scope sc, Dsymbol s, Dsymbol scopesym, Expression[] pe, Type[] pt, Dsymbol[] ps, Expression e, Type t, SemanticContext context) {
		t = s.getType();
		if (t == null) {
			// If the symbol is an import, try looking inside the import
			Import si;

			si = s.isImport();
			if (si != null) {
				s = si.search(loc, s.ident, 0, context);
				if (s != null && s != si) {
					// goto L1
					resolveHelper_L1_plus_end(sc, s, scopesym, pe, pt, ps, e, t, context);
					return;
				}
				s = si;
			}
			ps[0] = s;
			return;
		}
		if (t.ty == TY.Tinstance && t != this && t.deco == null) {
			error("forward reference to '%s'", t.toChars());
			return;
		}

		if (t != this) {
			if (t.reliesOnTident() != null) {
				Scope scx;

				for (scx = sc; true; scx = scx.enclosing) {
					if (scx == null) {
						error("forward reference to '%s'", t.toChars());
						return;
					}
					if (scx.scopesym == scopesym)
						break;
				}
				t = t.semantic(loc, scx, context);
				// ((TypeIdentifier )t).resolve(loc, scx, pe, &t, ps);
			}
		}
		if (t.ty == TY.Ttuple)
			pt[0] = t;
		else
			pt[0] = t.merge(context);
		if (s == null) {
			error("identifier '%s' is not defined", toChars());
		}
	}
	
	public void resolveHelper_L3(Scope sc, Expression[] pe, Expression e, SemanticContext context) {
		for(IdentifierExp id : idents) {
			e = e.type.dotExp(sc, e, id, context);
	    }
	    pe[0] = e;
	}
	
	public void resolveHelper_Lerror(IdentifierExp id) {
		/* TODO semantic
	    error("identifier '%s' of '%s' is not defined", id.toChars(), toChars());
	    */
	}

}