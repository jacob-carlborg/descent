package descent.internal.compiler.parser;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.lookup.DescentModuleFinder;
import descent.internal.core.CancelableNameEnvironment;
import descent.internal.core.CompilerConfiguration;
import descent.internal.core.JavaProject;
import descent.internal.core.util.Util;

public class SemanticContext {

	public boolean BREAKABI = true;
	public boolean IN_GCC = false;
	public boolean _DH = true;
	public boolean STRUCTTHISREF() {
		return apiLevel == Parser.D2;
	}

	// If DMD is being run on Win32
	public boolean _WIN32 = true;

	public IProblemRequestor problemRequestor;
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
	public StringTable ArrayOp_arrayfuncs = new StringTable();
	public int TemplateInstace_nest;
	public int TemplateMixin_nest;
	
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
	public boolean templateSemanticStarted = false;
	
	public final ASTNodeEncoder encoder;
	public boolean alwaysResolveFunctionSemanticRest;
	protected List<ASTDmdNode> templateEvaluationStack;
	
	/*
	 * This is for autocompletion, for suggesting overloads of
	 * aliased symbols.
	 */
	public boolean allowOvernextBySignature = false;

	public SemanticContext(
			IProblemRequestor problemRequestor, 
			Module module,
			IJavaProject project,
			WorkingCopyOwner owner,
			Global global,
			CompilerConfiguration config,
			ASTNodeEncoder encoder) throws JavaModelException {
		this.problemRequestor = problemRequestor;
		this.Module_rootModule = module;
		this.global = global;
		this.project = project;
		this.moduleFinder = newModuleFinder(new CancelableNameEnvironment((JavaProject) project, owner, null), config, encoder);
		this.stringTable = new StringTable();
		this.Type_tvoidptr = Type.tvoid.pointerTo(this);
		this.encoder = encoder;
		this.templateEvaluationStack = new LinkedList<ASTDmdNode>();
		if (project == null) {
			this.apiLevel = AST.D1;
		} else {
			this.apiLevel = Util.getApiLevel(project);
		}
		
		if (config.semanticAnalysisLevel == 0) {
			muteProblems++;
		}
		
		// for debugging purposes
//		this.moduleFinder = new DmdModuleFinder(global);

		Module_init();
		afterParse(module);
	}

	private static int uniqueIdCount = 0;
	public IdentifierExp uniqueId(String prefix) {
		return uniqueId(prefix, uniqueIdCount++);
	}
	
