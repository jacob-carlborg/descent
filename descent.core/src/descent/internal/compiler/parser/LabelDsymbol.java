package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class LabelDsymbol extends Dsymbol {

	public LabelStatement statement;

	public LabelDsymbol(IdentifierExp ident) {
		super(ident);
	}

	public LabelDsymbol(char[] ident) {
		this(new IdentifierExp(ident));
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return LABEL_DSYMBOL;
	}

	@Override
	public LabelDsymbol isLabel() {
		return this;
	}

}
