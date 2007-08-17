package dtool.dom.definitions;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;
import dtool.dom.references.Reference;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;

public class FunctionParameter extends DefUnit implements IFunctionParameter {
	

	public Reference type;
	public descent.internal.compiler.parser.InOut inout;
	public Expression defaultValue;
	
	protected FunctionParameter(descent.internal.compiler.parser.Argument elem) {
		super(elem.ident);
		setSourceRange(elem);
		
		
		if(elem.type instanceof TypeBasic && ((TypeBasic)elem.type).ty.name == null)
			this.type = null;
		else 
			this.type = Reference.convertType(elem.type);
		this.inout = elem.inout;
		this.defaultValue = Expression.convert(elem.defaultArg);
			
	}
	
	public FunctionParameter(Type type, IdentifierExp id) {
		super(id);
		setSourceRange(type.getStartPos(), id.getEndPos() - type.getStartPos());
		
		this.type = Reference.convertType(type);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			//TreeVisitor.acceptChildren(visitor, inout);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, defaultValue);
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
	
	public String toStringAsParameter() {
		return determineType() + " " + defname;
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
