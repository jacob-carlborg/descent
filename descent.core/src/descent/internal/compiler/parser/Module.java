package descent.internal.compiler.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.ICompilationUnit;
import descent.core.IConditional;
import descent.core.IImportDeclaration;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IParent;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.Signature;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.lookup.ModuleBuilder;
import descent.internal.compiler.lookup.SemanticRest;
import descent.internal.compiler.parser.ast.IASTVisitor;
import descent.internal.core.util.Util;


public class Module extends Package {
	
	private final static boolean FAST_SEARCH = false;

	public int apiLevel;
	public ModuleDeclaration md;
	public List<IProblem> problems;
	public Comment[] comments;
	public Pragma[] pragmas;
	public int[] lineEnds;
	public int semanticstarted; // has semantic() been started?
	public int semanticdone; // has semantic() been done?
	public boolean needmoduleinfo;
	public Module importedFrom;

	public boolean insearch;

	public char[] searchCacheIdent;
	public int searchCacheFlags;
	public Dsymbol searchCacheSymbol;

	public long debuglevel; // debug level
	public List<char[]> debugids; // debug identifiers
	public List<char[]> debugidsNot; // forward referenced debug identifiers

	public long versionlevel;
	public List<char[]> versionids; // version identifiers
	public List<char[]> versionidsNot; // forward referenced version identifiers

	public Array aimports; // all imported modules

	public File srcfile; // absolute path
	public char[] arg;
	
	// Very important: this field must be set to the module name
	// if signatures are to be requested
	public String moduleName; // foo.bar 
	private String signature; // Descent signature
	private ICompilationUnit javaElement;
	
	public SemanticRest rest;
	
	public ModuleBuilder builder;
	public HashtableOfCharArrayAndObject javaElementMembersCache;
	
	private Scope semanticScope;
	private Scope semantic2Scope;
	private Scope semantic3Scope;

	public Module(String filename, IdentifierExp ident) {
		super(ident);
		importedFrom = this;
	}

	@Override
	public Module isModule() {
		return this;
	}

	@Override
	public int getNodeType() {
		return MODULE;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, md);
			TreeVisitor.acceptChildren(visitor, members);
			
