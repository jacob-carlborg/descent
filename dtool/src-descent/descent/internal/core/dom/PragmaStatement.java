package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IPragmaStatement;
import descent.core.dom.IStatement;
import descent.core.domX.ASTVisitor;

public class PragmaStatement extends Statement implements IPragmaStatement {
	
	public Identifier ident;
	private Expression[] expressions;
	private Statement body;

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
		return PRAGMA_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChildren(visitor, expressions);
			acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}
	
}
