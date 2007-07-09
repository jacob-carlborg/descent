package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Initializer;
import dtool.dom.references.Entity;
import dtool.dom.statements.IStatement;
import dtool.refmodel.IScopeNode;

/**
 * A definition of a variable
 */
public class DefinitionVariable extends Definition implements IStatement {
	
	public Entity type;
	public Initializer init;

	public DefinitionVariable(descent.internal.core.dom.VarDeclaration elem) {
		convertDsymbol(elem);
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
	public IScopeNode getMembersScope() {
		return type.getTargetScope();
	}

}
