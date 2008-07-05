package descent.internal.compiler.lookup;

import descent.core.JavaModelException;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.core.util.Util;

public class LazyInterfaceDeclaration extends InterfaceDeclaration implements ILazyAggregate {
	
	private final ModuleBuilder builder;
	
	private Scope semanticScope;
	private Scope semantic2Scope;
	private Scope semantic3Scope;
	
	private LazyAggregateDeclaration lazy;
	
	public LazyInterfaceDeclaration(Loc loc, IdentifierExp id, BaseClasses baseclasses, ModuleBuilder builder) {
		super(loc, id, baseclasses);
		this.builder = builder;
		this.lazy = new LazyAggregateDeclaration(this);
	}
	
	private InterfaceDeclaration unlazyOne;
	public InterfaceDeclaration unlazy(SemanticContext context) {
		if (unlazyOne == null) {
			if (baseClass != null) {
				baseClass = baseClass.unlazy(context);
			}
			
			for (int i = 0; i < size(baseclasses); i++) {
				unlazy(baseclasses.get(i), context);
			}
			
			unlazyOne = new InterfaceDeclaration(loc, ident, baseclasses);
			unlazyOne.parent = this.parent;
			unlazyOne.members = new Dsymbols();
			try {
				builder.fill(getModule(), unlazyOne.members, javaElement.getChildren(), null);
			} catch (JavaModelException e) {
				Util.log(e);
			}
			unlazyOne.setJavaElement(this.javaElement);
			runMissingSemantic(unlazyOne, context);
		}
		return unlazyOne;
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
