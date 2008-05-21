package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class FileInitExp extends Expression {

	public FileInitExp(Loc loc) {
		super(loc, TOK.TOKfile);
	}

	@Override
	public int getNodeType() {
		return FILE_INIT_EXP;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
