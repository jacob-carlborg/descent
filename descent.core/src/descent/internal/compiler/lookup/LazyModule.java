package descent.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;

import descent.core.IJavaElement;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.lookup.ModuleBuilder.FillResult;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.Id;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.ProtDeclaration;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StorageClassDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.core.util.Util;

/**
 * Module which has it's search method optimized. Normaly a non-lazy module does the following:
 * - load up members, either from the source file or from the D model
 * - run semantic on each of those, even if the module of our interest (in which we'll provide
 *   autocompletion, go to definition, etc.) doesn't use all of the analyzed symbols.
 *   
 * To enhance this, the idea is the following:
 * - members are initialized to empty
 * - in each semantic pass the scope is saved
 * - when a search is requested we do the following:
 *   - I search it in my symbol table (symtab)
 *     - If it's there, I'm done
 *     - If not
 *       - If my imports are not initialized
 *         - I initialize my imports (to speed things up, CompilationUnitStructureRequestor stores the position of the last import location)
 *         - Run the semantic for them
 *       - Search in my cache (ident x IJavaElement[]) 
 *         - If no search was found, do normal search (super.search)
 *         - If a search was found
 *           - If it's not a top level element (hard)
 *             - act as if this module is not lazy: load members, run semantic, etc.
 *             - except for anonymous enums
 *           - If it's a top level member
 *             - I convert it to a Dsymbol
 *             - run semantic for it
 *             - return it
 *     
 * This process is only feasible for "easy" modules: modules which don't contain
 * mixin declartions or static if declarations (how do we know if a symbol is actually
 * found by expanding a mixin declaration, or how do we know a static if holds when
 * building the cache?). In the case the module is "hard"... well, it won't be a LazyModule,
 * it will be a Module. This is determined by the ModuleBuilder class.
 */
public class LazyModule extends Module implements ILazy {
	
	public boolean LAZY_CLASSES = true;
	public boolean LAZY_INTERFACES = true;
	public boolean LAZY_STRUCTS = true;

	private final ModuleBuilder builder;
	private HashtableOfCharArrayAndObject javaElementMembersCache;
	private boolean importsWereInitialized;
	private final HashtableOfCharArrayAndObject topLevelIdentifiers;
	private final int lastImportLocation;
	
	private Scope semanticScope;
	private Scope semantic2Scope;
	private Scope semantic3Scope;
	

	public LazyModule(String filename, IdentifierExp ident, ModuleBuilder builder, HashtableOfCharArrayAndObject topLevelIdentifiers, int lastImportLocation) {
		super(filename, ident);
		this.builder = builder;
		this.topLevelIdentifiers = topLevelIdentifiers;
		this.lastImportLocation = lastImportLocation;
	}
	
