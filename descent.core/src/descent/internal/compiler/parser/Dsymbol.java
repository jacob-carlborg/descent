package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;

public abstract class Dsymbol extends ASTNode {

	public static List<Dsymbol> arraySyntaxCopy(List<Dsymbol> a) {
		List<Dsymbol> b = new ArrayList<Dsymbol>();
		for (Dsymbol s : a) {
			b.add(s.syntaxCopy(null));
		}
		return b;
	}

	public static boolean oneMembers(List<Dsymbol> members, Dsymbol[] ps) {
		Dsymbol s = null;

		if (members != null) {
			for (int i = 0; i < members.size(); i++) {
				Dsymbol sx = members.get(i);

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

	public IdentifierExp ident;
	public IdentifierExp c_ident;
	public Dsymbol parent;
	public Loc loc;

	public Dsymbol(Loc loc) {
		this.loc = loc;
	}

	public Dsymbol(Loc loc, IdentifierExp ident) {
		this(loc);
		this.ident = ident;
		this.c_ident = null;
		this.parent = null;
	}

	public void addLocalClass(List<ClassDeclaration> aclasses) {

	}

	public int addMember(Scope sc, ScopeDsymbol sd, int memnum,
			SemanticContext context) {
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
			if (sd.isAggregateDeclaration() != null
					|| sd.isEnumDeclaration() != null) {
				if (ident.ident.equals(Id.__sizeof.string) || ident.ident.equals(Id.alignof.string)
						|| ident.ident.equals(Id.mangleof.string)) {
					context.acceptProblem(Problem.newSemanticMemberError(
							"Property " + ident.ident
									+ " cannot be redefined",
							IProblem.PropertyCanNotBeRedefined, 0, ident.start,
							ident.length));
				}
			}
			return 1;
		}
		return 0;
	}

	public void checkCtorConstInit() {

	}

	public void checkDeprecated(Scope sc, SemanticContext context) {
		if (!context.global.params.useDeprecated && isDeprecated()) {
			// Don't complain if we're inside a deprecated symbol's scope
			for (Dsymbol sp = sc.parent; sp != null; sp = sp.parent) {
				if (sp.isDeprecated()) {
					return;
				}
			}

			for (; sc != null; sc = sc.enclosing) {
				if (sc.scopesym != null && sc.scopesym.isDeprecated()) {
					return;
				}
			}

			error("is deprecated");
		}
	}

	public void defineRef(Dsymbol s) {
		throw new IllegalStateException("Must be implemented by subclasses");
	}

	@Override
	public DYNCAST dyncast() {
		return DYNCAST.DYNCAST_DSYMBOL;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Dsymbol)) {
			return false;
		}

		Dsymbol s = (Dsymbol) (o);
		return ident.equals(s.ident);
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

	public Type getType() {
		return null;
	}

	public boolean hasPointers(SemanticContext context) {
		return false;
	}

	public void inlineScan(SemanticContext context) {

	}

	public AggregateDeclaration isAggregateDeclaration() {
		return null;
	}

	public AliasDeclaration isAliasDeclaration() {
		return null;
	}

	public boolean isAnonymous() {
		return ident == null;
	}

	public AnonymousAggregateDeclaration isAnonymousAggregateDeclaration() {
		return null;
	}

	public ArrayScopeSymbol isArrayScopeSymbol() {
		return null;
	}
	
	public AttribDeclaration isAttribDeclaration() {
		return null;
	}

	public ClassDeclaration isClassDeclaration() {
		return null;
	}

	// are we a member of a class?
	public ClassDeclaration isClassMember() {
		Dsymbol parent = toParent();
		if (parent != null && parent.isClassDeclaration() != null) {
			return (ClassDeclaration) parent;
		}
		return null;
	}

	public CtorDeclaration isCtorDeclaration() {
		return null;
	}

	public Declaration isDeclaration() {
		return null;
	}

	public boolean isDeprecated() {
		return false;
	}

	public DtorDeclaration isDtorDeclaration() {
		return null;
	}

	public EnumDeclaration isEnumDeclaration() {
		return null;
	}

	public EnumMember isEnumMember() {
		return null;
	}

	public boolean isExport() {
		return false;
	}

	public boolean isforwardRef() {
		return false;
	}

	public FuncAliasDeclaration isFuncAliasDeclaration() {
		return null;
	}

	public FuncDeclaration isFuncDeclaration() {
		return null;
	}

	public FuncLiteralDeclaration isFuncLiteralDeclaration() {
		return null;
	}

	public Import isImport() {
		return null;
	}

	public boolean isImportedSymbol() {
		return false;
	}

	public InterfaceDeclaration isInterfaceDeclaration() {
		return null;
	}

	public InvariantDeclaration isInvariantDeclaration() {
		return null;
	}

	public LabelDsymbol isLabel() {
		return null;
	}

	/**
	 * Is this symbol a member of an AggregateDeclaration?
	 */
	public AggregateDeclaration isMember() {
		Dsymbol parent = toParent();
		return parent != null ? parent.isAggregateDeclaration() : null;
	}

	public Module isModule() {
		return null;
	}

	public NewDeclaration isNewDeclaration() {
		return null;
	}

	public Package isPackage() {
		return null;
	}

	public ScopeDsymbol isScopeDsymbol() {
		return null;
	}

	public StaticCtorDeclaration isStaticCtorDeclaration() {
		return null;
	}

	public StructDeclaration isStructDeclaration() {
		return null;
	}

	public TemplateDeclaration isTemplateDeclaration() {
		return null;
	}

	public TemplateInstance isTemplateInstance() {
		return null;
	}

	public TemplateMixin isTemplateMixin() {
		return null;
	}

	/**
	 * Is a 'this' required to access the member?
	 */
	public AggregateDeclaration isThis() {
		return null;
	}

	public TupleDeclaration isTupleDeclaration() {
		return null;
	}

	public UnionDeclaration isUnionDeclaration() {
		return null;
	}

	public UnitTestDeclaration isUnitTestDeclaration() {
		return null;
	}

	public VarDeclaration isVarDeclaration() {
		return null;
	}

	public WithScopeSymbol isWithScopeSymbol() {
		return null;
	}

	public String kind() {
		return "symbol";
	}

	public boolean needThis() {
		return false;
	}

	public boolean oneMember(Dsymbol[] ps) {
		ps[0] = this;
		return true;
	}

	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		return false;
	}

