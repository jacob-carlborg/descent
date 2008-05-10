package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class SynchronizedStatement extends Statement {

	public Expression exp, sourceExp;
	public Statement body, sourceBody;

	public SynchronizedStatement(Loc loc, Expression exp, Statement body) {
		super(loc);
		this.exp = this.sourceExp = exp;
		this.body = this.sourceBody = body;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		return body != null ? body.fallOffEnd(context) : true;
	}

	@Override
	public int getNodeType() {
		return SYNCHRONIZED_STATEMENT;
	}

	@Override
	public boolean hasBreak() {
		return false;
	}

	@Override
	public boolean hasContinue() {
		return false;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		if (exp != null) {
			ClassDeclaration cd;

			exp = exp.semantic(sc, context);
			exp = resolveProperties(sc, exp, context);
			cd = exp.type.isClassHandle();
			if (null == cd) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.CanOnlySynchronizeOnClassObjects, exp, new String[] { exp.type.toChars(context) }));
				}
			} else if (cd.isInterfaceDeclaration() != null) {
				Type t = new TypeIdentifier(Loc.ZERO, new IdentifierExp(
						Id.Object));

				t = t.semantic(Loc.ZERO, sc, context);
				exp = new CastExp(loc, exp, t);
				exp = exp.semantic(sc, context);
			}
		}
		if (body != null) {
			body = body.semantic(sc, context);
		}
		return this;
	}

	@Override
	public Statement syntaxCopy(SemanticContext context) {
		Expression e = exp != null ? exp.syntaxCopy(context) : null;
		SynchronizedStatement s = new SynchronizedStatement(loc, e,
				body != null ? body.syntaxCopy(context) : null);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("synchronized");
		if (exp != null) {
			buf.writebyte('(');
			exp.toCBuffer(buf, hgs, context);
			buf.writebyte(')');
		}
		if (body != null) {
			buf.writebyte(' ');
			body.toCBuffer(buf, hgs, context);
		}
	}

	@Override
	public boolean usesEH() {
		return true;
	}

}
