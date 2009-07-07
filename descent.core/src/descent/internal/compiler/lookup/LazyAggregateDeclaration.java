package descent.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;

import descent.core.IJavaElement;
import descent.core.JavaModelException;
import descent.internal.compiler.lookup.ModuleBuilder.FillResult;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.ProtDeclaration;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StorageClassDeclaration;
import descent.internal.core.util.Util;

public class LazyAggregateDeclaration {
	
	HashtableOfCharArrayAndObject javaElementMembersCache;
	List<Dsymbol> pendingPublicImports;
	List<Dsymbol> pendingPrivateImports;
	boolean cancelLazyness;
	ILazyAggregate lazy;
	
	public LazyAggregateDeclaration(ILazyAggregate lazy) {
		this.lazy = lazy;
	}
	
	public Dsymbol search(Loc loc, char[] ident, int flags, SemanticContext context) {
		if (cancelLazyness) {
			return lazy.super_search(loc, ident, flags, context);
		}
		
		Dsymbol s = lazy.symtab() != null ? lazy.symtab().lookup(ident) : null;
		
		if (s == null) {
			if (javaElementMembersCache == null) {
				FillResult result = fillJavaElementMemebrsCache(context);
				
				if (result.hasMixinDeclaration || result.hasStaticIf || result.hasAnon) {
					return search(loc, ident, flags, context);
				}
				
				// Anonymous enums are "hard" for me :-P
				if (result.hasAnonEnum) {
					return search(loc, ident, flags, context);
				}
			}
			
			Object target = javaElementMembersCache.get(ident);
			if (target != null) {
				if (lazy.symtab() == null) {
					lazy.symtab(new DsymbolTable());
				}
				
				if (target instanceof IJavaElement) {
					try {
						lazy.builder().fill(lazy.getModule(), lazy.members(), null, (IJavaElement) target);
					} catch (JavaModelException e) {
						Util.log(e);
					}
					
					s = lazy.members().get(lazy.members().size() - 1);
					
					if (!lazy.isRunningSemantic()) {
						s.addMember(lazy.semanticScope(), lazy.asScopeDsymbol(), 0, context);
						lazy.runMissingSemantic(s, context);
					}
				} else {
					Dsymbols symbols = new Dsymbols();
					
					List<IJavaElement> elemsList = (List<IJavaElement>) target;
					for(IJavaElement elem : elemsList) {
						try {
							lazy.builder().fill(lazy.getModule(), lazy.members(), null, (IJavaElement) elem);
						} catch (JavaModelException e) {
							Util.log(e);
						}
						
						s = lazy.members().get(lazy.members().size() - 1);
						if (lazy.semanticScope() != null) {
							if (!lazy.isRunningSemantic()) {
								s.addMember(lazy.semanticScope(), lazy.asScopeDsymbol(), 0, context);
							}
						}
						
						symbols.add(s);
					}
					
					if (!lazy.isRunningSemantic()) {
						for(Dsymbol sym : symbols) {
							lazy.runMissingSemantic(sym, context);
						}
					} else {
						assignParent(symbols);
					}
					
					s = symbols.get(0);
				}
				
				while(s.isAttribDeclaration() != null) {
					if (s instanceof ProtDeclaration) {
						s = ((ProtDeclaration) s).decl.get(0);
					} else if (s instanceof StorageClassDeclaration){
						s = ((StorageClassDeclaration) s).decl.get(0);
					}
				}
			}
		}
		
		if (s == null) {
			if ((flags & 1) == 0) {
				if (pendingPrivateImports != null) {
					for(Dsymbol imp : pendingPrivateImports) {
						imp.addMember(lazy.semanticScope(), lazy.asScopeDsymbol(), 0, context);
						lazy.runMissingSemantic(imp, context);
					}
					pendingPrivateImports = null;
				}
			}
			if (pendingPublicImports != null) {
				for(Dsymbol imp : pendingPublicImports) {
					imp.addMember(lazy.semanticScope(), lazy.asScopeDsymbol(), 0, context);
					lazy.runMissingSemantic(imp, context);
				}
				pendingPublicImports = null;
			}
		}

		if (s == null) {
			s = lazy.super_search(loc, ident, flags, context);
		}
		
		return s;
	}

	FillResult fillJavaElementMemebrsCache(SemanticContext context) {
		javaElementMembersCache = new HashtableOfCharArrayAndObject();
		List<Dsymbol> privateImports = new ArrayList<Dsymbol>();
		List<Dsymbol> publicImports = new ArrayList<Dsymbol>();
		FillResult result = null ;
		try {
			result = lazy.builder().fillJavaElementMembersCache(lazy, lazy.getJavaElement().getChildren(), lazy.members(), context);
			javaElementMembersCache = result.javaElementMembersCache;
			privateImports = result.privateImports;
			publicImports = result.publicImports;
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		if (!privateImports.isEmpty()) {
			pendingPrivateImports = privateImports;	
		}
		if (!publicImports.isEmpty()) {
			pendingPublicImports = publicImports;	
		}
		
		if (result.hasMixinDeclaration || result.hasStaticIf || result.hasAnon) {
			cancelLazyness = true;
			lazy.members(new Dsymbols());
			try {
				lazy.builder().fill(lazy.getModule(), lazy.members(), lazy.getJavaElement().getChildren(), null);
			} catch (JavaModelException e) {
				Util.log(e);
			}
			
			int size = lazy.members().size();
			
			if (!lazy.isRunningSemantic()) {
				for (int i = 0; i < size; i++) {
					Dsymbol sym = lazy.members().get(i);

					sym.addMember(lazy.semanticScope(), lazy.asScopeDsymbol(), 0, context);
					lazy.runMissingSemantic(sym, context);
				}
			} else {
				assignParent(lazy.members());
			}
		} else {
			if (result.pendingSemantic != null) {
				for(Dsymbol s : result.pendingSemantic) {
					lazy.runMissingSemantic(s, context);
				}
			}
		}
		
		return result;
	}
	
	public void runMissingSemantic(Dsymbol sym, SemanticContext context) {
		context.muteProblems++;
		if (lazy.semanticScope() != null) {			
			sym.semantic(Scope.copy(lazy.semanticScope()), context);
		}
		if (!lazy.isUnlazy()) {
			if (lazy.semantic2Scope() != null) {
				sym.semantic2(Scope.copy(lazy.semantic2Scope()), context);
			}
			if (lazy.semantic3Scope() != null) {
				sym.semantic3(Scope.copy(lazy.semantic3Scope()), context);
			}
		}
		context.muteProblems--;
	}
	
	private void assignParent(Dsymbols members) {
		for(Dsymbol sym : members) {
			if (sym instanceof AttribDeclaration) {
				assignParent(((AttribDeclaration) sym).decl);
			} else {
				sym.parent = (Dsymbol) this.lazy;
			}
		}
	}

}
