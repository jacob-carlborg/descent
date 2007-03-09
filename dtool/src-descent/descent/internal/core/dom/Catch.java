package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class Catch extends AbstractElement  {

	private final Type t;
	private final Identifier id;
	private final Statement handler;

	public Catch(Type t, Identifier id, Statement handler) {
		this.t = t;
		this.id = id;
		this.handler = handler;
	}
	
	public int getElementType() {
		return ElementTypes.CATCH_CLAUSE;
	}
	
	public IType getType() {
		return t;
	}
	
	public IName getName() {
		return id;
	}
	
	public IStatement getHandler() {
		return handler;
	}

	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, t);
			TreeVisitor.acceptChild(visitor, id);
			TreeVisitor.acceptChild(visitor, handler);
		}
		visitor.endVisit(this);
	}

	
}
