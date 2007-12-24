package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class Dsymbol extends ASTDmdNode implements IDsymbol {

	public static Arguments arraySyntaxCopy(Arguments a, SemanticContext context) {
		Arguments b = new Arguments();
		b.setDim(a.size());
		for(int i = 0; i < a.size(); i++) {
			Argument s = a.get(i);
			b.set(i, s.syntaxCopy(context));
		}
		return b;
	}

	public static Dsymbols arraySyntaxCopy(Dsymbols a, SemanticContext context) {
		Dsymbols b = null;
		if (a != null) {
			b = new Dsymbols();
			b.setDim(a.size());
			for(int i = 0; i < a.size(); i++) {
				IDsymbol s = a.get(i);
				b.set(i, s.syntaxCopy(null, context));
			}
		}
		return b;
	}

	public static boolean oneMembers(Dsymbols members, IDsymbol[] ps,
			SemanticContext context) {
		IDsymbol s = null;

		if (members != null) {
			for (int i = 0; i < members.size(); i++) {
				IDsymbol sx = members.get(i);

				boolean x = sx.oneMember(ps, context);
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
	public IDsymbol parent;
	public Loc loc;

	public Dsymbol() {
	}

	public Dsymbol(IdentifierExp ident) {
		this.ident = ident;
		this.c_ident = null;
		this.parent = null;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
	}

	public void addLocalClass(ClassDeclarations aclasses, SemanticContext context) {
		// empty
	}

	public int addMember(Scope sc, IScopeDsymbol sd, int memnum,
			SemanticContext context) {
		parent = sd;
		if (!isAnonymous()) // no name, so can't add it to symbol table
		{
			if (sd.symtab().insert(this) == null) // if name is already defined
			{
				Dsymbol s2;

				s2 = (Dsymbol) sd.symtab().lookup(ident); // SEMANTIC
				if (!s2.overloadInsert(this, context)) {
					ScopeDsymbol.multiplyDefined(Loc.ZERO, this, s2, context);
				}
			}
			if (sd.isAggregateDeclaration() != null
					|| sd.isEnumDeclaration() != null) {
				if (equals(ident, Id.__sizeof)
						|| equals(ident, Id.alignof)
						|| equals(ident, Id.mangleof)) {
					context.acceptProblem(Problem.newSemanticMemberError(
							IProblem.PropertyCanNotBeRedefined, 0, ident.start,
							ident.length,
							new String[] { new String(ident.ident) }));
				}
			}
			return 1;
		}
		return 0;
	}

	public void checkCtorConstInit(SemanticContext context) {
		// empty
	}

	public void checkDeprecated(Scope sc, SemanticContext context) {
		if (!context.global.params.useDeprecated && isDeprecated()) {
			// Don't complain if we're inside a deprecated symbol's scope
			for (IDsymbol sp = sc.parent; sp != null; sp = sp.parent()) {
				if (sp.isDeprecated()) {
					return;
				}
			}

			for (; sc != null; sc = sc.enclosing) {
				if (sc.scopesym != null && sc.scopesym.isDeprecated()) {
					return;
				}
			}

			context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolIsDeprecated, this, new String[] { toChars(context) }));
		}
	}

	public void defineRef(IDsymbol s) {
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
		if (ident == null) {
			return s.ident == null;
		}
		return ident.equals(s.ident);
	}

	public IModule getModule() {
		IModule m;
		IDsymbol s;

		s = this;
		while (s != null) {
			m = s.isModule();
			if (m != null) {
				return m;
			}
			s = s.parent();
		}
		return null;
	}

	@Override
	public int getNodeType() {
		return 0;
	}

	public Type getType() {
		return null;
	}

	public boolean hasPointers(SemanticContext context) {
		return false;
	}

	public void inlineScan(SemanticContext context) {
		// empty
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
	public IClassDeclaration isClassMember() {
		IDsymbol parent = toParent();
		if (parent != null && parent.isClassDeclaration() != null) {
			return (IClassDeclaration) parent;
		}
		return null;
	}

	public CtorDeclaration isCtorDeclaration() {
		return null;
	}

	public Declaration isDeclaration() {
		return null;
	}

	public DeleteDeclaration isDeleteDeclaration() {
		return null;
	}

	public boolean isDeprecated() {
		return false;
	}

	public DtorDeclaration isDtorDeclaration() {
		return null;
	}

	public IEnumDeclaration isEnumDeclaration() {
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
	public IAggregateDeclaration isMember() {
		IDsymbol parent = toParent();
		return parent != null ? parent.isAggregateDeclaration() : null;
	}

	public Module isModule() {
		return null;
	}

	public INewDeclaration isNewDeclaration() {
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
	
	public SymbolDeclaration isSymbolDeclaration()
	{
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
	public IAggregateDeclaration isThis() {
		return null;
	}

	public TupleDeclaration isTupleDeclaration() {
		return null;
	}

	public TypedefDeclaration isTypedefDeclaration() {
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
	
	public String kindForError(SemanticContext context) {
		StringBuilder sb = new StringBuilder();
		String p = locToChars(context);
		if (p != null) {
			sb.append(p);
			sb.append(": ");
		}
		if (isAnonymous()) {
			sb.append(kind());
			sb.append(" ");
		} else {
			sb.append(kind());
			sb.append(" ");
			sb.append(toPrettyChars(context));
			sb.append(" ");
		}
		return sb.toString();
	}

	public String mangle(SemanticContext context) {
		return Dsymbol_mangle(context);
	}
	
	protected String Dsymbol_mangle(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		String id;

		id = ident != null ? ident.toChars() : toChars(context);
		if (parent != null) {
			String p = parent.mangle(context);
			if (p.charAt(0) == '_' && p.charAt(1) == 'D') {
				p += 2;
			}
			buf.writestring(p);
		}
		buf.data.append(id.length())/*.append("u")*/.append(id);
		id = buf.toChars();
		buf.data = null;
		return id;
	}

	public boolean needThis() {
		return false;
	}

	public boolean oneMember(IDsymbol[] ps, SemanticContext context) {
		ps[0] = this;
		return true;
	}

	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		return false;
	}

	public IDsymbol pastMixin() {
		IDsymbol s = this;
		while (s != null && s.isTemplateMixin() != null) {
			s = s.parent();
		}
		return s;
	}

	public PROT prot() {
		return PROT.PROTpublic;
	}

	public IDsymbol search(Loc loc, char[] ident, int flags,
			SemanticContext context) {
		return null;
	}

	public final IDsymbol search(Loc loc, IdentifierExp ident, int flags,
			SemanticContext context) {
		return search(loc, ident.ident, flags, context);
	}

	public IDsymbol searchX(Loc loc, Scope sc, IdentifierExp id,
			SemanticContext context) {
		IDsymbol s = toAlias(context);
		IDsymbol sm;

		switch (id.dyncast()) {
		case DYNCAST_IDENTIFIER:
			sm = s.search(loc, id, 0, context);
			break;

		case DYNCAST_DSYMBOL: { // It's a template instance
			Dsymbol st = ((TemplateInstanceWrapper) id).tempinst;
			TemplateInstance ti = st.isTemplateInstance();
			id = ti.name;
			sm = s.search(loc, id, 0, context);
			if (null == sm) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.TemplateIdentifierIsNotAMemberOf, this, new String[] { id.toChars(), s.kind(), s.toChars(context) }));
				return null;
			}
			sm = sm.toAlias(context);
			ITemplateDeclaration td = sm.isTemplateDeclaration();
			if (null == td) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.SymbolIsNotATemplate, this, new String[] { id.toChars(), sm.kind() }));
				return null;
			}
			ti.tempdecl = td;
			if (0 == ti.semanticdone) {
				ti.semantic(sc, context);
			}
			sm = ti.toAlias(context);
			break;
		}

		default:
			throw new IllegalStateException("assert(0);");
		}
		return sm;
	}

	public void semantic(Scope sc, SemanticContext context) {
		// throw new IllegalStateException("No semantic routine for " + this);
	}

	public void semantic2(Scope sc, SemanticContext context) {

	}

	public void semantic3(Scope sc, SemanticContext context) {

	}

	public int size(SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.DSymbolHasNoSize, this, new String[] { toChars(context) }));
		return 0;
	}

	public IDsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		throw new IllegalStateException("Must be implemented by subclasses");
	}

	public IDsymbol toAlias(SemanticContext context) {
		return this;
	}
	
	public Symbol toSymbol() {
		throw new IllegalStateException("assert(0); // implement");
	}

	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring(toChars(context));
	}

	@Override
	public String toChars(SemanticContext context) {
		return (ident != null && ident.ident != null) ? ident.toChars() : "__anonymous";
	}

	public IDsymbol toParent() {
		return parent != null ? parent.pastMixin() : null;
	}

	public IDsymbol toParent2() {
		IDsymbol s = parent;
		while (s != null && s.isTemplateInstance() != null) {
			s = s.parent();
		}
		return s;
	}

	@Override
	public String toPrettyChars(SemanticContext context) {
		// TODO semantic
		return toChars(context);
	}

	public String locToChars(SemanticContext context) {
//	    IModule m = getModule();
//
//	    if (m != null && m.srcfile != null) {
//	    	loc.filename = m.srcfile.toString().toCharArray();
//	    }
	    return loc.toChars();
	}
	
	@Override
	public int getErrorStart() {
		if (ident != null) {
			return ident.getErrorStart();
		}
		return super.getErrorStart();
	}
	
	@Override
	public int getErrorLength() {
		if (ident != null) {
			return ident.getErrorLength();
		}
		return super.getErrorLength();
	}
	
	public IdentifierExp ident() {
		return ident;
	}
	
	public void ident(IdentifierExp ident) {
		this.ident = ident;
	}
	
	public IDsymbol parent() {
		return parent;
	}
	
	public void parent(IDsymbol parent) {
		this.parent = parent;
	}
	
	public Loc loc() {
		return loc;
	}
	
	public boolean synthetic() {
		return synthetic;
	}
	
	public void synthetic(boolean synthetic) {
		this.synthetic = synthetic;
	}
	
	@Override
	public int getLineNumber() {
		return loc.linnum;
	}
	
	public void setLineNumber(int lineNumber) {
		this.loc.linnum = lineNumber;
	}

}
