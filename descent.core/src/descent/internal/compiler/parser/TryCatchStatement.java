package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;


public class TryCatchStatement extends Statement {

	public Statement body, sourceBody;
	public Array<Catch> catches, sourceCatches;

	public TryCatchStatement(Loc loc, Statement body, Array<Catch> catches) {
		super(loc);
		this.body = this.sourceBody = body;
		this.catches = catches;
		if (this.catches != null) {
			this.sourceCatches = new Array<Catch>(this.catches);
		}
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceBody);
			TreeVisitor.acceptChildren(visitor, sourceCatches);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public int blockExit(SemanticContext context) {
		int result;

		result = body.blockExit(context);

		for (int i = 0; i < size(catches); i++) {
			Catch c = (Catch) catches.get(i);
			result |= c.blockExit(context);
		}
		return result;
	}

	@Override
	public int getNodeType() {
		return TRY_CATCH_STATEMENT;
	}

	@Override
	public boolean hasBreak() {
		return false;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		body = body.semanticScope(sc, null /*this*/, null, context);

		for (int i = 0; i < size(catches); i++) {
			Catch c;

			c = catches.get(i);
			c.semantic(sc, context);

			// Determine if current catch 'hides' any previous catches
			for (int j = 0; j < i; j++) {
				Catch cj = catches.get(j);
				String si = c.loc.toChars();
				String sj = cj.loc.toChars();

				if (c.type.toBasetype(context).implicitConvTo(
						cj.type.toBasetype(context), context) != MATCHnomatch) {
					if (context.acceptsErrors()) {
						context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.CatchHidesCatch, cj, new String[] { sj, si }));
					}
				}
			}
		}
		
		if (context.isD2()) {
			if (null == body) {
				return null;
			}
		}
		
		return this;
	}

	@Override
	public Statement syntaxCopy(SemanticContext context) {
		Array<Catch> a = new Array<Catch>();
		a.setDim(catches.size());
		for (int i = 0; i < a.size(); i++) {
			Catch c;

			c = catches.get(i);
			c = c.syntaxCopy(context);
			a.set(i, c);
		}
		TryCatchStatement s = context.newTryCatchStatement(loc, body.syntaxCopy(context), a);
		s.copySourceRange(this);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("try");
		buf.writenl();
		if (body != null) {
			body.toCBuffer(buf, hgs, context);
		}
		int i;
		for (i = 0; i < catches.size(); i++) {
			Catch c = catches.get(i);
			c.toCBuffer(buf, hgs, context);
		}
	}

	@Override
	public boolean usesEH(SemanticContext context) {
		return true;
	}

}
