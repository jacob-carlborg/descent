package dtool.dom.declarations;

import java.util.Collections;
import java.util.Iterator;

import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.Symbol;


public class PartialPackageDefUnitOfPackage extends PartialPackageDefUnit  {
	
	PartialPackageDefUnit child;
	
	protected PartialPackageDefUnitOfPackage(Symbol defname) {
		super(defname);
	}
	
	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		return Collections.singleton(child).iterator();
	}
	
	@Override
	public String toStringAsElement() {
		return getName() + "." + child.toStringAsElement();
	}

}
