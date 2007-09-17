package mmrnmhrm.core.dltk.search;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.matching.PatternLocator;
import org.eclipse.dltk.internal.core.search.matching.MatchingNodeSet;

import dtool.dom.definitions.DefUnit;
import dtool.dom.references.CommonRefQualified;
import dtool.dom.references.Reference;

public class DeeDefMatcher extends PatternLocator {
	
	/** XXX: Global used to pass parameters for the search, due to
	 * DLTK limitation. */
	public static DefUnit param_defunit;
	
	public DefUnit defunit;

	public DeeDefMatcher(DefUnit defunit, SearchPattern pattern) {
		super(pattern);
		this.defunit = defunit;
	}
	
	@SuppressWarnings("restriction")
	public int match(ASTNode node, MatchingNodeSet nodeSet) {
		if(node instanceof Reference) {
			// don't match qualifieds, the match will be made in its children
			if(node instanceof CommonRefQualified)
				return IMPOSSIBLE_MATCH;
			
			Reference ref = (Reference) node;
			if(!ref.canMatch(defunit))
				return IMPOSSIBLE_MATCH;
			
			Collection<DefUnit> defUnits = ref.findTargetDefUnits(false);
			if(defUnits == null)
				return IMPOSSIBLE_MATCH;
			for (Iterator<DefUnit> iter = defUnits.iterator(); iter.hasNext();) {
				DefUnit targetdefunit = iter.next();
				if(defunit.equals(targetdefunit))
					return nodeSet.addMatch(ref, ACCURATE_MATCH);
			}
		}
		return IMPOSSIBLE_MATCH;
	}


}
