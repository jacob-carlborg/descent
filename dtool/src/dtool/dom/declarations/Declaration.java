package dtool.dom.declarations;

import java.util.List;

import descent.core.dom.IDeclaration;
import descent.internal.core.dom.Dsymbol;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;

public abstract class Declaration extends ASTNeoNode {


	public static Declaration[] convertMany(IDeclaration[] declarationDefinitions) {
		Declaration[] decls = new Declaration[declarationDefinitions.length];
		for(int i = 0; i < declarationDefinitions.length;i++) {
			decls[i] = convert(declarationDefinitions[i]);
		}
		return decls;
	}

	public static Declaration[] convertMany(List<IDeclaration> declarationDefinitions) {
		Declaration[] decls = new Declaration[declarationDefinitions.size()];
		for(int i = 0; i < declarationDefinitions.size();i++) {
			decls[i] = convert(declarationDefinitions.get(i));
		}
		return decls;
	}

	public static Declaration convert(IDeclaration decl) {
		return (Declaration) DescentASTConverter.convertElem((ASTNode) decl);
	}

	public static Declaration convert(Dsymbol decl) {
		return (Declaration) DescentASTConverter.convertElem((ASTNode) decl);
	}
}
