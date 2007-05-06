package dtool.dom.declarations;

import util.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntityConstrainedRef;
import dtool.dom.expressions.Initializer;
import dtool.model.IScope;

/**
 * A definition of a variable
 */
public class DefinitionVariable extends Definition {
	
	public EntityConstrainedRef.TypeConstraint type;
	public Initializer init;

	public DefinitionVariable(descent.internal.core.dom.VarDeclaration elem) {
		super(elem);
		this.type = Entity.convertType(elem.type);
		this.init = Initializer.convert(elem.init);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, type);
			TreeVisitor.acceptChild(visitor, defname);
			TreeVisitor.acceptChild(visitor, init);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScope getScope() {
		return type.entity.getTargetDefUnit().getScope();
	}

}