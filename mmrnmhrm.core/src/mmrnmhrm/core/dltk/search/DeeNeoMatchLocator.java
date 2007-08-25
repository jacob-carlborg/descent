package mmrnmhrm.core.dltk.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;
import org.eclipse.dltk.core.search.matching.MatchLocator;
import org.eclipse.dltk.core.search.matching.PatternLocator;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.dltk.internal.core.search.matching.FieldLocator;
import org.eclipse.dltk.internal.core.search.matching.FieldPattern;
import org.eclipse.dltk.internal.core.search.matching.InternalSearchPattern;
import org.eclipse.dltk.internal.core.search.matching.MatchingNodeSet;
import org.eclipse.dltk.internal.core.search.matching.MethodPattern;
import org.eclipse.dltk.internal.core.search.matching.TypeDeclarationPattern;
import org.eclipse.dltk.internal.core.search.matching.TypeReferenceLocator;
import org.eclipse.dltk.internal.core.search.matching.TypeReferencePattern;

import dtool.dom.ast.ASTNeoNode;

public class DeeNeoMatchLocator extends MatchLocator {

	public DeeNeoMatchLocator(SearchPattern pattern, SearchRequestor requestor,
			IDLTKSearchScope scope, IProgressMonitor progressMonitor) {
		super(pattern, requestor, scope, progressMonitor);
		this.patternLocator = neoCreatePatternLocator(this.pattern);
		this.matchContainer = this.patternLocator.matchContainer();
	}
	
	@SuppressWarnings("restriction")
	public static PatternLocator neoCreatePatternLocator(SearchPattern pattern) {
		switch (((InternalSearchPattern) pattern).kind) {
			case IIndexConstants.TYPE_REF_PATTERN:
				return new TypeReferenceLocator((TypeReferencePattern) pattern);
			case IIndexConstants.TYPE_DECL_PATTERN:
				return new NeoTypeDefinitionLocator((TypeDeclarationPattern) pattern);
			case IIndexConstants.FIELD_PATTERN:
				 return new FieldLocator((FieldPattern) pattern);
			case IIndexConstants.METHOD_PATTERN:
				return new NeoTypeDefinitionLocator((MethodPattern) pattern);
		}
		return null;
	}
	
	@SuppressWarnings("restriction")
	@Override
	protected void reportMatching(ModuleDeclaration unit) throws CoreException {
		//super.reportMatching(unit);
		MatchingNodeSet nodeSet = currentPossibleMatch.nodeSet;
		
		if(true) {
			System.out.println("Report matching: "); //$NON-NLS-1$
			int size = nodeSet.matchingNodes == null ? 0
					: nodeSet.matchingNodes.elementSize;
			System.out.print("	- node set: accurate=" + size); //$NON-NLS-1$
			size = nodeSet.possibleMatchingNodesSet == null ? 0
					: nodeSet.possibleMatchingNodesSet.elementSize;
			System.out.println(", possible=" + size); //$NON-NLS-1$			

		}
		for (int i = 0; i < nodeSet.matchingNodes.keyTable.length; i++) {
			Object obj = nodeSet.matchingNodes.keyTable[i];
			if(obj instanceof ASTNeoNode) {
				ASTNeoNode node = (ASTNeoNode) obj;
				IModelElement modelElement = currentPossibleMatch.getModelElement();
				IType type = new SourceType((ModelElement)modelElement, node.toString());
				SearchMatch match = patternLocator.newDeclarationMatch(node,
						type, 1, node.sourceEnd() - node.getOffset() + 1,
						this);
				report(match);
			}
		}
		
		/*patternLocator.newDeclarationMatch(element, 1, reference.sourceStart(), length);
	*/
		super.reportMatching(unit);
	}
}