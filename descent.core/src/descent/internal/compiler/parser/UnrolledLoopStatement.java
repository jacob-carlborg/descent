package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class UnrolledLoopStatement extends Statement {

	public List<Statement> statements;

	public UnrolledLoopStatement(Loc loc, List<Statement> statements) {
		super(loc);
		this.statements = statements;
	}

	@Override
	public int getNodeType() {
		return UNROLLED_LOOP_STATEMENT;
	}

	public void accept0(IASTVisitor visitor) {
		Assert.fail("Accept0 on a fake node");
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		sc.noctor++;
		Scope scd = sc.push();
		scd.sbreak = this;
		scd.scontinue = this;

		for (int i = 0; i < statements.size(); i++) {
			Statement s = (Statement) statements.get(i);
			if (s != null) {
				s = s.semantic(scd, context);
				statements.set(i, s);
			}
		}

		scd.pop();
		sc.noctor--;
		return this;
	}
	
	@Override
	public boolean hasBreak() {
		return true;
	}
	
	@Override
	public boolean hasContinue() {
		return true;
	}
	
	@Override
	public boolean fallOffEnd() {
		// TODO semantic
		return false;
	}

}
