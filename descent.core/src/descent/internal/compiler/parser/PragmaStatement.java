package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class PragmaStatement extends Statement {

	public IdentifierExp ident;
	public Expressions args;
	public Statement body;

	public PragmaStatement(Loc loc, IdentifierExp ident, Expressions args,
			Statement body) {
		super(loc);
		this.ident = ident;
		this.args = args;
		this.body = body;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, args);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		if (body != null) {
			return body.fallOffEnd(context);
		}
		return true;
	}

	@Override
	public int getNodeType() {
		return PRAGMA_STATEMENT;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {

		// msg and lib char[] instances are reused by Lexer

		if (CharOperation.equals(ident.ident, Id.msg)) {
			if (args != null) {
				for (int i = 0; i < args.size(); i++) {
					Expression e = args.get(i);

					e = e.semantic(sc, context);
					e = e.optimize(WANTvalue | WANTinterpret, context);
					if (e.op == TOK.TOKstring) {

					} else {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.StringExpectedForPragmaMsg, 0,
								e.start, e.length));
					}
				}
			}
		} else if (CharOperation.equals(ident.ident, Id.lib)) {
			if (args == null || args.size() != 1) {
				context
						.acceptProblem(Problem
								.newSemanticTypeError(
										IProblem.LibPragmaMustRecieveASingleArgumentOfTypeString,
										0, start, "pragma".length()));
			} else {
				Expression e = args.get(0);
				e = e.semantic(sc, context);
				e = e.optimize(WANTvalue | WANTinterpret, context);
				args.set(0, e);
				if (e.op != TOK.TOKstring) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.StringExpectedForPragmaLib, 0, e.start,
							e.length));

				}
			}
		} else {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.UnrecognizedPragma, 0, ident.start, ident.length));
		}

		if (body != null) {
			body = body.semantic(sc, context);
		}
		return body;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("pragma (");
		buf.writestring(ident.toChars());
		if (args != null && args.size() > 0) {
			buf.writestring(", ");
			argsToCBuffer(buf, args, hgs, context);
		}
		buf.writeByte(')');
		if (body != null) {
			buf.writenl();
			buf.writeByte('{');
			buf.writenl();

			body.toCBuffer(buf, hgs, context);

			buf.writeByte('}');
			buf.writenl();
		} else {
			buf.writeByte(';');
			buf.writenl();
		}
	}

	@Override
	public boolean usesEH() {
		return body != null && body.usesEH();
	}

	@Override
	public Statement syntaxCopy() {
		Statement b = null;
		if (body != null)
			b = body.syntaxCopy();
		PragmaStatement s = new PragmaStatement(loc, ident, Expression
				.arraySyntaxCopy(args), b);
		return s;
	}

}
