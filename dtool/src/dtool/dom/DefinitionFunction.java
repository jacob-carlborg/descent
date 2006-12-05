package dtool.dom;

import descent.internal.core.dom.Statement;
import descent.internal.core.dom.TemplateParameter;
import descent.internal.core.dom.TypeFunction;
import dtool.dom.ext.ASTNeoVisitor;

/**
 * A definition of a function.
 */

public class DefinitionFunction extends Definition {

	//public Identifier outId;
	public TemplateParameter[] templateParameters;	
	public TypeFunction type;
	public Statement frequire;
	public Statement fbody;
	public Statement fensure;

	@Override
	public ArcheType getArcheType() {
		return ArcheType.Function;
	}
	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, templateParameters);
			acceptChild(visitor, type);
			acceptChild(visitor, frequire);
			acceptChild(visitor, fbody);
			acceptChild(visitor, fensure);
		}
		visitor.endVisit(this);
	}
}
