package dtool.descentadapter;

import descent.internal.compiler.parser.TOK;
import dtool.dom.ast.ASTNodeParentizer;
import dtool.dom.declarations.Declaration;
import dtool.dom.declarations.DeclarationImport;

public final class PostConvertionAdapter extends ASTNodeParentizer {

		
	//@Override
	public boolean visit(DeclarationImport node) {
		node.isTransitive = Declaration.hasInheritedProtection(node, TOK.TOKpublic);
		return true;
	}
	
}
