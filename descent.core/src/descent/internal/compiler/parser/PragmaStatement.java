package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class PragmaStatement extends Statement {

	public IdentifierExp ident;
	public List<Expression> args;
	public Statement body;

	public PragmaStatement(Loc loc, IdentifierExp ident, List<Expression> args, Statement body) {
		super(loc);
		this.ident = ident;
		this.args = args;
		this.body = body;
	}
	
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
	public Statement semantic(Scope sc, SemanticContext context) {
		if (CharOperation.equals(ident.ident, Id.msg)) {
			if (args != null) {
				for (int i = 0; i < args.size(); i++) {
					Expression e = (Expression) args.get(i);

					e = e.semantic(sc, context);
					e = e.optimize(WANTvalue | WANTinterpret);
					if (e.op == TOK.TOKstring) {
						StringExp se = (StringExp) e;
						/* TODO semantic
						 fprintf(stdmsg, "%.*s", (int)se.len, se.string);
						 */
					} else {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.IllegalParameters, 0,
								e.start, e.length, new String[] { "String expected for message" }));
					}
				}
				/* TODO semantic
				 fprintf(stdmsg, "\n");
				 */
			}
		} else if (CharOperation.equals(ident.ident, Id.lib)) {
			if (args == null || args.size() != 1) {
				context
						.acceptProblem(Problem
								.newSemanticTypeError(
										IProblem.IllegalParameters, 0, start,
										"pragma".length(), new String[] { "lib pragma must recieve a single argument of type string" }));
			} else {
				Expression e = (Expression) args.get(0);
				e = e.semantic(sc, context);
				e = e.optimize(WANTvalue | WANTinterpret);
				args.set(0, e);
				if (e.op != TOK.TOKstring) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.IllegalParameters, 0,
							e.start, e.length, new String[] { "String expected for library name" }));
				} else if (context.global.params.verbose) {
					/* TODO semantic
					 StringExp se = (StringExp )e;
					 char *name = (char *)mem.malloc(se.len + 1);
					 memcpy(name, se.string, se.len);
					 name[se.len] = 0;
					 printf("library   %s\n", name);
					 mem.free(name);
					 */
				}
			}
		} else {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.UnrecognizedPragma, 0, ident.start, ident.length));
		}

		if (body != null) {
			body = body.semantic(sc, context);
		}
		return body;
	}
	
	@Override
	public int getNodeType() {
		return PRAGMA_STATEMENT;
	}

}
