package dtool.dom.base;

import util.tree.TreeVisitor;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

/**
 * A qualified entity/name reference
 */
public abstract class EntityConstrainedRef extends ASTNeoNode {

	public Entity entity;
	
	public EReferenceConstraint refConstraint = null;
	
	public static enum EReferenceConstraint {	
		NONE,
		TYPE,
		EXPVALUE,
	}
	
	public EntityConstrainedRef(Entity entity) {
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
	
	public static class TypeConstraint extends EntityConstrainedRef {
		public TypeConstraint(Entity entity) { 
			super(entity); 
			refConstraint = EReferenceConstraint.EXPVALUE;
		}


	}
	public static class ValueConstraint extends EntityConstrainedRef {
		public ValueConstraint(Entity entity) { 
			super(entity); 
			refConstraint = EReferenceConstraint.TYPE;
		}
	}	
	public static class NoConstraint extends EntityConstrainedRef {
		public NoConstraint(Entity entity) { 
			super(entity); 
			refConstraint = EReferenceConstraint.NONE;
		}
	}
	
	public static class TypeEntity extends ASTNeoNode {
		Entity ent;
		
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, ent);
			}
			visitor.endVisit(this);
		}
	}

}
