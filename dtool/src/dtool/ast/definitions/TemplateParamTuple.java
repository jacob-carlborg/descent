package dtool.ast.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateTupleParameter;
import dtool.ast.IASTNeoVisitor;
import dtool.refmodel.IScopeNode;

public class TemplateParamTuple extends TemplateParameter {

	
	public TemplateParamTuple(TemplateTupleParameter elem) {
		super(elem.ident);
		convertNode(elem);
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
			TreeVisitor.acceptChildren(visitor, defname);
		}
		visitor.endVisit(this);
	}
}