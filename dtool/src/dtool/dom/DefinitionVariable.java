package dtool.dom;

import dtool.dombase.ASTNeoVisitor;
import dtool.dombase.IScope;

/**
 * A definition of a variable
 */
public class DefinitionVariable extends Definition {
	
	public Entity type;
	public descent.internal.core.dom.Initializer init;
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
			acceptChild(visitor, init);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScope getScope() {
		return type.getReferencedDefUnit().getScope();
	}

}