	public IdentifierExp uniqueId(String prefix, int i) {
		return new IdentifierExp((prefix + i).toCharArray());
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
			
			if (!templateSemanticStarted) {
				global.errors++;
			}
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
		// This one is important to see if the user configured Descent correctly
		if (problem.getID() == IProblem.MissingOrCurruptObjectDotD) {
			problemRequestor.acceptProblem(problem);
			if (problem.isError()) {
				if (!templateSemanticStarted) {
					global.errors++;
				}
			}
			return;
		}
		
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
			if (!templateSemanticStarted) {
				global.errors++;
			}
		}
	}

	private int generatedIds;	

	public IdentifierExp generateId(String prefix) {
		return generateId(prefix, ++generatedIds);
	}
	
	public IdentifierExp generateId(String prefix, int i) {
		String name = prefix + i;
		char[] id = name.toCharArray();
		return new IdentifierExp(id);
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
	
	
	public void startTemplateEvaluation(TemplateDeclaration node, Scope sc) {
		this.templateEvaluationStack.add(node);
	}
	
	public void endTemplateEvaluation(TemplateDeclaration node, Scope sc) {
		this.templateEvaluationStack.remove(this.templateEvaluationStack.size() - 1);
	}
	
	public void startFunctionInterpret(CallExp fd) {
		this.templateEvaluationStack.add(fd);
	}
	
	public void endFunctionInterpret(CallExp fd) {
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
	
	public final boolean isD1() {
		return apiLevel == Parser.D1;
	}
	
	public final boolean isD2() {
		return apiLevel == Parser.D2;
	}
	
	protected IModuleFinder newModuleFinder(INameEnvironment env, CompilerConfiguration config, ASTNodeEncoder encoder) {
		return new DescentModuleFinder(env, config, encoder);
	}
	
	protected Parser newParser(int apiLevel, char[] source) {
		return new Parser(apiLevel, source);
	}
	
	protected Parser newParser(char[] source, int offset, int length,
			boolean tokenizeComments, boolean tokenizePragmas,
			boolean tokenizeWhiteSpace, boolean recordLineSeparator,
			int apiLevel,
			char[][] taskTags, char[][] taskPriorities, boolean isTaskCaseSensitive,
			char[] filename) {
		return new Parser(source, offset, length, tokenizeComments, tokenizePragmas, tokenizeWhiteSpace, recordLineSeparator, apiLevel, taskTags, taskPriorities, isTaskCaseSensitive, filename);
	}
	
	protected boolean mustCopySourceRangeForMixins() {
		return false;
	}

	protected VarDeclaration newVarDeclaration(Loc loc, Type type, IdentifierExp exp, Initializer init) {
		return new VarDeclaration(loc, type, exp, init);
	}

	protected ConditionalDeclaration newConditionalDeclaration(Condition condition, Dsymbols a, Dsymbols elseDecl) {
		return new ConditionalDeclaration(condition, a, elseDecl);
	}

	protected StaticIfDeclaration newStaticIfDeclaration(Condition condition, Dsymbols a, Dsymbols aelse) {
		return new StaticIfDeclaration(condition, a, aelse);
	}

	protected CallExp newCallExp(Loc loc, Expression e, Expressions args) {
		return new CallExp(loc, e, args);
	}

	protected FuncDeclaration newFuncDeclaration(Loc loc, IdentifierExp ident,
			int storage_class, Type syntaxCopy) {
		return new FuncDeclaration(loc, ident, storage_class, syntaxCopy);
	}

	protected IfStatement newIfStatement(Loc loc, Argument a, Expression condition, Statement ifbody, Statement elsebody) {
		return new IfStatement(loc, a, condition, ifbody, elsebody);
	}

	protected ReturnStatement newReturnStatement(Loc loc, Expression e) {
		return new ReturnStatement(loc, e);
	}

	protected DeclarationStatement newDeclarationStatement(Loc loc, Expression e) {
		return new DeclarationStatement(loc, e);
	}

	protected ExpStatement newExpStatement(Loc loc, Expression e) {
		return new ExpStatement(loc, e);
	}

	protected BreakStatement newBreakStatement(Loc loc, IdentifierExp ident) {
		return new BreakStatement(loc, ident);
	}

	protected CaseStatement newCaseStatement(Loc loc, Expression expression, Statement statement) {
		return new CaseStatement(loc, expression, statement);
	}

	protected CompileStatement newCompileStatement(Loc loc, Expression e) {
		return new CompileStatement(loc, e);
	}

	protected CompoundStatement newCompoundStatement(Loc loc, Statements a) {
		return new CompoundStatement(loc, a);
	}

	protected ConditionalStatement newConditionalStatement(Loc loc, Condition condition, Statement statement, Statement e) {
		return new ConditionalStatement(loc, condition, statement, e);
	}

	protected ContinueStatement newContinueStatement(Loc loc, IdentifierExp ident) {
		return new ContinueStatement(loc, ident);
	}

	protected DefaultStatement newDefaultStatement(Loc loc, Statement statement) {
		return new DefaultStatement(loc, statement);
	}

	protected DoStatement newDoStatement(Loc loc, Statement statement, Expression expression) {
		return new DoStatement(loc, statement, expression);
	}

	protected ForeachRangeStatement newForeachRangeStatement(Loc loc, TOK op, Argument argument, Expression expression, Expression expression2, Statement statement) {
		return new ForeachRangeStatement(loc, op, argument, expression, expression2, statement);
	}

	protected ForeachStatement newForeachStatement(Loc loc, TOK op, Arguments args, Expression exp, Statement statement) {
		return new ForeachStatement(loc, op, args, exp, statement);
	}

	protected ForStatement newForStatement(Loc loc, Statement i, Expression c, Expression inc, Statement statement) {
		return new ForStatement(loc, i, c, inc, statement);
	}

	protected GotoCaseStatement newGotoCaseStatement(Loc loc, Expression e) {
		return new GotoCaseStatement(loc, e);
	}

	protected GotoDefaultStatement newGotoDefaultStatement(Loc loc) {
		return new GotoDefaultStatement(loc);
	}

	protected GotoStatement newGotoStatement(Loc loc, IdentifierExp ident) {
		return new GotoStatement(loc, ident);
	}

	protected LabelStatement newLabelStatement(Loc loc, IdentifierExp ident, Statement statement) {
		return new LabelStatement(loc, ident, statement);
	}

	protected OnScopeStatement newOnScopeStatement(Loc loc, TOK tok, Statement statement) {
		return new OnScopeStatement(loc, tok, statement);
	}

	protected PragmaStatement newPragmaStatement(Loc loc, IdentifierExp ident, Expressions expressions, Statement b) {
		return new PragmaStatement(loc, ident, expressions, b);
	}

	protected ScopeStatement newScopeStatement(Loc loc, Statement s) {
		return new ScopeStatement(loc, s);
	}

	protected StaticAssertStatement newStaticAssertStatement(StaticAssert assert1) {
		return new StaticAssertStatement(assert1);
	}

	protected SwitchStatement newSwitchStatement(Loc loc, Expression expression, Statement statement) {
		return new SwitchStatement(loc, expression, statement);
	}

	protected SynchronizedStatement newSynchronizedStatement(Loc loc, Expression e, Statement statement) {
		return new SynchronizedStatement(loc, e, statement);
	}

	protected ThrowStatement newThrowStatement(Loc loc, Expression expression) {
		return new ThrowStatement(loc, expression);
	}

	protected TryCatchStatement newTryCatchStatement(Loc loc, Statement statement, Array<Catch> a) {
		return new TryCatchStatement(loc, statement, a);
	}

	protected TryFinallyStatement newTryFinallyStatement(Loc loc, Statement statement, Statement statement2) {
		return new TryFinallyStatement(loc, statement, statement2);
	}

	protected VolatileStatement newVolatileStatement(Loc loc, Statement statement) {
		return new VolatileStatement(loc, statement);
	}

	protected WhileStatement newWhileStatement(Loc loc, Expression expression, Statement statement) {
		return new WhileStatement(loc, expression, statement);
	}

	protected WithStatement newWithStatement(Loc loc, Expression expression, Statement statement) {
		return new WithStatement(loc, expression, statement);
	}

	protected PragmaDeclaration newPragmaDeclaration(Loc loc, IdentifierExp ident, Expressions expressions, Dsymbols dsymbols) {
		return new PragmaDeclaration(loc, ident, expressions, dsymbols);
	}

	protected AlignDeclaration newAlignDeclaration(int salign, Dsymbols dsymbols) {
		return new AlignDeclaration(salign, dsymbols);
	}

	protected AnonDeclaration newAnonDeclaration(Loc loc, boolean isunion, Dsymbols dsymbols) {
		return new AnonDeclaration(loc, isunion, dsymbols);
	}

	protected CompileDeclaration newCompileDeclaration(Loc loc, Expression expression) {
		return new CompileDeclaration(loc, expression);
	}

	protected LinkDeclaration newLinkDeclaration(LINK linkage, Dsymbols dsymbols) {
		return new LinkDeclaration(linkage, dsymbols);
	}

	protected DebugSymbol newDebugSymbol(Loc loc, IdentifierExp ident, Version version) {
		return new DebugSymbol(loc, ident, version);
	}

	protected AliasDeclaration newAliasDeclaration(Loc loc, IdentifierExp ident, Type type) {
		return new AliasDeclaration(loc, ident, type);
	}

	protected AliasDeclaration newAliasDeclaration(Loc loc, IdentifierExp ident, Dsymbol dsymbol) {
		return new AliasDeclaration(loc, ident, dsymbol);
	}

	protected CtorDeclaration newCtorDeclaration(Loc loc, Arguments arguments, int varargs) {
		return new CtorDeclaration(loc, arguments, varargs);
	}

	protected DeleteDeclaration newDeleteDeclaration(Loc loc, Arguments arguments) {
		return new DeleteDeclaration(loc, arguments);
	}

	protected DtorDeclaration newDtorDeclaration(Loc loc, IdentifierExp ident) {
		return new DtorDeclaration(loc, ident);
	}

	protected InvariantDeclaration newInvariantDeclaration(Loc loc) {
		return new InvariantDeclaration(loc);
	}

	protected NewDeclaration newNewDeclaration(Loc loc, Arguments arguments, int varargs) {
		return new NewDeclaration(loc, arguments, varargs);
	}

	protected PostBlitDeclaration newPostBlitDeclaration(Loc loc, IdentifierExp ident) {
		return new PostBlitDeclaration(loc, ident);
	}

	protected StaticCtorDeclaration newStaticCtorDeclaration(Loc loc) {
		return new StaticCtorDeclaration(loc);
	}

	protected StaticDtorDeclaration newStaticDtorDeclaration(Loc loc) {
		return new StaticDtorDeclaration(loc);
	}

	protected UnitTestDeclaration newUnitTestDeclaration(Loc loc) {
		return new UnitTestDeclaration(loc);
	}

	protected TypedefDeclaration newTypedefDeclaration(Loc loc, IdentifierExp ident, Type basetype, Initializer init) {
		return new TypedefDeclaration(loc, ident, basetype, init);
	}

	protected EnumMember newEnumMember(Loc loc, IdentifierExp ident, Expression e, Type t) {
		return new EnumMember(loc, ident, e, t);
	}

	protected StaticAssert newStaticAssert(Loc loc, Expression expression, Expression expression2) {
		return new StaticAssert(loc, expression, expression2);
	}

	protected VersionSymbol newVersionSymbol(Loc loc, IdentifierExp ident, Version version) {
		return new VersionSymbol(loc, ident, version);
	}

	protected ClassDeclaration newClassDeclaration(Loc loc, IdentifierExp ident, BaseClasses baseClasses) {
		return new ClassDeclaration(loc, ident, baseClasses);
	}

	protected InterfaceDeclaration newInterfaceDeclaration(Loc loc, IdentifierExp ident, BaseClasses baseClasses) {
		return new InterfaceDeclaration(loc, ident, baseClasses);
	}

	protected UnionDeclaration newUnionDeclaration(Loc loc, IdentifierExp ident) {
		return new UnionDeclaration(loc, ident);
	}

	protected StructDeclaration newStructDeclaration(Loc loc, IdentifierExp ident) {
		return new StructDeclaration(loc, ident);
	}

	protected EnumDeclaration newEnumDeclaration(Loc loc, IdentifierExp ident, Type t) {
		return new EnumDeclaration(loc, ident, t);
	}

	protected TemplateDeclaration newTemplateDeclaration(Loc loc, IdentifierExp ident, TemplateParameters p, Expression c, Dsymbols d) {
		return new TemplateDeclaration(loc, ident, p, c, d);
	}

	protected TemplateMixin newTemplateMixin(Loc loc, IdentifierExp ident, Type type, Identifiers ids, Objects tiargs) {
		return new TemplateMixin(loc, ident, type, ids, tiargs, encoder);
	}

	public TemplateInstance newTemplateInstance(Loc loc, IdentifierExp name) {
		return new TemplateInstance(loc, name, encoder);
	}

	public Expression newTypeDotIdExp(Loc loc, Type type, IdentifierExp ident) {
		return new DotIdExp(loc, new TypeExp(loc, type), ident);
	}

	public CompoundDeclarationStatement newCompoundDeclarationStatement(Loc loc, Statements a) {
		return new CompoundDeclarationStatement(loc, a);
	}

}
