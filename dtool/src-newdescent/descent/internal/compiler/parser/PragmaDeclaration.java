package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TOK.TOKstring;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.core.domX.IASTVisitor;

public class PragmaDeclaration extends AttribDeclaration {

	public List<Expression> args;

	public PragmaDeclaration(Loc loc, IdentifierExp ident, List<Expression> args,
			List<Dsymbol> decl) {
		super(loc, decl);
		this.ident = ident;
		this.args = args;
	}

	@Override
	public int getNodeType() {
		return PRAGMA_DECLARATION;
	}

	@Override
	public String kind() {
		return "pragma";
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, args);
			TreeVisitor.acceptChildren(visitor, decl);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean oneMember(Dsymbol[] ps) {
		ps[0] = null;
		return true;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) { // Should be
		// merged with
		// PragmaStatement

		if (ident.ident.equals(Id.msg.string)) {
			if (args != null) {
				for (int i = 0; i < args.size(); i++) {
					Expression e = args.get(i);

					e = e.semantic(sc, context);
					e = e.optimize(WANTvalue | WANTinterpret);
					if (e.op == TOKstring) {
						/*
						 * TODO semantic StringExp se = (StringExp )e;
						 * fprintf(stdmsg, "%.*s", (int)se.len, se.string);
						 */
					} else {
						context.acceptProblem(Problem.newSemanticTypeError(
								"String expected for message",
								IProblem.IllegalParameters, 0,
								e.start, e.length));
					}
				}
				// fprintf(stdmsg, "\n");
			}
			// goto Lnodecl
			if (decl != null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						"pragma is missing closing ';'",
						IProblem.PragmaIsMissingClosingSemicolon, 0, start,
						"pragma".length()));
			}
			return;
		} else if (ident.ident.equals(Id.lib.string)) {
			if (args == null || args.size() != 1) {
				context
						.acceptProblem(Problem
								.newSemanticTypeError(
										"lib pragma must recieve a single argument of type string",
										IProblem.IllegalParameters, 0, start,
										"pragma".length()));
			} else {
				Expression e = args.get(0);

				e = e.semantic(sc, context);
				e = e.optimize(WANTvalue | WANTinterpret);
				args.set(0, e);
				if (e.op != TOKstring)
					context.acceptProblem(Problem.newSemanticTypeError(
							"String expected for library name",
							IProblem.IllegalParameters, 0, e.start, e.length));
				else if (context.global.params.verbose) {
					/*
					 * TODO semantic StringExp se = (StringExp )e; char *name =
					 * (char *)mem.malloc(se.len + 1); memcpy(name, se.string,
					 * se.len); name[se.len] = 0; printf("library %s\n", name);
					 * mem.free(name);
					 */
				}
			}
			// goto Lnodecl;
			if (decl != null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						"pragma is missing closing ';'",
						IProblem.PragmaIsMissingClosingSemicolon, 0, start,
						"pragma".length()));
			}
			return;
		} else {
			context.acceptProblem(Problem.newSemanticTypeError(
					"Unrecognized pragma", IProblem.UnrecognizedPragma, 0,
					ident.start, ident.length));
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
		pd = new PragmaDeclaration(loc, ident, Expression.arraySyntaxCopy(args),
				arraySyntaxCopy(decl));
		return pd;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.printf("pragma(%s");
		buf.printf(ident.toChars());
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
