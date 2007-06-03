package dtool.dom.definitions;

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
	
	public Parameter() {
	}
	
	public Parameter(descent.internal.core.dom.Argument elem) {
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
