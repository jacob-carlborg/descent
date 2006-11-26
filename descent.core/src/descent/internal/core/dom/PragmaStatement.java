package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IPragmaStatement;
import descent.core.dom.IStatement;

public class PragmaStatement extends Statement implements IPragmaStatement {
	
	public Identifier ident;
	private IExpression[] expressions;
	private IStatement body;

	public PragmaStatement(Loc loc, Identifier ident, List<Expression> args, Statement body) {
		this.ident = ident;
		if (args != null) {
			expressions = args.toArray(new IExpression[args.size()]);
		}
		this.body = body;
	}
	
	public IName getIdentifier() {
		return ident;
	}
	
	public IExpression[] getArguments() {
		return expressions == null ? new IExpression[0] : expressions;
	}
	
	public IStatement getBody() {
		return body;
	}
	
	public int getElementType() {
		return PRAGMA_STATEMENT;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChildren(visitor, expressions);
			acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}
	
}
