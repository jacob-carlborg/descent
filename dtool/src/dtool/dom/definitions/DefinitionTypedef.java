package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.TypedefDeclaration;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Initializer;
import dtool.dom.references.Reference;
import dtool.dom.statements.IStatement;
import dtool.refmodel.IScopeNode;

public class DefinitionTypedef extends Definition implements IStatement {

	Reference type;
	Initializer initializer;
	
	public DefinitionTypedef(TypedefDeclaration elem) {
		convertDsymbol(elem);
		this.type = Reference.convertType(elem.type);
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
		return EArcheType.Typedef;
	}
	
	@Override
	public String toStringAsCodeCompletion() {
		return defname + " -> " + type.toString() + " - " + getModuleScope();
	}

	@Override
	public IScopeNode getMembersScope() {
		return type.getTargetScope();
	}

}
