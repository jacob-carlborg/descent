package descent.internal.compiler.parser;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.core.CompilerConfiguration;
import descent.internal.core.util.Util;

public class SemanticContext {
	
	public static class ModuleMissingSemantic {
		public Module module;
		public Dsymbol symbol;
		public ModuleMissingSemantic(Module module, Dsymbol symbol) {
			this.module = module;
			this.symbol = symbol;
		}
	}

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
	public ClassDeclaration Type_typeinfoconst;
	public ClassDeclaration Type_typeinfoinvariant;

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
	public int apiLevel;
	
	public StringTable stringTable;
	public DsymbolTable st;
	
	/*
	 * If != 0, problems are not reported.
	 */
	public int muteProblems = 0;
	
	/*
	 * If true, errors no longer are reported.
	 */
	public boolean fatalWasSignaled;
	
	public final ASTNodeEncoder encoder;
	public boolean alwaysResolveFunctionSemanticRest;
	private final List<ASTDmdNode> templateEvaluationStack;
	
	/*
	 * This is for autocompletion, for suggesting overloads of
	 * aliased symbols.
	 */
	public boolean allowOvernextBySignature = false;

	public SemanticContext(
			IProblemRequestor problemRequestor, 
			Module module,
			IJavaProject project,
			IModuleFinder moduleFinder,
			Global global,
			CompilerConfiguration config,
			ASTNodeEncoder encoder) {
		this.problemRequestor = problemRequestor;
		this.Module_rootModule = module;
		this.global = global;
		this.project = project;
		this.moduleFinder = moduleFinder;
		this.stringTable = new StringTable();
		this.Type_tvoidptr = Type.tvoid.pointerTo(this);
		this.encoder = encoder;
		this.templateEvaluationStack = new LinkedList<ASTDmdNode>();
		this.apiLevel = Util.getApiLevel(project);
		
		if (config.semanticAnalysisLevel == 0) {
			muteProblems++;
		}
		
		// for debugging purposes
//		global.path.add("c:\\ary\\programacion\\d\\1.020\\dmd\\src\\phobos");
//		this.moduleFinder = new DmdModuleFinder(global);

		Module_init();
		afterParse(module);
	}
	
	private static int uniqueIdCount = 0;
	public IdentifierExp uniqueId(String prefix) {
		uniqueIdCount++;
		return new IdentifierExp((prefix + uniqueIdCount).toCharArray());
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
			module.ident = module.md.id();
			Dsymbol[] pparent = { module.parent };
			dst = Package.resolve(module.md.packages(), pparent, null, this);
			module.parent = pparent[0];
		} else {
			dst = Module_modules;
		}

