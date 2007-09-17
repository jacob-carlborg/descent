package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeInstance extends TypeQualified {

	public TemplateInstance tempinst;

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, tempinst);
		}
		visitor.endVisit(this);
	}

	public TypeInstance(Loc loc, TemplateInstance tempinst) {
		super(loc, TY.Tinstance);
		this.tempinst = tempinst;
	}

	@Override
	public int getNodeType() {
		return TYPE_INSTANCE;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		OutBuffer tmp = new OutBuffer();

		tempinst.toCBuffer(tmp, hgs, context);
		// TODO semantic
		// toCBuffer2Helper(&tmp, NULL, hgs);
		buf.prependstring(tmp.toChars());
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
	}

}
