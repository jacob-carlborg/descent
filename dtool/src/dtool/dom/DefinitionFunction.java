package dtool.dom;

import java.util.List;

import descent.internal.core.dom.Argument;
import descent.internal.core.dom.LINK;
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
	//public TypeFunction type;
	public List<Argument> arguments;
	public int varargs;
	public LINK linkage;
	public EntityReference.TypeEntityReference rettype;
	
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
			//acceptChild(visitor, type);
			acceptChild(visitor, frequire);
			acceptChild(visitor, fbody);
			acceptChild(visitor, fensure);
		}
		visitor.endVisit(this);
	}
}
