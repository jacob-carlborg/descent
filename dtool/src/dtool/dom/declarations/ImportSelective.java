package dtool.dom.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.ASTNode;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Import;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DeclarationImport.ImportFragment;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.RefIdentifier;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.INonScopedBlock;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class ImportSelective extends ImportFragment implements INonScopedBlock {
	
	public static class ImportSelectiveFragment extends DefUnit {

		public RefIdentifier targetname;

		public ImportSelective impSel; // Non Structural Element
		
		public ImportSelectiveFragment(IdentifierExp name) {
			super(name);
		}

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
			IScope scope = impSel.moduleRef.getTargetScope();
			String name = targetname != null ? targetname.name : defname.name;
			
			DefUnitSearch search = new DefUnitSearch(name, impSel.moduleRef, true);
			EntityResolver.findDefUnitInScope(scope, search);
			if(search.getDefUnits() == null)
				return null;
			
			DefUnit defunit = search.getDefUnits().iterator().next();
			return defunit.getMembersScope();
		}
	}
	
	public static ImportSelectiveFragment create(IdentifierExp name,
			IdentifierExp alias, ImportSelective impSel) {
	
		ImportSelectiveFragment impSelfrag;
		
		if(alias == null) {
			impSelfrag = new ImportSelectiveFragment(name);
			impSelfrag.setSourceRange(name);
		} else {
			impSelfrag = new ImportSelectiveFragment(alias);
			impSelfrag.targetname = new RefIdentifier(name);
			impSelfrag.setSourceRange(alias.start, name.getEndPos());
		}
		impSelfrag.impSel = impSel;
		return impSelfrag;
	}
	
	
	public ImportSelectiveFragment impSelFrags[];
	
	public ImportSelective(Import selImport) {
		super(selImport);
		
		int importsSize = selImport.names.size(); 
		this.impSelFrags = new ImportSelectiveFragment[importsSize];
		for(int i = 0; i < importsSize; i++) {
			this.impSelFrags[i] = ImportSelective.create(selImport.names.get(i),
					selImport.aliases.get(i), this);
		}
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, moduleRef);
			TreeVisitor.acceptChildren(visitor, impSelFrags);
		}
		visitor.endVisit(this);
	
	}
	public Iterator<? extends ASTNode> getMembersIterator() {
		return Arrays.asList(impSelFrags).iterator();
	}

	@Override
	public void searchDefUnit(CommonDefUnitSearch options) {
		// Do nothing. Selective imports do not contribute secondary-space DefUnits
		// TODO: This is a bug in D, it's not according to the spec.
	}

}