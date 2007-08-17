package dtool.dom.declarations;

import java.util.Collections;
import java.util.Iterator;

import descent.core.domX.ASTNode;
import dtool.dom.definitions.Symbol;


public class PartialPackageDefUnitOfPackage extends PartialPackageDefUnit  {
	
	PartialPackageDefUnit child;
	
	protected PartialPackageDefUnitOfPackage(Symbol defname) {
		super(defname);
	}
	
	public Iterator<? extends ASTNode> getMembersIterator() {
		return Collections.singleton(child).iterator();
	}
	
	@Override
	public String toString() {
		return getName() + "." + child.toString();
	}

}
