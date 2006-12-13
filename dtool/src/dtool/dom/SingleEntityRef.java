package dtool.dom;

import descent.core.domX.ASTNode;
import dtool.dom.ext.ASTNeoVisitor;

/**
 * Names a Symbol/Entity, but is not part of a definition.
 * An Identifier (or TemplateInstance) in the DMD AST 
 */
public abstract class SingleEntityRef extends ASTElement {
	
	public static class Identifier extends SingleEntityRef {
		public String name;

		public Identifier() { super(); }

		public Identifier(String name) {
			this.name = name;
		}
		
		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			visitor.endVisit(this);
		}
		
		public String toString() {
			return name;
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
	
	public static class TemplateInstance extends SingleEntityRef {
		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				// TODO: accept children
			}
			visitor.endVisit(this);
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

	public static class TypeDynArray extends SingleEntityRef {
		public SingleEntityRef elemtype;

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
}
