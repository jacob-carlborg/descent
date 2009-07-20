package descent.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import descent.core.IJavaElement;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.IdentifierExp;
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

	public LazyStructDeclaration(char[] filename, int lineNumber, IdentifierExp id, ModuleBuilder builder) {
		super(filename, lineNumber, id);
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
				// We need to insert them in order, for struct initializers
				try {
					IJavaElement[] children = javaElement.getChildren();
					final List<String> names = new ArrayList<String>();
					for(IJavaElement child : children) {
						names.add(child.getElementName());
					}
					
					List<String> keys = new ArrayList<String>();
					
					for(char[] key : lazy.javaElementMembersCache.keys()) {
						if (key != null && CharOperation.prefixEquals(prefix, key, false)) {
							keys.add(new String(key));
						}
					}
					
					Collections.sort(keys, new Comparator<String>() {
						public int compare(String arg0, String arg1) {
							int i0 = names.indexOf(arg0);
							int i1 = names.indexOf(arg1);
							return i0 < i1 ? -1 : (i0 > i1 ? 1 : 0);
						}
					});
					
					for(String key : keys) {
						search(null, 0, key.toCharArray(), 0, context);
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		}
		return this;
	}
	
	/**
	 * For sizeof, we need the members, so unlazy.
	 */
	@Override
	public int size(SemanticContext context) {
		unlazy(CharOperation.NO_CHAR, context);
		return super.size(context);
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
