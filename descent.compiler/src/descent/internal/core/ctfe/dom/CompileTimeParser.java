package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.AlignDeclaration;
import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.Array;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.BreakStatement;
import descent.internal.compiler.parser.CaseRangeStatement;
import descent.internal.compiler.parser.CaseStatement;
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
import descent.internal.compiler.parser.GotoCaseStatement;
import descent.internal.compiler.parser.GotoDefaultStatement;
import descent.internal.compiler.parser.GotoStatement;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.IfStatement;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.InvariantDeclaration;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.LabelStatement;
import descent.internal.compiler.parser.LinkDeclaration;
import descent.internal.compiler.parser.NewDeclaration;
import descent.internal.compiler.parser.Objects;
import descent.internal.compiler.parser.OnScopeStatement;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.PostBlitDeclaration;
import descent.internal.compiler.parser.PragmaDeclaration;
import descent.internal.compiler.parser.PragmaStatement;
import descent.internal.compiler.parser.ReturnStatement;
import descent.internal.compiler.parser.ScopeStatement;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.Statements;
import descent.internal.compiler.parser.StaticAssert;
import descent.internal.compiler.parser.StaticAssertStatement;
import descent.internal.compiler.parser.StaticCtorDeclaration;
import descent.internal.compiler.parser.StaticDtorDeclaration;
import descent.internal.compiler.parser.StaticIfCondition;
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
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.UnitTestDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionSymbol;
import descent.internal.compiler.parser.VolatileStatement;
import descent.internal.compiler.parser.WhileStatement;
import descent.internal.compiler.parser.WithStatement;
import descent.internal.compiler.parser.ASTNodeEncoder.IParserFactory;

public class CompileTimeParser extends Parser {
	
	private final static IParserFactory factory = new IParserFactory() {

		public Parser newParser(char[] source, int offset, int length,
				boolean tokenizeComments, boolean tokenizePragmas,
				boolean tokenizeWhiteSpace, boolean recordLineSeparator,
				int apiLevel, char[][] taskTags, char[][] taskPriorities,
				boolean isTaskCaseSensitive, char[] filename) {
			return new CompileTimeParser(apiLevel, source, offset, length, filename, recordLineSeparator);
		}
		
	};

	public CompileTimeParser(int apiLevel, char[] source, int offset, int length,
			char[] filename, boolean recordLineSeparator) {
		super(apiLevel, source, offset, length, null, null, recordLineSeparator, false, filename, 
				new ASTNodeEncoder(apiLevel, factory));
	}
	
	@Override
	protected TemplateInstance newTemplateInstance(char[] filename, int lineNumber, IdentifierExp id,
			ASTNodeEncoder encoder) {
		return new CompileTimeTemplateInstance(filename, lineNumber, id, encoder);
	}
	
	@Override
	protected VarDeclaration newVarDeclaration(char[] filename, int lineNumber, Type type, IdentifierExp ident, Initializer init) {
		return new CompileTimeVarDeclaration(filename, lineNumber, type, ident, init);
	}
	
	@Override
	protected ConditionalDeclaration newConditionalDeclaration(Condition condition, Dsymbols a, Dsymbols aelse) {
		return new CompileTimeConditionalDeclaration(condition, a, aelse);
	}
	
	@Override
	protected StaticIfDeclaration newStaticIfDeclaration(StaticIfCondition condition, Dsymbols a, Dsymbols aelse) {
		return new CompileTimeStaticIfDeclaration(condition, a, aelse);
	}
	
	@Override
	protected Expression newCallExp(char[] filename, int lineNumber, Expression e, Expressions expressions) {
		return new CompileTimeCallExp(filename, lineNumber, e, expressions);
	}
	
	@Override
	protected FuncDeclaration newFuncDeclaration(char[] filename, int lineNumber, IdentifierExp ident, int storage_class, TypeFunction typeFunction) {
		return new CompileTimeFuncDeclaration(filename, lineNumber, ident, storage_class, typeFunction);
	}
	
