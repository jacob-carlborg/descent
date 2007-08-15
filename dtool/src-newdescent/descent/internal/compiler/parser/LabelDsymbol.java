package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class LabelDsymbol extends Dsymbol {
	
	public LabelStatement statement;
	
	public LabelDsymbol(Loc loc, IdentifierExp ident) {
		super(loc, ident);
	}
	
	public LabelDsymbol(Loc loc, Identifier ident) {
		super(loc, new IdentifierExp(Loc.ZERO, ident));
	}

	@Override
	public int getNodeType() {
		return LABEL_DSYMBOL;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}

}
