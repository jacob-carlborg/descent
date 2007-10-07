package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import descent.internal.compiler.parser.ast.NaiveASTFlattener;

// DMD 1.020
public class Module extends Package {

	public int apiLevel;
	public ModuleDeclaration md;
	public List<IProblem> problems;
	public Comment[] comments;
	public Pragma[] pragmas;
	public int[] lineEnds;
	public int semanticstarted; // has semantic() been started?
	public int semanticdone; // has semantic() been done?
	public Dsymbols deferred;
	public boolean needmoduleinfo;
	public Module importedFrom;
	
	public boolean insearch;
	
	/* PERHAPS single-cached searching... I think we can do better than this
	public char[] searchCacheIdent;
	public int searchCacheFlags;
	public Dsymbol searchCacheSymbol; */

	public long debuglevel; // debug level
	public List<char[]> debugids; // debug identifiers
	public List<char[]> debugidsNot; // forward referenced debug identifiers

	public long versionlevel;
	public List<char[]> versionids; // version identifiers
	public List<char[]> versionidsNot; // forward referenced version
										// identifiers

	public Module(Loc loc) {
		super(loc);
		deferred = new Dsymbols();
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
		if (semanticstarted != 0)
			return;

		semanticstarted = 1;

		// Note that modules get their own scope, from scratch.
		// This is so regardless of where in the syntax a module
		// gets imported, it is unaffected by context.
		Scope sc = Scope.createGlobal(this, context);

		/*
		 * TODO // Add import of "object" if this module isn't "object" if
		 * (ident != Id::object) { Import *im = new Import(0, NULL, Id::object,
		 * NULL, 0); members->shift(im); }
		 */

		symtab = new DsymbolTable();

		// TODO This is the current replacement of Add import of "object" if
		// this module isn't "object". Use ident and pass ident to Module's ctor
		if (md == null || md.id == null || 
				!CharOperation.equals(md.id.ident, Id.object)) {
			symtab.insert(context.object);
			symtab.insert(context.classinfo);
			symtab.insert(context.typeinfo);
			symtab.insert(context.typeinfoclass);
			symtab.insert(context.typeinfointerface);
			symtab.insert(context.typeinfostruct);
			symtab.insert(context.typeinfotypedef);
			symtab.insert(context.typeinfopointer);
			symtab.insert(context.typeinfoarray);
			symtab.insert(context.typeinfostaticarray);
			symtab.insert(context.typeinfoassociativearray);
			symtab.insert(context.typeinfoenum);
			symtab.insert(context.typeinfofunction);
			symtab.insert(context.typeinfodelegate);
			symtab.insert(context.typeinfotypelist);
		}

		if (members != null) {

			// Add all symbols into module's symbol table
			for (Dsymbol s : members) {
				s.addMember(null, sc.scopesym, 1, context);
			}

			// Pass 1 semantic routines: do public side of the definition
			for (int i = 0; i < members.size(); i++) {
				Dsymbol s;
				s = members.get(i);
				s.semantic(sc, context);
			}

			runDeferredSemantic(context);
		}

		sc = sc.pop();
		sc.pop();

		semanticdone = semanticstarted;
	}

	@Override
	public void semantic2(Scope scope, SemanticContext context) {
		if (deferred != null && deferred.size() > 0) {
			for (Dsymbol sd : deferred) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotResolveForwardReference, 0, sd.start, sd.length));
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
				Dsymbol s;
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
				Dsymbol s;
				s = members.get(i);
				s.semantic3(sc, context);
			}
		}

		sc = sc.pop();
		sc.pop();
		semanticdone = semanticstarted;
	}

	public void addDeferredSemantic(Dsymbol s) {
		if (deferred == null) {
			deferred = new Dsymbols();
		}

		// Don't add it if it is already there
		for (int i = 0; i < deferred.size(); i++) {
			Dsymbol sd = (Dsymbol) deferred.get(i);

			if (sd == s) {
				return;
			}
		}

		deferred.add(s);
	}

	public void toModuleArray() {
		// TODO semantic
	}

	public void toModuleAssert() {
		// TODO semantic
	}

	public static int nested;

	public void addDefferedSemantic(Dsymbol s)
	{
		// Don't add it if it is already there
		for (int i = 0; i < deferred.size(); i++)
		{
			Dsymbol sd = (Dsymbol) deferred.get(i);

			if (sd == s)
				return;
		}

		//printf("Module.addDeferredSemantic('%s')\n", s.toChars());
		deferred.add(s);
	}
	
	public void runDeferredSemantic(SemanticContext context) {
		int len;

		if (nested != 0)
			return;
		nested++;

		do {
			context.dprogress = 0;
			len = deferred.size();
			if (0 == len) {
				break;
			}

			Dsymbol[] todo = new Dsymbol[deferred.size()];
			for (int i = 0; i < deferred.size(); i++) {
				todo[i] = deferred.get(i);
			}
			deferred.clear();

			for (int i = 0; i < len; i++) {
				Dsymbol s = todo[i];

				s.semantic(null, context);
			}
		} while (deferred.size() < len || context.dprogress != 0); // while
																	// making
																	// progress
		nested--;
	}

	@Override
	public void appendBinding(StringBuilder sb) {
		if (md != null) {
			NaiveASTFlattener f = new NaiveASTFlattener();
			f.visitModuleDeclarationName(md);
			sb.append(f.getResult());
		}
	}

	@Override
	public String kind()
	{
		return "module";
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context)
	{
		// TODO Auto-generated method stub
		super.toCBuffer(buf, hgs, context);
	}

	@Override
	public Dsymbol search(Loc loc, char[] ident, int flags,
			SemanticContext context)
	{
		/* Since modules can be circularly referenced,
		 * need to stop infinite recursive searches.
		 */

		//printf("%s Module.search('%s', flags = %d) insearch = %d\n", toChars(), ident.toChars(), flags, insearch);
		Dsymbol s;
		if (insearch)
			s = null;
		/* PERHPAS single-cached searching
		else if (CharOperation.equals(searchCacheIdent, ident)
				&& searchCacheFlags == flags)
			s = searchCacheSymbol; */
		else
		{
			insearch = true;
			s = super.search(loc, ident, flags, context);
			insearch = false;
			
			/* searchCacheIdent = ident;
			searchCacheSymbol = s;
			searchCacheFlags = flags; */
		}
		return s;
	}
	
	public boolean needModuleInfo()
	{
		return needmoduleinfo /* global.params.cov */;
	}
	
	// PERHAPS void inlineScan();
	
}
