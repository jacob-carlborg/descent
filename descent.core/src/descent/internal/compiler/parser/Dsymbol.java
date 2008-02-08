package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.IJavaElement;
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
			// Descent: if it's null, problems were reported
			if (sd == null) {
				return 0;
			}
			
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

	// Added "reference" parameter in order to signal better errors
	public void checkDeprecated(Scope sc, SemanticContext context, INode reference) {
		SemanticMixin.checkDeprecated(this, sc, context, reference);
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
		return SemanticMixin.getModule(this);
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
		return SemanticMixin.Dsymbol_mangle(this, context);
	}

	public boolean needThis() {
		return false;
	}

	public boolean oneMember(IDsymbol[] ps, SemanticContext context) {
		return SemanticMixin.oneMember(this, ps, context);
	}

	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		return false;
	}

	public IDsymbol pastMixin() {
		return SemanticMixin.pastMixin(this);
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
		return SemanticMixin.searchX(this, loc, sc, id, context);
	}

	public void semantic(Scope sc, SemanticContext context) {
		throw new IllegalStateException("No semantic routine for " + this);
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
		SemanticMixin.toCBuffer(this, buf, hgs, context);
	}

	@Override
	public String toChars(SemanticContext context) {
		return SemanticMixin.toChars(this, context);
	}

	public IDsymbol toParent() {
		return SemanticMixin.toParent(this);
	}

	public IDsymbol toParent2() {
		return SemanticMixin.toParent2(this);
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
		// TODO Descent semantic line number
		if (loc == null) {
			return 0;
		}
		return loc.linnum;
	}
	
	public void setLineNumber(int lineNumber) {
		if (loc != null) {
			this.loc.linnum = lineNumber;
		}
	}
	
	// For Descent, used to get the type of a symbol
	public Type type() {
		return null;
	}
	
	public String getSignature() {
		return SemanticMixin.getSignature(this);
	}
	
	public void appendSignature(StringBuilder sb) {
		SemanticMixin.appendSignature(this, sb);
	}
	
	public IJavaElement getJavaElement() {
		return null;
	}
	
	public char getSignaturePrefix() {
		return 0;
	}
	
	public long getFlags() {
		long flags = 0;
		if (modifiers != null) {
			for(Modifier modifier : modifiers) {
				flags |= modifier.getFlags();
			}
		}
		return flags;
	}
	
	public IDsymbol effectiveParent() {
		IDsymbol p = parent;
		while(p instanceof FuncLiteralDeclaration) {
			p = parent.parent();
		}
		return p;
	}
	
	public boolean templated() {
		return false;
	}

}
