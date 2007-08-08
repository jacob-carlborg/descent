package dtool.dom.declarations;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.IteratorUtil;
import dtool.dom.ast.ASTNode;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.references.RefModule;
import dtool.refmodel.IScopeNode;


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
