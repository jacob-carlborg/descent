package descent.internal.compiler.lookup;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StructDeclaration;

public class LazyStructDeclaration extends StructDeclaration implements ILazyAggregate {

	private final ModuleBuilder builder;
	
	private Scope semanticScope;
	private Scope semantic2Scope;
	private Scope semantic3Scope;
	
	private final LazyAggregateDeclaration lazy;

	public LazyStructDeclaration(Loc loc, IdentifierExp id, ModuleBuilder builder) {
		super(loc, id);
		this.builder = builder;
		this.lazy = new LazyAggregateDeclaration(this);
	}
	
	private boolean isUnlazy;
	
	public boolean isUnlazy() {
		return isUnlazy;
	}
	
	public StructDeclaration unlazy(char[] prefix, SemanticContext context) {
		if (!isUnlazy) {
			isUnlazy = true;
			
			if (lazy.javaElementMembersCache == null) {
				lazy.fillJavaElementMemebrsCache(context);
			}
			
			if (!lazy.cancelLazyness) {
				
				for(char[] key : lazy.javaElementMembersCache.keys()) {
					if (key != null && CharOperation.prefixEquals(prefix, key, false)) {
						search(Loc.ZERO, key, 0, context);
					}
				}
			}
		}
		return this;
	}
	
	@Override
	protected void semanticScope(Scope sc) {
		this.semanticScope = sc;
	}
	
	@Override
	protected void semantic2Scope(Scope sc) {
		this.semantic2Scope = sc;
	}
	
	@Override
	protected void semantic3Scope(Scope sc) {
		this.semantic3Scope = sc;
	}
	
	@Override
	public Dsymbol search(Loc loc, char[] ident, int flags, SemanticContext context) {
		return lazy.search(loc, ident, flags, context);
	}
	
	public void runMissingSemantic(Dsymbol sym, SemanticContext context) {
		lazy.runMissingSemantic(sym, context);
	}

	public ScopeDsymbol asScopeDsymbol() {
		return this;
	}

	public Scope getSemanticScope() {
		return semanticScope;
	}
	
	public IdentifierExp getIdent() {
		return ident;
	}
	
	public ModuleBuilder builder() {
		return builder;
	}

	public void members(Dsymbols dsymbols) {
		this.members = dsymbols;
	}

	public Dsymbols members() {
		return members;
	}

	public Scope semanticScope() {
		return semanticScope;
	}
	
	public Scope semantic2Scope() {
		return semantic2Scope;
	}
	
	public Scope semantic3Scope() {
		return semantic3Scope;
	}

	public Dsymbol super_search(Loc loc, char[] ident, int flags, SemanticContext context) {
		return super.search(loc, ident, flags, context);
	}

	public DsymbolTable symtab() {
		return symtab;
	}

	public void symtab(DsymbolTable table) {
		this.symtab = table;
	}

}
