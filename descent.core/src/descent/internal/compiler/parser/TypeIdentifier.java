package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public class TypeIdentifier extends TypeQualified {

	public IdentifierExp ident;

	public TypeIdentifier(IdentifierExp ident) {
		super(TY.Tident);
		this.ident = ident;
	}
	
	@Override
	public Expression toExpression() {
		Expression e = new IdentifierExp(ident.ident);
		e.setSourceRange(ident.start, ident.length);
		if (idents != null) {
			for(IdentifierExp id : idents) {
				e = new DotIdExp(e, id);
				e.setSourceRange(ident.start, id.start + id.length - ident.start);
			}
		}
		return e;
	}
	
	@Override
	public Type semantic(Scope sc, SemanticContext context) {
		Type[] t = { null };
	    Expression[] e = { null };
	    Dsymbol[] s = { null };

	    //printf("TypeIdentifier::semantic(%s)\n", toChars());
	    resolve(sc, e, t, s, context);
	    if (t[0] != null)
	    {
		//printf("\tit's a type %d, %s, %s\n", t.ty, t.toChars(), t.deco);

		if (t[0].ty == TY.Ttypedef)
		{   TypeTypedef tt = (TypeTypedef) t[0];

		    if (tt.sym.sem == 1) {
		    	context.acceptProblem(Problem.newSemanticTypeError("Circular reference of typedef " + tt.sym.ident, IProblem.CircularDefinition, 0, tt.sym.ident.start, tt.sym.ident.length));
		    }
		}
	    }
	    else
	    {
		if (s != null)
		{
			/* TODO semantic
		    s.error(loc, "is used as a type");
		    */
		}
		else {
			/* TODO semantic
		    error(loc, "%s is used as a type", toChars());
		    */
		}
		t[0] = tvoid;
	    }
	    //t.print();
	    return t[0];
	}
	
	@Override
	public Dsymbol toDsymbol(Scope sc, SemanticContext context) {
		Dsymbol s;
		Dsymbol[] scopesym = { null };

		// printf("TypeIdentifier::toDsymbol('%s')\n", toChars());
		if (sc == null)
			return null;
		// printf("ident = '%s'\n", ident.toChars());
		s = sc.search(ident, scopesym, context);
		if (s != null) {
			s = s.toAlias(context);
			if (idents != null) {
				for (IdentifierExp id : idents) {
					Dsymbol sm;
					// printf("\tid = '%s'\n", id.toChars());
					if (id.dyncast() != Identifier.DYNCAST_IDENTIFIER) {
						// It's a template instance
						// printf("\ttemplate instance id\n");
						TemplateDeclaration td;
						TemplateInstance ti = ((TemplateInstanceWrapper) id).tempinst;
						id = (IdentifierExp) ti.idents.get(0);
						sm = s.search(id, 0, context);
						if (sm == null) {
							/* TODO semantic
							error("template identifier %s is not a member of %s",
									id.toChars(), s.toChars());
							*/
							break;
						}
						sm = sm.toAlias(context);
						td = sm.isTemplateDeclaration();
						if (td == null) {
							/* TODO semantic
							error("%s is not a template", id.toChars());
							*/
							break;
						}
						ti.tempdecl = td;
						if (!ti.semanticdone)
							ti.semantic(sc, context);
						sm = ti.toAlias(context);
					} else
						sm = s.search(id, 0, context);
					s = sm;
	
					if (s == null) // failed to find a symbol
					{ // printf("\tdidn't find a symbol\n");
						break;
					}
					s = s.toAlias(context);
				}
			}
		}
		return s;
	}
	
	@Override
	public void resolve(Scope sc, Expression[] pe, Type[] pt, Dsymbol[] ps, SemanticContext context) {
		Dsymbol s;
	    Dsymbol[] scopesym = { null };

	    //printf("TypeIdentifier::resolve(sc = %p, idents = '%s')\n", sc, toChars());
	    s = sc.search(ident, scopesym, context);
	    resolveHelper(sc, s, scopesym[0], pe, pt, ps, context);
	}
	
	@Override
	public Type reliesOnTident() {
		return this;
	}

	@Override
	public int kind() {
		return TYPE_IDENTIFIER;
	}
	
	@Override
	public String toString() {
		return ident.toString();
	}

}
