package dtool.ast.definitions;

import descent.internal.compiler.parser.InterfaceDeclaration;
import dtool.ast.IASTNeoVisitor;

/**
 * A definition of an interface aggregate. 
 */
public class DefinitionInterface extends DefinitionClass {

	
	@SuppressWarnings("unchecked")
	public DefinitionInterface(InterfaceDeclaration elem) {
		super(elem);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		acceptNodeChildren(visitor, children);
		visitor.endVisit(this);
	}

}
