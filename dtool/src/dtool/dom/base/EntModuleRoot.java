package dtool.dom.base;

import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;

public class EntModuleRoot extends Entity {
	//public EntitySingle baseent;
	
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);

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
