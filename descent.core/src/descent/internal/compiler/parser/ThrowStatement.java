package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ThrowStatement extends Statement {

	public Expression exp, sourceExp;

	public ThrowStatement(Loc loc, Expression exp) {
		super(loc);
		this.exp = this.sourceExp = exp;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceExp);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		return false;
	}

	@Override
	public int getNodeType() {
		return THROW_STATEMENT;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		FuncDeclaration fd = (FuncDeclaration) sc.parent.isFuncDeclaration(); // SEMANTIC
		fd.hasReturnExp |= 2;

		if (sc.incontract != 0) {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.ThrowStatementsCannotBeInContracts, this));
			}
		}
		exp = exp.semantic(sc, context);
		exp = resolveProperties(sc, exp, context);
		if (null == exp.type.toBasetype(context).isClassHandle()) {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.CanOnlyThrowClassObjects, exp, new String[] { exp.type.toChars(context) }));
			}
		}
		return this;
	}

	@Override
	public Statement syntaxCopy(SemanticContext context) {
		ThrowStatement s = new ThrowStatement(loc, exp.syntaxCopy(context));
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("throw ");
		exp.toCBuffer(buf, hgs, context);
		buf.writeByte(';');
		buf.writenl();
	}

}
