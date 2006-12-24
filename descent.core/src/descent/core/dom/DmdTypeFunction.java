package descent.core.dom;

import java.util.Collections;
import java.util.List;

import descent.internal.core.parser.LINK;
import descent.internal.core.parser.TY;

/**
 * Internal class that is a TypeFunction
 * @author Ary
 *
 */
class DmdTypeFunction extends DmdType {
	
	public List<Argument> arguments;
	public boolean varargs;
	public LINK linkage;

	public DmdTypeFunction(AST ast, List<Argument> arguments, Type treturn, boolean varargs, LINK linkage) {
		super(ast, TY.Tfunction, treturn);
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
