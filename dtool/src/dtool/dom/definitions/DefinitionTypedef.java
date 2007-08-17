package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TypedefDeclaration;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Initializer;
import dtool.dom.references.Reference;
import dtool.dom.statements.IStatement;
import dtool.refmodel.IScopeNode;

public class DefinitionTypedef extends Definition implements IStatement {

	Reference type;
	Initializer initializer;
	
	public DefinitionTypedef(TypedefDeclaration elem) {
		super(elem);
		this.type = Reference.convertType(elem.sourceBasetype);
		this.initializer = Initializer.convert(elem.init);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, type);
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
