package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Entity;

public class NamelessParameter extends ASTNeoNode implements IFunctionParameter {

	public Entity type;
	public descent.internal.core.dom.InOut inout;
	//public Expression defaultValue;

	protected NamelessParameter(descent.internal.core.dom.Argument elem) {
		convertNode(elem);
		
		this.type = Entity.convertType(elem.type);
		this.inout = elem.inout;
		//this.defaultValue = Expression.convert(elem.defaultValue);
			
	}
	
	
	public String toStringAsParameter() {
		return type.toString();
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, type);
			//TreeVisitor.acceptChildren(visitor, inout);
			//TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);	
	}

}
