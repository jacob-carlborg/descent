package dtool.dom;

import dtool.dom.ext.ASTNeoVisitor;

/**
 * A definition of a variable
 */
public class DefinitionVariable extends Definition {
	
	public EntityReference type;
	public descent.internal.core.dom.Initializer init;
	
	@Override
	public ArcheType getArcheType() {
		return ArcheType.Variable;
	}

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
			acceptChild(visitor, init);
		}
		visitor.endVisit(this);
	}

}
