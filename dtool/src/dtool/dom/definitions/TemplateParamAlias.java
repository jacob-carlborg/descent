package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.TemplateAliasParameter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.refmodel.IScopeNode;

public class TemplateParamAlias extends TemplateParameter {

	public TemplateParamAlias(TemplateAliasParameter elem) {
		convertNode(elem);
		convertIdentifier(elem.tp_ident);
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
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
