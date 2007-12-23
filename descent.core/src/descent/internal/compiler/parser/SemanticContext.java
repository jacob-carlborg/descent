package descent.internal.compiler.parser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.env.INameEnvironment;

public class SemanticContext {

	public boolean BREAKABI = true;
	public boolean IN_GCC = false;
	public boolean _DH = true;

	// If DMD is being run on Win32
	public boolean _WIN32 = true;

	private IProblemRequestor problemRequestor;
	public Global global;
	public IJavaProject project;
	public INameEnvironment environment;

	// TODO file imports should be selectable in a dialog or something
	public Map<String, File> fileImports = new HashMap<String, File>();
	
	public ClassDeclaration ClassDeclaration_object;
	public ClassDeclaration ClassDeclaration_classinfo;
	public ClassDeclaration Type_typeinfo;
	public ClassDeclaration Type_typeinfoclass;
	public ClassDeclaration Type_typeinfointerface;
	public ClassDeclaration Type_typeinfostruct;
	public ClassDeclaration Type_typeinfotypedef;
	public ClassDeclaration Type_typeinfopointer;
	public ClassDeclaration Type_typeinfoarray;
	public ClassDeclaration Type_typeinfostaticarray;
	public ClassDeclaration Type_typeinfoassociativearray;
	public ClassDeclaration Type_typeinfoenum;
	public ClassDeclaration Type_typeinfofunction;
	public ClassDeclaration Type_typeinfodelegate;
	public ClassDeclaration Type_typeinfotypelist;

	public Type Type_tvoidptr;
	
	public Module Module_rootModule;
	public DsymbolTable Module_modules;
	public Array Module_amodules;
	public Dsymbols Module_deferred;
	public int Module_dprogress;
	public ClassDeclaration Module_moduleinfo;
	public boolean Module_nested = false;
	public int ASTDmdNode_idn;
	public int CompoundStatement_num;
	public Dsymbol TemplateAliasParameter_sdummy = null;
	public Expression TemplateValueParameter_edummy = null;
	public TypeInfoDeclaration[] Type_internalTI = new TypeInfoDeclaration[TY.values().length];
	
	public StringTable stringTable;

	public DsymbolTable st;
	public int muteProblems = 0;

	public SemanticContext(
			IProblemRequestor problemRequestor, 
			Module module,
			IJavaProject project,
			INameEnvironment environment,
			Global global) {
		this.problemRequestor = problemRequestor;
		this.Module_rootModule = module;
		this.global = global;
		this.project = project;
		this.environment = environment;
		this.stringTable = new StringTable();
		this.Type_tvoidptr = Type.tvoid.pointerTo(this);

		Module_init();
		afterParse(module);
	}

	private void Module_init() {
		this.Module_modules = new DsymbolTable();
	}
	
	/*
	 * This code is invoked by DMD after parsing a module.
	 */
	public void afterParse(Module module) {
		DsymbolTable dst;

		if (module.md != null) {
			module.ident = module.md.id;
			IDsymbol[] pparent = { module.parent };
			dst = Package.resolve(module.md.packages, pparent, null, this);
			module.parent = pparent[0];
		} else {
			dst = Module_modules;
		}

		// Update global list of modules
		if (null == dst.insert(module)) {
			if (module.md != null) {
				acceptProblem(Problem.newSemanticTypeError(
						IProblem.ModuleIsInMultiplePackages, module.md, new String[] { module.md.toChars(this) }));
			} else {
				acceptProblem(Problem.newSemanticTypeError(
						IProblem.ModuleIsInMultipleDefined, module.md));
			}
		} else {
			if (Module_amodules == null) {
				Module_amodules = new Dsymbols();
			}
			Module_amodules.add(module);
		}
	}

	public void acceptProblem(IProblem problem) {
//		System.out.println(problem);
		if (global.gag == 0 && muteProblems == 0 && problemRequestor != null) {
			problemRequestor.acceptProblem(problem);
		}
		global.errors++;
	}

	private int generatedIds;	

	public IdentifierExp generateId(String prefix) {
		String name = prefix + ++generatedIds;
		char[] id = name.toCharArray();
		return new IdentifierExp(Loc.ZERO, id);
	}

	public IFuncDeclaration genCfunc(Type treturn, char[] id) {
		IFuncDeclaration fd;
		TypeFunction tf;
		IDsymbol s;

		// See if already in table
		if (st == null)
			st = new DsymbolTable();
		s = st.lookup(id);
		if (s != null) {
			fd = s.isFuncDeclaration();
			Assert.isNotNull(fd);
			Assert.isTrue(fd.type().nextOf().equals(treturn));
		} else {
			tf = new TypeFunction(null, treturn, 0, LINK.LINKc);
			fd = new FuncDeclaration(Loc.ZERO, new IdentifierExp(id),
					STC.STCstatic, tf);
			fd.protection(PROT.PROTpublic);
			fd.linkage(LINK.LINKc);

			st.insert(fd);
		}
		return fd;
	}
	
