package dtool.dom.declarations;

import java.util.List;

import descent.core.dom.IDeclaration;
import descent.internal.core.dom.Dsymbol;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNode;

public abstract class Declaration {


	public static ASTNode[] convertMany(IDeclaration[] declarationDefinitions) {
		ASTNode[] decls = new ASTNode[declarationDefinitions.length];
		for(int i = 0; i < declarationDefinitions.length;i++) {
			decls[i] = convert(declarationDefinitions[i]);
		}
		return decls;
	}

	public static ASTNode[] convertMany(List<IDeclaration> declarationDefinitions) {
		ASTNode[] decls = new ASTNode[declarationDefinitions.size()];
		for(int i = 0; i < declarationDefinitions.size();i++) {
			decls[i] = convert(declarationDefinitions.get(i));
		}
		return decls;
	}

	public static ASTNode convert(IDeclaration decl) {
		return (ASTNode) DescentASTConverter.convertElem((ASTNode) decl);
	}

	public static ASTNode convert(Dsymbol decl) {
		return (ASTNode) DescentASTConverter.convertElem((ASTNode) decl);
	}
}
