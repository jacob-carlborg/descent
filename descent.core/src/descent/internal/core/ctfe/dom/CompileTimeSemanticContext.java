package descent.internal.core.ctfe.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.ctfe.IDebugger;
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
import descent.internal.compiler.parser.CaseRangeStatement;
import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.Catch;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.CompileStatement;
import descent.internal.compiler.parser.CompoundDeclarationStatement;
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
import descent.internal.compiler.parser.IStringTableHolder;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.IfStatement;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.InvariantDeclaration;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.LabelStatement;
import descent.internal.compiler.parser.LinkDeclaration;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.NewDeclaration;
import descent.internal.compiler.parser.Objects;
import descent.internal.compiler.parser.OnScopeStatement;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.PostBlitDeclaration;
import descent.internal.compiler.parser.PragmaDeclaration;
import descent.internal.compiler.parser.PragmaStatement;
import descent.internal.compiler.parser.Problem;
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
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateMixin;
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
import descent.internal.core.ctfe.CompileTimeModuleFinder;

public class CompileTimeSemanticContext extends SemanticContext {
	
	private final IDebugger debugger;
	private int fDisabledStepping;

	public CompileTimeSemanticContext(IProblemRequestor problemRequestor,
			Module module, IJavaProject project, WorkingCopyOwner owner,
			Global global, CompilerConfiguration config, ASTNodeEncoder encoder, IStringTableHolder holder, IDebugger debugger) throws JavaModelException {
		super(problemRequestor, module, project, owner, global, config, encoder, holder);
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
	public void startTemplateEvaluation(TemplateDeclaration node, Scope sc) {
		if (fDisabledStepping > 0)
			return;
		
		super.startTemplateEvaluation(node, sc);
				
		debugger.enterStackFrame(node);
		debugger.stepBegin(node, sc);
	}
	
	@Override
	public void endTemplateEvaluation(TemplateDeclaration node, Scope sc) {
		if (fDisabledStepping > 0)
			return;
		
		super.endTemplateEvaluation(node, sc);
		
		debugger.stepEnd(node, sc);
		debugger.exitStackFrame(node);
	}
	
	public void enterFunctionInterpret(ASTDmdNode node) {
		if (fDisabledStepping > 0)
			return;
		
		debugger.enterStackFrame(node);
	}
	
	public void exitFunctionInterpret(ASTDmdNode node) {
		if (fDisabledStepping > 0)
			return;
		
		debugger.exitStackFrame(node);
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
	public void acceptProblem(Problem problem) {
		List<ASTDmdNode> temp = this.templateEvaluationStack;
		this.templateEvaluationStack = new ArrayList<ASTDmdNode>(0);
		
		super.acceptProblem(problem);
		
		this.templateEvaluationStack = temp;
	}
	
	@Override
	protected IModuleFinder newModuleFinder(INameEnvironment env, CompilerConfiguration config, ASTNodeEncoder encoder2) {
		return new CompileTimeModuleFinder(env, config, encoder2);
	}
	
	@Override
	protected Parser newParser(int apiLevel, char[] source) {
		CompileTimeParser parser = new CompileTimeParser(apiLevel, source, 0, source.length, null, false);
		parser.holder = this;
		return parser;
	}
	
	@Override
	protected Parser newParser(char[] source, int offset, int length, boolean tokenizeComments, boolean tokenizePragmas, boolean tokenizeWhiteSpace, boolean recordLineSeparator, int apiLevel, char[][] taskTags, char[][] taskPriorities, boolean isTaskCaseSensitive, char[] filename) {
		CompileTimeParser parser = new CompileTimeParser(apiLevel, source, offset, length, filename, recordLineSeparator);
		parser.holder = this;
		return parser;
	}
	
	@Override
	protected boolean mustCopySourceRangeForMixins() {
		return false;
	}
	
	@Override
	protected VarDeclaration newVarDeclaration(char[] filename, int lineNumber, Type type, IdentifierExp exp, Initializer init) {
		return new CompileTimeVarDeclaration(filename, lineNumber, type, exp, init);
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
	public CallExp newCallExp(char[] filename, int lineNumber, Expression e, Expressions args) {
		return new CompileTimeCallExp(filename, lineNumber, e, args);
	}
	
	@Override
	protected FuncDeclaration newFuncDeclaration(char[] filename, int lineNumber, IdentifierExp ident, int storage_class, Type syntaxCopy) {
		return new CompileTimeFuncDeclaration(filename, lineNumber, ident, storage_class, syntaxCopy);
	}
	
	@Override
	protected IfStatement newIfStatement(char[] filename, int lineNumber, Argument a, Expression condition, Statement ifbody, Statement elsebody) {
		return new CompileTimeIfStatement(filename, lineNumber, a, condition, ifbody, elsebody);
	}
	
	@Override
	protected ReturnStatement newReturnStatement(char[] filename, int lineNumber, Expression e) {
		return new CompileTimeReturnStatement(filename, lineNumber, e);
	}
	
	@Override
	protected DeclarationStatement newDeclarationStatement(char[] filename, int lineNumber, Expression e) {
		return new CompileTimeDeclarationStatement(filename, lineNumber, e);
	}
	
	@Override
	protected ExpStatement newExpStatement(char[] filename, int lineNumber, Expression e) {
		return new CompileTimeExpStatement(filename, lineNumber, e);
	}
	
	@Override
	public BreakStatement newBreakStatement(char[] filename, int lineNumber, IdentifierExp ident) {
		return new CompileTimeBreakStatement(filename, lineNumber, ident);
	}
	
	@Override
	public CaseStatement newCaseStatement(char[] filename, int lineNumber, Expression expression, Statement statement) {
		return new CompileTimeCaseStatement(filename, lineNumber, expression, statement);
	}
	
	@Override
	public CaseRangeStatement newCaseRangeStatement(char[] filename,
			int lineNumber, Expression first, Expression last,
			Statement statement) {
		return new CompileTimeCaseRangeStatement(filename, lineNumber, first, last, statement);
	}
	
	@Override
	public CompileStatement newCompileStatement(char[] filename, int lineNumber, Expression e) {
		return new CompileTimeCompileStatement(filename, lineNumber, e);
	}
	
	@Override
	protected CompoundStatement newCompoundStatement(char[] filename, int lineNumber, Statements a) {
		return new CompileTimeCompoundStatement(filename, lineNumber, a);
	}
	
	@Override
	protected ConditionalStatement newConditionalStatement(char[] filename, int lineNumber, Condition condition, Statement statement, Statement e) {
		return new CompileTimeConditionalStatement(filename, lineNumber, condition, statement, e);
	}
	
	@Override
	protected ContinueStatement newContinueStatement(char[] filename, int lineNumber, IdentifierExp ident) {
		return new CompileTimeContinueStatement(filename, lineNumber, ident);
	}
	
	@Override
	protected DefaultStatement newDefaultStatement(char[] filename, int lineNumber, Statement statement) {
		return new CompileTimeDefaultStatement(filename, lineNumber, statement);
	}
	
	@Override
	public DoStatement newDoStatement(char[] filename, int lineNumber, Statement statement, Expression expression) {
		return new CompileTimeDoStatement(filename, lineNumber, statement, expression);
	}
	
	@Override
	protected ForeachRangeStatement newForeachRangeStatement(char[] filename, int lineNumber, TOK op, Argument argument, Expression expression, Expression expression2, Statement statement) {
		return new CompileTimeForeachRangeStatement(filename, lineNumber, op, argument, expression, expression2, statement);
	}
	
	@Override
	protected ForeachStatement newForeachStatement(char[] filename, int lineNumber, TOK op, Arguments args, Expression exp, Statement statement) {
		return new CompileTimeForeachStatement(filename, lineNumber, op, args, exp, statement);
	}
	
	@Override
	public ForStatement newForStatement(char[] filename, int lineNumber, Statement i, Expression c, Expression inc, Statement statement) {
		return new CompileTimeForStatement(filename, lineNumber, i, c, inc, statement);
	}
	
	@Override
	protected GotoCaseStatement newGotoCaseStatement(char[] filename, int lineNumber, Expression e) {
		return new CompileTimeGotoCaseStatement(filename, lineNumber, e);
	}
	
	@Override
	protected GotoDefaultStatement newGotoDefaultStatement(char[] filename, int lineNumber) {
		return new CompileTimeGotoDefaultStatement(filename, lineNumber);
	}
	
	@Override
	protected GotoStatement newGotoStatement(char[] filename, int lineNumber, IdentifierExp ident) {
		return new CompileTimeGotoStatement(filename, lineNumber, ident);
	}
	
	@Override
	protected LabelStatement newLabelStatement(char[] filename, int lineNumber, IdentifierExp ident, Statement statement) {
		return new CompileTimeLabelStatement(filename, lineNumber, ident, statement);
	}
	
	@Override
	protected OnScopeStatement newOnScopeStatement(char[] filename, int lineNumber, TOK tok, Statement statement) {
		return new CompileTimeOnScopeStatement(filename, lineNumber, tok, statement);
	}
	
	@Override
	protected PragmaStatement newPragmaStatement(char[] filename, int lineNumber, IdentifierExp ident, Expressions expressions, Statement b) {
		return new CompileTimePragmaStatement(filename, lineNumber, ident, expressions, b);
	}
	
	@Override
	protected ScopeStatement newScopeStatement(char[] filename, int lineNumber, Statement s) {
		return new CompileTimeScopeStatement(filename, lineNumber, s);
	}
	
	@Override
	protected StaticAssertStatement newStaticAssertStatement(StaticAssert assert1) {
		return new CompileTimeStaticAssertStatement(assert1);
	}
	
	@Override
	protected SwitchStatement newSwitchStatement(char[] filename, int lineNumber, Expression expression, Statement statement, boolean isfinal) {
		return new CompileTimeSwitchStatement(filename, lineNumber, expression, statement, isfinal);
	}
	
	@Override
	protected SynchronizedStatement newSynchronizedStatement(char[] filename, int lineNumber, Expression e, Statement statement) {
		return new CompileTimeSynchronizedStatement(filename, lineNumber, e, statement);
	}
	
	@Override
	protected ThrowStatement newThrowStatement(char[] filename, int lineNumber, Expression expression) {
		return new CompileTimeThrowStatement(filename, lineNumber, expression);
	}
	
	@Override
	protected TryCatchStatement newTryCatchStatement(char[] filename, int lineNumber, Statement statement, Array<Catch> a) {
		return new CompileTimeTryCatchStatement(filename, lineNumber, statement, a);
	}
	
	@Override
	protected TryFinallyStatement newTryFinallyStatement(char[] filename, int lineNumber, Statement statement, Statement statement2) {
		return new CompileTimeTryFinallyStatement(filename, lineNumber, statement, statement2);
	}
	
	@Override
	protected VolatileStatement newVolatileStatement(char[] filename, int lineNumber, Statement statement) {
		return new CompileTimeVolatileStatement(filename, lineNumber, statement);
	}
	
	@Override
	protected WhileStatement newWhileStatement(char[] filename, int lineNumber, Expression expression, Statement statement) {
		return new CompileTimeWhileStatement(filename, lineNumber, expression, statement);
	}
	
	@Override
	protected WithStatement newWithStatement(char[] filename, int lineNumber, Expression expression, Statement statement) {
		return new CompileTimeWithStatement(filename, lineNumber, expression, statement);
	}
	
	@Override
	protected PragmaDeclaration newPragmaDeclaration(char[] filename, int lineNumber, IdentifierExp ident, Expressions expressions, Dsymbols dsymbols) {
		return new CompileTimePragmaDeclaration(filename, lineNumber, ident, expressions, dsymbols);
	}
	
	@Override
	protected AlignDeclaration newAlignDeclaration(int salign, Dsymbols dsymbols) {
		return new CompileTimeAlignDeclaration(salign, dsymbols);
	}
	
	@Override
	protected AnonDeclaration newAnonDeclaration(char[] filename, int lineNumber, boolean isunion, Dsymbols dsymbols) {
		return new CompileTimeAnonDeclaration(filename, lineNumber, isunion, dsymbols);
	}
	
	@Override
	protected CompileDeclaration newCompileDeclaration(char[] filename, int lineNumber, Expression expression) {
		return new CompileTimeCompileDeclaration(filename, lineNumber, expression);
	}
	
	@Override
	protected LinkDeclaration newLinkDeclaration(LINK linkage, Dsymbols dsymbols) {
		return new CompileTimeLinkDeclaration(linkage, dsymbols);
	}
	
	@Override
	protected DebugSymbol newDebugSymbol(char[] filename, int lineNumber, IdentifierExp ident, Version version) {
		return new CompileTimeDebugSymbol(filename, lineNumber, ident, version);
	}
	
	@Override
	protected AliasDeclaration newAliasDeclaration(char[] filename, int lineNumber, IdentifierExp ident, Dsymbol dsymbol) {
		return new CompileTimeAliasDeclaration(filename, lineNumber, ident, dsymbol);
	}
	
	@Override
	protected AliasDeclaration newAliasDeclaration(char[] filename, int lineNumber, IdentifierExp ident, Type type) {
		return new CompileTimeAliasDeclaration(filename, lineNumber, ident, type);
	}
	
	@Override
	protected CtorDeclaration newCtorDeclaration(char[] filename, int lineNumber, Arguments arguments, int varargs) {
		return new CompileTimeCtorDeclaration(filename, lineNumber, arguments, varargs);
	}
	
	@Override
	protected DeleteDeclaration newDeleteDeclaration(char[] filename, int lineNumber, Arguments arguments) {
		return new CompileTimeDeleteDeclaration(filename, lineNumber, arguments);
	}
	
	@Override
	protected DtorDeclaration newDtorDeclaration(char[] filename, int lineNumber, IdentifierExp ident) {
		return new CompileTimeDtorDeclaration(filename, lineNumber, ident);
	}
	
	@Override
	protected InvariantDeclaration newInvariantDeclaration(char[] filename, int lineNumber) {
		return new CompileTimeInvariantDeclaration(filename, lineNumber);
	}
	
	@Override
	protected NewDeclaration newNewDeclaration(char[] filename, int lineNumber, Arguments arguments, int varargs) {
		return new CompileTimeNewDeclaration(filename, lineNumber, arguments, varargs);
	}
	
	@Override
	protected PostBlitDeclaration newPostBlitDeclaration(char[] filename, int lineNumber, IdentifierExp ident) {
		return new CompileTimePostBlitDeclaration(filename, lineNumber, ident);
	}
	
	@Override
	protected StaticCtorDeclaration newStaticCtorDeclaration(char[] filename, int lineNumber) {
		return new CompileTimeStaticCtorDeclaration(filename, lineNumber);
	}
	
	@Override
	protected StaticDtorDeclaration newStaticDtorDeclaration(char[] filename, int lineNumber) {
		return new CompileTimeStaticDtorDeclaration(filename, lineNumber);
	}
	
	@Override
	protected UnitTestDeclaration newUnitTestDeclaration(char[] filename, int lineNumber) {
		return new CompileTimeUnitTestDeclaration(filename, lineNumber);
	}
	
	@Override
	protected TypedefDeclaration newTypedefDeclaration(char[] filename, int lineNumber, IdentifierExp ident, Type basetype, Initializer init) {
		return new CompileTimeTypedefDeclaration(filename, lineNumber, ident, basetype, init);
	}
	
	@Override
	protected EnumMember newEnumMember(char[] filename, int lineNumber, IdentifierExp ident, Expression e, Type t) {
		return new CompileTimeEnumMember(filename, lineNumber, ident, e, t);
	}
	
	@Override
	protected StaticAssert newStaticAssert(char[] filename, int lineNumber, Expression expression, Expression expression2) {
		return new CompileTimeStaticAssert(filename, lineNumber, expression, expression2);
	}
	
	@Override
	protected VersionSymbol newVersionSymbol(char[] filename, int lineNumber, IdentifierExp ident, Version version) {
		return new CompileTimeVersionSymbol(filename, lineNumber, ident, version);
	}
	
	@Override
	protected ClassDeclaration newClassDeclaration(char[] filename, int lineNumber, IdentifierExp ident, BaseClasses baseClasses) {
		return new CompileTimeClassDeclaration(filename, lineNumber, ident, baseClasses);
	}
	
	@Override
	protected InterfaceDeclaration newInterfaceDeclaration(char[] filename, int lineNumber, IdentifierExp ident, BaseClasses baseClasses) {
		return new CompileTimeInterfaceDeclaration(filename, lineNumber, ident, baseClasses);
	}
	
	@Override
	protected UnionDeclaration newUnionDeclaration(char[] filename, int lineNumber, IdentifierExp ident) {
		return new CompileTimeUnionDeclaration(filename, lineNumber, ident);
	}
	
	@Override
	protected StructDeclaration newStructDeclaration(char[] filename, int lineNumber, IdentifierExp ident) {
		return new CompileTimeStructDeclaration(filename, lineNumber, ident);
	}
	
	@Override
	protected EnumDeclaration newEnumDeclaration(char[] filename, int lineNumber, IdentifierExp ident, Type t) {
		return new CompileTimeEnumDeclaration(filename, lineNumber, ident, t);
	}
	
	@Override
	protected TemplateDeclaration newTemplateDeclaration(char[] filename, int lineNumber, IdentifierExp ident, TemplateParameters p, Expression c, Dsymbols d) {
		return new CompileTimeTemplateDeclaration(filename, lineNumber, ident, p, c, d);
	}
	
	@Override
	protected TemplateMixin newTemplateMixin(char[] filename, int lineNumber, IdentifierExp ident, Type type, Identifiers ids, Objects tiargs) {
		return new CompileTimeTemplateMixin(filename, lineNumber, ident, type, ids, tiargs, encoder);
	}
	
	@Override
	public TemplateInstance newTemplateInstance(char[] filename, int lineNumber, IdentifierExp name) {
		return new CompileTimeTemplateInstance(filename, lineNumber, name, encoder);
	}
	
	@Override
	public CompoundDeclarationStatement newCompoundDeclarationStatement(char[] filename, int lineNumber, Statements a) {
		return new CompileTimeCompoundDeclarationStatement(filename, lineNumber, a);
	}

}
