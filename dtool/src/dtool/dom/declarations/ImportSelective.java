/**
 * 
 */
package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.Import;
import descent.internal.core.dom.SelectiveImport;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DeclarationImport.ImportFragment;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Symbol;
import dtool.dom.definitions.DefUnit.EArcheType;
import dtool.dom.references.EntIdentifier;
import dtool.dom.references.EntModule;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.EntitySearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class ImportSelective extends ImportFragment {
	
	public static class ImportSelectiveFragment extends DefUnit {

		public static ImportSelectiveFragment create(
				SelectiveImport imprt, ImportSelective impSel) {

			ImportSelectiveFragment impSelfrag = new ImportSelectiveFragment();
			impSelfrag.convertNode(imprt);
			impSelfrag.impSel = impSel;
			
			if(imprt.alias == null) {
				impSelfrag.defname = new Symbol(imprt.name);
			} else {
				impSelfrag.defname = new Symbol(imprt.alias);
				impSelfrag.targetname = new EntIdentifier(imprt.name);
			}
			return impSelfrag;
		}

		public EntIdentifier targetname;

		public ImportSelective impSel; // Non Structural Element
		

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, defname);
				TreeVisitor.acceptChildren(visitor, targetname);
			}
			visitor.endVisit(this);		
		}


		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias;
		}


		@Override
		public IScopeNode getMembersScope() {
			IScope scope = impSel.moduleEnt.getTargetScope();
			String name = targetname != null ? targetname.name : defname.name;
			
			EntitySearch search = EntitySearch.newSearch(name, impSel.moduleEnt, true);
			EntityResolver.findDefUnitInScope(scope, search);
			if(search.getDefUnits() == null)
				return null;
			
			DefUnit defunit = search.getDefUnits().iterator().next();
			return defunit.getMembersScope();
		}
	}
	
	ImportSelectiveFragment aliases[];
	
	public ImportSelective(Import selImport) {
		super(selImport);
		
		int importsSize = selImport.getSelectiveImports().length; 
		this.aliases = new ImportSelectiveFragment[importsSize];
		for(int i = 0; i < importsSize; i++) {
			SelectiveImport imprt = selImport.getSelectiveImports()[i]; 
			this.aliases[i] = ImportSelectiveFragment.create(imprt, this);
		}
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, moduleEnt);
			TreeVisitor.acceptChildren(visitor, aliases);
		}
		visitor.endVisit(this);
	}

	@Override
	public void searchDefUnit(EntitySearch options) {
		// Do nothing. Aliasing imports do not contribute secondary-space DefUnits
	}
}