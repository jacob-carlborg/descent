package dtool.dom.definitions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TypedefDeclaration;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Initializer;
import dtool.dom.references.Entity;
import dtool.refmodel.IScope;

public class DefinitionTypedef extends Definition {

	Entity type;
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
	public IScope getMembersScope() {
		return type.getTargetScope();
	}

}
