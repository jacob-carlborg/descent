package dtool.dom.definitions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TemplateTupleParameter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.model.IScope;

public class TemplateParamTuple extends TemplateParameter {

	
	public TemplateParamTuple(TemplateTupleParameter elem) {
		convertNode(elem);
		convertIdentifier(elem.ident);
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Tuple;
	}

	@Override
	public IScope getMembersScope() {
		// TODO return intrinsic universal
		return null;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, defname);
		}
		visitor.endVisit(this);
	}
}
