package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKdeclaration;

// DMD 1.020
public class DeclarationStatement extends ExpStatement {

	public DeclarationStatement(Loc loc, Dsymbol s) {
		super(loc, new DeclarationExp(loc, s));
	}

	public DeclarationStatement(Loc loc, Expression exp) {
		super(loc, exp);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor,
					((DeclarationExp) exp).declaration);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return DECLARATION_STATEMENT;
	}

	@Override
	public void scopeCode(Statement[] sentry, Statement[] sexception,
			Statement[] sfinally) {
		sentry[0] = null;
		sexception[0] = null;
		sfinally[0] = null;

		if (exp != null) {
			if (exp.op == TOKdeclaration) {
				DeclarationExp de = (DeclarationExp) (exp);
				VarDeclaration v = de.declaration.isVarDeclaration();
				if (v != null) {
					Expression e;

					e = v.callAutoDtor();
					if (e != null) {
						sfinally[0] = new ExpStatement(loc, e);
					}
				}
			}
		}
	}

	@Override
	public Statement syntaxCopy() {
		DeclarationStatement ds = new DeclarationStatement(loc, exp
				.syntaxCopy());
		return ds;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		exp.toCBuffer(buf, hgs, context);
	}

}
