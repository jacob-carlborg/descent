package dtool.dom.definitions;

import util.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;
import dtool.dom.references.Entity;
import dtool.refmodel.IScopeNode;

public class FunctionParameter extends DefUnit {
	
	public Entity type;
	public descent.internal.core.dom.InOut inout;
	public Expression defaultValue;
	
	public FunctionParameter() {
	}
	
	public FunctionParameter(descent.internal.core.dom.Argument elem) {
		super();
		setSourceRange(elem);
		convertIdentifier(elem.id);
		setSourceRange(elem);
		
		this.type = Entity.convertType(elem.type);
		this.inout = elem.inout;
		this.defaultValue = Expression.convert(elem.defaultValue);
			
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Parameter;
	}

	@Override
	public IScopeNode getMembersScope() {
		// TODO Auto-generated method stub
		return null;
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