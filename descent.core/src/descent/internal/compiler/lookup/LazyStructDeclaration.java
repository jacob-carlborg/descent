package descent.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;

import descent.core.IJavaElement;
import descent.core.JavaModelException;
import descent.internal.compiler.lookup.ModuleBuilder.FillResult;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.ProtDeclaration;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StorageClassDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.core.util.Util;

public class LazyStructDeclaration extends StructDeclaration implements ILazy {

	private final ModuleBuilder builder;
	private HashtableOfCharArrayAndObject javaElementMembersCache;
	private List<Dsymbol> pendingPublicImports;
	private List<Dsymbol> pendingPrivateImports;
	
	private Scope semanticScope;
	private Scope semantic2Scope;
	private Scope semantic3Scope;
	
	private boolean cancelLazyness;

	public LazyStructDeclaration(Loc loc, IdentifierExp id, ModuleBuilder builder) {
		super(loc, id);
		this.builder = builder;
	}
	
	private StructDeclaration unlazyOne;
	public StructDeclaration unlazy(SemanticContext context) {
		if (unlazyOne == null) {
			unlazyOne = new StructDeclaration(loc, ident);
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
		if (cancelLazyness) {
			return super.search(loc, ident, flags, context);
		}
		
		Dsymbol s = symtab != null ? symtab.lookup(ident) : null;
		
		if (s == null) {
			if (javaElementMembersCache == null) {
				javaElementMembersCache = new HashtableOfCharArrayAndObject();
				List<Dsymbol> privateImports = new ArrayList<Dsymbol>();
				List<Dsymbol> publicImports = new ArrayList<Dsymbol>();
				FillResult result = null ;
				try {
					result = builder.fillJavaElementMembersCache(this, this.javaElement.getChildren(), javaElementMembersCache, members, privateImports, publicImports, context);
				} catch (JavaModelException e) {
					Util.log(e);
				}
				
				if (result.hasMixinDeclaration || result.hasStaticIf) {
					cancelLazyness = true;
					members = new Dsymbols();
					try {
						builder.fill(getModule(), members, javaElement.getChildren(), null);
					} catch (JavaModelException e) {
						Util.log(e);
					}
					
					for(Dsymbol sym : members) {
						sym.addMember(semanticScope, this, 0, context);
						runMissingSemantic(sym, context);
					}
					
					return search(loc, ident, flags, context);
				}
				
				if (!privateImports.isEmpty()) {
					pendingPrivateImports = privateImports;	
				}
				if (!publicImports.isEmpty()) {
					pendingPublicImports = publicImports;	
				}
				
				// Anonymous enums are "hard" for me :-P
				if (result.hasAnonEnum) {
					return search(loc, ident, flags, context);
				}
			}
			
			Object target = javaElementMembersCache.get(ident);
			if (target != null) {
				if (this.symtab == null) {
					this.symtab = new DsymbolTable();
				}
				
				if (target instanceof IJavaElement) {
					try {
						builder.fill(this.getModule(), members, null, (IJavaElement) target);
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
							builder.fill(this.getModule(), members, null, (IJavaElement) elem);
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
			}
		}
		
		if (s == null) {
			if ((flags & 1) == 0) {
				if (pendingPrivateImports != null) {
					for(Dsymbol imp : pendingPrivateImports) {
						imp.addMember(semanticScope, this, 0, context);
						runMissingSemantic(imp, context);
					}
					pendingPrivateImports = null;
				}
			}
			if (pendingPublicImports != null) {
				for(Dsymbol imp : pendingPublicImports) {
					imp.addMember(semanticScope, this, 0, context);
					runMissingSemantic(imp, context);
				}
				pendingPublicImports = null;
			}
		}

		if (s == null) {
			s = super.search(loc, ident, flags, context);
		}
		
		return s;
	}
	
	public void runMissingSemantic(Dsymbol sym, SemanticContext context) {
		context.muteProblems++;
		if (semanticScope != null) {			
			sym.semantic(semanticScope, context);
		}
		if (semantic2Scope != null) {
			sym.semantic2(semantic2Scope, context);
		}
		if (semantic3Scope != null) {
			sym.semantic3(semantic3Scope, context);
		}
		context.muteProblems--;
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

}
