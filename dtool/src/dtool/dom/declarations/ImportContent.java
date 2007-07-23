package dtool.dom.declarations;

import descent.internal.core.dom.Import;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.EntitySearch;

public class ImportContent extends ImportStatic {

	public ImportContent(Import elem) {
		super(elem);
	}
	
	@Override
	public void searchDefUnit(EntitySearch options) {
		EntityResolver.findDefUnitInContentImport(this, options);
	}
}