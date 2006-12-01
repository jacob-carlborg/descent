package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IArgument;
import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;

public class TypeFunction extends Type {
	
	public List<Argument> arguments;
	public int varargs;
	public LINK linkage;

	public TypeFunction(List<Argument> arguments, Type treturn, int varargs, LINK linkage) {
		super(TY.Tfunction, treturn);
		this.arguments = arguments;
		this.varargs = varargs;
		this.linkage = linkage;
	}
	
	public IType getReturnType() {
		return this.next;
	}
	
	public int getElementType() {
		// TODO
		return 0;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
	
	public IArgument[] getArguments() {
		if (arguments == null) return new IArgument[0];
		return arguments.toArray(new IArgument[arguments.size()]);
	}

}
