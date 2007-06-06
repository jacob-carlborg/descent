package dtool.dom.base;

import util.tree.TreeVisitor;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

/**
 * A qualified entity/name reference with a semantic constraint
 */
public abstract class BaseEntityRef extends ASTNeoNode {

	public Entity entity;
	
	public EReferenceConstraint refConstraint = null;
	
	public static enum EReferenceConstraint {	
		NONE,
		TYPE,
		EXPVALUE,
	}
	
	public BaseEntityRef(Entity entity) {
		setSourceRange(entity);
		this.entity = entity;
	}

	
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, entity);
		}
		visitor.endVisit(this);
	}
	
	public static class TypeConstraint extends BaseEntityRef {
		public TypeConstraint(Entity entity) { 
			super(entity); 
			refConstraint = EReferenceConstraint.EXPVALUE;
		}


	}
	public static class ValueConstraint extends BaseEntityRef {
		public ValueConstraint(Entity entity) { 
			super(entity); 
			refConstraint = EReferenceConstraint.TYPE;
		}
	}	
	public static class NoConstraint extends BaseEntityRef {
		public NoConstraint(Entity entity) { 
			super(entity); 
			refConstraint = EReferenceConstraint.NONE;
		}
	}
	

}
