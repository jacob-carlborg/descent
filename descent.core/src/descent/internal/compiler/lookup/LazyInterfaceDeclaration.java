package descent.internal.compiler.lookup;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.SemanticContext;

public class LazyInterfaceDeclaration extends InterfaceDeclaration implements ILazyAggregate {
	
	private final ModuleBuilder builder;
	
	private Scope semanticScope;
	private Scope semantic2Scope;
	private Scope semantic3Scope;
	
	private LazyAggregateDeclaration lazy;
	
	public LazyInterfaceDeclaration(char[] filename, int lineNumber, IdentifierExp id, BaseClasses baseclasses, ModuleBuilder builder) {
		super(filename, lineNumber, id, baseclasses);
		this.builder = builder;
		this.lazy = new LazyAggregateDeclaration(this);
	}
	
	private boolean isUnlazy;
	
	public boolean isUnlazy() {
		return isUnlazy;
	}
	
	public InterfaceDeclaration unlazy(char[] prefix, SemanticContext context) {
		if (!isUnlazy) {
			isUnlazy = true;
			
			if (baseClass != null) {
				baseClass = baseClass.unlazy(prefix, context);
			}
			
			for (int i = 0; i < size(baseclasses); i++) {
				unlazy(baseclasses.get(i), prefix, context);
			}
			
			if (lazy.javaElementMembersCache == null) {
				lazy.fillJavaElementMemebrsCache(context);
			}
			
			if (!lazy.cancelLazyness) {
				for(char[] key : lazy.javaElementMembersCache.keys()) {
					if (key != null && CharOperation.prefixEquals(prefix, key, false)) {
						search(null, 0, key, 0, context);
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
	public Dsymbol search(char[] filename, int lineNumber, char[] ident, int flags, SemanticContext context) {
		return lazy.search(filename, lineNumber, ident, flags, context);
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

	public Dsymbol super_search(char[] filename, int lineNumber, char[] ident, int flags, SemanticContext context) {
		return super.search(filename, lineNumber, ident, flags, context);
	}

	public DsymbolTable symtab() {
		return symtab;
	}

	public void symtab(DsymbolTable table) {
		this.symtab = table;
	}
	
	private int isRunningSemantic;
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		isRunningSemantic++;
		
		super.semantic(sc, context);
		
		isRunningSemantic--;
	}
	
	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		isRunningSemantic++;
		
		super.semantic2(sc, context);
		
		isRunningSemantic--;
	}
	
	@Override
	public void semantic3(Scope sc, SemanticContext context) {
		isRunningSemantic++;
		
		super.semantic3(sc, context);
		
		isRunningSemantic--;
	}
	
	public boolean isRunningSemantic() {
		return isRunningSemantic != 0;
	}

}
