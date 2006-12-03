package descent.internal.core.dom;

import descent.core.dom.ICatchClause;
import descent.core.dom.ASTVisitor;
import descent.core.dom.ISimpleName;
import descent.core.dom.IStatement;
import descent.core.dom.IType;

public class Catch extends ASTNode implements ICatchClause {

	private final Type t;
	private final Identifier id;
	private final Statement handler;

	public Catch(Type t, Identifier id, Statement handler) {
		this.t = t;
		this.id = id;
		this.handler = handler;
	}
	
	public int getNodeType0() {
		return CATCH_CLAUSE;
	}
	
	public IType getType() {
		return t;
	}
	
	public ISimpleName getName() {
		return id;
	}
	
	public IStatement getHandler() {
		return handler;
	}

	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, t);
			acceptChild(visitor, id);
			acceptChild(visitor, handler);
		}
		visitor.endVisit(this);
	}

	
}