			acceptSynthetic(visitor);
		}
		visitor.endVisit(this);
	}

	public boolean hasSyntaxErrors() {
		return problems.size() != 0;
	}

	public void semantic(SemanticContext context) {
		semantic(null, context);
		
		// COMMENTED THIS, since when there are syntax errors we would
		// like to have a better recovery. But halt if fatal was signaled.
//		if (context.global.errors > 0) {
//			return;
//		}
		if (context.fatalWasSignaled) {
			return;
		}

		// COMMENTED THIS, since when there are syntax errors we would
		// like to have a better recovery. But halt if fatal was signaled.
		semantic2(null, context);
//		if (context.global.errors > 0) {
//			return;
//		}
		if (context.fatalWasSignaled) {
			return;
		}

		semantic3(null, context);
	}

	@Override
	public void semantic(Scope scope, SemanticContext context) {
		long time = System.currentTimeMillis();
		
		if (rest != null && !rest.isConsumed()) {
			rest.setSemanticContext(null, context);
			return;
		}
		
		if (semanticstarted != 0) {
			return;
		}
		
//		for (int i = 0; i < nest; i++) {
//			System.out.print("  ");
//		}
//		System.out.println(this.moduleName);
//		nest++;

		semanticstarted = 1;

		// Note that modules get their own scope, from scratch.
		// This is so regardless of where in the syntax a module
		// gets imported, it is unaffected by context.
		Scope sc = Scope.createGlobal(this, context);
		semanticScope = sc;

		if (ident == null || ident.ident != Id.object) {
			Import im = new Import(Loc.ZERO, null,
					new IdentifierExp(Id.object), null, false);
			im.synthetic = true;
			if (members == null) {
				members = new Dsymbols();
			}
			members.add(0, im);
		}

		symtab = new DsymbolTable();
		
		boolean imObject = CharOperation.equals(ident.ident, Id.object);
		
		// Add all symbols into module's symbol table
		// But, if this i'm module object, assign to SemanticContext's variables
		// (this only happens the first time object is parsed
		if (imObject) {
			for (int i = 0; i < size(members); i++) {
				Dsymbol s = members.get(i);
				context.checkObjectMember(s);
				s.addMember(null, sc.scopesym, 1, context);
			}
		} else {
			// Add all symbols into module's symbol table
			for (int i = 0; i < size(members); i++) {
				Dsymbol s = members.get(i);
				s.addMember(null, sc.scopesym, 1, context);
			}
		}

		// Pass 1 semantic routines: do public side of the definition
		for (int i = 0; i < size(members); i++) {
			Dsymbol s = members.get(i);
			s.semantic(sc, context);
			runDeferredSemantic(context);
		}

		sc = sc.pop();
		sc.pop();

		semanticdone = semanticstarted;
		
		time = System.currentTimeMillis() - time;
		if (time > 10) {
			System.out.println("Module#semantic(" + moduleName + ") = " + time);
		}
		
//		nest--;
	}

	@Override
	public void semantic2(Scope scope, SemanticContext context) {
		if (javaElement != null && FAST_SEARCH) {
			return;
		}
		
		long time = System.currentTimeMillis();
		
		if (rest != null && !rest.isConsumed()) {
			rest.setSemanticContext(null, context);
			return;
		}
		
		if (context.Module_deferred != null
				&& context.Module_deferred.size() > 0) {
			for (Dsymbol sd : context.Module_deferred) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CannotResolveForwardReference, sd.getLineNumber(), sd.getErrorStart(), sd.getErrorLength()));
				}
			}
			return;
		}
		if (semanticstarted >= 2) {
			return;
		}

		if (semanticstarted != 1) {
			throw new IllegalStateException("assert(semanticstarted == 1);");
		}
		semanticstarted = 2;

		// Note that modules get their own scope, from scratch.
		// This is so regardless of where in the syntax a module
		// gets imported, it is unaffected by context.
		Scope sc = Scope.createGlobal(this, context); // create root scope
		semantic2Scope = sc;

		// Pass 2 semantic routines: do initializers and function bodies
		if (members != null) {
			for (int i = 0; i < members.size(); i++) {
				Dsymbol s;
				s = members.get(i);
				s.semantic2(sc, context);
			}
		}

		sc = sc.pop();
		sc.pop();
		semanticdone = semanticstarted;
		
		time = System.currentTimeMillis() - time;
		if (time > 10) {
			System.out.println("Module#semantic2(" + moduleName + ") = " + time);
		}
	}

	@Override
	public void semantic3(Scope scope, SemanticContext context) {
		if (javaElement != null && FAST_SEARCH) {
			return;
		}
		
		long time = System.currentTimeMillis();
		
		if (rest != null && !rest.isConsumed()) {
			rest.setSemanticContext(null, context);
			return;
		}
		
		if (semanticstarted >= 3) {
			return;
		}

		if (semanticstarted != 2) {
			// SEMANTIC
			// throw new IllegalStateException("assert(semanticstarted == 2);");
			return;
		}
		semanticstarted = 3;

		// Note that modules get their own scope, from scratch.
		// This is so regardless of where in the syntax a module
		// gets imported, it is unaffected by context.
		Scope sc = Scope.createGlobal(this, context); // create root scope
		semantic3Scope = sc;

		// Pass 3 semantic routines: do initializers and function bodies
		if (members != null) {
			for (int i = 0; i < members.size(); i++) {
				Dsymbol s;
				s = members.get(i);
				s.semantic3(sc, context);
			}
		}

		sc = sc.pop();
		sc.pop();
		semanticdone = semanticstarted;
		
		time = System.currentTimeMillis() - time;
		if (time > 10) {
			System.out.println("Module#semantic3(" + moduleName + ") = " + time);
		}
	}

	public void addDeferredSemantic(Dsymbol s, SemanticContext context) {
		if (context.Module_deferred == null) {
			context.Module_deferred = new Dsymbols();
		}

		// Don't add it if it is already there
		for (int i = 0; i < context.Module_deferred.size(); i++) {
			Dsymbol sd = context.Module_deferred.get(i);

			if (sd == s) {
				return;
			}
		}

		context.Module_deferred.add(s);
	}

	public void toModuleArray() {
		// TODO semantic
	}

	public void toModuleAssert() {
		// TODO semantic
	}

	public void runDeferredSemantic(SemanticContext context) {
		int len;

		if (context.Module_nested) {
			return;
		}
		context.Module_nested = true;

		do {
			context.Module_dprogress = 0;
			len = size(context.Module_deferred);
			if (0 == len) {
				break;
			}

			Dsymbol[] todo = new Dsymbol[size(context.Module_deferred)];
			for (int i = 0; i < size(context.Module_deferred); i++) {
				todo[i] = context.Module_deferred.get(i);
			}
			context.Module_deferred.clear();

			for (int i = 0; i < len; i++) {
				Dsymbol s = todo[i];

				s.semantic(null, context);
			}
		} while (size(context.Module_deferred) < len
				|| context.Module_dprogress != 0); // while
		// making
		// progress
		context.Module_nested = false;
	}

	@Override
	public String kind() {
		return "module";
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		// TODO Auto-generated method stub
		super.toCBuffer(buf, hgs, context);
	}

	@Override
	public Dsymbol search(Loc loc, char[] ident, int flags,
			SemanticContext context) {
		/* Since modules can be circularly referenced,
		 * need to stop infinite recursive searches.
		 */
		
		if ("_P_WAIT".equals(new String(ident))) {
			System.out.println(123456);
		}

		Dsymbol s = null;
		if (insearch) {
			s = null;
		} else if (ASTDmdNode.equals(this.searchCacheIdent, ident)
				&& this.searchCacheFlags == flags) {
			s = this.searchCacheSymbol;
		} else {
			s = symtab != null ? symtab.lookup(ident) : null;
			
			if (s == null && builder != null) {
				if (javaElementMembersCache == null) {
					javaElementMembersCache = new HashtableOfCharArrayAndObject();
					List<Dsymbol> imports = new ArrayList<Dsymbol>();
					boolean anon = false;
					try {						
						anon = fillJavaElementMembersCache(this.javaElement.getChildren(), members, imports, context);
					} catch (JavaModelException e) {
						Util.log(e);
					}
					
					// First process imports, because the other symbols may need
					// them for their semantic analysis
					if (!imports.isEmpty()) {
						for(Dsymbol imp : imports) {
							imp.addMember(semanticScope, this, 0, context);
							runMissingSemantic(imp, context);
						}
					}
					
					if (anon) {
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
							builder.fill(this, members, new ModuleBuilder.State(), (IJavaElement) target);
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
								builder.fill(this, members, new ModuleBuilder.State(), (IJavaElement) elem);
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
							if (semanticScope != null) {
								sym.semantic(semanticScope, context);
							}
							if (semantic2Scope != null) {
								sym.semantic2(semantic2Scope, context);
							}
							if (semantic3Scope != null) {
								sym.semantic3(semantic3Scope, context);
							}
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
			
			this.insearch = true;
			
			if (s == null) {
				s = super.search(loc, ident, flags, context);
			}
			
			this.insearch = false;

			this.searchCacheIdent = ident;			
			this.searchCacheSymbol = s;
			this.searchCacheFlags = flags;
		}
		
		return s;
	}

	private void runMissingSemantic(Dsymbol sym, SemanticContext context) {
		if (semanticScope != null) {
//			sym.addMember(semanticScope, this, 0, context);
			sym.semantic(semanticScope, context);
		}
		if (semantic2Scope != null) {
			sym.semantic2(semantic2Scope, context);
		}
		if (semantic3Scope != null) {
			sym.semantic3(semantic3Scope, context);
		}
	}

	// true if we encountered an anonymous enum
	private boolean fillJavaElementMembersCache(IJavaElement[] elements, Dsymbols symbols, List<Dsymbol> imports, SemanticContext context) {
		boolean anon = false;
		
		try {
			for(IJavaElement child : elements) {
				switch(child.getElementType()) {
				case IJavaElement.IMPORT_CONTAINER:
					anon |= fillJavaElementMembersCache(((IParent) child).getChildren(), symbols, imports, context);
					break;
				case IJavaElement.IMPORT_DECLARATION:
					Dsymbol s = builder.fillImportDeclaration(this, symbols, (IImportDeclaration) child);
					imports.add(s);
					break;
				case IJavaElement.CONDITIONAL:
					IConditional cond = (IConditional) child;
					if (cond.isStaticIfDeclaration()) {
						throw new IllegalStateException("Shouldn't reach this point");
					} else if (cond.isVersionDeclaration()) {
						String name = cond.getElementName();
						char[] nameC = name.toCharArray();
						try {
							long value = Long.parseLong(name);
							if (builder.config.isVersionEnabled(value)) {
								anon |= fillJavaElementMembersCache(cond.getThenChildren(), symbols, imports, context);
							} else {
								anon |= fillJavaElementMembersCache(cond.getElseChildren(), symbols, imports, context);
							}
						} catch(NumberFormatException e) {
							if (builder.config.isVersionEnabled(nameC)) {
								anon |= fillJavaElementMembersCache(cond.getThenChildren(), symbols, imports, context);
							} else {
								anon |= fillJavaElementMembersCache(cond.getElseChildren(), symbols, imports, context);
							}
						}
					} else if (cond.isDebugDeclaration()) {
						String name = cond.getElementName();
						char[] nameC = name.toCharArray();
						try {
							long value = Long.parseLong(name);
							if (builder.config.isDebugEnabled(value)) {
								anon |= fillJavaElementMembersCache(cond.getThenChildren(), symbols, imports, context);
							} else {
								anon |= fillJavaElementMembersCache(cond.getElseChildren(), symbols, imports, context);
							}
						} catch(NumberFormatException e) {
							if (builder.config.isDebugEnabled(nameC)) {
								anon |= fillJavaElementMembersCache(cond.getThenChildren(), symbols, imports, context);
							} else {
								anon |= fillJavaElementMembersCache(cond.getElseChildren(), symbols, imports, context);
							}
						}
					}
					break;
				case IJavaElement.INITIALIZER:
					IInitializer init = (IInitializer) child;
					if (init.isAlign()) {
						Dsymbols sub = new Dsymbols();
						anon |= fillJavaElementMembersCache(init.getChildren(), sub, imports, context);
						
						AlignDeclaration member = new AlignDeclaration(Integer.parseInt(init.getElementName()), sub);
						symbols.add(member);
					} else if (init.isDebugAssignment()) {
						throw new IllegalStateException("Shouldn't reach this point");
					} else if (init.isVersionAssignment()) {
						throw new IllegalStateException("Shouldn't reach this point");
					} else if (init.isMixin()) {
						throw new IllegalStateException("Shouldn't reach this point");
					} else if (init.isExtern()) {
						// Also try to lazily initialize things inside:
						// extern(C) {
						//   // ...
						// }
						
						Dsymbols sub = new Dsymbols();
						anon |= fillJavaElementMembersCache(init.getChildren(), sub, imports, context);
						
						LinkDeclaration member = new LinkDeclaration(ModuleBuilder.getLink(init), sub);
						members.add(builder.wrap(member, init));
					}
					break;
				case IJavaElement.FIELD:
				case IJavaElement.METHOD:
				case IJavaElement.TYPE:
					char[] ident = child.getElementName().toCharArray();
					if (ident == null || ident.length == 0) {
						// Anonymous: it must be an enum, at the top level there
						// isn't a use for an annonymous class, template, etc.
						IType type = (IType) child;
						if (type.isEnum()) {
							Dsymbol sym = builder.fillEnum(this, members, type, true /* surface */);
							sym.addMember(this.semanticScope, this, 0, context);
							runMissingSemantic(sym, context);
							anon = true;
						}
					} else {
						if (javaElementMembersCache.containsKey(ident)) {
							Object object = javaElementMembersCache.get(ident);
							
							if (object instanceof IJavaElement) {
								List<IJavaElement> elemsList = new ArrayList<IJavaElement>();
								elemsList.add((IJavaElement) object);
								elemsList.add(child);
								javaElementMembersCache.put(ident, elemsList);
							} else {
								List<IJavaElement> elemsList = (List<IJavaElement>) object;
								elemsList.add(child);
							}
						} else {
							javaElementMembersCache.put(ident, child);
						}
					}
					break;
				case IJavaElement.PACKAGE_DECLARATION:
					break;
				default:
					throw new IllegalStateException("Unknown type: " + child.getElementType());
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		return anon;
	}

	public static Module load(Loc loc, Identifiers packages,
			IdentifierExp ident, SemanticContext context) {
		
		// Delegate to SemanticContext in order to start resolving the Descent way
		return context.load(loc, packages, ident);
	}
	
	@Override
	public String getSignature() {
		if (signature == null) {
			StringBuilder sb = new StringBuilder();
			appendSignature(sb);
			signature = sb.toString();
		}
		return signature;
	}
	
	public void appendSignature(StringBuilder sb) {
		sb.append(Signature.C_MODULE);
		String[] pieces = moduleName.split("\\.");
		for(String piece : pieces) {
			sb.append(piece.length());
			sb.append(piece);
		}
	}
	
	public String getFullyQualifiedName() {
		return moduleName;
	}

	public void setJavaElement(ICompilationUnit unit) {
		this.javaElement = unit;
	}
	
	@Override
	public ICompilationUnit getJavaElement() {
		return javaElement;
	}
	
	public void consumeRestStructure() {
		if (rest != null && !rest.isStructureKnown()) {
			rest.buildStructure();
		}
	}
	
	public void consumeRest() {
		if (rest != null && !rest.isConsumed()) {
			rest.consume(this);
		}
	}

}
