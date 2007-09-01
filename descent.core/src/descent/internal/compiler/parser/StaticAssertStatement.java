package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class StaticAssertStatement extends Statement {

	public StaticAssert sa;

	public StaticAssertStatement(StaticAssert sa) {
		super(sa.loc);
		this.sa = sa;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sa);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return STATIC_ASSERT_STATEMENT;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		sa.semantic2(sc, context);
		return null;
	}

	@Override
	public Statement syntaxCopy() {
		StaticAssertStatement s = new StaticAssertStatement((StaticAssert) sa
				.syntaxCopy(null));
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		sa.toCBuffer(buf, hgs, context);
	}

}
