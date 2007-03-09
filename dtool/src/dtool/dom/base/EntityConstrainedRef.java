package dtool.dom.base;

import util.tree.TreeVisitor;
import dtool.dom.ast.ASTNeoVisitor;

/**
 * A qualified entity/name reference
 */
public abstract class EntityConstrainedRef extends ASTNeoNode {

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
			TreeVisitor.acceptChildren(visitor, ent);
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
	
	public static class TypeEntity extends ASTNeoNode {
		Entity ent;
		
		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, ent);
			}
			visitor.endVisit(this);
		}
	}

}
