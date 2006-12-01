package dtool.dom;

import descent.core.domX.ASTNode;
import descent.internal.core.dom.Initializer;
import descent.internal.core.dom.Type;
import dtool.dom.ext.ASTNeoVisitor;


public class DefinitionVariable extends Definition {
	
	public Type type;
	public Initializer init;
	
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
