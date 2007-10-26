package descent.internal.compiler.parser;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

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
			Dsymbol s = members.get(i);
			s.addMember(null, sc.scopesym, 1, context);
		}

		// Pass 1 semantic routines: do public side of the definition
		for (int i = 0; i < size(members); i++) {
			Dsymbol s = members.get(i);
			s.semantic(sc, context);
		}

		runDeferredSemantic(context);

		sc = sc.pop();
		sc.pop();

		semanticdone = semanticstarted;
	}

	@Override
	public void semantic2(Scope scope, SemanticContext context) {
		if (context.Module_deferred != null
				&& context.Module_deferred.size() > 0) {
			for (Dsymbol sd : context.Module_deferred) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.CannotResolveForwardReference, 0, sd.start,
						sd.length));
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

		Dsymbol s;
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

	public boolean needModuleInfo() {
		return needmoduleinfo /* global.params.cov */;
	}

	public static Module load(Loc loc, Identifiers packages,
			IdentifierExp ident, SemanticContext context) {
		Module m;
		String filename;
		String moduleName;

		// Build module filename by turning:
		//	foo.bar.baz
		// into:
		//	foo\bar\baz
		filename = ident.toChars();
		moduleName = filename;
		if (packages != null && size(packages) != 0) {
			OutBuffer buf1 = new OutBuffer();
			OutBuffer buf2 = new OutBuffer();
			int i;

			for (i = 0; i < size(packages); i++) {
				IdentifierExp pid = packages.get(i);

				String pidChars = pid.toChars();
				buf1.writestring(pidChars);
				buf2.writestring(pidChars);
				if (context._WIN32) {
					buf1.writeByte('\\');
				} else {
					buf1.writeByte('/');
				}
				buf2.writestring('.');
			}
			buf1.writestring(filename);
			buf2.writestring(filename);
			filename = buf1.extractData();
			moduleName = buf2.extractData();
		}

		m = new Module(filename, ident);
		m.loc = loc;
		m.moduleName = moduleName;

		/* Search along global.path for .di file, then .d file.
		 */
		File result = null;
		String resultRelative = null;

		File fdi = new File(filename + ".di");
		File fd = new File(filename + ".d");

		if (fdi.exists()) {
			result = fdi;
		} else if (fd.exists()) {
			result = fd;
		} else if (null == context.global.path) {

		} else {
			for (int i = 0; i < size(context.global.path); i++) {
				String p = context.global.path.get(i);
				File n = new File(p, fdi.toString());
				if (n.exists()) {
					result = n;
					resultRelative = fdi.toString();
					break;
				}
				n = new File(p, fd.toString());
				if (n.exists()) {
					result = n;
					resultRelative = fd.toString();
					break;
				}
			}
		}

		if (result != null) {
			m.srcfile = result;
		}

		char[] contents = getContents(result);
		if (contents == null) {
			int start = packages == null || packages.size() == 0 ? ident.start : packages.get(0).start;
			int length = ident.start + ident.length - start;
			
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.ImportCannotBeResolved, 0, start, length, new String[] { filename.replace(context._WIN32 ? '\\' : '/', '.') }));
			
			return m;
		}

		Parser parser = new Parser(context.Module_rootModule.apiLevel, contents, resultRelative.toCharArray());
		parser.parseModuleObj(m);

		context.afterParse(m);

		// If we're in object.d, assign the well known class declarations
		if ("object".equals(filename)) {
			for (Dsymbol symbol : m.members) {
				if (symbol.ident == null || symbol.ident.ident == null) {
					continue;
				}

				if (equals(symbol.ident, Id.Object)) {
					context.ClassDeclaration_object = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.ClassInfo)) {
					context.ClassDeclaration_classinfo = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo)) {
					context.Type_typeinfo = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_Class)) {
					context.Type_typeinfoclass = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_Interface)) {
					context.Type_typeinfointerface = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_Struct)) {
					context.Type_typeinfostruct = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_Typedef)) {
					context.Type_typeinfotypedef = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_Pointer)) {
					context.Type_typeinfopointer = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_Array)) {
					context.Type_typeinfoarray = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_StaticArray)) {
					context.Type_typeinfostaticarray = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_AssociativeArray)) {
					context.Type_typeinfoassociativearray = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_Enum)) {
					context.Type_typeinfoenum = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_Function)) {
					context.Type_typeinfofunction = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_Delegate)) {
					context.Type_typeinfodelegate = (ClassDeclaration) symbol;
				} else if (equals(symbol.ident, Id.TypeInfo_Tuple)) {
					context.Type_typeinfotypelist = (ClassDeclaration) symbol;
				}
			}
		}

		return m;
	}

	private static char[] getContents(File file) {
		try {
			char[] contents = new char[(int) file.length()];
			FileReader r = new FileReader(file);
			r.read(contents);
			r.close();
			return contents;
		} catch (Exception e) {
			return null;
		}
	}

	// PERHAPS void inlineScan();

}
