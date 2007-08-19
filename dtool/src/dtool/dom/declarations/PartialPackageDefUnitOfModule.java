package dtool.dom.declarations;

import java.util.Collections;
import java.util.Iterator;

import melnorme.miscutil.IteratorUtil;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.RefModule;

public class PartialPackageDefUnitOfModule extends PartialPackageDefUnit {

	RefModule moduleRef;
	DefUnit module;
	
	public PartialPackageDefUnitOfModule(Symbol name) {
		super(name);
	}

	public Iterator<? extends ASTNode> getMembersIterator() {
		if(module != null)
			return Collections.singleton(module).iterator();
		else {
			// Could we cache this result?
			Module targetModule = (Module) moduleRef.findTargetDefUnit();
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
			return getName() + "." + moduleRef.module;
		}
	}


}
