package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateTupleParameter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.refmodel.IScopeNode;

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
	public IScopeNode getMembersScope() {
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
