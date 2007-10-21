package descent.internal.compiler.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import descent.core.IProblemRequestor;
import descent.core.compiler.IProblem;

public class SemanticContext {

	public boolean BREAKABI = true;
	public boolean IN_GCC = false;
	public boolean _DH = true;

	// If DMD is being run on Win32
	public boolean _WIN32 = true;

	private IProblemRequestor problemRequestor;
	public Global global;

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

	public SemanticContext(IProblemRequestor problemRequestor, Module module,
			Global global) {
		this.problemRequestor = problemRequestor;
		this.Module_rootModule = module;
		this.global = global;
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
			Dsymbol[] pparent = { module.parent };
			dst = Package.resolve(module.md.packages, pparent, null, this);
			module.parent = pparent[0];
		} else {
			dst = Module_modules;
		}

		// Update global list of modules
		if (null == dst.insert(module)) {
			if (module.md != null) {
				acceptProblem(Problem.newSemanticTypeError(
						IProblem.ModuleIsInMultiplePackages, 0, module.md.start,
						module.md.length, new String[] { module.md.toChars(this) }));
			} else {
				acceptProblem(Problem.newSemanticTypeError(
						IProblem.ModuleIsInMultipleDefined, 0, module.md.start,
						module.md.length));
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
		if (global.gag == 0 && muteProblems == 0) {
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

	public FuncDeclaration genCfunc(Type treturn, char[] id) {
		FuncDeclaration fd;
		TypeFunction tf;
		Dsymbol s;

		// See if already in table
		if (st == null)
			st = new DsymbolTable();
		s = st.lookup(id);
		if (s != null) {
			fd = s.isFuncDeclaration();
			Assert.isNotNull(fd);
			Assert.isTrue(fd.type.nextOf().equals(treturn));
		} else {
			tf = new TypeFunction(null, treturn, 0, LINK.LINKc);
			fd = new FuncDeclaration(Loc.ZERO, new IdentifierExp(id),
					STC.STCstatic, tf);
			fd.protection = PROT.PROTpublic;
			fd.linkage = LINK.LINKc;

			st.insert(fd);
		}
		return fd;
	}

	public Module loadModule(String fullyQualifiedName) {
		return null;
		// TODO module loading
	}

}
