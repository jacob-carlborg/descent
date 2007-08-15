package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Type;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;
import dtool.dom.references.Reference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;

public class FunctionParameter extends DefUnit implements IFunctionParameter {
	

	public Reference type;
	public descent.internal.compiler.parser.InOut inout;
	public Expression defaultValue;
	
	public FunctionParameter() {
	}
	
	protected FunctionParameter(descent.internal.compiler.parser.Argument elem) {
		convertNode(elem);
		convertIdentifier(elem.ident);
		setSourceRange(elem);
		
		this.type = Reference.convertType(elem.type);
		this.inout = elem.inout;
		this.defaultValue = Expression.convert(elem.defaultArg);
			
	}
	
	public FunctionParameter(Type type, IdentifierExp id) {
		convertIdentifier(id);
		setSourceRange(type.getStartPos(), id.getEndPos() - type.getStartPos());
		
		this.type = Reference.convertType(type);
	}

	public String toStringAsParameter() {
		return type + " " + defname;
	}
	
	@Override
	public String toStringFullSignature() {
		String str = getArcheType().toString() + "  "
			+ type.toString() + " " + getName();
		return str;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Parameter;
	}
	
	@Override
	public String toStringAsCodeCompletion() {
		return defname + "   " + type.toString() + " - "
				+ NodeUtil.getOuterDefUnit(this);
	}
		
	
	@Override
	public IScopeNode getMembersScope() {
		return type.getTargetScope();
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, type);
			//TreeVisitor.acceptChildren(visitor, inout);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);	
	}

}
