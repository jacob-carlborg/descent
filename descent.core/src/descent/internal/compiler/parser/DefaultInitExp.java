package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class DefaultInitExp extends Expression {

	private final TOK subop;

	public DefaultInitExp(Loc loc, TOK subop) {
		super(loc, TOK.TOKdefault);
		this.subop = subop;
	}

	@Override
	public int getNodeType() {
		return DEFAULT_INIT_EXP;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring(subop.toString());
	}

}
