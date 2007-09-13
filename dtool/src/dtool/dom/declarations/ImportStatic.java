/**
 * 
 */
package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.Import;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DeclarationImport.ImportFragment;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.ReferenceResolver;

public class ImportStatic extends ImportFragment {
	
	private PartialPackageDefUnit defunit; // Non-Structural Element
	//private String[] names; // Non-Structural Element

	public ImportStatic(Import elem) {
		super(elem);
		//names = moduleRef.packageName.split("\\.");
	}
	
	private String[] getPackageNames() {
		return moduleRef.packages;
	}
	
	public DefUnit getPartialDefUnit() {

		if(getPackageNames().length == 0 || getPackageNames()[0] == "") {
			return moduleRef.findTargetDefUnit();
		}
		
		// Do lazy PartialDefUnit creation
		if(defunit == null) {
			defunit = PartialPackageDefUnit.createPartialDefUnits(getPackageNames(),
					moduleRef, null); 
		}
		return defunit;
	}
	


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, moduleRef);
		}
		visitor.endVisit(this);
	}

	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch search) {
		ReferenceResolver.findDefUnitInStaticImport(this, search);
	}
	
	
}