package dtool.dom.declarations;

import descent.internal.compiler.parser.Import;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.ReferenceResolver;
import dtool.refmodel.DefUnitSearch;

public class ImportContent extends ImportStatic {

	public ImportContent(Import elem) {
		super(elem);
	}
	
	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch options) {
		ReferenceResolver.findDefUnitInContentImport(this, options);
	}
}