package dtool.dom.base;

import util.ExceptionAdapter;
import dtool.dom.ast.ASTNeoVisitor;
import dtool.dom.ast.IScope;
import dtool.dom.base.EntitySingle.Identifier;
import dtool.model.BindingResolver;
import dtool.model.ModelException;

/**
 * A qualified entity/name reference
 * XXX: Consider in the future to be an interface?
 */
public abstract class Entity extends ASTElement {

	//public EntitySingleRef[] ents; 
	
	public EReferenceConstraint refConstraint = null;
	
	public static enum EReferenceConstraint {	
		none,
		type,
		expvalue
	}
	
	public abstract DefUnit getReferencedDefUnit();

	
	public static class QualifiedEnt extends Entity {
		public Entity topent;
		public EntitySingle baseent;

		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				acceptChildren(visitor, topent);
				acceptChildren(visitor, baseent);
			}
			visitor.endVisit(this);
		}
		
		@Override
		public DefUnit getReferencedDefUnit() {
			IScope scope = topent.getReferencedDefUnit().getScope();
			Identifier id = (Identifier) baseent;
			try {
				BindingResolver.findDefUnit(scope.getDefUnits(), id.name );
			} catch (ModelException e) {
				throw new ExceptionAdapter(e);
			}

			return null;
		}
		
		public String toString() {
			return topent + "." + baseent;
		}

	}
	
	public static class ModuleRootEnt extends Entity {
		//public EntitySingle baseent;
		
		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
/*			if (children) {
				acceptChildren(visitor, baseent);
			}
*/			visitor.endVisit(this);

		}

		public String toString() {
			return "";
		}

		@Override
		public DefUnit getReferencedDefUnit() {
			ASTNode elem = this;
			// Search for module elem
			while((elem instanceof Module) == false)
				elem = elem.parent;
			return ((Module)elem);
		}
	}
}

