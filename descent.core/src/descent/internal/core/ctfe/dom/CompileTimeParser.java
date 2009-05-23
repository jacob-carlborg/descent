package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.AlignDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.Array;
import descent.internal.compiler.parser.BreakStatement;
import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.CompileStatement;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.ContinueStatement;
import descent.internal.compiler.parser.DeclarationStatement;
import descent.internal.compiler.parser.DefaultStatement;
import descent.internal.compiler.parser.DoStatement;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Dsymbols;
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
import descent.internal.compiler.parser.IfStatement;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.LabelStatement;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.OnScopeStatement;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.PragmaDeclaration;
import descent.internal.compiler.parser.PragmaStatement;
import descent.internal.compiler.parser.ReturnStatement;
import descent.internal.compiler.parser.ScopeStatement;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.Statements;
import descent.internal.compiler.parser.StaticAssert;
import descent.internal.compiler.parser.StaticAssertStatement;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.StaticIfDeclaration;
import descent.internal.compiler.parser.SwitchStatement;
import descent.internal.compiler.parser.SynchronizedStatement;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.ThrowStatement;
import descent.internal.compiler.parser.TryCatchStatement;
import descent.internal.compiler.parser.TryFinallyStatement;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.VolatileStatement;
import descent.internal.compiler.parser.WhileStatement;
import descent.internal.compiler.parser.WithStatement;

public class CompileTimeParser extends Parser {

	public CompileTimeParser(int apiLevel, char[] source, int offset, int length,
			char[] filename, boolean recordLineSeparator) {
		super(apiLevel, source, offset, length, filename, recordLineSeparator);
	}
	
	@Override
	protected TemplateInstance newTemplateInstance(Loc loc, IdentifierExp id,
			ASTNodeEncoder encoder) {
		return new CompileTimeTemplateInstance(loc, id, encoder);
	}
	