	@Override
	protected IfStatement newIfStatement(char[] filename, int lineNumber, Argument arg, Expression condition, Statement ifbody, Statement elsebody) {
		return new CompileTimeIfStatement(filename, lineNumber, arg, condition, ifbody, elsebody);
	}
	
	@Override
	protected ReturnStatement newReturnStatement(char[] filename, int lineNumber, Expression exp) {
		return new CompileTimeReturnStatement(filename, lineNumber, exp);
	}
	
	@Override
	protected DeclarationStatement newDeclarationStatement(char[] filename, int lineNumber, Dsymbol d) {
		return new CompileTimeDeclarationStatement(filename, lineNumber, d);
	}
	
	@Override
	protected ExpStatement newExpStatement(char[] filename, int lineNumber, Expression exp) {
		return new CompileTimeExpStatement(filename, lineNumber, exp);
	}
	
	@Override
	protected BreakStatement newBreakStatement(char[] filename, int lineNumber, IdentifierExp ident) {
		return new CompileTimeBreakStatement(filename, lineNumber, ident);
	}
	
	@Override
	protected CaseStatement newCaseStatement(char[] filename, int lineNumber, Expression exp, Statement statement, int caseEnd, int expStart, int expLength) {
		return new CompileTimeCaseStatement(filename, lineNumber, exp, statement);
	}
	
	@Override
	protected CaseRangeStatement newCaseRangeStatement(char[] filename,
			int lineNumber, Expression first, Expression last, Statement s) {
		return new CompileTimeCaseRangeStatement(filename, lineNumber, first, last, s);
	}
	
	@Override
	protected CompileStatement newCompileStatement(char[] filename, int lineNumber, Expression e) {
		return new CompileTimeCompileStatement(filename, lineNumber, e);
	}
	
	@Override
	protected CompoundStatement newCompoundStatement(char[] filename, int lineNumber, Statements statements) {
		return new CompileTimeCompoundStatement(filename, lineNumber, statements);
	}
	
	@Override
	protected ConditionalStatement newConditionalStatement(char[] filename, int lineNumber, Condition condition, Statement ifbody, Statement elsebody) {
		return new CompileTimeConditionalStatement(filename, lineNumber, condition, ifbody, elsebody);
	}
	
	@Override
	protected ContinueStatement newContinueStatement(char[] filename, int lineNumber, IdentifierExp ident) {
		return new CompileTimeContinueStatement(filename, lineNumber, ident);
	}
	
	@Override
	protected DefaultStatement newDefaultStatement(char[] filename, int lineNumber, Statement s) {
		return new CompileTimeDefaultStatement(filename, lineNumber, s);
	}
	
	@Override
	protected DoStatement newDoStatement(char[] filename, int lineNumber, Statement body, Expression condition2) {
		return new CompileTimeDoStatement(filename, lineNumber, body, condition2);
	}
	
	@Override
	protected ForeachRangeStatement newForeachRangeStatement(char[] filename, int lineNumber, TOK op, Argument a, Expression aggr, Expression upr, Statement body) {
		return new CompileTimeForeachRangeStatement(filename, lineNumber, op, a, aggr, upr, body);
	}
	
	@Override
	protected ForeachStatement newForeachStatement(char[] filename, int lineNumber, TOK op, Arguments arguments, Expression aggr, Statement body) {
		return new CompileTimeForeachStatement(filename, lineNumber, op, arguments, aggr, body);
	}
	
	@Override
	protected ForStatement newForStatement(char[] filename, int lineNumber, Statement init, Expression condition2, Expression increment, Statement body) {
		return new CompileTimeForStatement(filename, lineNumber, init, condition2, increment, body);
	}
	
