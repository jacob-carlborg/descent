package descent.core.dom;

import descent.core.WorkingCopyOwner;
import descent.core.dom.DefaultBindingResolver.BindingTables;

/**
 * Computes semantics for a CompilationUnit.
 */
public class SemanticComputer extends ASTVisitor {
	
	DefaultBindingResolver resolver;
	
	public SemanticComputer(WorkingCopyOwner owner, BindingTables bindingTables) {
		resolver = new DefaultBindingResolver(owner, bindingTables);
	}
	
	@Override
	public boolean visit(CompilationUnit node) {
		node.ast.setBindingResolver(resolver);		
		
		return false;
	}

}
