/**
 * 
 */
package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.Import;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DeclarationImport.ImportFragment;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.EntitySearch;

public class ImportStatic extends ImportFragment {
	
	private PartialPackageDefUnit defunit; // Non-Structural Element
	private String[] names; // Non-Structural Element

	public ImportStatic(Import elem) {
		super(elem);
		names = moduleEnt.packageName.split("\\.");
	}
	
	public DefUnit getDefUnit() {

		if(names.length == 0 || names[0] == "") {
			return moduleEnt.findTargetDefUnit();
		}
		
		// Do lazy PartialDefUnit creation
		if(defunit == null) {
			defunit = PartialPackageDefUnit.createPartialDefUnits(names,
					moduleEnt, null); 
		}
		return defunit;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, moduleEnt);
		}
		visitor.endVisit(this);
	}

	@Override
	public void searchDefUnit(EntitySearch options) {
		EntityResolver.findDefUnitInStaticImport(this, options);
	}
	
	
}