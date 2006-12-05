package descent.internal.core.dom;

import java.util.Collections;
import java.util.List;

import descent.core.dom.ASTVisitor;

public class TypeFunction extends Type {
	
	public List<Argument> arguments;
	public boolean varargs;
	public LINK linkage;

	public TypeFunction(List<Argument> arguments, Type treturn, boolean varargs, LINK linkage) {
		super(TY.Tfunction, treturn);
		this.arguments = arguments;
		this.varargs = varargs;
		this.linkage = linkage;
	}
	
	public Type getReturnType() {
		return this.next;
	}
	
	public int getNodeType0() {
		// TODO
		return 0;
	}
	
	@Override
	void accept0(ASTVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
	
	public List<Argument> getArguments() {
		if (arguments == null) return Collections.EMPTY_LIST;
		return arguments;
	}

}
