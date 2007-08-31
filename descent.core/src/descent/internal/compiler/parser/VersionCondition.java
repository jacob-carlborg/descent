package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class VersionCondition extends DVCondition {

	public VersionCondition(long level, Identifier id) {
		super(level, id);
	}

	@Override
	public int getConditionType() {
		return VERSION;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (ident != null)
			buf.printf("version (" + ident.toChars() + ")");
		else
			buf.printf("version (" + level + ")");
	}

	@Override
	public char[] toCharArray() {
		if (ident != null) {
			return ident.string;
		} else {
			return String.valueOf(level).toCharArray();
		}
	}

}
