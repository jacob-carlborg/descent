package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

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
	
	public long debuglevel;	// debug level
	public List<char[]> debugids;		// debug identifiers
	public List<char[]> debugidsNot;		// forward referenced debug identifiers
	
	public long versionlevel;
	public List<char[]> versionids;		// version identifiers
	public List<char[]> versionidsNot;	// forward referenced version identifiers

	public Module(Loc loc) {
		super(loc);
		deferred = new Dsymbols();
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
		semantic2(null, context);
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

		// TODO This is the current replacement of Add import of "object" if this module isn't "object"
		if (ident == null || ident.ident != Id.object) {
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
			for (Dsymbol s : members) {
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
				sd.error("unable to resolve forward reference in definition");
			}
			return;
		}
		if (semanticstarted >= 2) {
			return;
		}
		Assert.isTrue(semanticstarted == 1);
		semanticstarted = 2;

		// Note that modules get their own scope, from scratch.
		// This is so regardless of where in the syntax a module
		// gets imported, it is unaffected by context.
		Scope sc = Scope.createGlobal(this, context); // create root scope

		// Pass 2 semantic routines: do initializers and function bodies
		if (members != null) {
			for (Dsymbol s : members) {
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
		Assert.isTrue(semanticstarted == 2);
		semanticstarted = 3;

		// Note that modules get their own scope, from scratch.
		// This is so regardless of where in the syntax a module
		// gets imported, it is unaffected by context.
		Scope sc = Scope.createGlobal(this, context); // create root scope

		// Pass 3 semantic routines: do initializers and function bodies
		if (members != null) {
			for (Dsymbol s : members) {
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
		} while (deferred.size() < len || context.dprogress != 0); // while making progress
		nested--;
	}
	
	@Override
	public void appendBinding(StringBuilder sb) {
		if (md != null) {
			NaiveASTFlattener f = new NaiveASTFlattener();
			f.visitModuleDeclarationName(md);
			sb.append(f.toString());
		}
	}

}
