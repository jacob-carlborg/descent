package dtool.descentadapter;

import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.dom.ast.ASTNeoUpTreeVisitor;
import dtool.dom.declarations.Declaration;
import dtool.dom.declarations.DeclarationImport;

public final class PostConvertionAdapter extends ASTNeoUpTreeVisitor {

	private ASTNode parent = null;
		
	@Override
	public void preVisit(ASTNode elem) {
		elem.setParent(parent); // Set parent to current parent
		parent = elem; // Set as new parent
	}
	
	@Override
	public void postVisit(ASTNode elem) {
		parent = elem.getParent(); // Restore previous parent
	}
	
	//@Override
	public boolean visit(DeclarationImport node) {
		node.isTransitive = hasEffectiveProtection(node, TOK.TOKpublic);
		return true;
	}
	
	public static boolean hasEffectiveProtection(ASTNode elem, TOK tok) {
		return Declaration.hasInheritedProtection(elem, tok);
	}
	
}
