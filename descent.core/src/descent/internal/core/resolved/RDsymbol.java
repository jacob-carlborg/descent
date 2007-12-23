package descent.internal.core.resolved;

import descent.internal.compiler.parser.ClassDeclarations;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.FuncAliasDeclaration;
import descent.internal.compiler.parser.FuncLiteralDeclaration;
import descent.internal.compiler.parser.HdrGenState;
import descent.internal.compiler.parser.IAggregateDeclaration;
import descent.internal.compiler.parser.IAliasDeclaration;
import descent.internal.compiler.parser.IClassDeclaration;
import descent.internal.compiler.parser.ICtorDeclaration;
import descent.internal.compiler.parser.IDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IEnumDeclaration;
import descent.internal.compiler.parser.IEnumMember;
import descent.internal.compiler.parser.IFuncDeclaration;
import descent.internal.compiler.parser.IImport;
import descent.internal.compiler.parser.IInterfaceDeclaration;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.INewDeclaration;
import descent.internal.compiler.parser.IPackage;
import descent.internal.compiler.parser.IScopeDsymbol;
import descent.internal.compiler.parser.IStaticCtorDeclaration;
import descent.internal.compiler.parser.IStructDeclaration;
import descent.internal.compiler.parser.ISymbolDeclaration;
import descent.internal.compiler.parser.ITemplateDeclaration;
import descent.internal.compiler.parser.IUnionDeclaration;
import descent.internal.compiler.parser.IVarDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.OutBuffer;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.TupleDeclaration;
import descent.internal.compiler.parser.Type;

public class RDsymbol extends RNode implements IDsymbol {

	public void addLocalClass(ClassDeclarations aclasses, SemanticContext context) {
	}

	public int addMember(Scope sc, IScopeDsymbol sd, int memnum, SemanticContext context) {
		return 0;
	}

	public void checkCtorConstInit(SemanticContext context) {
	}

	public void checkDeprecated(Scope sc, SemanticContext context) {
	}

	public void defineRef(IDsymbol s) {
	}

	public IModule getModule() {
		return null;
	}

	public Type getType() {
		return null;
	}

	public boolean hasPointers(SemanticContext context) {
		return false;
	}

	public IdentifierExp ident() {
		return null;
	}

	public void inlineScan(SemanticContext context) {
	}

	public IAggregateDeclaration isAggregateDeclaration() {
		return null;
	}

	public IAliasDeclaration isAliasDeclaration() {
		return null;
	}

	public IClassDeclaration isClassDeclaration() {
		return null;
	}

	public IClassDeclaration isClassMember() {
		return null;
	}

	public ICtorDeclaration isCtorDeclaration() {
		return null;
	}

	public IDeclaration isDeclaration() {
		return null;
	}

	public boolean isDeprecated() {
		return false;
	}

	public IEnumDeclaration isEnumDeclaration() {
		return null;
	}

	public IEnumMember isEnumMember() {
		return null;
	}

	public FuncAliasDeclaration isFuncAliasDeclaration() {
		return null;
	}

	public IFuncDeclaration isFuncDeclaration() {
		return null;
	}

	public FuncLiteralDeclaration isFuncLiteralDeclaration() {
		return null;
	}

	public IImport isImport() {
		return null;
	}

	public boolean isImportedSymbol() {
		return false;
	}

	public IInterfaceDeclaration isInterfaceDeclaration() {
		return null;
	}

	public IAggregateDeclaration isMember() {
		return null;
	}

	public IModule isModule() {
		return null;
	}

	public INewDeclaration isNewDeclaration() {
		return null;
	}

	public IPackage isPackage() {
		return null;
	}

	public IScopeDsymbol isScopeDsymbol() {
		return null;
	}

	public IStaticCtorDeclaration isStaticCtorDeclaration() {
		return null;
	}

	public IStructDeclaration isStructDeclaration() {
		return null;
	}

	public ISymbolDeclaration isSymbolDeclaration() {
		return null;
	}

	public ITemplateDeclaration isTemplateDeclaration() {
		return null;
	}

	public TemplateInstance isTemplateInstance() {
		return null;
	}

	public TemplateMixin isTemplateMixin() {
		return null;
	}

	public IAggregateDeclaration isThis() {
		return null;
	}

	public TupleDeclaration isTupleDeclaration() {
		return null;
	}

	public IUnionDeclaration isUnionDeclaration() {
		return null;
	}

	public IVarDeclaration isVarDeclaration() {
		return null;
	}

	public boolean isforwardRef() {
		return false;
	}

	public String kind() {
		return null;
	}

	public Loc loc() {
		return null;
	}

	public String locToChars(SemanticContext context) {
		return null;
	}

	public String mangle(SemanticContext context) {
		return null;
	}

	public boolean needThis() {
		return false;
	}

	public boolean oneMember(Dsymbol[] ps, SemanticContext context) {
		return false;
	}

	public IDsymbol parent() {
		return null;
	}

	public void parent(IDsymbol parent) {
	}

	public IDsymbol pastMixin() {
		return null;
	}

	public PROT prot() {
		return null;
	}

	public IDsymbol search(Loc loc, IdentifierExp ident, int flags, SemanticContext context) {
		return null;
	}

	public IDsymbol search(Loc loc, char[] ident, int flags, SemanticContext context) {
		return null;
	}

	public IDsymbol searchX(Loc loc, Scope sc, IdentifierExp id, SemanticContext context) {
		return null;
	}

	public void semantic(Scope scope, SemanticContext context) {
	}

	public void semantic2(Scope scope, SemanticContext context) {
	}

	public void semantic3(Scope scope, SemanticContext context) {
	}

	public IDsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		return null;
	}

	public IDsymbol toAlias(SemanticContext context) {
		return null;
	}

	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
	}

	public IDsymbol toParent() {
		return null;
	}

	public IDsymbol toParent2() {
		return null;
	}

	public String toPrettyChars(SemanticContext context) {
		return null;
	}

}
