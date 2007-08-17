package dtool.dom.definitions;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Initializer;
import dtool.dom.references.Reference;
import dtool.dom.statements.IStatement;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;

/**
 * A definition of a variable
 */
public class DefinitionVariable extends Definition implements IStatement {
	
	public Reference type;
	public Initializer init;

	public DefinitionVariable(descent.internal.compiler.parser.VarDeclaration elem) {
		super(elem);
		this.type = Reference.convertType(elem.type);
		this.init = Initializer.convert(elem.init);
	}
	
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, init);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}

	private IDefUnitReference determineType() {
		if(type != null)
			return type;
		return NativeDefUnit.nullReference;
	}
	
	@Override
	public String toStringFullSignature() {
		String str = getArcheType().toString() + "  "
			+ determineType().toString() + " " + getName();
		return str;
	}
	
	@Override
	public String toStringAsCodeCompletion() {
		return defname + "   " + determineType().toString() + " - "
				+ NodeUtil.getOuterDefUnit(this);
	}


	@Override
	public IScopeNode getMembersScope() {
		Collection<DefUnit> defunits = determineType().findTargetDefUnits(true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next().getMembersScope();
		//return defunit.getMembersScope();
	}

}
