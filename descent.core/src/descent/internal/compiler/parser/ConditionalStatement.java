package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ConditionalStatement extends Statement {

	public Condition condition;
	public Statement ifbody;
	public Statement elsebody;

	public ConditionalStatement(Loc loc, Condition condition, Statement ifbody,
			Statement elsebody) {
		super(loc);
		this.condition = condition;
		this.ifbody = ifbody;
		this.elsebody = elsebody;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, condition);
			TreeVisitor.acceptChildren(visitor, ifbody);
			TreeVisitor.acceptChildren(visitor, elsebody);
		}
		visitor.endVisit(this);
	}

	@Override
	public Statements flatten(Scope sc, SemanticContext context) {
		Statement s;

		if (condition.include(sc, null, context)) {
			s = ifbody;
		} else {
			s = elsebody;
		}

		Statements a = new Statements();
		a.add(s);
		return a;
	}

	@Override
	public int getNodeType() {
		return CONDITIONAL_STATEMENT;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		if (condition.include(sc, null, context)) {
			ifbody = ifbody.semantic(sc, context);
			return ifbody;
		} else {
			if (elsebody != null) {
				elsebody = elsebody.semantic(sc, context);
			}
			return elsebody;
		}
	}

	@Override
	public Statement syntaxCopy(SemanticContext context) {
		Statement e = null;
		if (elsebody != null) {
			e = elsebody.syntaxCopy(context);
		}
		ConditionalStatement s = new ConditionalStatement(loc, condition
				.syntaxCopy(context), ifbody.syntaxCopy(context), e);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		condition.toCBuffer(buf, hgs, context);
		buf.writenl();
		if (ifbody != null) {
			ifbody.toCBuffer(buf, hgs, context);
		}
		if (elsebody != null) {
			buf.writestring("else");
			buf.writenl();
			elsebody.toCBuffer(buf, hgs, context);
		}
		buf.writenl();
	}

	@Override
	public boolean usesEH() {
		return (ifbody != null && ifbody.usesEH())
				|| (elsebody != null && elsebody.usesEH());
	}

}
