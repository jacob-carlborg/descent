package descent.internal.compiler.parser;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class VersionCondition extends DVCondition {

	private static char[][] resevered = {
			{ 'D', 'i', 'g', 'i', 't', 'a', 'l', 'M', 'a', 'r', 's' },
			{ 'X', '8', '6' }, { 'X', '8', '6', '_', '6', '4' },
			{ 'W', 'i', 'n', 'd', 'o', 'w', 's' }, { 'W', 'i', 'n', '3', '2' },
			{ 'W', 'i', 'n', '6', '4' }, { 'l', 'i', 'n', 'u', 'x' },
			{ 'L', 'i', 't', 't', 'l', 'e', 'E', 'n', 'd', 'i', 'a', 'n' },
			{ 'B', 'i', 'g', 'E', 'n', 'd', 'i', 'a', 'n' }, { 'a', 'l', 'l' },
			{ 'n', 'o', 'n', 'e' }, };

	public VersionCondition(long level, char[] id) {
		super(level, id);
	}

	@Override
	public int getConditionType() {
		return VERSION;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (ident != null) {
			buf.writestring("version (");
			buf.writestring(ident);
			buf.writestring(")");
		} else {
			buf.writestring("version (");
			buf.writestring(level);
			buf.writestring(")");
		}
	}

	@Override
	public char[] toCharArray() {
		if (ident != null) {
			return ident;
		} else {
			return String.valueOf(level).toCharArray();
		}
	}

	public static void checkPredefined(Loc loc, IdentifierExp ident,
			SemanticContext context) {
		for (int i = 0; i < resevered.length; i++) {
			if (ident.ident != null
					&& CharOperation.equals(ident.ident, resevered[i])) {
				// goto Lerror;
				ident
						.error(
								loc,
								"version identifier '%s' is reserved and cannot be set",
								ident.toChars());
			}
		}

		if (ident.ident != null && ident.ident[0] == 'D'
				&& ident.ident[1] == '_') {
			// goto Lerror;
			ident.error(loc,
					"version identifier '%s' is reserved and cannot be set",
					ident.toChars());
		}
	}

}
