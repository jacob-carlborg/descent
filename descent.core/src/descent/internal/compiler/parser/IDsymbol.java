package descent.internal.compiler.parser;

public interface IDsymbol extends INode {

	IdentifierExp ident();
	
	void ident(IdentifierExp ident);
	
	PROT prot();

	IEnumMember isEnumMember();

	IFuncDeclaration isFuncDeclaration();

	IModule isModule();

	boolean needThis();

	IDsymbol toAlias(SemanticContext context);
	
	FuncAliasDeclaration isFuncAliasDeclaration();
	
	FuncLiteralDeclaration isFuncLiteralDeclaration();
	
	void checkDeprecated(Scope sc, SemanticContext context, INode reference);
	
	IDeclaration isDeclaration();
	
	IVarDeclaration isVarDeclaration();
	
	String toPrettyChars(SemanticContext context);
	
	String locToChars(SemanticContext context);
	
	IClassDeclaration isClassDeclaration();
	
	IDsymbol search(Loc loc, IdentifierExp ident, int flags, SemanticContext context);
	
	IDsymbol search(Loc loc, char[] ident, int flags, SemanticContext context);
	
	IDsymbol searchX(Loc loc, Scope sc, IdentifierExp id, SemanticContext context);
	
	ITemplateDeclaration isTemplateDeclaration();
	
	IDsymbol toParent();
	
	IDsymbol toParent2();
	
	String kind();
	
	IDsymbol parent();
	
	void parent(IDsymbol parent);
	
	TemplateInstance isTemplateInstance();
	
	IClassDeclaration isClassMember();
	
	IAliasDeclaration isAliasDeclaration();
	
	TupleDeclaration isTupleDeclaration();
	
	IAggregateDeclaration isAggregateDeclaration();
	
	IModule getModule();
	
	IAggregateDeclaration isMember();
	
	IPackage isPackage();
	
	boolean isDeprecated();
	
	String mangle(SemanticContext context);
	
	TemplateMixin isTemplateMixin();
	
	IDsymbol pastMixin();
	
	Type getType();
	
	Import isImport();
	
	boolean isforwardRef();
	
	void defineRef(IDsymbol s);
	
	IStructDeclaration isStructDeclaration();
	
	IScopeDsymbol isScopeDsymbol();
	
	IInterfaceDeclaration isInterfaceDeclaration();
	
	IAggregateDeclaration isThis();
	
	Loc loc();
	
	ICtorDeclaration isCtorDeclaration();
	
	IStaticCtorDeclaration isStaticCtorDeclaration();
	
	IUnionDeclaration isUnionDeclaration();
	
	INewDeclaration isNewDeclaration();
	
	void checkCtorConstInit(SemanticContext context);
	
	IDsymbol syntaxCopy(IDsymbol s, SemanticContext context);
	
	boolean oneMember(IDsymbol[] ps, SemanticContext context);
	
	int addMember(Scope sc, IScopeDsymbol sd, int memnum, SemanticContext context);
	
	IEnumDeclaration isEnumDeclaration();
	
	void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context);
	
	void inlineScan(SemanticContext context);
	
	void addLocalClass(ClassDeclarations aclasses, SemanticContext context);
	
	boolean hasPointers(SemanticContext context);
	
	boolean isImportedSymbol();
	
	ISymbolDeclaration isSymbolDeclaration();
	
	ITypedefDeclaration isTypedefDeclaration();
	
	AttribDeclaration isAttribDeclaration();
	
	boolean synthetic();
	
	void synthetic(boolean synthetic);
	
	String kindForError(SemanticContext context);
	
	IArrayScopeSymbol isArrayScopeSymbol();
	
	WithScopeSymbol isWithScopeSymbol();
	
	// For Descent, used to get the type of a symbol
	Type type();
	
	String getSignature();
	
	void semantic(Scope scope, SemanticContext context);
	
	void semantic2(Scope scope, SemanticContext context);
	
	void semantic3(Scope scope, SemanticContext context);

}