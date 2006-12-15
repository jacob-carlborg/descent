package dtool.dom;

import util.ExceptionAdapter;
import descent.core.domX.ASTNode;
import dtool.dombase.ASTNeoVisitor;
import dtool.dombase.IScope;
import dtool.model.BindingResolver;
import dtool.model.ModelException;

public abstract class EntitySingle extends Entity {
	public String name;
	
	public static class Identifier extends EntitySingle {

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
		
		public IScope getParentScope() {
			ASTNode elem = this;
			// Search for module elem
			while((elem instanceof IScope) == false)
				elem = elem.parent;
			return ((IScope)elem);
		}

		
		@Override
		public DefUnit getReferencedDefUnit() {
			IScope scope = getParentScope();
			try {
				BindingResolver.findDefUnit(scope.getDefUnits(), name);
			} catch (ModelException e) {
				throw new ExceptionAdapter(e);
			}
			return null;
		}
	}

	public static class TemplateInstance extends EntitySingle {
		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				// TODO: accept children
			}
			visitor.endVisit(this);
		}

		@Override
		public DefUnit getReferencedDefUnit() {
			// TODO CHANGE
			return null;
		}
	}
}
