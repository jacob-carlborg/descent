package dtool.refmodel;

import dtool.dom.ast.ASTNode;
import dtool.dom.definitions.Module;

public class NodeUtil {

	public static Module getModule(ASTNode elem) {
		// Search for module elem
		while((elem instanceof Module) == false) {
			if(elem == null)
				return null;
			elem = elem.getParent();
		}
		
		return ((Module)elem);
	}

}
