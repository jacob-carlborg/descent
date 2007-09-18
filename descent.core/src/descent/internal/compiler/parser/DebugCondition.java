package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class DebugCondition extends DVCondition {

	public DebugCondition(Loc loc, long level, char[] id) {
		super(loc, level, id);
	}

	@Override
	public int getConditionType() {
		return DEBUG;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

	@Override
	public char[] toCharArray() {
		if (ident != null) {
			return ident;
		} else {
			return String.valueOf(level).toCharArray();
		}
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		if (ident != null) {
			buf.writestring("debug (");
			buf.writestring(ident);
			buf.writestring(")");
		} else {
			buf.writestring("debug (");
			buf.writestring(level);
			buf.writestring(")");
		}
	}

}
