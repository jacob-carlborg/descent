package descent.internal.compiler.parser;

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
					if (id.ident.dyncast() != Identifier.DYNCAST_IDENTIFIER) {
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
	public int kind() {
		return TYPE_IDENTIFIER;
	}
	
	@Override
	public String toString() {
		return ident.toString();
	}

}
