package descent.core.dom;

import java.util.Collections;
import java.util.List;

import descent.internal.core.parser.TY;

/**
 * Internal class that is a TypeFunction.
 */
class DmdTypeFunction extends DmdType {
	
	public List<Argument> arguments;
	public boolean varargs;

	public DmdTypeFunction(AST ast, List<Argument> arguments, Type treturn, boolean varargs) {
		super(ast, TY.Tfunction, treturn);
		this.arguments = arguments;
		this.varargs = varargs;
	}
	
	public Type getReturnType() {
		return this.next;
	}
	
	public int getNodeType0() {
		return 0;
	}
	
	@Override
	void accept0(ASTVisitor visitor) {
		
	}
	
	public List<Argument> getArguments() {
		if (arguments == null) return Collections.EMPTY_LIST;
		return arguments;
	}

	@Override
	List internalStructuralPropertiesForType(int apiLevel) {
		return null;
	}

	@Override
	ASTNode clone0(AST target) {
		return null;
	}

	@Override
	int memSize() {
		return 0;
	}

	@Override
	boolean subtreeMatch0(ASTMatcher matcher, Object other) {
		return false;
	}

	@Override
	int treeSize() {
		return 0;
	}

}
