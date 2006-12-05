package dtool.dom;

import dtool.dom.ext.ASTNeoVisitor;

/** 
 * A D code entity. Can be a type, var, template, etc.
 * TODO
 */
public abstract class Entity extends ASTElement {

//	 XXX: ? Contain a root, or Subclass root?
	public static class BasicEntity extends Entity {
		public RefRoot root; 
		public SymbolReference[] ents; 

		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				acceptChild(visitor, root);
				acceptChildren(visitor, ents);
			}
			visitor.endVisit(this);
		}
	}

	public static abstract class RefRoot extends ASTElement {
		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				// TODO: accept children
			}
			visitor.endVisit(this);
		}
	}
	
	
	public static class Root_Module extends RefRoot {
	}

	public static class Root_PrimitiveType extends RefRoot {
	}

	public static class Root_Typeof extends RefRoot {
	}
	
	public static abstract class CompositeEntity extends Entity {
	}

	public static abstract class TypePointer extends CompositeEntity {
	}

	public static abstract class TypeStaticArray extends CompositeEntity {
		// expression , const
	}

	
}


