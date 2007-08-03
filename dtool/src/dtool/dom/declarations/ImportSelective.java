package dtool.dom.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.Import;
import descent.internal.core.dom.SelectiveImport;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DeclarationImport.ImportFragment;
import dtool.dom.definitions.DefSymbol;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.EntIdentifier;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.INonScopedBlock;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class ImportSelective extends ImportFragment implements INonScopedBlock {
	
	public static class ImportSelectiveFragment extends DefUnit {

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
			
			DefUnitSearch search = new DefUnitSearch(name, impSel.moduleEnt, true);
			EntityResolver.findDefUnitInScope(scope, search);
			if(search.getDefUnits() == null)
				return null;
			
			DefUnit defunit = search.getDefUnits().iterator().next();
			return defunit.getMembersScope();
		}
	}
	
	public static ImportSelectiveFragment create(
			SelectiveImport imprt, ImportSelective impSel) {
	
		ImportSelectiveFragment impSelfrag = new ImportSelectiveFragment();
		impSelfrag.convertNode(imprt);
		impSelfrag.impSel = impSel;
		
		if(imprt.alias == null) {
			impSelfrag.defname = new DefSymbol(imprt.name, impSelfrag);
		} else {
			impSelfrag.defname = new DefSymbol(imprt.alias,impSelfrag);
			impSelfrag.targetname = new EntIdentifier(imprt.name);
		}
		return impSelfrag;
	}
	
	public ImportSelectiveFragment aliases[];
	
	public ImportSelective(Import selImport) {
		super(selImport);
		
		int importsSize = selImport.getSelectiveImports().length; 
		this.aliases = new ImportSelectiveFragment[importsSize];
		for(int i = 0; i < importsSize; i++) {
			SelectiveImport imprt = selImport.getSelectiveImports()[i]; 
			this.aliases[i] = ImportSelective.create(imprt, this);
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
	public Iterator<? extends ASTNode> getMembersIterator() {
		return Arrays.asList(aliases).iterator();
	}

	@Override
	public void searchDefUnit(CommonDefUnitSearch options) {
		// Do nothing. Selective imports do not contribute secondary-space DefUnits
		// TODO: This is a bug in D, it's not according to the spec.
	}

}