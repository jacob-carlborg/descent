package dtool.dom.declarations;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TypedefDeclaration;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntityConstrainedRef;
import dtool.dom.expressions.Initializer;
import dtool.model.IScope;

public class DefinitionTypedef extends Definition {

	EntityConstrainedRef.TypeConstraint type;
	Initializer initializer;
	
	public DefinitionTypedef(TypedefDeclaration elem) {
		super(elem);
		this.type = Entity.convertType(elem.type);
		this.initializer = Initializer.convert(elem.init);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, type);
			TreeVisitor.acceptChildren(visitor, initializer);
		}
		visitor.endVisit(this);
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Enum;
	}

	@Override
	public IScope getScope() {
		// TODO Auto-generated method stub
		return null;
	}

}
