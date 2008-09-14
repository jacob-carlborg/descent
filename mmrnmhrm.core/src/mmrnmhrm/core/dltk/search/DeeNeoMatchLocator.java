package mmrnmhrm.core.dltk.search;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.SourceModelUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.search.BasicSearchEngine;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;
import org.eclipse.dltk.core.search.matching.MatchLocator;
import org.eclipse.dltk.core.search.matching.PatternLocator;
import org.eclipse.dltk.internal.core.search.matching.FieldPattern;
import org.eclipse.dltk.internal.core.search.matching.InternalSearchPattern;
import org.eclipse.dltk.internal.core.search.matching.MatchingNodeSet;
import org.eclipse.dltk.internal.core.search.matching.MethodPattern;
import org.eclipse.dltk.internal.core.search.matching.OrLocator;
import org.eclipse.dltk.internal.core.search.matching.OrPattern;
import org.eclipse.dltk.internal.core.search.matching.TypeDeclarationPattern;
import org.eclipse.dltk.internal.core.search.matching.TypeReferencePattern;

import dtool.ast.ASTNeoNode;

public class DeeNeoMatchLocator extends MatchLocator {

	/*
	public RubyMatchLocator(SearchPattern pattern, SearchRequestor requestor,
			IDLTKSearchScope scope, IProgressMonitor progressMonitor) {
		super(pattern, requestor, scope, progressMonitor);
	}
	*/
	
	public DeeNeoMatchLocator(SearchPattern pattern, SearchRequestor requestor,
			IDLTKSearchScope scope, IProgressMonitor progressMonitor) {
		super(pattern, requestor, scope, progressMonitor);
		this.patternLocator = neoCreatePatternLocator(this.pattern);
		this.matchContainer = this.patternLocator.matchContainer();
	}
	
	public static PatternLocator neoCreatePatternLocator(SearchPattern pattern) {
		if(DeeCore.DEBUG_MODE)
			System.out.println("== Requested match pattern: " + pattern);
		
		if(DeeDefPatternLocator.GLOBAL_param_defunit != null) {
			DeeDefPatternLocator defMatcher = new DeeDefPatternLocator(DeeDefPatternLocator.GLOBAL_param_defunit, pattern);
			DeeDefPatternLocator.GLOBAL_param_defunit = null;
			return defMatcher;
		}
		
		switch (((InternalSearchPattern) pattern).kind) {
			case IIndexConstants.TYPE_REF_PATTERN:
				return new DeeNeoPatternLocator((TypeReferencePattern) pattern);
			case IIndexConstants.TYPE_DECL_PATTERN:
				return new DeeNeoPatternLocator((TypeDeclarationPattern) pattern);
			case IIndexConstants.FIELD_PATTERN:
				 return new DeeNeoPatternLocator((FieldPattern) pattern);
			case IIndexConstants.METHOD_PATTERN:
				return new DeeNeoPatternLocator((MethodPattern) pattern);
			case IIndexConstants.OR_PATTERN:
				return new OrLocator((OrPattern) pattern);
			
		}

		return null;
	}
	
	
	
	// XXX: DLTK copied code
	@Override
	protected void reportMatching(ModuleDeclaration unit) throws CoreException {
		//DeeModuleDeclaration deeDec = (DeeModuleDeclaration) unit;
		//super.reportMatching(unit);
		MatchingNodeSet nodeSet = currentPossibleMatch.nodeSet;
		
		if (true || BasicSearchEngine.VERBOSE) {
			System.out.println("Report matching: "); //$NON-NLS-1$
			int size = nodeSet.matchingNodes == null ? 0
					: nodeSet.matchingNodes.elementSize;
			System.out.print("	- node set: accurate=" + size); //$NON-NLS-1$
			size = nodeSet.possibleMatchingNodesSet == null ? 0
					: nodeSet.possibleMatchingNodesSet.elementSize;
			System.out.println(", possible=" + size); //$NON-NLS-1$			

		}
		// All matches already correctly determined
		for (int i = 0; i < nodeSet.matchingNodes.keyTable.length; i++) {
			Object obj = nodeSet.matchingNodes.keyTable[i];
			if(obj instanceof ASTNeoNode) {
				ASTNeoNode node = (ASTNeoNode) obj;
				Integer accLevel = (Integer) nodeSet.matchingNodes.valueTable[i];
				//IModelElement modelElement = currentPossibleMatch.getModelElement();
				//IModelElement enclosingElement = modelElement;
				IMember enclosingType = SourceModelUtil.getTypeHandle(node);
				//Logg.main.println(enclosingType.getFullyQualifiedName());
				SearchMatch match = patternLocator.newDeclarationMatch(node,
						enclosingType, accLevel.intValue(), this);
				
				report(match);
			}
		}
				
		super.reportMatching(unit);
	}
	

}