		// Update global list of modules
		if (null == dst.insert(module)) {
			if (module.md != null) {
				if (acceptsErrors()) {
					acceptProblem(Problem.newSemanticTypeError(
							IProblem.ModuleIsInMultiplePackages, module.md, new String[] { module.md.toChars(this) }));
				}
			} else {
				if (module.md == null) {
					if (acceptsErrors()) {
						acceptProblem(Problem.newSemanticTypeError(
								IProblem.ModuleIsInMultipleDefined, 0, 0, 1));
					}
				} else {
					if (acceptsErrors()) {
						acceptProblem(Problem.newSemanticTypeError(
								IProblem.ModuleIsInMultipleDefined, module.md));
					}
				}
			}
		} else {
			if (Module_amodules == null) {
				Module_amodules = new Dsymbols();
			}
			Module_amodules.add(module);
		}
	}
	
	public final boolean acceptsErrors() {
		if (fatalWasSignaled) {
			return false;
		}
		
		if (global.gag == 0 && muteProblems == 0 && problemRequestor != null) {
			return true;
		} else {
			// Each acceptProblem is preceded by acceptsProblems, and originaly
			// global.errors is incremented, so...
			
			global.errors++;
			return false;
		}
	}
	
	public final boolean acceptsWarnings() {
		if (fatalWasSignaled) {
			return false;
		}
		
		if (global.gag == 0 && muteProblems == 0 && problemRequestor != null) {
			return true;
		} else {
			return false;
		}
	}

	public void acceptProblem(Problem problem) {
		// Don't report more problems if fatal was signaled
		if (fatalWasSignaled) {
			return;
		}
		
		if (global.gag == 0 && muteProblems == 0 && problemRequestor != null) {
//			System.out.println("~~~" + problem);
			
			if (!templateEvaluationStack.isEmpty()) {
				ASTDmdNode target = templateEvaluationStack.get(0);
				problem.setSourceStart(target.start);
				problem.setSourceEnd(target.start + target.length - 1);				
			}
			
			problemRequestor.acceptProblem(problem);
		}
		
		if (problem.isError()) {
			global.errors++;
		}
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
	
	
	public void startTemplateEvaluation(ASTDmdNode node) {
		this.templateEvaluationStack.add(node);
	}
	
	public void endTemplateEvaluation() {
		this.templateEvaluationStack.remove(this.templateEvaluationStack.size() - 1);
	}
	
	public Module load(Loc loc, Identifiers packages,
			IdentifierExp ident) {
		
		// Build the compound module name
		char[][] compoundName = new char[(packages == null ? 0 : packages.size()) + 1][];
		if (packages != null) {
			for(int i = 0; i < packages.size(); i++) {
				compoundName[i] = packages.get(i).ident;
			}
		}
		compoundName[compoundName.length - 1] = ident.ident;
		
		Module m = moduleFinder.findModule(compoundName, this);
		if (m == null){
			int start = packages == null || packages.size() == 0 ? ident.start : packages.get(0).start;
			int length = ident.start + ident.length - start;
			
			if (acceptsErrors()) {
				acceptProblem(Problem.newSemanticTypeError(IProblem.ImportCannotBeResolved, ident.getLineNumber(), start, length, new String[] { CharOperation.toString(compoundName) }));
			}
			return null;
		}

		afterParse(m);
		
		// If we're in object.d, assign the well known class declarations
		if (compoundName.length == 1 && CharOperation.equals(compoundName[0], Id.object)) {
			if (m.members != null) {
				for (Dsymbol symbol : m.members) {
					checkObjectMember(symbol);
				}
			}
		}
		
		return m;
	}
	
	public void checkObjectMember(Dsymbol s) {
		if (s.ident == null || s.ident.ident == null) {
			return;
		}
		
		if (ASTDmdNode.equals(s.ident, Id.Object)) {
			ClassDeclaration_object = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.ClassInfo)) {
			ClassDeclaration_classinfo = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo)) {
			Type_typeinfo = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Class)) {
			Type_typeinfoclass = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Interface)) {
			Type_typeinfointerface = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Struct)) {
			Type_typeinfostruct = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Typedef)) {
			Type_typeinfotypedef = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Pointer)) {
			Type_typeinfopointer = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Array)) {
			Type_typeinfoarray = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_StaticArray)) {
			Type_typeinfostaticarray = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_AssociativeArray)) {
			Type_typeinfoassociativearray = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Enum)) {
			Type_typeinfoenum = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Function)) {
			Type_typeinfofunction = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Delegate)) {
			Type_typeinfodelegate = (ClassDeclaration) s;
		} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Tuple)) {
			Type_typeinfotypelist = (ClassDeclaration) s;
		}
		
		if (isD2()) {
			if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Const)) {
				Type_typeinfoconst = (ClassDeclaration) s;
			} else if (ASTDmdNode.equals(s.ident, Id.TypeInfo_Invariant)) {
				Type_typeinfoinvariant = (ClassDeclaration) s;
			}
		}
	}

	private Map<Type, TypeInfoDeclaration> typeInfoDeclarations = new HashMap<Type, TypeInfoDeclaration>();
	public TypeInfoDeclaration getTypeInfo(Type t) {
		return typeInfoDeclarations.get(t);
	}

	public void setTypeInfo(Type t, TypeInfoDeclaration vtinfo) {
		typeInfoDeclarations.put(t, vtinfo);
	}
	
	public boolean isD2() {
		return apiLevel == Parser.D2;
	}

}
