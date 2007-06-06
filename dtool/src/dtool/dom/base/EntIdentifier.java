package dtool.dom.base;

import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.model.BindingResolver;
import dtool.model.IScope;

public class EntIdentifier extends EntitySingle {

	public EntIdentifier() { super(); }

	public EntIdentifier(String name) {
		this.name = name;
	}
	
	public EntIdentifier(descent.internal.core.dom.Identifier elem) {
		setSourceRange(elem);
		this.name = elem.string;
	}

	public EntIdentifier(descent.internal.core.dom.TypeBasic elem) {
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
	
	protected IScope getFirstScope() {
		ASTNode elem = this;
		while((elem instanceof IScope) == false)
			elem = elem.getParent();
		return ((IScope)elem);
	}

	
	@Override
	public DefUnit getTargetDefUnit() {
		IScope scope = getFirstScope();
		return BindingResolver.getDefUnit(scope, name);
	}
}

