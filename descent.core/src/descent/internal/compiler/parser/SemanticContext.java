package descent.internal.compiler.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.env.IModuleFinder;

public class SemanticContext {

	public boolean BREAKABI = true;
	public boolean IN_GCC = false;
	public boolean _DH = true;

	// If DMD is being run on Win32
	public boolean _WIN32 = true;

	private IProblemRequestor problemRequestor;
	public Global global;
	public IJavaProject project;
	public IModuleFinder moduleFinder;

	// TODO file imports should be selectable in a dialog or something
	public Map<String, File> fileImports = new HashMap<String, File>();
	
	public IClassDeclaration ClassDeclaration_object;
	public IClassDeclaration ClassDeclaration_classinfo;
	public IClassDeclaration Type_typeinfo;
	public IClassDeclaration Type_typeinfoclass;
	public IClassDeclaration Type_typeinfointerface;
	public IClassDeclaration Type_typeinfostruct;
	public IClassDeclaration Type_typeinfotypedef;
	public IClassDeclaration Type_typeinfopointer;
	public IClassDeclaration Type_typeinfoarray;
	public IClassDeclaration Type_typeinfostaticarray;
	public IClassDeclaration Type_typeinfoassociativearray;
	public IClassDeclaration Type_typeinfoenum;
	public IClassDeclaration Type_typeinfofunction;
	public IClassDeclaration Type_typeinfodelegate;
	public IClassDeclaration Type_typeinfotypelist;

	public Type Type_tvoidptr;
	
	public Module Module_rootModule;
	public IDsymbolTable Module_modules;
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

	public IDsymbolTable st;
	public int muteProblems = 0;
	
	// A cache to retrieve faster a type from it's signature
	public Map<String, Type> signatureToTypeCache;

	public SemanticContext(
			IProblemRequestor problemRequestor, 
			Module module,
			IJavaProject project,
			IModuleFinder moduleFinder,
			Global global) {
		this.problemRequestor = problemRequestor;
		this.Module_rootModule = module;
		this.global = global;
		this.project = project;
		this.moduleFinder = moduleFinder;
		this.stringTable = new StringTable();
		this.Type_tvoidptr = Type.tvoid.pointerTo(this);
		this.signatureToTypeCache = new HashMap<String, Type>();

		Module_init();
		afterParse(module);
	}

	private void Module_init() {
		this.Module_modules = new DsymbolTable();
	}
	
	/*
	 * This code is invoked by DMD after parsing a module.
	 */
	public void afterParse(IModule module) {
		IDsymbolTable dst;

		if (module.md() != null) {
			module.ident(module.md().id());
			IDsymbol[] pparent = { module.parent() };
			dst = Package.resolve(module.md().packages(), pparent, null, this);
			module.parent(pparent[0]);
		} else {
			dst = Module_modules;
		}

		// Update global list of modules
		if (null == dst.insert(module)) {
			if (module.md() != null) {
				acceptProblem(Problem.newSemanticTypeError(
						IProblem.ModuleIsInMultiplePackages, module.md(), new String[] { module.md().toChars(this) }));
			} else {
				acceptProblem(Problem.newSemanticTypeError(
						IProblem.ModuleIsInMultipleDefined, module.md()));
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
		
		IModule m = moduleFinder.findModule(compoundName, this);
		if (m == null){
			int start = packages == null || packages.size() == 0 ? ident.start : packages.get(0).start;
			int length = ident.start + ident.length - start;
			
			acceptProblem(Problem.newSemanticTypeError(IProblem.ImportCannotBeResolved, ident.getLineNumber(), start, length, new String[] { CharOperation.toString(compoundName) }));
			return null;
		}
		
		afterParse(m);
		
		// If we're in object.d, assign the well known class declarations
		if (compoundName.length == 1 && CharOperation.equals(compoundName[0], Id.object)) {
			for (IDsymbol symbol : m.members()) {
				checkObjectMember(symbol);
			}
		}
		
		return m;
	}
	
	public void checkObjectMember(IDsymbol s) {
		if (s.ident() == null || s.ident().ident == null) {
			return;
		}
		
		if (ASTDmdNode.equals(s.ident(), Id.Object)) {
			ClassDeclaration_object = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.ClassInfo)) {
			ClassDeclaration_classinfo = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo)) {
			Type_typeinfo = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_Class)) {
			Type_typeinfoclass = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_Interface)) {
			Type_typeinfointerface = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_Struct)) {
			Type_typeinfostruct = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_Typedef)) {
			Type_typeinfotypedef = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_Pointer)) {
			Type_typeinfopointer = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_Array)) {
			Type_typeinfoarray = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_StaticArray)) {
			Type_typeinfostaticarray = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_AssociativeArray)) {
			Type_typeinfoassociativearray = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_Enum)) {
			Type_typeinfoenum = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_Function)) {
			Type_typeinfofunction = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_Delegate)) {
			Type_typeinfodelegate = (IClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident(), Id.TypeInfo_Tuple)) {
			Type_typeinfotypelist = (IClassDeclaration) s;
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
