package descent.internal.core.ctfe.dom;

import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.AlignDeclaration;
import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.Array;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.BreakStatement;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.Catch;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.CompileStatement;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.ContinueStatement;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.DeclarationStatement;
import descent.internal.compiler.parser.DefaultStatement;
import descent.internal.compiler.parser.DeleteDeclaration;
import descent.internal.compiler.parser.DoStatement;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.DtorDeclaration;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.ForStatement;
import descent.internal.compiler.parser.ForeachRangeStatement;
import descent.internal.compiler.parser.ForeachStatement;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.Global;
import descent.internal.compiler.parser.GotoCaseStatement;
import descent.internal.compiler.parser.GotoDefaultStatement;
import descent.internal.compiler.parser.GotoStatement;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.IfStatement;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.InvariantDeclaration;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.LabelStatement;
import descent.internal.compiler.parser.LinkDeclaration;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.NewDeclaration;
import descent.internal.compiler.parser.OnScopeStatement;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.PostBlitDeclaration;
import descent.internal.compiler.parser.PragmaDeclaration;
import descent.internal.compiler.parser.PragmaStatement;
import descent.internal.compiler.parser.ReturnStatement;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeStatement;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.Statements;
import descent.internal.compiler.parser.StaticAssert;
import descent.internal.compiler.parser.StaticAssertStatement;
import descent.internal.compiler.parser.StaticCtorDeclaration;
import descent.internal.compiler.parser.StaticDtorDeclaration;
import descent.internal.compiler.parser.StaticIfDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.SwitchStatement;
import descent.internal.compiler.parser.SynchronizedStatement;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TemplateParameters;
import descent.internal.compiler.parser.ThrowStatement;
import descent.internal.compiler.parser.TryCatchStatement;
import descent.internal.compiler.parser.TryFinallyStatement;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.UnitTestDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionSymbol;
import descent.internal.compiler.parser.VolatileStatement;
import descent.internal.compiler.parser.WhileStatement;
import descent.internal.compiler.parser.WithStatement;
import descent.internal.core.CompilerConfiguration;
import descent.internal.core.ctfe.CompileTimeDescentModuleFinder;
import descent.internal.core.ctfe.IDebugger;

public class CompileTimeSemanticContext extends SemanticContext {
	
	private final IDebugger debugger;
	private int fDisabledStepping;

	public CompileTimeSemanticContext(IProblemRequestor problemRequestor,
			Module module, IJavaProject project, WorkingCopyOwner owner,
			Global global, CompilerConfiguration config, ASTNodeEncoder encoder, IDebugger debugger) throws JavaModelException {
		super(problemRequestor, module, project, owner, global, config, encoder);
		this.debugger = debugger;
	}
	
	public void stepBegin(ASTDmdNode node, Scope sc) {
		if (fDisabledStepping > 0)
			return;
		
		debugger.stepBegin(node, sc);
	}
	
	public void stepEnd(ASTDmdNode node, Scope sc) {
		if (fDisabledStepping > 0)
			return;
		
		debugger.stepEnd(node, sc);
	}
	
	public void stepBegin(ASTDmdNode node, InterState is) {
		if (fDisabledStepping > 0)
			return;
		
		debugger.stepBegin(node, is);
	}
	
	public void stepEnd(ASTDmdNode node, InterState is) {
		if (fDisabledStepping > 0)
			return;
		
		debugger.stepEnd(node, is);
	}
	
	@Override
	public void startTemplateEvaluation(ASTDmdNode node) {
		if (fDisabledStepping > 0)
			return;
		
		super.startTemplateEvaluation(node);
		
		debugger.enterStackFrame();
	}
	
	@Override
	public void endTemplateEvaluation() {
		if (fDisabledStepping > 0)
			return;
		
		super.endTemplateEvaluation();
		
		debugger.exitStackFrame();
	}
	
	public void enterFunctionInterpret() {
		if (fDisabledStepping > 0)
			return;
		
		debugger.enterStackFrame();
	}
	
	public void exitFunctionInterpret() {
		if (fDisabledStepping > 0)
			return;
		
		debugger.exitStackFrame();
	}
	
	public void message(String message) {
		debugger.message(message);
	}
	
	public void disableStepping() {
		fDisabledStepping++;
	}

	public void enableStepping() {
		fDisabledStepping--;
	}
	
