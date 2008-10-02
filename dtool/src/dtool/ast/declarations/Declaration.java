package dtool.ast.declarations;

import java.util.Iterator;
import java.util.List;

import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.TOK;
import dtool.ast.ASTNeoNode;
import dtool.descentadapter.DescentASTConverter;
import dtool.refmodel.IScope;

public abstract class Declaration {


	public static ASTNeoNode[] convertMany(descent.internal.compiler.parser.Declaration[] declarationDefinitions) {
		ASTNeoNode[] decls = new ASTNeoNode[declarationDefinitions.length];
		for(int i = 0; i < declarationDefinitions.length;i++) {
			decls[i] = convert(declarationDefinitions[i]);
		}
		return decls;
	}

	public static ASTNeoNode[] convertMany(List<Dsymbol> declarationDefinitions) {
		ASTNeoNode[] decls = new ASTNeoNode[declarationDefinitions.size()];
		for(int i = 0; i < declarationDefinitions.size();i++) {
			decls[i] = convert(declarationDefinitions.get(i));
		}
		return decls;
	}

	public static ASTNeoNode convert(descent.internal.compiler.parser.Declaration decl) {
		return (ASTNeoNode) DescentASTConverter.convertElem(decl);
	}

	public static ASTNeoNode convert(Dsymbol decl) {
		return (ASTNeoNode) DescentASTConverter.convertElem(decl);
	}

	public static int hasModifier(List<Modifier> modifiers, TOK tok) {
		int i = 0;
		if(modifiers == null)
			return -1;
		for (Iterator<Modifier> iter = modifiers.iterator(); iter.hasNext(); i++) {
			Modifier modifier = iter.next();
			if(modifier.tok == tok)
				return i;
		}
		return -1;
	}

	public static boolean hasInheritedProtection(ASTNeoNode elem, TOK tok) {
		while(elem != null && !(elem instanceof IScope)) {
			elem = elem.getParent();
			if(elem instanceof DeclarationProtection) {
				DeclarationProtection pdecl = (DeclarationProtection) elem;
				if(pdecl.modifier.tok == tok)
					return true;
				else
					return false;
			}
		} 
		return false;
	}
}
