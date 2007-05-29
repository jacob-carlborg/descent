package dtool.dom.declarations;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TemplateValueParameter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntityConstrainedRef;
import dtool.dom.expressions.Expression;
import dtool.model.IScope;

public class TemplateParamValue extends DefUnit {

	public EntityConstrainedRef.TypeConstraint type;
	public Expression specvalue;
	public Expression defaultvalue;

	public TemplateParamValue(TemplateValueParameter elem) {
		convertNode(elem);
		convertIdentifier(elem.id);
		this.type = Entity.convertType(elem.tp_valtype);
		this.specvalue = Expression.convert(elem.tp_specvalue);
		this.defaultvalue = Expression.convert(elem.tp_defaultvalue);
	}

	@Override
	public EArcheType getArcheType() {
		// TODO Auto-generated method stub
		return null;
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
			TreeVisitor.acceptChild(visitor, defname);
			TreeVisitor.acceptChild(visitor, specvalue);
			TreeVisitor.acceptChild(visitor, defaultvalue);
		}
		visitor.endVisit(this);	
	}
	
}