	public Dsymbol pastMixin() {
		Dsymbol s = this;
		while (s != null && s.isTemplateMixin() != null) {
			s = s.parent;
		}
		return s;
	}

	public PROT prot() {
		return PROT.PROTpublic;
	}

	public Dsymbol search(Loc loc, String ident, int flags, SemanticContext context) {
		return null;
	}

	public final Dsymbol search(Loc loc, IdentifierExp ident, int flags,
			SemanticContext context) {
		return search(loc, ident.ident, flags, context);
	}

	public void semantic(Scope sc, SemanticContext context) {
		// throw new IllegalStateException("No semantic routine for " + this);
	}

	public void semantic2(Scope sc, SemanticContext context) {

	}

	public void semantic3(Scope sc, SemanticContext context) {

	}

	public int size() {
		error("Dsymbol '%s' has no size\n", toChars());
		return 0;
	}

	public Dsymbol syntaxCopy(Dsymbol s) {
		throw new IllegalStateException("Must be implemented by subclasses");
	}

	public Dsymbol toAlias(SemanticContext context) {
		return this;
	}

	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring(toChars());
	}

	@Override
	public String toChars() {
		return ident != null ? ident.toChars() : "__anonymous";
	}

	public Dsymbol toParent() {
		return parent != null ? parent.pastMixin() : null;
	}

	public Dsymbol toParent2() {
		Dsymbol s = parent;
		while (s != null && s.isTemplateInstance() != null) {
			s = s.parent;
		}
		return s;
	}

	@Override
	public String toPrettyChars() {
		// TODO semantic
		return toChars();
	}

}
