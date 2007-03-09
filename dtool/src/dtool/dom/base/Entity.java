package dtool.dom.base;

import util.tree.TreeVisitor;
import dtool.dom.ast.ASTNeoVisitor;
import dtool.dom.base.EntitySingle.Identifier;
import dtool.model.BindingResolver;
import dtool.model.IScope;

/**
 * A qualified entity/name reference
 * XXX: Consider in the future to be an interface?
 */
public abstract class Entity extends ASTNeoNode {

	//public EntitySingleRef[] ents; 
	
	public EReferenceConstraint refConstraint = null;
	
	public static enum EReferenceConstraint {	
		none,
		type,
		expvalue
	}
	
	public abstract DefUnit getTargetDefUnit();

	
	public static class QualifiedEnt extends Entity {
		public Entity topent;
		public EntitySingle baseent;

		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, topent);
				TreeVisitor.acceptChildren(visitor, baseent);
			}
			visitor.endVisit(this);
		}
		
		@Override
		public DefUnit getTargetDefUnit() {
			IScope scope = topent.getTargetDefUnit().getScope();
			Identifier id = (Identifier) baseent;
			BindingResolver.getDefUnit(scope.getDefUnits(), id.name );

			return null;
		}
		
		public String toString() {
			return topent + "." + baseent;
		}

	}
	
	public static class ModuleRootEnt extends Entity {
		//public EntitySingle baseent;
		
		public void accept0(ASTNeoVisitor visitor) {
			visitor.visit(this);
/*			if (children) {
				acceptChildren(visitor, baseent);
			}
*/			visitor.endVisit(this);

		}

		public String toString() {
			return "";
		}

		@Override
		public DefUnit getTargetDefUnit() {
			ASTNode elem = this;
			// Search for module elem
			while((elem instanceof Module) == false)
				elem = elem.getParent();
			return ((Module)elem);
		}
	}
}

