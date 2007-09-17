package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKstring;

// DMD 1.020
public class PragmaDeclaration extends AttribDeclaration {

	public List<Expression> args;

	public PragmaDeclaration(Loc loc, IdentifierExp ident,
			List<Expression> args, List<Dsymbol> decl) {
		super(loc, decl);
		this.ident = ident;
		this.args = args;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, args);
			TreeVisitor.acceptChildren(visitor, decl);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return PRAGMA_DECLARATION;
	}

	@Override
	public String kind() {
		return "pragma";
	}

	@Override
	public boolean oneMember(Dsymbol[] ps, SemanticContext context) {
		ps[0] = null;
		return true;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) { // Should be
		// merged with PragmaStatement

		if (CharOperation.equals(ident.ident, Id.msg)) {
			if (args != null) {
				for (int i = 0; i < args.size(); i++) {
					Expression e = args.get(i);

					e = e.semantic(sc, context);
					e = e.optimize(WANTvalue | WANTinterpret, context);
					if (e.op == TOKstring) {
					} else {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.StringExpectedForPragmaMsg, 0,
								e.start, e.length));
					}
				}
			}
			// goto Lnodecl
			if (decl != null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.PragmaIsMissingClosingSemicolon, 0, start, 6));
			}
			return;
		} else if (CharOperation.equals(ident.ident, Id.lib)) {
			if (args == null || args.size() != 1) {
				context
						.acceptProblem(Problem
								.newSemanticTypeError(
										IProblem.LibPragmaMustRecieveASingleArgumentOfTypeString,
										0, start, 6));
			} else {
				Expression e = args.get(0);

				e = e.semantic(sc, context);
				e = e.optimize(WANTvalue | WANTinterpret, context);
				args.set(0, e);
				if (e.op != TOKstring) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.StringExpectedForPragmaLib, 0, e.start,
							e.length));
				}
			}
			// goto Lnodecl;
			if (decl != null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.PragmaIsMissingClosingSemicolon, 0, start, 6));
			}
			return;
		} else {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.UnrecognizedPragma, 0, ident.start, ident.length));
		}

		if (decl != null) {
			for (Dsymbol s : decl) {
				s.semantic(sc, context);
			}
		}
		return;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		PragmaDeclaration pd;

		Assert.isTrue(s == null);
		pd = new PragmaDeclaration(loc, ident,
				Expression.arraySyntaxCopy(args), arraySyntaxCopy(decl));
		return pd;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("pragma(");
		buf.writestring(ident.toChars());
		if (args != null) {
			for (Expression e : args) {
				buf.writestring(", ");
				e.toCBuffer(buf, hgs, context);
			}
		}
		buf.writestring(")");
		super.toCBuffer(buf, hgs, context);
	}

}
