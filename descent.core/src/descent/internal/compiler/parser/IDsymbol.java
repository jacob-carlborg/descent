package descent.internal.compiler.parser;

public interface IDsymbol extends INode {

	IdentifierExp ident();
	
	PROT prot();

	IEnumMember isEnumMember();

	IFuncDeclaration isFuncDeclaration();

	IModule isModule();

	boolean needThis();

	IDsymbol toAlias(SemanticContext context);
	
	FuncAliasDeclaration isFuncAliasDeclaration();
	
	FuncLiteralDeclaration isFuncLiteralDeclaration();
	
	void checkDeprecated(Scope sc, SemanticContext context);
	
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
	
	IImport isImport();
	
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
	
	IDsymbol syntaxCopy(Dsymbol s, SemanticContext context);
	
	boolean oneMember(Dsymbol[] ps, SemanticContext context);
	
	int addMember(Scope sc, IScopeDsymbol sd, int memnum, SemanticContext context);
	
	IEnumDeclaration isEnumDeclaration();
	
	void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context);
	
	void inlineScan(SemanticContext context);
	
	void addLocalClass(ClassDeclarations aclasses, SemanticContext context);
	
	boolean hasPointers(SemanticContext context);
	
	boolean isImportedSymbol();
	
	ISymbolDeclaration isSymbolDeclaration();
	
	void semantic(Scope scope, SemanticContext context);
	
	void semantic2(Scope scope, SemanticContext context);
	
	void semantic3(Scope scope, SemanticContext context);

}
