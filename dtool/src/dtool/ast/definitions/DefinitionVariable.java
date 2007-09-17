package dtool.ast.definitions;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Initializer;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;

/**
 * A definition of a variable
 */
public class DefinitionVariable extends Definition implements IStatement {
	
	public Reference type;
	public Initializer init;

	public DefinitionVariable(descent.internal.compiler.parser.VarDeclaration elem) {
		super(elem);
		this.type = ReferenceConverter.convertType(elem.type);
		this.init = Initializer.convert(elem.init);
	}
	
	@Override
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
	public IScopeNode getMembersScope() {
		Collection<DefUnit> defunits = determineType().findTargetDefUnits(true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next().getMembersScope();
		//return defunit.getMembersScope();
	}
	
	private String getTypeString() {
		if(type != null)
			return type.toStringAsElement();
		return "auto";
	}
	
	@Override
	public String toStringForHoverSignature() {
		String str = getArcheType().toString() + "  "
			+ getTypeString() + " " + getName();
		return str;
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return defname.toStringAsElement() + "   " + getTypeString() + " - "
				+ getModuleScope().toStringAsElement();
	}

}
