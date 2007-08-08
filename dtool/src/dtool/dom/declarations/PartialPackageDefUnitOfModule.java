package dtool.dom.declarations;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.IteratorUtil;

import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.references.RefModule;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class PartialPackageDefUnitOfModule extends PartialPackageDefUnit {

	RefModule entModule;
	DefUnit module;
	
	public Iterator<? extends ASTNode> getMembersIterator() {
		if(module != null)
			return Collections.singleton(module).iterator();
		else {
			// Could we cache this result?
			Module targetModule = (Module) entModule.findTargetDefUnit();
			if(targetModule != null)
				return Collections.singleton(targetModule).iterator();
			return IteratorUtil.getEMPTY_ITERATOR();
		}
	}
	
	@Override
	public String toString() {
		if(module != null)
			return getName() + "." + module.toString();
		else {
			return getName() + "." + entModule.moduleName;
		}
	}


}