	@Override
	protected GotoCaseStatement newGotoCaseStatement(char[] filename, int lineNumber, Expression exp) {
		return new CompileTimeGotoCaseStatement(filename, lineNumber, exp);
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
	protected LabelStatement newLabelStatement(char[] filename, int lineNumber, IdentifierExp label, Statement s) {
		return new CompileTimeLabelStatement(filename, lineNumber, label, s);
	}
	
	@Override
	protected OnScopeStatement newOnScopeStatement(char[] filename, int lineNumber, TOK t2, Statement st) {
		return new CompileTimeOnScopeStatement(filename, lineNumber, t2, st);
	}
	
	@Override
	protected PragmaStatement newPragmaStatement(char[] filename, int lineNumber, IdentifierExp ident, Expressions args, Statement body) {
		return new CompileTimePragmaStatement(filename, lineNumber, ident, args, body);
	}
	
	@Override
	protected ScopeStatement newScopeStatement(char[] filename, int lineNumber, Statement statement) {
		return new CompileTimeScopeStatement(filename, lineNumber, statement);
	}
	
	@Override
	protected StaticAssertStatement newStaticAssertStatement(StaticAssert assert1) {
		return new CompileTimeStaticAssertStatement(assert1);
	}
	
	@Override
	protected SwitchStatement newSwitchStatement(char[] filename, int lineNumber, Expression condition2, Statement body, boolean isfinal) {
		return new CompileTimeSwitchStatement(filename, lineNumber, condition2, body, isfinal);
	}
	
	@Override
	protected SynchronizedStatement newSynchronizedStatement(char[] filename, int lineNumber, Expression exp, Statement body) {
		return new CompileTimeSynchronizedStatement(filename, lineNumber, exp, body);
	}
	
	@Override
	protected ThrowStatement newThrowStatement(char[] filename, int lineNumber, Expression exp) {
		return new CompileTimeThrowStatement(filename, lineNumber, exp);
	}
	
	@Override
	protected TryCatchStatement newTryCatchStatement(char[] filename, int lineNumber, Statement body, Array catches) {
		return new CompileTimeTryCatchStatement(filename, lineNumber, body, catches);
	}
	
	@Override
	protected TryFinallyStatement newTryFinallyStatement(char[] filename, int lineNumber, Statement s, Statement finalbody, boolean b) {
		return new CompileTimeTryFinallyStatement(filename, lineNumber, s, finalbody, b);
	}
	
	@Override
	protected VolatileStatement newVolatileStatement(char[] filename, int lineNumber, Statement s) {
		return new CompileTimeVolatileStatement(filename, lineNumber, s);
	}
	
	@Override
	protected WhileStatement newWhileStatement(char[] filename, int lineNumber, Expression condition2, Statement body) {
		return new CompileTimeWhileStatement(filename, lineNumber, condition2, body);
	}
	
	@Override
	protected WithStatement newWithStatement(char[] filename, int lineNumber, Expression exp, Statement body) {
		return new CompileTimeWithStatement(filename, lineNumber, exp, body);
	}
	
	@Override
	protected PragmaDeclaration newPragmaDeclaration(char[] filename, int lineNumber, IdentifierExp ident, Expressions args, Dsymbols a) {
		return new CompileTimePragmaDeclaration(filename, lineNumber, ident, args, a);
	}
	
	@Override
	protected AlignDeclaration newAlignDeclaration(int i, Dsymbols a) {
		return new CompileTimeAlignDeclaration(i, a);
	}
	
	@Override
	protected AnonDeclaration newAnonDeclaration(char[] filename, int lineNumber, boolean b, Dsymbols decl) {
		return new CompileTimeAnonDeclaration(filename, lineNumber, b, decl);
	}
	
	@Override
	protected CompileDeclaration newCompileDeclaration(char[] filename, int lineNumber, Expression e) {
		return new CompileTimeCompileDeclaration(filename, lineNumber, e);
	}
	
	@Override
	protected LinkDeclaration newLinkDeclaration(LINK link, Dsymbols ax) {
		return new CompileTimeLinkDeclaration(link, ax);
	}
	
	@Override
	protected DebugSymbol newDebugSymbol(char[] filename, int lineNumber, IdentifierExp id, Version version) {
		return new CompileTimeDebugSymbol(filename, lineNumber, id, version);
	}
	
	@Override
	protected AliasDeclaration newAliasDeclaration(char[] filename, int lineNumber, IdentifierExp ident, Type t) {
		return new CompileTimeAliasDeclaration(filename, lineNumber, ident, t);
	}
	
	@Override
	protected CtorDeclaration newCtorDeclaration(char[] filename, int lineNumber, Arguments arguments, int i) {
		return new CompileTimeCtorDeclaration(filename, lineNumber, arguments, i);
	}
	
	@Override
	protected DeleteDeclaration newDeleteDeclaration(char[] filename, int lineNumber, Arguments arguments) {
		return new CompileTimeDeleteDeclaration(filename, lineNumber, arguments);
	}
	
	@Override
	protected DtorDeclaration newDtorDeclaration(char[] filename, int lineNumber) {
		return new CompileTimeDtorDeclaration(filename, lineNumber);
	}
	
	@Override
	protected InvariantDeclaration newInvariantDeclaration(char[] filename, int lineNumber) {
		return new CompileTimeInvariantDeclaration(filename, lineNumber);
	}
	
	@Override
	protected NewDeclaration newNewDeclaration(char[] filename, int lineNumber, Arguments arguments, int i) {
		return new CompileTimeNewDeclaration(filename, lineNumber, arguments, i);
	}
	
	@Override
	protected PostBlitDeclaration newPostBlitDeclaration(char[] filename, int lineNumber) {
		return new CompileTimePostBlitDeclaration(filename, lineNumber);
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
	protected TypedefDeclaration newTypedefDeclaration(char[] filename, int lineNumber, IdentifierExp ident, Type t, Initializer init) {
		return new CompileTimeTypedefDeclaration(filename, lineNumber, ident, t, init);
	}
	
	@Override
	protected EnumMember newEnumMember(char[] filename, int lineNumber, IdentifierExp ident, Expression value) {
		return new CompileTimeEnumMember(filename, lineNumber, ident, value);
	}
	
	@Override
	protected EnumMember newEnumMember(char[] filename, int lineNumber, IdentifierExp exp, Expression value, Type type) {
		return new CompileTimeEnumMember(filename, lineNumber, exp, value, type);
	}
	
	@Override
	protected StaticAssert newStaticAssert(char[] filename, int lineNumber, Expression exp, Expression msg) {
		return new CompileTimeStaticAssert(filename, lineNumber, exp, msg);
	}
	
	@Override
	protected VersionSymbol newVersionSymbol(char[] filename, int lineNumber, IdentifierExp id, Version version) {
		return new CompileTimeVersionSymbol(filename, lineNumber, id, version);
	}
	
	@Override
	protected ClassDeclaration newClassDeclaration(char[] filename, int lineNumber, IdentifierExp id, BaseClasses baseClasses) {
		return new CompileTimeClassDeclaration(filename, lineNumber, id, baseClasses);
	}
	
	@Override
	protected InterfaceDeclaration newInterfaceDeclaration(char[] filename, int lineNumber, IdentifierExp id, BaseClasses baseClasses) {
		return new CompileTimeInterfaceDeclaration(filename, lineNumber, id, baseClasses);
	}
	
	@Override
	protected UnionDeclaration newUnionDeclaration(char[] filename, int lineNumber, IdentifierExp id) {
		return new CompileTimeUnionDeclaration(filename, lineNumber, id);
	}
	
	@Override
	protected StructDeclaration newStructDeclaration(char[] filename, int lineNumber, IdentifierExp id) {
		return new CompileTimeStructDeclaration(filename, lineNumber, id);
	}
	
	@Override
	protected EnumDeclaration newEnumDeclaration(char[] filename, int lineNumber, IdentifierExp id, Type t) {
		return new CompileTimeEnumDeclaration(filename, lineNumber, id, t);
	}
	
	@Override
	protected TemplateDeclaration newTemplateDeclaration(char[] filename, int lineNumber, IdentifierExp ident, TemplateParameters tpl, Expression constraint, Dsymbols decldefs) {
		return new CompileTimeTemplateDeclaration(filename, lineNumber, ident, tpl, constraint, decldefs);
	}
	
	@Override
	protected TemplateMixin newTemplateMixin(char[] filename, int lineNumber, IdentifierExp id, Type tqual, Identifiers idents, Objects tiargs) {
		return new CompileTimeTemplateMixin(filename, lineNumber, id, tqual, idents, tiargs, encoder);
	}

}
