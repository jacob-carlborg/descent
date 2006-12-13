package dtool.dom;

import descent.core.domX.ASTNode;
import descent.internal.core.dom.Expression;
import dtool.dom.ext.ASTNeoVisitor;

/**
 * A qualified entity/name reference
 */
public abstract class EntityId extends ASTElement {

	public boolean moduleRoot; 
	public SingleEntityRef[] ents; 
	
	public EReferenceConstraint refConstraint = null;
	
	public static enum EReferenceConstraint {	
		none,
		type,
		expvalue
	}

	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, ents);
		}
		visitor.endVisit(this);
	}
	
	public static class TypeEntityReference extends EntityId {
		public TypeEntityReference() { 
			super(); 
			refConstraint = EReferenceConstraint.expvalue;
		}
	}
	public static class ValueEntityReference extends EntityId {
		public ValueEntityReference() { 
			super(); 
			refConstraint = EReferenceConstraint.type;
		}
	}	
	public static class AnyEntityReference extends EntityId {
		public AnyEntityReference() { 
			super(); 
			refConstraint = EReferenceConstraint.none;
		}
	}

	
	
	public static class TypeDynArray extends EntityId {
		public EntityId elemtype;

		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				acceptChildren(visitor, elemtype);
			}
			visitor.endVisit(this);
		}

		public String toString() {
			return elemtype + "[]";
		}
	}

	public static class TypeofRef extends SingleEntityRef {
		public ASTNode expression;

		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				acceptChild(visitor, expression);
			}
			visitor.endVisit(this);
		}
		
		public String toString() {
			return "typeof(" + "???" +")";
		}
	}

	public static class TypePointer extends SingleEntityRef {
		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				// TODO: accept children
			}
			visitor.endVisit(this);
		}
	}

	public static class TypeStaticArray extends SingleEntityRef {
		// expression , const

		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				// TODO: accept children
			}
			visitor.endVisit(this);
		}
	}

}

