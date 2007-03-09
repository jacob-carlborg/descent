package dtool.dom.base;

import util.tree.TreeVisitor;
import dtool.dom.ast.ASTNeoVisitor;
import dtool.model.IScope;

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
			TreeVisitor.acceptChild(visitor, type);
			TreeVisitor.acceptChild(visitor, symbol);
			TreeVisitor.acceptChild(visitor, init);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScope getScope() {
		return type.getTargetDefUnit().getScope();
	}

}
