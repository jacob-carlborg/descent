package dtool.dom;

import dtool.dom.ext.ASTNeoVisitor;

/**
 * Names a Symbol/Entity, but is not part of a definition.
 * An Identifier (or TemplateInstance) in the DMD AST 
 */
public class SymbolReference extends ASTElement {
	public String name;

	public SymbolReference() {
	}
	
	public SymbolReference(String name) {
		this.name = name;
	}

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			// TODO: accept children
		}
		visitor.endVisit(this);
	}
	
	public static class SymbolReference_Name extends SymbolReference {
		
	}

	public static class SymbolReference_TemplateInstance extends SymbolReference {
		
	}
}

