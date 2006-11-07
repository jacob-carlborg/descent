package descent.internal.core.dom;

import descent.core.dom.ICatch;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.dom.IType;

public class Catch extends AbstractElement implements ICatch {

	private final Type t;
	private final Identifier id;
	private final Statement handler;

	public Catch(Loc loc, Type t, Identifier id, Statement handler) {
		this.t = t;
		this.id = id;
		this.handler = handler;
	}
	
	public int getElementType() {
		return CATCH;
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

	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, t);
			acceptChild(visitor, id);
			acceptChild(visitor, handler);
		}
		visitor.endVisit(this);
	}

	
}
