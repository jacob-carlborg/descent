package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class VersionCondition extends DVCondition {

	public static final char[][] resevered = {
			{ 'D', 'i', 'g', 'i', 't', 'a', 'l', 'M', 'a', 'r', 's' },
			{ 'X', '8', '6' }, 
			{ 'X', '8', '6', '_', '6', '4' },
			{ 'W', 'i', 'n', 'd', 'o', 'w', 's' }, 
			{ 'W', 'i', 'n', '3', '2' },
			{ 'W', 'i', 'n', '6', '4' }, 
			{ 'l', 'i', 'n', 'u', 'x' },
			{ 'P', 'o', 's', 'i', 'x' },
			{ 'O', 'S', 'X' },
			{ 'F', 'r', 'e', 'e', 'B', 'S', 'D' },
			{ 'S', 'o', 'l', 'a', 'r', 'i', 's' },
			{ 'L', 'i', 't', 't', 'l', 'e', 'E', 'n', 'd', 'i', 'a', 'n' },
			{ 'B', 'i', 'g', 'E', 'n', 'd', 'i', 'a', 'n' }, 
			{ 'a', 'l', 'l' },
			{ 'n', 'o', 'n', 'e' }, };

	public static void checkPredefined(Loc loc, IdentifierExp ident,
			SemanticContext context) {
		for (int i = 0; i < resevered.length; i++) {
			if (ident.ident != null
					&& equals(ident, resevered[i])) {
				// goto Lerror;
				if (context.acceptsErrors()) {
					context
							.acceptProblem(Problem.newSemanticTypeError(
									IProblem.VersionIdentifierReserved, ident,
									new String[] { new String(ident.ident) }));
				}
			}
		}

		if (ident.ident != null && ident.ident[0] == 'D'
				&& ident.ident[1] == '_') {
			// goto Lerror;
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.VersionIdentifierReserved, ident, new String[] { new String(ident.ident) }));
			}
		}
	}

	public VersionCondition(Module mod, Loc loc, long level, char[] id) {
		super(mod, loc, level, id);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

	public void addGlobalIdent(IdentifierExp ident, SemanticContext context) {
		checkPredefined(Loc.ZERO, ident, context);
		addPredefinedGlobalIdent(ident, context);
	}

	public void addPredefinedGlobalIdent(IdentifierExp ident,
			SemanticContext context) {
		if (null == context.global.params.versionids) {
			context.global.params.versionids = new HashtableOfCharArrayAndObject();
		}
		context.global.params.versionids.put(ident.ident, this);
	}

	@Override
	public int getConditionType() {
		return VERSION;
	}

	@Override
	public boolean include(Scope sc, ScopeDsymbol s, SemanticContext context) {
		if (inc == 0) {
			inc = 2;
			if (ident != null) {
				if (findCondition(mod.versionids, ident)) {
					inc = 1;
				} else if (findCondition(context.global.params.versionids,
						ident)) {
					inc = 1;
				} else {
					if (null == mod.versionidsNot) {
						mod.versionidsNot = new HashtableOfCharArrayAndObject();
					}
					mod.versionidsNot.put(ident, this);
				}
			} else if (level <= context.global.params.versionlevel
					|| level <= mod.versionlevel) {
				inc = 1;
			}
		}
		return (inc == 1);
	}

	public void setGlobalLevel(long level, SemanticContext context) {
		context.global.params.versionlevel = level;
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

}
