package dtool.descentadapter;

import descent.internal.core.dom.AsmStatement;
import descent.internal.core.dom.BreakStatement;
import descent.internal.core.dom.CaseStatement;
import descent.internal.core.dom.Catch;
import descent.internal.core.dom.ConditionalStatement;
import descent.internal.core.dom.ContinueStatement;
import descent.internal.core.dom.DeclarationStatement;
import descent.internal.core.dom.DefaultStatement;
import descent.internal.core.dom.DoStatement;
import descent.internal.core.dom.ExpStatement;
import descent.internal.core.dom.ForStatement;
import descent.internal.core.dom.ForeachStatement;
import descent.internal.core.dom.GotoCaseStatement;
import descent.internal.core.dom.GotoDefaultStatement;
import descent.internal.core.dom.GotoStatement;
import descent.internal.core.dom.IfStatement;
import descent.internal.core.dom.LabelStatement;
import descent.internal.core.dom.OnScopeStatement;
import descent.internal.core.dom.PragmaStatement;
import descent.internal.core.dom.ReturnStatement;
import descent.internal.core.dom.ScopeStatement;
import descent.internal.core.dom.StaticAssertStatement;
import descent.internal.core.dom.SwitchStatement;
import descent.internal.core.dom.SynchronizedStatement;
import descent.internal.core.dom.ThrowStatement;
import descent.internal.core.dom.TryCatchStatement;
import descent.internal.core.dom.TryFinallyStatement;
import descent.internal.core.dom.VolatileStatement;
import descent.internal.core.dom.WhileStatement;
import descent.internal.core.dom.WithStatement;
import dtool.dom.statements.CompoundStatement;
import dtool.dom.statements.StatementAsm;
import dtool.dom.statements.StatementBreak;
import dtool.dom.statements.StatementCase;
import dtool.dom.statements.StatementConditional;
import dtool.dom.statements.StatementContinue;
import dtool.dom.statements.StatementDeclaration;
import dtool.dom.statements.StatementDefault;
import dtool.dom.statements.StatementDo;
import dtool.dom.statements.StatementExp;
import dtool.dom.statements.StatementFor;
import dtool.dom.statements.StatementForeach;
import dtool.dom.statements.StatementGoto;
import dtool.dom.statements.StatementGotoCase;
import dtool.dom.statements.StatementGotoDefault;
import dtool.dom.statements.StatementIf;
import dtool.dom.statements.StatementLabel;
import dtool.dom.statements.StatementOnScope;
import dtool.dom.statements.StatementPragma;
import dtool.dom.statements.StatementReturn;
import dtool.dom.statements.StatementScope;
import dtool.dom.statements.StatementStaticAssert;
import dtool.dom.statements.StatementSwitch;
import dtool.dom.statements.StatementSynchronized;
import dtool.dom.statements.StatementThrow;
import dtool.dom.statements.StatementTry;
import dtool.dom.statements.StatementVolatile;
import dtool.dom.statements.StatementWhile;
import dtool.dom.statements.StatementWith;

public class StatementConverter extends ExpressionConverter {

	public boolean visit(descent.internal.core.dom.CompoundStatement elem) {
		return endAdapt(new CompoundStatement(elem));
	}
	
	public boolean visit(AsmStatement element) {
		return endAdapt(new StatementAsm(element));
	}
	
	public boolean visit(BreakStatement element) {
		return endAdapt(new StatementBreak(element));
	}

	public boolean visit(CaseStatement element) {
		return endAdapt(new StatementCase(element));
	}
	
	public boolean visit(ConditionalStatement element) {
		return endAdapt(new StatementConditional(element));
	}

	public boolean visit(ContinueStatement element) {
		return endAdapt(new StatementContinue(element));
	}

	public boolean visit(DeclarationStatement element) {
		return endAdapt(new StatementDeclaration(element));
	}

	public boolean visit(DefaultStatement element) {
		return endAdapt(new StatementDefault(element));
	}

	public boolean visit(DoStatement element) {
		return endAdapt(new StatementDo(element));
	}
	
	public boolean visit(ExpStatement element) {
		return endAdapt(new StatementExp(element));
	}

	public boolean visit(ForeachStatement element) {
		return endAdapt(new StatementForeach(element));
	}

	public boolean visit(ForStatement element) {
		return endAdapt(new StatementFor(element));
	}

	public boolean visit(GotoCaseStatement element) {
		return endAdapt(new StatementGotoCase(element));
	}

	public boolean visit(GotoDefaultStatement element) {
		return endAdapt(new StatementGotoDefault(element));
	}

	public boolean visit(GotoStatement element) {
		return endAdapt(new StatementGoto(element));
	}

	public boolean visit(IfStatement element) {
		return endAdapt(new StatementIf(element));
	}

	public boolean visit(LabelStatement element) {
		return endAdapt(new StatementLabel(element));
	}

	public boolean visit(OnScopeStatement element) {
		return endAdapt(new StatementOnScope(element));
	}

	public boolean visit(PragmaStatement element) {
		return endAdapt(new StatementPragma(element));
	}

	public boolean visit(ReturnStatement element) {
		return endAdapt(new StatementReturn(element));
	}

	public boolean visit(ScopeStatement element) {
		return endAdapt(new StatementScope(element));
	}

	public boolean visit(StaticAssertStatement element) {
		return endAdapt(new StatementStaticAssert(element));
	}

	public boolean visit(SwitchStatement element) {
		return endAdapt(new StatementSwitch(element));
	}

	public boolean visit(SynchronizedStatement element) {
		return endAdapt(new StatementSynchronized(element));
	}

	public boolean visit(ThrowStatement element) {
		return endAdapt(new StatementThrow(element));
	}

	public boolean visit(TryCatchStatement element) {
		return endAdapt(new StatementTry(element));
	}

	public boolean visit(TryFinallyStatement element) {
		return endAdapt(new StatementTry(element));
	}

	public boolean visit(VolatileStatement element) {
		return endAdapt(new StatementVolatile(element));
	}

	public boolean visit(WhileStatement element) {
		return endAdapt(new StatementWhile(element));
	}

	public boolean visit(WithStatement element) {
		return endAdapt(new StatementWith(element));
	}
	
	public boolean visit(Catch element) {
		return endAdapt(new StatementTry.CatchClause(element));
	}

}