	public IModule load(Loc loc, Identifiers packages,
			IdentifierExp ident) {
		
		// Build the compound module name
		char[][] compoundName = new char[(packages == null ? 0 : packages.size()) + 1][];
		if (packages != null) {
			for(int i = 0; i < packages.size(); i++) {
				compoundName[i] = packages.get(i).ident;
			}
		}
		compoundName[compoundName.length - 1] = ident.ident;
		
		IModule m = environment.findModule(compoundName);
		if (m == null) {
			int start = packages == null || packages.size() == 0 ? ident.start : packages.get(0).start;
			int length = ident.start + ident.length - start;
			
			acceptProblem(Problem.newSemanticTypeError(IProblem.ImportCannotBeResolved, ident.getLineNumber(), start, length, new String[] { CharOperation.toString(compoundName) }));
		}
		return m;
		
//		Module m;
//		String filename;
//		String moduleName;
//
//		// Build module filename by turning:
//		//	foo.bar.baz
//		// into:
//		//	foo\bar\baz
//		filename = ident.toChars();
//		moduleName = filename;
//		if (packages != null && ASTDmdNode.size(packages) != 0) {
//			OutBuffer buf1 = new OutBuffer();
//			OutBuffer buf2 = new OutBuffer();
//			int i;
//
//			for (i = 0; i < ASTDmdNode.size(packages); i++) {
//				IdentifierExp pid = packages.get(i);
//
//				String pidChars = pid.toChars();
//				buf1.writestring(pidChars);
//				buf2.writestring(pidChars);
//				if (_WIN32) {
//					buf1.writeByte('\\');
//				} else {
//					buf1.writeByte('/');
//				}
//				buf2.writestring('.');
//			}
//			buf1.writestring(filename);
//			buf2.writestring(filename);
//			filename = buf1.extractData();
//			moduleName = buf2.extractData();
//		}
//
//		m = new Module(filename, ident);
//		m.loc = loc;
//		m.moduleName = moduleName;
//
//		/* Search along global.path for .di file, then .d file.
//		 */
//		File result = null;
//		String resultRelative = null;
//
//		File fdi = new File(filename + ".di");
//		File fd = new File(filename + ".d");
//
//		if (fdi.exists()) {
//			result = fdi;
//		} else if (fd.exists()) {
//			result = fd;
//		} else if (null == global.path) {
//
//		} else {
//			for (int i = 0; i < ASTDmdNode.size(global.path); i++) {
//				String p = global.path.get(i);
//				File n = new File(p, fdi.toString());
//				if (n.exists()) {
//					result = n;
//					resultRelative = fdi.toString();
//					break;
//				}
//				n = new File(p, fd.toString());
//				if (n.exists()) {
//					result = n;
//					resultRelative = fd.toString();
//					break;
//				}
//			}
//		}
//
//		if (result != null) {
//			m.srcfile = result;
//		}
//
//		char[] contents = getContents(result);
//		if (contents == null) {
//			int start = packages == null || packages.size() == 0 ? ident.start : packages.get(0).start;
//			int length = ident.start + ident.length - start;
//			
//			acceptProblem(Problem.newSemanticTypeError(IProblem.ImportCannotBeResolved, ident.getLineNumber(), start, length, new String[] { filename.replace(_WIN32 ? '\\' : '/', '.') }));
//			
//			return m;
//		}
//
//		Parser parser = new Parser(Module_rootModule.apiLevel, contents, resultRelative.toCharArray());
//		parser.parseModuleObj(m);
//
//		afterParse(m);
//
//		// If we're in object.d, assign the well known class declarations
//		if ("object".equals(filename)) {
//			for (Dsymbol symbol : m.members) {
//				if (symbol.ident == null || symbol.ident.ident == null) {
//					continue;
//				}
//
//				if (ASTDmdNode.equals(symbol.ident, Id.Object)) {
//					ClassDeclaration_object = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.ClassInfo)) {
//					ClassDeclaration_classinfo = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo)) {
//					Type_typeinfo = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_Class)) {
//					Type_typeinfoclass = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_Interface)) {
//					Type_typeinfointerface = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_Struct)) {
//					Type_typeinfostruct = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_Typedef)) {
//					Type_typeinfotypedef = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_Pointer)) {
//					Type_typeinfopointer = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_Array)) {
//					Type_typeinfoarray = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_StaticArray)) {
//					Type_typeinfostaticarray = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_AssociativeArray)) {
//					Type_typeinfoassociativearray = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_Enum)) {
//					Type_typeinfoenum = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_Function)) {
//					Type_typeinfofunction = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_Delegate)) {
//					Type_typeinfodelegate = (ClassDeclaration) symbol;
//				} else if (ASTDmdNode.equals(symbol.ident, Id.TypeInfo_Tuple)) {
//					Type_typeinfotypelist = (ClassDeclaration) symbol;
//				}
//			}
//		}
//
//		return m;
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

	private Map<Type, TypeInfoDeclaration> typeInfoDeclarations = new HashMap<Type, TypeInfoDeclaration>();
	public TypeInfoDeclaration getTypeInfo(Type t) {
		return typeInfoDeclarations.get(t);
	}

	public void setTypeInfo(Type t, TypeInfoDeclaration vtinfo) {
		typeInfoDeclarations.put(t, vtinfo);
	}

}
