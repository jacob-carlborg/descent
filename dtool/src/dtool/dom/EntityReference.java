package dtool.dom;

import dtool.dom.ext.ASTNeoVisitor;

/**
 * TODO 
 */
public abstract class EntityReference extends ASTElement {

	public Entity entity;
	public EReferenceConstraint refConstraint = null;
	
	public static enum EReferenceConstraint {
		none,
		type,
		expvalue
	}

	public EntityReference() { }
	
	public EntityReference(Entity entity) {
		this.entity = entity;
	}
	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, entity);
		}
		visitor.endVisit(this);
	}
	
	public static class TypeEntityReference extends EntityReference {
		public TypeEntityReference() { super(); }
		public TypeEntityReference(Entity entity) {
			super(entity);
		}
	}
	public static class ValueEntityReference extends EntityReference {
		public ValueEntityReference() { super(); }
		public ValueEntityReference(Entity entity) {
			super(entity);
		}
	}	
	public static class AnyEntityReference extends EntityReference {
		public AnyEntityReference() { super(); }
		public AnyEntityReference(Entity entity) {
			super(entity);
		}
	}

}
