package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDelegateType;
import descent.core.dom.IPointerType;
import descent.core.dom.IType;

public class DmdTypePointer extends DmdType implements IPointerType, IDelegateType {
	
	public DmdTypePointer(AST ast, Type t) {
		super(ast, TY.Tpointer, t);
	}
	
	public int getNodeType0() {
		return next instanceof DmdTypeFunction ? POINTER_TO_FUNCTION_TYPE : POINTER_TYPE;
	}
	
	public IType getComponentType() {
		return (IType) next;
	}
	
	public IType getReturnType() {
		return ((DmdTypeFunction) next).getReturnType();
	}
	
	public List<Argument> arguments() {
		return ((DmdTypeFunction) next).getArguments();
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
	}
	
	@Override
	public String toString() {
		return next.toString() + "*";
	}

}
