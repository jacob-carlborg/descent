package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateTypeParameter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;
import dtool.refmodel.IScopeNode;

public class TemplateParamType extends TemplateParameter {

	public Reference specType;
	public Reference defaultType;

	public TemplateParamType(TemplateTypeParameter elem) {
		super(elem.ident);
		convertNode(elem);
		this.specType = Reference.convertType(elem.specType);
		this.defaultType = Reference.convertType(elem.defaultType);
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
	}

	/*
	 * Can be null
	 */
	@Override
	public IScopeNode getMembersScope() {
		if(specType == null)
			return null;
		return specType.getTargetScope();
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, specType);
			TreeVisitor.acceptChildren(visitor, defaultType);
		}
		visitor.endVisit(this);
	}


}
