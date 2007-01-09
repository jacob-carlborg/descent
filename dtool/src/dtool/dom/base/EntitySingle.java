package dtool.dom.base;

import dtool.dom.ast.ASTNeoVisitor;
import dtool.model.BindingResolver;
import dtool.model.IScope;

public abstract class EntitySingle extends Entity {
	public String name;
	
	public static class Identifier extends EntitySingle {

		public Identifier() { super(); }

		public Identifier(String name) {
			this.name = name;
		}
		
		public void accept0(ASTNeoVisitor visitor) {
			visitor.visit(this);
			visitor.endVisit(this);
		}
		
		public String toString() {
			return name;
		}
		
		public IScope getParentScope() {
			ASTNode elem = this;
			// Search for module elem
			while((elem instanceof IScope) == false)
				elem = elem.getParent();
			return ((IScope)elem);
		}

		
		@Override
		public DefUnit getTargetDefUnit() {
			IScope scope = getParentScope();
			return BindingResolver.getDefUnit(scope.getDefUnits(), name);
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
		public DefUnit getTargetDefUnit() {
			// TODO CHANGE
			return null;
		}
	}
}
