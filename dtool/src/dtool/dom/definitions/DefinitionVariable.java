package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Initializer;
import dtool.dom.references.Reference;
import dtool.dom.statements.IStatement;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;

/**
 * A definition of a variable
 */
public class DefinitionVariable extends Definition implements IStatement {
	
	public Reference type;
	public Initializer init;

	public DefinitionVariable(descent.internal.compiler.parser.VarDeclaration elem) {
		convertDsymbol(elem);
		this.type = Reference.convertType(elem.type);
		this.init = Initializer.convert(elem.init);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	@Override
	public String toStringFullSignature() {
		String str = getArcheType().toString() + "  "
			+ type.toString() + " " + getName();
		return str;
	}
	
	@Override
	public String toStringAsCodeCompletion() {
		return defname + "   " + type.toString() + " - "
				+ NodeUtil.getOuterDefUnit(this);
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
