package dtool.dom.declarations;

import descent.internal.core.dom.Import;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.DefUnitSearch;

public class ImportContent extends ImportStatic {

	public ImportContent(Import elem) {
		super(elem);
	}
	
	@Override
	public void searchDefUnit(CommonDefUnitSearch options) {
		EntityResolver.findDefUnitInContentImport(this, options);
	}
}