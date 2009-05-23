package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.BE.*;

public class ScopeStatement extends Statement {

	public Statement statement, sourceStatement;

	public ScopeStatement(Loc loc, Statement s) {
		super(loc);
		this.statement = this.sourceStatement = s;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public int blockExit(SemanticContext context) {
		return statement != null ? statement.blockExit(context) : BEfallthru;
	}

	@Override
	public boolean comeFrom() {
		return statement != null ? statement.comeFrom() : false;
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		return statement != null ? statement.fallOffEnd(context) : true;
	}

	@Override
	public int getNodeType() {
		return SCOPE_STATEMENT;
	}

	@Override
	public boolean hasBreak() {
		return statement != null ? statement.hasBreak() : false;
	}

	@Override
	public boolean hasContinue() {
		return statement != null ? statement.hasContinue() : false;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		if (istate.start == this) {
			istate.start = null;
		}
		return statement != null ? statement.interpret(istate, context) : null;
	}

	@Override
	public ScopeStatement isScopeStatement() {
		return this;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		sc.numberForLocalVariables++;
		
		ScopeDsymbol sym;

		if (statement != null) {
			Statements a;

			sym = new ScopeDsymbol();
			sym.parent = sc.scopesym;
			sc = sc.push(sym);

			a = statement.flatten(sc, context);
			if (a != null) {
				statement = new CompoundStatement(loc, a);
			}

			statement = statement.semantic(sc, context);
			if (statement != null) {
				Statement[] sentry = { null };
				Statement[] sexception = { null };
				Statement[] sfinally = { null };

				statement.scopeCode(sc, sentry, sexception, sfinally);
				if (sfinally[0] != null) {
					statement = new CompoundStatement(loc, statement,
							sfinally[0]);
				}
			}

			sc.pop();
		}
		return this;
	}

	@Override
	public Statement syntaxCopy(SemanticContext context) {
		Statement s;

		s = statement != null ? statement.syntaxCopy(context) : null;
		s = context.newScopeStatement(loc, s);
		s.copySourceRange(this);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writeByte('{');
		buf.writenl();

		if (statement != null) {
			statement.toCBuffer(buf, hgs, context);
		}

		buf.writeByte('}');
		buf.writenl();
	}

	@Override
	public boolean usesEH(SemanticContext context) {
		return statement != null ? statement.usesEH(context) : false;
	}

}
