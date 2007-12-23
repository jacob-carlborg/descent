package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class DotTemplateExp extends UnaExp {

	public ITemplateDeclaration td;

	public DotTemplateExp(Loc loc, Expression e1, ITemplateDeclaration td) {
		super(loc, TOK.TOKdottd, e1);
		this.td = td;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return DOT_TEMPLATE_EXP;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
		buf.writeByte('.');
		buf.writestring(td.toChars(context));
	}

}
