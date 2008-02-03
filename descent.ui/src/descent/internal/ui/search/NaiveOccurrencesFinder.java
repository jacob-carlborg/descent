package descent.internal.ui.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.IDocument;

import descent.core.IJavaElement;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTVisitor;
import descent.core.dom.BreakStatement;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ConstructorDeclaration;
import descent.core.dom.ContinueStatement;
import descent.core.dom.DebugDeclaration;
import descent.core.dom.DebugStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.GotoStatement;
import descent.core.dom.IBinding;
import descent.core.dom.LabeledStatement;
import descent.core.dom.SimpleName;
import descent.core.dom.Version;
import descent.core.dom.VersionDeclaration;
import descent.core.dom.VersionStatement;
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
	
	private final static int KIND_BINDING = 1;
	private final static int KIND_VERSION = 2;
	private final static int KIND_LABEL = 3;
	
	private AST fAST;
	private ASTNode fSelectedNode;
	private IBinding fBinding;
	private int kind;
	
	private ASTNode fStart;
	private List<ASTNode> fResult;
	
	public NaiveOccurrencesFinder() {
		fResult= new ArrayList();
	}

	public void collectOccurrenceMatches(IJavaElement element, IDocument document, Collection resultingMatches) {
		
	}
	
	public String initialize(CompilationUnit root, int offset, int length) {
		return initialize(root, NodeFinder.perform(root, offset, length));
	}
	
	public String initialize(CompilationUnit root, ASTNode node) {
		fAST= root.getAST();
		
		if (node instanceof SimpleName) {
			fSelectedNode = node;
			
			if (isLabel((SimpleName) node)) {
				ASTNode parent = fSelectedNode.getParent();
				while(parent != null && !(parent instanceof FunctionDeclaration) && !(parent instanceof ConstructorDeclaration)) {
					parent = parent.getParent();
				}
				
				fStart = parent;
				kind = KIND_LABEL;
			} else {
				fBinding = ((SimpleName) fSelectedNode).resolveBinding();
				fStart = root;
				kind = KIND_BINDING;
			}
			
			
		} else if (node instanceof Version) {
			fSelectedNode = node;
			fStart = root;
			
			kind = KIND_VERSION;
		} else {
			return "Node must be SimpleName or Version";
		}
		
		return null;
	}

	public String getElementName() {
		if (fSelectedNode != null) {
			return ASTNodes.asString(fSelectedNode);
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
	
	public List<ASTNode> perform() {
		fStart.accept(this);
		if (fSelectedNode != null) {
			fResult.add(fSelectedNode);
		}
		return fResult;
	}
	
	@Override
	public boolean visit(Version node) {
		if (kind == KIND_VERSION && node.getValue().equals(((Version) fSelectedNode).getValue())) {
			fResult.add(node);
		}
		return false;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		if (kind == KIND_BINDING && fBinding != null && fBinding == node.resolveBinding()) {
			fResult.add(node);
		} else if (kind == KIND_LABEL && isLabel(node)) {
			fResult.add(node);
		}
		return false;
	}
	
	private static boolean isLabel(SimpleName node) {
		return (node.getParent() instanceof LabeledStatement &&
				node.getLocationInParent().equals(LabeledStatement.LABEL_PROPERTY) ||
			(node.getParent() instanceof GotoStatement &&
				node.getLocationInParent().equals(GotoStatement.LABEL_PROPERTY))) ||
			(node.getParent() instanceof BreakStatement &&
					node.getLocationInParent().equals(BreakStatement.LABEL_PROPERTY)) ||
					(node.getParent() instanceof ContinueStatement &&
							node.getLocationInParent().equals(ContinueStatement.LABEL_PROPERTY));
	}

}