	@Override
	protected IModuleFinder newModuleFinder(INameEnvironment env, CompilerConfiguration config, ASTNodeEncoder encoder2) {
		return new CompileTimeDescentModuleFinder(env, config, encoder2);
	}
	
	@Override
	protected Parser newParser(int apiLevel, char[] source) {
		return new CompileTimeParser(apiLevel, source, 0, source.length, null, false);
	}
	
	@Override
	protected Parser newParser(char[] source, int offset, int length, boolean tokenizeComments, boolean tokenizePragmas, boolean tokenizeWhiteSpace, boolean recordLineSeparator, int apiLevel, char[][] taskTags, char[][] taskPriorities, boolean isTaskCaseSensitive, char[] filename) {
		return new CompileTimeParser(apiLevel, source, offset, length, filename, recordLineSeparator);
	}
	
	@Override
	protected VarDeclaration newVarDeclaration(Loc loc, Type type, IdentifierExp exp, Initializer init) {
		return new CompileTimeVarDeclaration(loc, type, exp, init);
	}
	
	@Override
	public ConditionalDeclaration newConditionalDeclaration(Condition condition, Dsymbols a, Dsymbols elseDecl) {
		return new CompileTimeConditionalDeclaration(condition, a, elseDecl);
	}
	
	@Override
	protected StaticIfDeclaration newStaticIfDeclaration(Condition condition, Dsymbols a, Dsymbols aelse) {
		return new CompileTimeStaticIfDeclaration(condition, a, aelse);
	}

	@Override
	public CallExp newCallExp(Loc loc, Expression e, Expressions args) {
		return new CompileTimeCallExp(loc, e, args);
	}
	
	@Override
	protected FuncDeclaration newFuncDeclaration(Loc loc, IdentifierExp ident, int storage_class, Type syntaxCopy) {
		return new CompileTimeFuncDeclaration(loc, ident, storage_class, syntaxCopy);
	}
	
	@Override
	protected IfStatement newIfStatement(Loc loc, Argument a, Expression condition, Statement ifbody, Statement elsebody) {
		return new CompileTimeIfStatement(loc, a, condition, ifbody, elsebody);
	}
	
	@Override
	protected ReturnStatement newReturnStatement(Loc loc, Expression e) {
		return new CompileTimeReturnStatement(loc, e);
	}
	
	@Override
	protected DeclarationStatement newDeclarationStatement(Loc loc, Expression e) {
		return new CompileTimeDeclarationStatement(loc, e);
	}
	
	@Override
	protected ExpStatement newExpStatement(Loc loc, Expression e) {
		return new CompileTimeExpStatement(loc, e);
	}
	
	@Override
	public BreakStatement newBreakStatement(Loc loc, IdentifierExp ident) {
		return new CompileTimeBreakStatement(loc, ident);
	}
	
	@Override
	public CaseStatement newCaseStatement(Loc loc, Expression expression, Statement statement) {
		return new CompileTimeCaseStatement(loc, expression, statement);
	}
	
	@Override
	public CompileStatement newCompileStatement(Loc loc, Expression e) {
		return new CompileTimeCompileStatement(loc, e);
	}
	
	@Override
	protected CompoundStatement newCompoundStatement(Loc loc, Statements a) {
		return new CompileTimeCompoundStatement(loc, a);
	}
	
	@Override
	protected ConditionalStatement newConditionalStatement(Loc loc, Condition condition, Statement statement, Statement e) {
		return new CompileTimeConditionalStatement(loc, condition, statement, e);
	}
	
	@Override
	protected ContinueStatement newContinueStatement(Loc loc, IdentifierExp ident) {
		return new CompileTimeContinueStatement(loc, ident);
	}
	
	@Override
	protected DefaultStatement newDefaultStatement(Loc loc, Statement statement) {
		return new CompileTimeDefaultStatement(loc, statement);
	}
	
	@Override
	public DoStatement newDoStatement(Loc loc, Statement statement, Expression expression) {
		return new CompileTimeDoStatement(loc, statement, expression);
	}
	
	@Override
	protected ForeachRangeStatement newForeachRangeStatement(Loc loc, TOK op, Argument argument, Expression expression, Expression expression2, Statement statement) {
		return new CompileTimeForeachRangeStatement(loc, op, argument, expression, expression2, statement);
	}
	
	@Override
	protected ForeachStatement newForeachStatement(Loc loc, TOK op, Arguments args, Expression exp, Statement statement) {
		return new CompileTimeForeachStatement(loc, op, args, exp, statement);
	}
	
