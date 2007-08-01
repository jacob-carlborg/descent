package dtool.refmodel;

import java.util.Collection;

import melnorme.miscutil.Assert;
import dtool.dom.ast.ASTNodeFinder;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.expressions.Expression;
import dtool.dom.references.EntIdentifier;
import dtool.dom.references.Entity;

/** Class that does a scoped name lookup for matches that start with 
 * a given prefix name. 
 * TODO: Matches with the same name as matches in a scope with higher 
 * priority are not added.
 * This abstract class is used for code completion. */
public abstract class PartialEntitySearch extends CommonDefUnitSearch {

	protected PartialSearchOptions searchOptions;

	public PartialEntitySearch(PartialSearchOptions searchOptions) {
		this.searchOptions = searchOptions;
	}
	
	public Module getReferenceModule() {
		if(searchRefModule == null)
			searchRefModule = ((ASTNeoNode) searchOptions.searchScope).getModule();
		return searchRefModule;
	}

	
	@Override
	public boolean matches(DefUnit defUnit) {
		return defUnit.getName().startsWith(searchOptions.searchPrefix);
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	public static String doCompletionSearch(final int offset, ASTNode module,
			PartialSearchOptions searchOptions, CommonDefUnitSearch search) {
		ASTNode node = ASTNodeFinder.findElement(module, offset);
		
		if(node instanceof Entity)  {
			Entity ref = (Entity) node;
			if(node instanceof EntIdentifier) {

				EntIdentifier refIdent = (EntIdentifier) node;
				searchOptions.prefixLen = offset - refIdent.getOffset();
				searchOptions.rplLen = refIdent.getLength() - searchOptions.prefixLen;
				searchOptions.searchPrefix = refIdent.name.substring(0, searchOptions.prefixLen);

				
				if(node.getParent() instanceof IEntQualified) {
					IEntQualified parent = (IEntQualified) node.getParent();
					if(parent.getSubEnt() == node) {
						// Fixme here
						searchOptions.searchScope = NodeUtil.getOuterScope(ref);
						doCompletionQualified(search, parent);
						return null;
					} else {
						Assert.isTrue(parent.getRoot() == node);
					}
				}
				searchOptions.searchScope = NodeUtil.getOuterScope(ref);
				EntityResolver.findDefUnitInExtendedScope(searchOptions.searchScope, search);
			} else {
				return "Don't know how to complete for node: "+node+"(TODO)";
			}
		} else if(node instanceof IScopeNode) {
			searchOptions.searchScope = (IScopeNode) node;
			EntityResolver.findDefUnitInExtendedScope(searchOptions.searchScope, search);
		} else if(node instanceof Expression) {
			searchOptions.searchScope = NodeUtil.getOuterScope(node);
			EntityResolver.findDefUnitInExtendedScope(searchOptions.searchScope, search);
		} else {
			return "No Completion Available";
		}
		return null;
	}

	private static void doCompletionQualified(CommonDefUnitSearch search, IEntQualified qref) {
		Collection<DefUnit> defunits = qref.getRoot().findTargetDefUnits(false);
		if(defunits == null)
			return;

		for (DefUnit unit : defunits) {
			IScopeNode scope = unit.getMembersScope();
			EntityResolver.findDefUnitInScope(scope, search);
		}
	}

}
