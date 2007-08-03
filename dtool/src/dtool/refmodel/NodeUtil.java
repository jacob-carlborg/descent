package dtool.refmodel;

import melnorme.miscutil.tree.IElement;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.DefinitionAggregate.BaseClass;

public class NodeUtil {

	/** Gets the module of the given ASTNode. */
	public static Module getParentModule(ASTNode elem) {
		// Search for module elem
		while((elem instanceof Module) == false) {
			if(elem == null)
				return null;
			elem = elem.getParent();
		}
		
		return ((Module)elem);
	}

	/** Finds the first outer scope of the given element, 
	 * navegating through the element's parents. */
	public static IScopeNode getOuterScope(IElement startElem) {
			IElement elem = startElem.getParent();
	
			while(elem != null) {
				if (elem instanceof IScopeNode)
					return (IScopeNode) elem;
				
				if (elem instanceof BaseClass) {
					// Skip aggregate defunit scope (this is important) 
					elem = elem.getParent().getParent();
					continue;
				}
				
				elem = elem.getParent();
			}
			return null;
		}

	public static DefUnit getOuterDefUnit(ASTNeoNode node) {
		IElement elem = node.getParent();
		while(elem != null) {
			if (elem instanceof DefUnit)
				return (DefUnit) elem;
			elem = elem.getParent();
		}
		return null;
	}

}
