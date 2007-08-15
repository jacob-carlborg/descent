package dtool.dom.declarations;

import java.util.Collections;
import java.util.Iterator;

import descent.core.domX.ASTNode;


public class PartialPackageDefUnitOfPackage extends PartialPackageDefUnit  {
	
	PartialPackageDefUnit child;
	
	protected PartialPackageDefUnitOfPackage() {
	}
	
	public Iterator<? extends ASTNode> getMembersIterator() {
		return Collections.singleton(child).iterator();
	}
	
	@Override
	public String toString() {
		return getName() + "." + child.toString();
	}

}
