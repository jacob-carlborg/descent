package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Parser.PScurlyscope;
import static descent.internal.compiler.parser.Parser.PSsemi;
import static descent.internal.compiler.parser.TOK.TOKeof;

// DMD 1.020
public class CompileStatement extends Statement {

	public Expression exp;

	public CompileStatement(Loc loc, Expression exp) {
		super(loc);
		this.exp = exp;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return COMPILE_STATEMENT;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		exp = exp.semantic(sc, context);
		exp = ASTDmdNode.resolveProperties(sc, exp, context);
		exp = exp.optimize(ASTDmdNode.WANTvalue | ASTDmdNode.WANTinterpret,
				context);
		if (exp.op != TOK.TOKstring) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.ArgumentToMixinMustBeString, this, new String[] { exp.toChars(context) }));
			return this;
		}
		StringExp se = (StringExp) exp;
		se = se.toUTF8(sc, context);
		Parser p = new Parser(context.Module_rootModule.apiLevel, se.string);
		p.loc = loc;

		Statements statements = new Statements();
		while (p.token.value != TOKeof) {
			Statement s = p.parseStatement(PSsemi | PScurlyscope);
			statements.add(s);
		}
		
		// TODO semantic do this better
		if (p.problems != null) {
			for (int i = 0; i < p.problems.size(); i++) {
				Problem problem = (Problem) p.problems.get(i);
				problem.setSourceStart(start);
				problem.setSourceEnd(start + length - 1);
				context.acceptProblem(problem);
			}
		}

		Statement s = new CompoundStatement(loc, statements);
		return s.semantic(sc, context);
	}

	@Override
	public Statement syntaxCopy() {
		Expression e = exp.syntaxCopy();
		CompileStatement es = new CompileStatement(loc, e);
		return es;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("mixin(");
		exp.toCBuffer(buf, hgs, context);
		buf.writestring(");");
		if (0 == hgs.FLinit.init) {
			buf.writenl();
		}
	}

}
