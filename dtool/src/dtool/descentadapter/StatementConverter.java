package dtool.descentadapter;

import descent.internal.compiler.parser.AsmBlock;
import descent.internal.compiler.parser.AsmStatement;
import descent.internal.compiler.parser.BreakStatement;
import descent.internal.compiler.parser.CaseRangeStatement;
import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.Catch;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.ContinueStatement;
import descent.internal.compiler.parser.DeclarationExp;
import descent.internal.compiler.parser.DeclarationStatement;
import descent.internal.compiler.parser.DefaultStatement;
import descent.internal.compiler.parser.DoStatement;
import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.ForStatement;
import descent.internal.compiler.parser.ForeachRangeStatement;
import descent.internal.compiler.parser.ForeachStatement;
import descent.internal.compiler.parser.GotoCaseStatement;
import descent.internal.compiler.parser.GotoDefaultStatement;
import descent.internal.compiler.parser.GotoStatement;
import descent.internal.compiler.parser.IfStatement;
import descent.internal.compiler.parser.LabelStatement;
import descent.internal.compiler.parser.OnScopeStatement;
import descent.internal.compiler.parser.PragmaStatement;
import descent.internal.compiler.parser.ReturnStatement;
import descent.internal.compiler.parser.ScopeStatement;
import descent.internal.compiler.parser.StaticAssertStatement;
import descent.internal.compiler.parser.SwitchStatement;
import descent.internal.compiler.parser.SynchronizedStatement;
import descent.internal.compiler.parser.ThrowStatement;
import descent.internal.compiler.parser.TryCatchStatement;
import descent.internal.compiler.parser.TryFinallyStatement;
import descent.internal.compiler.parser.VolatileStatement;
import descent.internal.compiler.parser.WhileStatement;
import descent.internal.compiler.parser.WithStatement;
import dtool.ast.ASTNeoNode;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationStaticAssert;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.StatementAsm;
import dtool.ast.statements.StatementBreak;
import dtool.ast.statements.StatementCase;
import dtool.ast.statements.StatementCaseRange;
import dtool.ast.statements.StatementContinue;
import dtool.ast.statements.StatementDefault;
import dtool.ast.statements.StatementDo;
import dtool.ast.statements.StatementExp;
import dtool.ast.statements.StatementFor;
import dtool.ast.statements.StatementForeach;
import dtool.ast.statements.StatementForeachRange;
import dtool.ast.statements.StatementGoto;
import dtool.ast.statements.StatementGotoCase;
import dtool.ast.statements.StatementGotoDefault;
import dtool.ast.statements.StatementIf;
import dtool.ast.statements.StatementLabel;
import dtool.ast.statements.StatementOnScope;
import dtool.ast.statements.StatementReturn;
import dtool.ast.statements.StatementSwitch;
import dtool.ast.statements.StatementSynchronized;
import dtool.ast.statements.StatementThrow;
import dtool.ast.statements.StatementTry;
import dtool.ast.statements.StatementVolatile;
import dtool.ast.statements.StatementWhile;
import dtool.ast.statements.StatementWith;

public final class StatementConverter extends ExpressionConverter {
	
	@Override
	public boolean visit(ForeachRangeStatement node) {
		return endAdapt(new StatementForeachRange(node));
	}
	
	@Override
	public boolean visit(AsmBlock node) {
		return endAdapt(new BlockStatement(node));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.CompoundStatement elem) {
		return endAdapt(new BlockStatement(elem));
	}
	
	@Override
	public boolean visit(AsmStatement element) {
		return endAdapt(new StatementAsm(element));
	}
	
	@Override
	public boolean visit(BreakStatement element) {
		return endAdapt(new StatementBreak(element));
	}

	@Override
	public boolean visit(CaseStatement element) {
		return endAdapt(new StatementCase(element));
	}
	
	@Override
	public boolean visit(CaseRangeStatement element) {
		return endAdapt(new StatementCaseRange(element));
	}
	
	@Override
	public boolean visit(ConditionalStatement element) {
		return endAdapt(DeclarationConditional.create(element));
	}

	@Override
	public boolean visit(ContinueStatement element) {
		return endAdapt(new StatementContinue(element));
	}

	@Override
	public boolean visit(DeclarationStatement element) {
		return visit((DeclarationExp) element.exp);
	}

	@Override
	public boolean visit(DefaultStatement element) {
		return endAdapt(new StatementDefault(element));
	}

	@Override
	public boolean visit(DoStatement element) {
		return endAdapt(new StatementDo(element));
	}
	
	@Override
	public boolean visit(ExpStatement element) {
		return endAdapt(new StatementExp(element));
	}
	
	@Override
	public boolean visit(ForeachStatement element) {
		return endAdapt(new StatementForeach(element));
	}

	@Override
	public boolean visit(ForStatement element) {
		return endAdapt(new StatementFor(element));
	}

	@Override
	public boolean visit(GotoCaseStatement element) {
		return endAdapt(new StatementGotoCase(element));
	}

	@Override
	public boolean visit(GotoDefaultStatement element) {
		return endAdapt(new StatementGotoDefault(element));
	}

	@Override
	public boolean visit(GotoStatement element) {
		return endAdapt(new StatementGoto(element));
	}

	@Override
	public boolean visit(IfStatement element) {
		return endAdapt(new StatementIf(element));
	}

	@Override
	public boolean visit(LabelStatement element) {
		return endAdapt(new StatementLabel(element));
	}

	@Override
	public boolean visit(OnScopeStatement element) {
		return endAdapt(new StatementOnScope(element));
	}

	@Override
	public boolean visit(PragmaStatement element) {
		return endAdapt(new DeclarationPragma(element));
	}

	@Override
	public boolean visit(ReturnStatement element) {
		return endAdapt(new StatementReturn(element));
	}

	@Override
	public boolean visit(ScopeStatement element) {
		return endAdapt(new BlockStatement(element));
	}

	@Override
	public boolean visit(StaticAssertStatement element) {
		return endAdapt(new DeclarationStaticAssert(element));
	}

	@Override
	public boolean visit(SwitchStatement element) {
		return endAdapt(new StatementSwitch(element));
	}

	@Override
	public boolean visit(SynchronizedStatement element) {
		return endAdapt(new StatementSynchronized(element));
	}

	@Override
	public boolean visit(ThrowStatement element) {
		return endAdapt(new StatementThrow(element));
	}

	@Override
	public boolean visit(TryCatchStatement element) {
		return endAdapt(new StatementTry(element));
	}

	@Override
	public boolean visit(TryFinallyStatement element) {
		return endAdapt(new StatementTry(element));
	}

	@Override
	public boolean visit(VolatileStatement element) {
		return endAdapt(new StatementVolatile(element));
	}

	@Override
	public boolean visit(WhileStatement element) {
		return endAdapt(new StatementWhile(element));
	}

	@Override
	public boolean visit(WithStatement element) {
		return endAdapt(new StatementWith(element));
	}
	
	@Override
	public boolean visit(Catch element) {
		return endAdapt(new StatementTry.CatchClause(element));
	}

	@Override
	public boolean visit(DeclarationExp node) {
		return endAdapt((ASTNeoNode) dtool.ast.declarations.Declaration.convert(node.declaration));
	}

}