	@Override
	public ForStatement newForStatement(Loc loc, Statement i, Expression c, Expression inc, Statement statement) {
		return new CompileTimeForStatement(loc, i, c, inc, statement);
	}
	
	@Override
	protected GotoCaseStatement newGotoCaseStatement(Loc loc, Expression e) {
		return new CompileTimeGotoCaseStatement(loc, e);
	}
	
	@Override
	protected GotoDefaultStatement newGotoDefaultStatement(Loc loc) {
		return new CompileTimeGotoDefaultStatement(loc);
	}
	
	@Override
	protected GotoStatement newGotoStatement(Loc loc, IdentifierExp ident) {
		return new CompileTimeGotoStatement(loc, ident);
	}
	
	@Override
	protected LabelStatement newLabelStatement(Loc loc, IdentifierExp ident, Statement statement) {
		return new CompileTimeLabelStatement(loc, ident, statement);
	}
	
	@Override
	protected OnScopeStatement newOnScopeStatement(Loc loc, TOK tok, Statement statement) {
		return new CompileTimeOnScopeStatement(loc, tok, statement);
	}
	
	@Override
	protected PragmaStatement newPragmaStatement(Loc loc, IdentifierExp ident, Expressions expressions, Statement b) {
		return new CompileTimePragmaStatement(loc, ident, expressions, b);
	}
	
	@Override
	protected ScopeStatement newScopeStatement(Loc loc, Statement s) {
		return new CompileTimeScopeStatement(loc, s);
	}
	
	@Override
	protected StaticAssertStatement newStaticAssertStatement(StaticAssert assert1) {
		return new CompileTimeStaticAssertStatement(assert1);
	}
	
	@Override
	protected SwitchStatement newSwitchStatement(Loc loc, Expression expression, Statement statement) {
		return new CompileTimeSwitchStatement(loc, expression, statement);
	}
	
	@Override
	protected SynchronizedStatement newSynchronizedStatement(Loc loc, Expression e, Statement statement) {
		return new CompileTimeSynchronizedStatement(loc, e, statement);
	}
	
	@Override
	protected ThrowStatement newThrowStatement(Loc loc, Expression expression) {
		return new CompileTimeThrowStatement(loc, expression);
	}
	
	@Override
	protected TryCatchStatement newTryCatchStatement(Loc loc, Statement statement, Array<Catch> a) {
		return new CompileTimeTryCatchStatement(loc, statement, a);
	}
	
	@Override
	protected TryFinallyStatement newTryFinallyStatement(Loc loc, Statement statement, Statement statement2) {
		return new CompileTimeTryFinallyStatement(loc, statement, statement2);
	}
	
	@Override
	protected VolatileStatement newVolatileStatement(Loc loc, Statement statement) {
		return new CompileTimeVolatileStatement(loc, statement);
	}
	
	@Override
	protected WhileStatement newWhileStatement(Loc loc, Expression expression, Statement statement) {
		return new CompileTimeWhileStatement(loc, expression, statement);
	}
	
	@Override
	protected WithStatement newWithStatement(Loc loc, Expression expression, Statement statement) {
		return new CompileTimeWithStatement(loc, expression, statement);
	}
	
	@Override
	protected PragmaDeclaration newPragmaDeclaration(Loc loc, IdentifierExp ident, Expressions expressions, Dsymbols dsymbols) {
		return new CompileTimePragmaDeclaration(loc, ident, expressions, dsymbols);
	}
	
	@Override
	protected AlignDeclaration newAlignDeclaration(int salign, Dsymbols dsymbols) {
		return new CompileTimeAlignDeclaration(salign, dsymbols);
	}
	
	@Override
	protected AnonDeclaration newAnonDeclaration(Loc loc, boolean isunion, Dsymbols dsymbols) {
		return new CompileTimeAnonDeclaration(loc, isunion, dsymbols);
	}
	
	@Override
	protected CompileDeclaration newCompileDeclaration(Loc loc, Expression expression) {
		return new CompileTimeCompileDeclaration(loc, expression);
	}
	
	@Override
	protected LinkDeclaration newLinkDeclaration(LINK linkage, Dsymbols dsymbols) {
		return new CompileTimeLinkDeclaration(linkage, dsymbols);
	}
	
