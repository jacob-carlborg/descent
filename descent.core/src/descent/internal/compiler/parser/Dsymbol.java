package descent.internal.compiler.parser;

import java.util.List;

import org.eclipse.core.runtime.Assert;

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
	
	public ScopeDsymbol isScopeDsymbol() {
		return null;
	}
	
	public AliasDeclaration isAliasDeclaration() {
		return null;
	}
	
	public CtorDeclaration isCtorDeclaration() {
		return null;
	}
	
	public DtorDeclaration isDtorDeclaration() {
		return null;
	}
	
	public InvariantDeclaration isInvariantDeclaration() {
		return null;
	}
	
	public UnitTestDeclaration isUnitTestDeclaration() {
		return null;
	}
	
	public NewDeclaration isNewDeclaration() {
		return null;
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
	
	public UnionDeclaration isUnionDeclaration() {
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
	
	public ArrayScopeSymbol isArrayScopeSymbol() {
		return null;
	}
	
	public Module isModule() {
		return null;
	}
	
	public void checkDeprecated(Scope sc, SemanticContext context) {
		if (!context.global.params.useDeprecated && isDeprecated()) {
			// Don't complain if we're inside a deprecated symbol's scope
			for (Dsymbol sp = sc.parent; sp != null; sp = sp.parent) {
				if (sp.isDeprecated())
					return;
			}

			for (; sc != null; sc = sc.enclosing) {
				if (sc.scopesym != null && sc.scopesym.isDeprecated())
					return;
			}

			error("is deprecated");
		}
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
	
	public ClassDeclaration isClassMember() { // are we a member of a class?
		Dsymbol parent = toParent();
		if (parent != null && parent.isClassDeclaration() != null) {
			return (ClassDeclaration) parent;
		}
		return null;
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
	
	public Module getModule() {
		Module m;
		Dsymbol s;

		s = this;
		while (s != null) {
			m = s.isModule();
			if (m != null) {
				return m;
			}
			s = s.parent;
		}
		return null;
	}
	
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum, SemanticContext context) {
		parent = sd;
		if (!isAnonymous()) // no name, so can't add it to symbol table
		{
			if (sd.symtab.insert(this) == null) // if name is already defined
			{
				Dsymbol s2;

				s2 = sd.symtab.lookup(ident);
				if (!s2.overloadInsert(this, context)) {
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
	
	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		return false;
	} 

	public void semantic(Scope sc, SemanticContext context) {
		//throw new IllegalStateException("No semantic routine for " + this);
	}
	
	public void semantic2(Scope sc, SemanticContext context) {
		
	}
	
	public void semantic3(Scope sc, SemanticContext context) {
		
	}
	
	public final Dsymbol search(IdentifierExp ident, int flags, SemanticContext context) {
		return search(ident.ident, flags, context);
	}
	
	public Dsymbol search(Identifier ident, int flags, SemanticContext context) {
		return null;
	}
	
	private boolean oneMember(Dsymbol[] ps) {
	    ps[0] = this;
	    return true;
	}
	
	public static boolean oneMembers(List<Dsymbol> members, Dsymbol[] ps) {
	    Dsymbol s = null;

		if (members != null) {
			for (int i = 0; i < members.size(); i++) {
				Dsymbol sx = (Dsymbol) members.get(i);

				boolean x = sx.oneMember(ps);
				if (!x) {
					Assert.isTrue(ps[0] == null);
					return false;
				}
				if (ps[0] != null) {
					if (s != null) { // more than one symbol
						ps[0] = null;
						return false;
					}
					s = ps[0];
				}
			}
		}
		ps[0] = s; // s is the one symbol, NULL if none
		return true;
	}

}
