package dtool.dom;

import dtool.dombase.ASTNeoVisitor;

/**
 * A qualified entity/name reference
 */
public abstract class EntityConstrainedRef extends ASTElement {

	Entity ent;
	
	public EReferenceConstraint refConstraint = null;
	
	public static enum EReferenceConstraint {	
		none,
		type,
		expvalue
	}

	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, ent);
		}
		visitor.endVisit(this);
	}
	
	public static class TypeConstraint extends EntityConstrainedRef {
		public TypeConstraint() { 
			super(); 
			refConstraint = EReferenceConstraint.expvalue;
		}
	}
	public static class ValueConstraint extends EntityConstrainedRef {
		public ValueConstraint() { 
			super(); 
			refConstraint = EReferenceConstraint.type;
		}
	}	
	public static class NoConstraint extends EntityConstrainedRef {
		public NoConstraint() { 
			super(); 
			refConstraint = EReferenceConstraint.none;
		}
	}
	
	public static class TypeEntity extends ASTElement {
		Entity ent;
		
		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				acceptChildren(visitor, ent);
			}
			visitor.endVisit(this);
		}
	}

}
