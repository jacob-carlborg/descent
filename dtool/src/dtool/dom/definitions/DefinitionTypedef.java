package dtool.dom.definitions;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TypedefDeclaration;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.BaseEntityRef;
import dtool.dom.expressions.Initializer;
import dtool.model.IScope;

public class DefinitionTypedef extends Definition {

	BaseEntityRef.TypeConstraint type;
	Initializer initializer;
	
	public DefinitionTypedef(TypedefDeclaration elem) {
		convertDsymbol(elem);
		this.type = Entity.convertType(elem.type);
		this.initializer = Initializer.convert(elem.init);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, defname);
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
		return type.entity.getTargetDefUnit().getScope();
	}

	public List<DefUnit> getDefUnits() {
		return getScope().getDefUnits();
	}

}
