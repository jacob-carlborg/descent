package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.Type;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;

public class NamelessParameter extends ASTNeoNode implements IFunctionParameter {

	public Reference type;
	public descent.internal.compiler.parser.InOut inout;
	//public Expression defaultValue;

	protected NamelessParameter(descent.internal.compiler.parser.Argument elem) {
		convertNode(elem);
		this.type = Reference.convertType(elem.type);
		this.inout = elem.inout;
		//this.defaultValue = Expression.convert(elem.defaultValue);
			
	}
	
	public NamelessParameter(Type type) {
		convertNode(type);
		this.type = Reference.convertType(type);
	}


	public String toStringAsParameter() {
		return type.toString();
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			//TreeVisitor.acceptChildren(visitor, inout);
			//TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);	
	}

}
