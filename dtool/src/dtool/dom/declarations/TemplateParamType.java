package dtool.dom.declarations;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TemplateTypeParameter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntityConstrainedRef;
import dtool.model.IScope;

public class TemplateParamType extends DefUnit {


	public EntityConstrainedRef.TypeConstraint specType;
	public EntityConstrainedRef.TypeConstraint defaultType;

	public TemplateParamType(TemplateTypeParameter elem) {
		convertNode(elem);
		convertIdentifier(elem.ident);
		this.specType = Entity.convertType(elem.tp_spectype);
		this.defaultType = Entity.convertType(elem.tp_defaulttype);
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
			TreeVisitor.acceptChild(visitor, defname);
			TreeVisitor.acceptChild(visitor, specType);
			TreeVisitor.acceptChild(visitor, defaultType);
		}
		visitor.endVisit(this);
	}


}