	@Override
	protected VarDeclaration newVarDeclaration(Loc loc, Type type, IdentifierExp ident, Initializer init) {
		return new CompileTimeVarDeclaration(loc, type, ident, init);
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
	protected Expression newCallExp(Loc loc, Expression e, Expressions expressions) {
		return new CompileTimeCallExp(loc, e, expressions);
	}
	
	@Override
	protected FuncDeclaration newFuncDeclaration(Loc loc, IdentifierExp ident, int storage_class, TypeFunction typeFunction) {
		return new CompileTimeFuncDeclaration(loc, ident, storage_class, typeFunction);
	}
	
	@Override
	protected IfStatement newIfStatement(Loc loc, Argument arg, Expression condition, Statement ifbody, Statement elsebody) {
		return new CompileTimeIfStatement(loc, arg, condition, ifbody, elsebody);
	}
	
	@Override
	protected ReturnStatement newReturnStatement(Loc loc, Expression exp) {
		return new CompileTimeReturnStatement(loc, exp);
	}
	
	@Override
	protected DeclarationStatement newDeclarationStatement(Loc loc, Dsymbol d) {
		return new CompileTimeDeclarationStatement(loc, d);
	}
	
	@Override
	protected ExpStatement newExpStatement(Loc loc, Expression exp) {
		return new CompileTimeExpStatement(loc, exp);
	}
	
	@Override
	protected BreakStatement newBreakStatement(Loc loc, IdentifierExp ident) {
		return new CompileTimeBreakStatement(loc, ident);
	}
	
	@Override
	protected CaseStatement newCaseStatement(Loc loc, Expression exp, Statement statement, int caseEnd, int expStart, int expLength) {
		return new CompileTimeCaseStatement(loc, exp, statement);
	}
	
	@Override
	protected CompileStatement newCompileStatement(Loc loc, Expression e) {
		return new CompileTimeCompileStatement(loc, e);
	}
	
	@Override
	protected CompoundStatement newCompoundStatement(Loc loc, Statements statements) {
		return new CompileTimeCompoundStatement(loc, statements);
	}
	
	@Override
	protected ConditionalStatement newConditionalStatement(Loc loc, Condition condition, Statement ifbody, Statement elsebody) {
		return new CompileTimeConditionalStatement(loc, condition, ifbody, elsebody);
	}
	
	@Override
	protected ContinueStatement newContinueStatement(Loc loc, IdentifierExp ident) {
		return new CompileTimeContinueStatement(loc, ident);
	}
	
	@Override
	protected DefaultStatement newDefaultStatement(Loc loc, Statement s) {
		return new CompileTimeDefaultStatement(loc, s);
	}
	
	@Override
	protected DoStatement newDoStatement(Loc loc, Statement body, Expression condition2) {
		return new CompileTimeDoStatement(loc, body, condition2);
	}
	
	@Override
	protected ForeachRangeStatement newForeachRangeStatement(Loc loc, TOK op, Argument a, Expression aggr, Expression upr, Statement body) {
		return new CompileTimeForeachRangeStatement(loc, op, a, aggr, upr, body);
	}
	
	@Override
	protected ForeachStatement newForeachStatement(Loc loc, TOK op, Arguments arguments, Expression aggr, Statement body) {
		return new CompileTimeForeachStatement(loc, op, arguments, aggr, body);
	}
	
	@Override
	protected ForStatement newForStatement(Loc loc, Statement init, Expression condition2, Expression increment, Statement body) {
		return new CompileTimeForStatement(loc, init, condition2, increment, body);
	}
	
	@Override
	protected GotoCaseStatement newGotoCaseStatement(Loc loc, Expression exp) {
		return new CompileTimeGotoCaseStatement(loc, exp);
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
	protected LabelStatement newLabelStatement(Loc loc, IdentifierExp label, Statement s) {
		return new CompileTimeLabelStatement(loc, label, s);
	}
	
	@Override
	protected OnScopeStatement newOnScopeStatement(Loc loc, TOK t2, Statement st) {
		return new CompileTimeOnScopeStatement(loc, t2, st);
	}
	
	@Override
	protected PragmaStatement newPragmaStatement(Loc loc, IdentifierExp ident, Expressions args, Statement body) {
		return new CompileTimePragmaStatement(loc, ident, args, body);
	}
	
	@Override
	protected ScopeStatement newScopeStatement(Loc loc, Statement statement) {
		return new CompileTimeScopeStatement(loc, statement);
	}
	
	@Override
	protected StaticAssertStatement newStaticAssertStatement(StaticAssert assert1) {
		return new CompileTimeStaticAssertStatement(assert1);
	}
	
	@Override
	protected SwitchStatement newSwitchStatement(Loc loc, Expression condition2, Statement body) {
		return new CompileTimeSwitchStatement(loc, condition2, body);
	}
	
	@Override
	protected SynchronizedStatement newSynchronizedStatement(Loc loc, Expression exp, Statement body) {
		return new CompileTimeSynchronizedStatement(loc, exp, body);
	}
	
	@Override
	protected ThrowStatement newThrowStatement(Loc loc, Expression exp) {
		return new CompileTimeThrowStatement(loc, exp);
	}
	
	@Override
	protected TryCatchStatement newTryCatchStatement(Loc loc, Statement body, Array catches) {
		return new CompileTimeTryCatchStatement(loc, body, catches);
	}
	
	@Override
	protected TryFinallyStatement newTryFinallyStatement(Loc loc, Statement s, Statement finalbody, boolean b) {
		return new CompileTimeTryFinallyStatement(loc, s, finalbody, b);
	}
	
	@Override
	protected VolatileStatement newVolatileStatement(Loc loc, Statement s) {
		return new CompileTimeVolatileStatement(loc, s);
	}
	
	@Override
	protected WhileStatement newWhileStatement(Loc loc, Expression condition2, Statement body) {
		return new CompileTimeWhileStatement(loc, condition2, body);
	}
	
	@Override
	protected WithStatement newWithStatement(Loc loc, Expression exp, Statement body) {
		return new CompileTimeWithStatement(loc, exp, body);
	}
	
	@Override
	protected PragmaDeclaration newPragmaDeclaration(Loc loc, IdentifierExp ident, Expressions args, Dsymbols a) {
		return new CompileTimePragmaDeclaration(loc, ident, args, a);
	}
	
	@Override
	protected AlignDeclaration newAlignDeclaration(int i, Dsymbols a) {
		return new CompileTimeAlignDeclaration(i, a);
	}

}
