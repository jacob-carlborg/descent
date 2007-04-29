package dtool.dom.declarations;

import util.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntityConstrainedRef;
import dtool.dom.expressions.Expression;
import dtool.model.IScope;

public class Parameter extends DefUnit {
	
	public EntityConstrainedRef.TypeConstraint type;
	public descent.internal.core.dom.InOut inout;
	public Expression defaultValue;
	
	public Parameter(descent.internal.core.dom.Argument argument) {
		super(argument.id);
		setSourceRange(argument);
		
		this.type = Entity.convertType(argument.type);
		this.inout = argument.inout;
		this.defaultValue = Expression.convert(argument.defaultValue);
			
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Parameter;
	}

	@Override
	public IScope getScope() {
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
