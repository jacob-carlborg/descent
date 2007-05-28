package dtool.dom.base;

import java.util.List;

import util.tree.TreeVisitor;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit;
import dtool.model.BindingResolver;
import dtool.model.IScope;

public abstract class EntitySingle extends Entity {
	public String name;
	
	public static class Identifier extends EntitySingle {

		public Identifier() { super(); }

		public Identifier(String name) {
			this.name = name;
		}
		
		public Identifier(descent.internal.core.dom.Identifier elem) {
			setSourceRange(elem);
			this.name = elem.string;
		}

		public Identifier(descent.internal.core.dom.TypeBasic elem) {
			setSourceRange(elem);
			this.name = elem.toString();
		}

		public void accept0(IASTNeoVisitor visitor) {
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
		
		public List<ASTNeoNode> tiargs;
		
		public TemplateInstance(descent.internal.core.dom.TemplateInstance elem) {
			setSourceRange(elem);
			this.name = elem.string;
			this.tiargs = DescentASTConverter.convertMany(elem.tiargs, tiargs);
		}

		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, tiargs);
			}
			visitor.endVisit(this);
		}

		@Override
		public DefUnit getTargetDefUnit() {
			// TODO CHANGE
			return null;
		}
	}

	public static EntitySingle convert(descent.internal.core.dom.Identifier elem) {
		return (EntitySingle) DescentASTConverter.convertElem(elem);
	}
}
