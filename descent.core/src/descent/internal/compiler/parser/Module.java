package descent.internal.compiler.parser;

import java.io.File;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class Module extends Package implements IModule {

	public int apiLevel;
	public ModuleDeclaration md;
	public List<IProblem> problems;
	public Comment[] comments;
	public Pragma[] pragmas;
	public int[] lineEnds;
	public int semanticstarted; // has semantic() been started?
	public int semanticdone; // has semantic() been done?
	public boolean needmoduleinfo;
	public IModule importedFrom;

	public boolean insearch;

	public char[] searchCacheIdent;
	public int searchCacheFlags;
	public IDsymbol searchCacheSymbol;

	public long debuglevel; // debug level
	public List<char[]> debugids; // debug identifiers
	public List<char[]> debugidsNot; // forward referenced debug identifiers

	public long versionlevel;
	public List<char[]> versionids; // version identifiers
	public List<char[]> versionidsNot; // forward referenced version identifiers

	public Array aimports; // all imported modules

	public File srcfile; // absolute path
	public char[] arg;
	public String moduleName; // foo.bar

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
		if (context.global.errors > 0) {
			return;
		}

		semantic2(null, context);
		if (context.global.errors > 0) {
			return;
		}

		semantic3(null, context);
	}

	@Override
	public void semantic(Scope scope, SemanticContext context) {
		if (semanticstarted != 0) {
			return;
		}

		semanticstarted = 1;

		// Note that modules get their own scope, from scratch.
		// This is so regardless of where in the syntax a module
		// gets imported, it is unaffected by context.
		Scope sc = Scope.createGlobal(this, context);

		if (ident == null || ident.ident != Id.object) {
			Import im = new Import(Loc.ZERO, null,
					new IdentifierExp(Id.object), null, false);
			if (members == null) {
				members = new Dsymbols();
			}
			members.add(0, im);
		}

		symtab = new DsymbolTable();

		// Add all symbols into module's symbol table
		for (int i = 0; i < size(members); i++) {
			IDsymbol s = members.get(i);
			s.addMember(null, sc.scopesym, 1, context);
		}

		// Pass 1 semantic routines: do public side of the definition
		for (int i = 0; i < size(members); i++) {
			IDsymbol s = members.get(i);
			s.semantic(sc, context);
			runDeferredSemantic(context);
		}		

		sc = sc.pop();
		sc.pop();

		semanticdone = semanticstarted;
	}

	@Override
	public void semantic2(Scope scope, SemanticContext context) {
		if (context.Module_deferred != null
				&& context.Module_deferred.size() > 0) {
			for (IDsymbol sd : context.Module_deferred) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.CannotResolveForwardReference, sd));
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

		// Pass 2 semantic routines: do initializers and function bodies
		if (members != null) {
			for (int i = 0; i < members.size(); i++) {
				IDsymbol s;
				s = members.get(i);
				s.semantic2(sc, context);
			}
		}

		sc = sc.pop();
		sc.pop();
		semanticdone = semanticstarted;
	}

	@Override
	public void semantic3(Scope scope, SemanticContext context) {
		if (semanticstarted >= 3) {
			return;
		}

		if (semanticstarted != 2) {
			throw new IllegalStateException("assert(semanticstarted == 2);");
		}
		semanticstarted = 3;

		// Note that modules get their own scope, from scratch.
		// This is so regardless of where in the syntax a module
		// gets imported, it is unaffected by context.
		Scope sc = Scope.createGlobal(this, context); // create root scope

		// Pass 3 semantic routines: do initializers and function bodies
		if (members != null) {
			for (int i = 0; i < members.size(); i++) {
				IDsymbol s;
				s = members.get(i);
				s.semantic3(sc, context);
			}
		}

		sc = sc.pop();
		sc.pop();
		semanticdone = semanticstarted;
	}

	public void addDeferredSemantic(Dsymbol s, SemanticContext context) {
		if (context.Module_deferred == null) {
			context.Module_deferred = new Dsymbols();
		}

		// Don't add it if it is already there
		for (int i = 0; i < context.Module_deferred.size(); i++) {
			IDsymbol sd = context.Module_deferred.get(i);

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

			IDsymbol[] todo = new Dsymbol[size(context.Module_deferred)];
			for (int i = 0; i < size(context.Module_deferred); i++) {
				todo[i] = context.Module_deferred.get(i);
			}
			context.Module_deferred.clear();

			for (int i = 0; i < len; i++) {
				IDsymbol s = todo[i];

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
	public IDsymbol search(Loc loc, char[] ident, int flags,
			SemanticContext context) {
		
		/* Since modules can be circularly referenced,
		 * need to stop infinite recursive searches.
		 */

		IDsymbol s;
		if (insearch) {
			s = null;
		} else if (equals(searchCacheIdent, ident)
				&& searchCacheFlags == flags) {
			s = searchCacheSymbol;
		} else {
			insearch = true;
			s = super.search(loc, ident, flags, context);
			insearch = false;

			searchCacheIdent = ident;
			searchCacheSymbol = s;
			searchCacheFlags = flags;
		}
		
		return s;
	}

	public boolean needmoduleinfo() {
		return needmoduleinfo /* global.params.cov */;
	}

	public static IModule load(Loc loc, Identifiers packages,
			IdentifierExp ident, SemanticContext context) {
		
		// Delegate to SemanticContext in order to start resolving the Descent way
		return context.load(loc, packages, ident);
	}
	
	public void importedFrom(IModule module) {
		this.importedFrom = module;
	}
	
	public IModule importedFrom() {
		return this.importedFrom;
	}
	
	public void needmoduleinfo(boolean value) {
		this.needmoduleinfo = value;
	}
	
	public int semanticdone() {
		return semanticdone;
	}
	
	public List<char[]> debugids() {
		return debugids;
	}
	
	public void debugids(List<char[]> debugids) {
		this.debugids = debugids;
	}
	
	public List<char[]> debugidsNot() {
		return debugidsNot;
	}
	
	public void debugidsNot(List<char[]> debugidsNot) {
		this.debugidsNot = debugidsNot;
	}
	
	public long debuglevel() {
		return debuglevel;
	}
	
	public void debuglevel(long debuglevel) {
		this.debuglevel = debuglevel;
	}
	
	public List<char[]> versionids() {
		return versionids;
	}
	
	public void versionids(List<char[]> versionids) {
		this.versionids = versionids;
	}
	
	public List<char[]> versionidsNot() {
		return versionidsNot;
	}
	
	public void versionidsNot(List<char[]> versionidsNot) {
		this.versionidsNot = versionidsNot;
	}
	
	public long versionlevel() {
		return versionlevel;
	}
	
	public void versionlevel(long versionlevel) {
		this.versionlevel = versionlevel;
	}
	
	public IModuleDeclaration md() {
		return md;
	}

	// PERHAPS void inlineScan();

}
