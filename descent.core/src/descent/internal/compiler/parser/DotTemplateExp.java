package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class DotTemplateExp extends UnaExp {

	public TemplateDeclaration td;

	public DotTemplateExp(Loc loc, Expression e1, TemplateDeclaration td) {
		super(loc, TOK.TOKdottd, e1);
		this.td = td;
	}
	
	
	@Override
	public int getNodeType() {
		return 0;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
	    buf.writeByte('.');
	    buf.writestring(td.toChars());
	}

}