	private Module unlazyOne;
	public Module unlazy(SemanticContext context) {
		if (unlazyOne == null) {
			unlazyOne = builder.buildNonLazyModule(javaElement);
			unlazyOne.semantic(context);
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
	public Dsymbol search(Loc loc, char[] ident, int flags,
			SemanticContext context) {
		Dsymbol s = symtab != null ? symtab.lookup(ident) : null;
		
		if (s == null) {
			
			// Only build my imports at first
			if (!importsWereInitialized) {
				importsWereInitialized = true;
				List<Dsymbol> privateImports = new ArrayList<Dsymbol>();
				List<Dsymbol> publicImports = new ArrayList<Dsymbol>();
				try {
					builder.fillImports(this, this.javaElement.getChildren(), privateImports, publicImports, context, lastImportLocation);
				} catch (JavaModelException e) {
					Util.log(e);
				}
				
				for(Dsymbol imp : privateImports) {
					imp.addMember(semanticScope, this, 0, context);
					runMissingSemantic(imp, context);
				}
				
				for(Dsymbol imp : publicImports) {
					imp.addMember(semanticScope, this, 0, context);
					runMissingSemantic(imp, context);
				}
			}
			
			boolean easy = true;
			if (javaElementMembersCache == null) {
				Object target = topLevelIdentifiers.get(ident);
				
				if (target != null) {
					if (target instanceof IJavaElement) {
						IJavaElement element = (IJavaElement) target;
						if (isEasy(element)) {
							s = processTarget(context, target);
						} else {
							easy = false;
						}
					} else {
						List<IJavaElement> list = (List<IJavaElement>) target;
						for(IJavaElement element : list) {
							if (!isEasy(element)) {
								easy = false;
								break;
							}
						}
						
						if (easy) {
							s = processTarget(context, target);
						}
					}
				} else {
					easy = false;
				}
			} else {
				easy = false;
			}
			
			if (!easy) {
				if (topLevelIdentifiers.containsKey(ident)) {
					if (javaElementMembersCache == null) {
						javaElementMembersCache = new HashtableOfCharArrayAndObject();
						List<Dsymbol> privateImports = new ArrayList<Dsymbol>();
						List<Dsymbol> publicImports = new ArrayList<Dsymbol>();
						FillResult result = null;
						try {
							result = builder.fillJavaElementMembersCache(this, this.javaElement.getChildren(), javaElementMembersCache, members, privateImports, publicImports, context);
						} catch (JavaModelException e) {
							Util.log(e);
						}
						
						// Anonymous enums are "hard" for me :-P
						if (result != null && result.hasAnonEnum) {
							return search(loc, ident, flags, context);
						}
					}
					
					Object target = javaElementMembersCache.get(ident);
					if (target != null) {
						s = processTarget(context, target);
					}
				}
			}
		}
		
		// Optimization for the well know type Object:
		// If we are a lazy module and we don't define Object and we are request to search for Object
		// by this module (allowing search in other modules), then it's the well known Object.
		// This optimization is here because if only Object needs to be searched and we don't
		// do this, all private imports will be resolved, thus slowing everything unnecessarily
		if (s == null && builder != null && (flags & 1) == 0 && CharOperation.equals(Id.Object, ident) && !topLevelIdentifiers.containsKey(ident)) {
			s = context.ClassDeclaration_object;
		}
		
		if (s == null) {
			s = super.search(loc, ident, flags, context);
		}
		
		return s;
	}

	private boolean isEasy(IJavaElement element) {
		return element.getParent().getElementType() == IJavaElement.COMPILATION_UNIT;
	}

	private Dsymbol processTarget(SemanticContext context, Object target) {
		Dsymbol s = null;
		
		if (this.symtab == null) {
			this.symtab = new DsymbolTable();
		}
		
		if (target instanceof IJavaElement) {
			try {
				boolean done = false;
				if (target instanceof IType) {
					IType type = (IType) target;
					if (!type.isTemplate() && !type.isForwardDeclaration()) {
						if (LAZY_CLASSES && type.isClass()) {
							ClassDeclaration cd = new LazyClassDeclaration(builder.getLoc(this, type), ModuleBuilder.getIdent(type), builder.getBaseClasses(type), builder);
							cd.setJavaElement(type);
							cd.members = new Dsymbols();
							s = builder.wrap(cd, type);
							members.add(s);
							done = true;
						} else if (LAZY_INTERFACES && type.isInterface()) {
							InterfaceDeclaration cd = new LazyInterfaceDeclaration(builder.getLoc(this, type), ModuleBuilder.getIdent(type), builder.getBaseClasses(type), builder);
							cd.setJavaElement(type);
							cd.members = new Dsymbols();
							s = builder.wrap(cd, type);
							members.add(s);
							done = true;
						} else if (LAZY_STRUCTS && type.isStruct()) {
							StructDeclaration cd = new LazyStructDeclaration(builder.getLoc(this, type), ModuleBuilder.getIdent(type), builder);
							cd.setJavaElement(type);
							cd.members = new Dsymbols();
							s = builder.wrap(cd, type);
							members.add(s);
							done = true;
						}
					}
				}

				if (!done) {
					try {
						builder.fill(this, members, null, (IJavaElement) target);
					} catch (JavaModelException e) {
						Util.log(e);
					}
				}
			} catch (JavaModelException e) {
				Util.log(e);
			}
			
			s = members.get(members.size() - 1);
			s.addMember(this.semanticScope, this, 0, context);
			runMissingSemantic(s, context);
		} else {
			Dsymbols symbols = new Dsymbols();
			
			List<IJavaElement> elemsList = (List<IJavaElement>) target;
			for(IJavaElement elem : elemsList) {
				try {
					builder.fill(this, members, null, (IJavaElement) elem);
				} catch (JavaModelException e) {
					Util.log(e);
				}
				
				s = members.get(members.size() - 1);
				if (semanticScope != null) {
					s.addMember(semanticScope, this, 0, context);
				}
				
				symbols.add(s);
			}
			
			for(Dsymbol sym : symbols) {
				runMissingSemantic(sym, context);
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
		
		return s;
	}
	
	public void runMissingSemantic(Dsymbol sym, SemanticContext context) {
		context.muteProblems++;
		if (semanticScope != null) {
			sym.semantic(Scope.copy(semanticScope), context);
		}
		if (semantic2Scope != null) {
			sym.semantic2(Scope.copy(semantic2Scope), context);
		}
		if (semantic3Scope != null) {
			sym.semantic3(Scope.copy(semantic3Scope), context);
		}
		context.muteProblems--;
	}
	
	public Scope getSemanticScope() {
		return semanticScope;
	}
	
	public ScopeDsymbol asScopeDsymbol() {
		return this;
	}
	
	public IdentifierExp getIdent() {
		return ident;
	}

}
