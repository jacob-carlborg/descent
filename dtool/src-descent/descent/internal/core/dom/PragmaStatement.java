package descent.internal.core.dom;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;


import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IPragmaStatement;
import descent.core.domX.IASTVisitor;

public class PragmaStatement extends Statement implements IPragmaStatement {
	
	public Identifier ident;
	public Expression[] expressions;
	public Statement body;

	public PragmaStatement(Identifier ident, List<Expression> args, Statement body) {
		this.ident = ident;
		if (args != null) {
			expressions = args.toArray(new Expression[args.size()]);
		}
		this.body = body;
	}
	
	public IName getIdentifier() {
		return ident;
	}
	
	public IExpression[] getArguments() {
		return expressions == null ? new IExpression[0] : expressions;
	}
	
	public Statement getBody() {
		return body;
	}
	
	public int getElementType() {
		return ElementTypes.PRAGMA_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, ident);
			TreeVisitor.acceptChildren(visitor, expressions);
			TreeVisitor.acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}
	
}
