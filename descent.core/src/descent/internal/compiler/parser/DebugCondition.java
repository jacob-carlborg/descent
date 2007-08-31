package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class DebugCondition extends DVCondition {

	public DebugCondition(long level, Identifier id) {
		super(level, id);
	}

	@Override
	public int getConditionType() {
		return DEBUG;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

	@Override
	public char[] toCharArray() {
		if (ident != null) {
			return ident.string;
		} else {
			return String.valueOf(level).toCharArray();
		}
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		if (ident != null)
			buf.printf("debug (" + ident.toChars() + ")");
		else
			buf.printf("debug (" + level + ")");
	}

}
