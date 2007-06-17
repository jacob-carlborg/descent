package descent.internal.ui.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.IDocument;

import descent.core.IJavaElement;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTVisitor;
import descent.core.dom.CompilationUnit;
import descent.core.dom.SimpleName;
import descent.internal.corext.dom.ASTNodes;
import descent.internal.corext.dom.NodeFinder;

/**
 * A naive occurrences finder traverses all the AST hierarchy finding
 * SimpleName nodes that has the same name as the SimpleName node under the cursor,
 * if it's a SimpleName.
 * 
 * TODO JDT UI remove when a more powerfull occurrences finder is implemented
 */
public class NaiveOccurrencesFinder extends ASTVisitor implements IOccurrencesFinder {
	
	private AST fAST;
	private SimpleName fSelectedName;
	
	private ASTNode fStart;
	private List fResult;
	
	public NaiveOccurrencesFinder() {
		fResult= new ArrayList();
	}

	public void collectOccurrenceMatches(IJavaElement element, IDocument document, Collection resultingMatches) {
		int a = 2;
	}
	
	public String initialize(CompilationUnit root, int offset, int length) {
		return initialize(root, NodeFinder.perform(root, offset, length));
	}
	
	public String initialize(CompilationUnit root, ASTNode node) {
		fAST= root.getAST();
		if (!(node instanceof SimpleName)) {
			return "Node must be SimpleName";  
		}
		fSelectedName = (SimpleName) node;
		fStart = root;
		return null;
	}

	public String getElementName() {
		if (fSelectedName != null) {
			return ASTNodes.asString(fSelectedName);
		}
		return null;
	}

	public String getJobLabel() {
		return "Search for naive ocurrences"; //$NON-NLS-1$
	}

	public String getUnformattedPluralLabel() {
		return "''{0}'' - {1} naive occurrences in ''{2}''"; //$NON-NLS-1$
	}

	public String getUnformattedSingularLabel() {
		return "''{0}'' - 1 naive occurrences in ''{1}''"; //$NON-NLS-1$
	}
	
	public List perform() {
		fStart.accept(this);
		if (fSelectedName != null) {
			fResult.add(fSelectedName);
		}
		return fResult;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		if (fSelectedName != null && fSelectedName != node && fSelectedName.getIdentifier().equals(node.getIdentifier())) {
			fResult.add(node);
		}
		return false;
	}

}
