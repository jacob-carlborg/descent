package dtool.dom;

import descent.core.domX.ASTNode;
import descent.internal.core.dom.Expression;
import dtool.dom.ext.ASTNeoVisitor;

/**
 * A qualified entity/name reference
 */
public abstract class EntityReference extends ASTElement {

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
	
	public static class TypeEntityReference extends EntityReference {
		public TypeEntityReference() { 
			super(); 
			refConstraint = EReferenceConstraint.expvalue;
		}
	}
	public static class ValueEntityReference extends EntityReference {
		public ValueEntityReference() { 
			super(); 
			refConstraint = EReferenceConstraint.type;
		}
	}	
	public static class AnyEntityReference extends EntityReference {
		public AnyEntityReference() { 
			super(); 
			refConstraint = EReferenceConstraint.none;
		}
	}

}
