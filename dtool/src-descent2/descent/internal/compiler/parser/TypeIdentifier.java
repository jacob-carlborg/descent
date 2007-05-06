package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public class TypeIdentifier extends TypeQualified {

	public IdentifierExp ident;

	public TypeIdentifier(Loc loc, IdentifierExp ident) {
		super(loc, TY.Tident);
		this.ident = ident;
	}
	
	public TypeIdentifier(Loc loc, Identifier ident) {
		this(loc, new IdentifierExp(loc, ident));
	}
	
	@Override
	public Expression toExpression() {
		Expression e = new IdentifierExp(loc, ident.ident);
		e.setSourceRange(ident.start, ident.length);
		if (idents != null) {
			for(IdentifierExp id : idents) {
				e = new DotIdExp(loc, e, id);
				e.setSourceRange(ident.start, id.start + id.length - ident.start);
			}
		}
		return e;
	}
	
	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		Type[] t = { null };
		Expression[] e = { null };
		Dsymbol[] s = { null };

		// printf("TypeIdentifier::semantic(%s)\n", toChars());
		resolve(loc, sc, e, t, s, context);
		if (t[0] != null) {
			// printf("\tit's a type %d, %s, %s\n", t.ty, t.toChars(), t.deco);

			if (t[0].ty == TY.Ttypedef) {
				TypeTypedef tt = (TypeTypedef) t[0];

				if (tt.sym.sem == 1) {
					context.acceptProblem(Problem.newSemanticTypeError(
							"Circular reference of typedef " + tt.sym.ident,
							IProblem.CircularDefinition, 0, tt.sym.ident.start,
							tt.sym.ident.length));
				}
			}
		} else {
			if (s[0] != null) {
				// TODO semantic remove the following if but leave the body
				if (s[0].ident.ident != Id.Object) {
					context.acceptProblem(Problem.newSemanticTypeError(s[0].ident
							+ " cannot be resolved to a type", IProblem.UsedAsAType, 0,
							s[0].ident.start, s[0].ident.length));
				}
			} else {
				// TODO semantic remove the following if but leave the body
				if (ident.ident != Id.Object) {
					context.acceptProblem(Problem.newSemanticTypeError(this.ident
							+ " cannot be resolved to a type", IProblem.UsedAsAType, 0,
							this.ident.start, this.ident.length));
				}
			}
			// TODO see if this change is ok, I change it to error to get
			// just one error on things like "Clazz x;" where "Clazz is not defined".
			// (otherwise I get "voids have no value" also).
			t[0] = terror;
		}
		return t[0];
	}
	
	@Override
	public Dsymbol toDsymbol(Scope sc, SemanticContext context) {
		Dsymbol s;
		Dsymbol[] scopesym = { null };

		if (sc == null)
			return null;
		s = sc.search(loc, ident, scopesym, context);
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
							error("template identifier %s is not a member of %s", id.toChars(), s.toChars());
							break;
						}
						sm = sm.toAlias(context);
						td = sm.isTemplateDeclaration();
						if (td == null) {
							error("%s is not a template", id.toChars());
							break;
						}
						ti.tempdecl = td;
						if (!ti.semanticdone)
							ti.semantic(sc, context);
						sm = ti.toAlias(context);
					} else
						sm = s.search(loc, id, 0, context);
					s = sm;

					if (s == null) // failed to find a symbol
					{
						break;
					}
					s = s.toAlias(context);
				}
			}
		}
		return s;
	}
	
	@Override
	public void resolve(Loc loc, Scope sc, Expression[] pe, Type[] pt, Dsymbol[] ps, SemanticContext context) {
		Dsymbol s;
	    Dsymbol[] scopesym = { null };

	    //printf("TypeIdentifier::resolve(sc = %p, idents = '%s')\n", sc, toChars());
	    s = sc.search(loc, ident, scopesym, context);
	    resolveHelper(sc, s, scopesym[0], pe, pt, ps, context);
	}
	
	@Override
	public Type reliesOnTident() {
		return this;
	}

	@Override
	public int getNodeType() {
		return TYPE_IDENTIFIER;
	}
	
	@Override
	public String toString() {
		return ident.toString();
	}

}