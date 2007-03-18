package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public abstract class Dsymbol extends ASTNode {
	
	public Dsymbol parent;
	public IdentifierExp ident;
	
	public Dsymbol() {
	}
	
	public Dsymbol(IdentifierExp ident) {
		this.ident = ident;
	}
	
	public Type getType() {
		return null;
	}
	
	public boolean isDeprecated() {
		return false;
	}
	
	public boolean isAnonymous() {
		return ident == null;
	}
	
	public TemplateDeclaration isTemplateDeclaration() {
		return null;
	}
	
	public Import isImport() {
		return null;
	}
	
	public Declaration isDeclaration() {
		return null;
	}
	
	public AnonymousAggregateDeclaration isAnonymousAggregateDeclaration() {
		return null;
	}
	
	public TupleDeclaration isTupleDeclaration() {
		return null;
	}
	
	public AggregateDeclaration isAggregateDeclaration() {
		return null;	
	}
	
	public EnumDeclaration isEnumDeclaration() {
		return null;
	}
	
	public EnumMember isEnumMember() {
		return null;
	}
	
	public FuncDeclaration isFuncDeclaration() {
		return null;
	}
	
	public ClassDeclaration isClassDeclaration() {
		return null;
	}
	
	public InterfaceDeclaration isInterfaceDeclaration() {
		return null;
	}
	
	public StructDeclaration isStructDeclaration() {
		return null;
	}
	
	public TemplateInstance isTemplateInstance() {
		return null;
	}
	
	public TemplateMixin isTemplateMixin() {
		return null;
	}
	
	public VarDeclaration isVarDeclaration() {
		return null;
	}
	
	public FuncAliasDeclaration isFuncAliasDeclaration() {
		return null;
	}
	
	public Module isModule() {
		return null;
	}
	
	public void checkDeprecated(Scope sc, SemanticContext context) {
		/* TODO semantic
		if (!global.params.useDeprecated && isDeprecated())
	    {
		// Don't complain if we're inside a deprecated symbol's scope
		for (Dsymbol *sp = sc->parent; sp; sp = sp->parent)
		{   if (sp->isDeprecated())
			return;
		}

		for (; sc; sc = sc->enclosing)
		{
		    if (sc->scopesym && sc->scopesym->isDeprecated())
			return;
		}

		error(loc, "is deprecated");
	    }
	    */
	}
	
	/**
	 * Is a 'this' required to access the member?
	 */
	public AggregateDeclaration isThis() {
		return null;
	}
	
	/**
	 * Is this symbol a member of an AggregateDeclaration?
	 */
	public AggregateDeclaration isMember() {
		Dsymbol parent = toParent();
	    return parent != null ? parent.isAggregateDeclaration() : null;
	}
	
	public Dsymbol toParent() {
		return parent != null ? parent.pastMixin() : null;
	}
	
	public Dsymbol toParent2() {
		Dsymbol s = parent;
		while (s != null && s.isTemplateInstance() != null)
			s = s.parent;
		return s;
	}
	
	public Dsymbol pastMixin() {
		Dsymbol s = this;
		while (s != null && s.isTemplateMixin() != null)
			s = s.parent;
		return s;
	}
	
	// resolve real symbol
	public Dsymbol toAlias(SemanticContext context) {
		return this;
	}
	
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum, SemanticContext context) {
		parent = sd;
		if (!isAnonymous()) // no name, so can't add it to symbol table
		{
			if (sd.symtab.insert(this) == null) // if name is already defined
			{
				Dsymbol s2;

				s2 = sd.symtab.lookup(ident);
				if (!s2.overloadInsert(this)) {
					context.multiplyDefined(this, s2);
				}
			}
			if (sd.isAggregateDeclaration() != null || sd.isEnumDeclaration() != null) {
				if (ident.ident == Id.__sizeof 
						|| ident.ident == Id.alignof 
						|| ident.ident == Id.mangleof) {
					context.acceptProblem(Problem.newSemanticMemberError("Property " + ident.ident.string + " cannot be redefined", IProblem.PropertyCanNotBeRedefined, 0, ident.start, ident.length));
				}
			}
			return 1;
		}
		return 0;
	}
	
	public boolean overloadInsert(Dsymbol dsymbol) {
		return false;
	} 

	public void semantic(Scope sc, SemanticContext context) {
		
	}
	
	public void semantic2(Scope sc, SemanticContext context) {
		
	}
	
	public void semantic3(Scope sc, SemanticContext context) {
		
	}
	
	public Dsymbol search(IdentifierExp ident, int flags, SemanticContext context) {
		return null;
	}

}
