package dtool.dom.base;

import java.util.List;

import descent.internal.core.dom.Argument;
import descent.internal.core.dom.LINK;
import descent.internal.core.dom.Statement;
import descent.internal.core.dom.TemplateParameter;
import dtool.dom.ast.ASTNeoVisitor;
import dtool.model.IScope;

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
	public EntityConstrainedRef.TypeConstraint rettype;
	
	public Statement frequire;
	public Statement fbody;
	public Statement fensure;

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Function;
	}
	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			// TODO... ah shit, the symbol is in the middle of the type
			acceptChildren(visitor, rettype);
			acceptChildren(visitor, symbol);
			acceptChildren(visitor, templateParameters);
			acceptChildren(visitor, arguments);
			//acceptChild(visitor, type);
			acceptChild(visitor, frequire);
			acceptChild(visitor, fbody);
			acceptChild(visitor, fensure);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScope getScope() {
		// TODO Auto-generated method stub
		return null;
	}
}