	@Override
	protected DebugSymbol newDebugSymbol(Loc loc, IdentifierExp ident, Version version) {
		return new CompileTimeDebugSymbol(loc, ident, version);
	}
	
	@Override
	protected AliasDeclaration newAliasDeclaration(Loc loc, IdentifierExp ident, Dsymbol dsymbol) {
		return new CompileTimeAliasDeclaration(loc, ident, dsymbol);
	}
	
	@Override
	protected AliasDeclaration newAliasDeclaration(Loc loc, IdentifierExp ident, Type type) {
		return new CompileTimeAliasDeclaration(loc, ident, type);
	}
	
	@Override
	protected CtorDeclaration newCtorDeclaration(Loc loc, Arguments arguments, int varargs) {
		return new CompileTimeCtorDeclaration(loc, arguments, varargs);
	}
	
	@Override
	protected DeleteDeclaration newDeleteDeclaration(Loc loc, Arguments arguments) {
		return new CompileTimeDeleteDeclaration(loc, arguments);
	}
	
	@Override
	protected DtorDeclaration newDtorDeclaration(Loc loc, IdentifierExp ident) {
		return new CompileTimeDtorDeclaration(loc, ident);
	}
	
	@Override
	protected InvariantDeclaration newInvariantDeclaration(Loc loc) {
		return new CompileTimeInvariantDeclaration(loc);
	}
	
	@Override
	protected NewDeclaration newNewDeclaration(Loc loc, Arguments arguments, int varargs) {
		return new CompileTimeNewDeclaration(loc, arguments, varargs);
	}
	
	@Override
	protected PostBlitDeclaration newPostBlitDeclaration(Loc loc, IdentifierExp ident) {
		return new CompileTimePostBlitDeclaration(loc, ident);
	}
	
	@Override
	protected StaticCtorDeclaration newStaticCtorDeclaration(Loc loc) {
		return new CompileTimeStaticCtorDeclaration(loc);
	}
	
	@Override
	protected StaticDtorDeclaration newStaticDtorDeclaration(Loc loc) {
		return new CompileTimeStaticDtorDeclaration(loc);
	}
	
	@Override
	protected UnitTestDeclaration newUnitTestDeclaration(Loc loc) {
		return new CompileTimeUnitTestDeclaration(loc);
	}
	
	@Override
	protected TypedefDeclaration newTypedefDeclaration(Loc loc, IdentifierExp ident, Type basetype, Initializer init) {
		return new CompileTimeTypedefDeclaration(loc, ident, basetype, init);
	}
	
	@Override
	protected EnumMember newEnumMember(Loc loc, IdentifierExp ident, Expression e, Type t) {
		return new CompileTimeEnumMember(loc, ident, e, t);
	}
	
	@Override
	protected StaticAssert newStaticAssert(Loc loc, Expression expression, Expression expression2) {
		return new CompileTimeStaticAssert(loc, expression, expression2);
	}
	
	@Override
	protected VersionSymbol newVersionSymbol(Loc loc, IdentifierExp ident, Version version) {
		return new CompileTimeVersionSymbol(loc, ident, version);
	}
	
	@Override
	protected ClassDeclaration newClassDeclaration(Loc loc, IdentifierExp ident, BaseClasses baseClasses) {
		return new CompileTimeClassDeclaration(loc, ident, baseClasses);
	}
	
	@Override
	protected InterfaceDeclaration newInterfaceDeclaration(Loc loc, IdentifierExp ident, BaseClasses baseClasses) {
		return new CompileTimeInterfaceDeclaration(loc, ident, baseClasses);
	}
	
	@Override
	protected UnionDeclaration newUnionDeclaration(Loc loc, IdentifierExp ident) {
		return new CompileTimeUnionDeclaration(loc, ident);
	}
	
	@Override
	protected StructDeclaration newStructDeclaration(Loc loc, IdentifierExp ident) {
		return new CompileTimeStructDeclaration(loc, ident);
	}
	
	@Override
	protected EnumDeclaration newEnumDeclaration(Loc loc, IdentifierExp ident, Type t) {
		return new CompileTimeEnumDeclaration(loc, ident, t);
	}
	
	@Override
	protected TemplateDeclaration newTemplateDeclaration(Loc loc, IdentifierExp ident, TemplateParameters p, Expression c, Dsymbols d) {
		return new CompileTimeTemplateDeclaration(loc, ident, p, c, d);
	}

}
