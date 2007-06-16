package descent.internal.ui.text.correction;

import descent.core.ICompilationUnit;
import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnit;

import descent.internal.corext.dom.NodeFinder;

import descent.ui.text.java.IInvocationContext;

import descent.internal.ui.javaeditor.ASTProvider;

/**
  */
public class AssistContext implements IInvocationContext {

	private ICompilationUnit fCompilationUnit;
	private int fOffset;
	private int fLength;

	private CompilationUnit fASTRoot;

	/*
	 * Constructor for CorrectionContext.
	 */
	public AssistContext(ICompilationUnit cu, int offset, int length) {
		fCompilationUnit= cu;
		fOffset= offset;
		fLength= length;

		fASTRoot= null;
	}

	/**
	 * Returns the compilation unit.
	 * @return Returns a ICompilationUnit
	 */
	public ICompilationUnit getCompilationUnit() {
		return fCompilationUnit;
	}

	/**
	 * Returns the length.
	 * @return int
	 */
	public int getSelectionLength() {
		return fLength;
	}

	/**
	 * Returns the offset.
	 * @return int
	 */
	public int getSelectionOffset() {
		return fOffset;
	}

	public CompilationUnit getASTRoot() {
		if (fASTRoot == null) {
			fASTRoot= ASTProvider.getASTProvider().getAST(fCompilationUnit, ASTProvider.WAIT_YES, null);
			if (fASTRoot == null) {
				// see bug 63554
				fASTRoot= ASTResolving.createQuickFixAST(fCompilationUnit, null);
			}
		}
		return fASTRoot;
	}


	/**
	 * @param root The ASTRoot to set.
	 */
	public void setASTRoot(CompilationUnit root) {
		fASTRoot= root;
	}

	/*(non-Javadoc)
	 * @see descent.ui.text.java.IInvocationContext#getCoveringNode()
	 */
	public ASTNode getCoveringNode() {
		NodeFinder finder= new NodeFinder(fOffset, fLength);
		getASTRoot().accept(finder);
		return finder.getCoveringNode();
	}

	/*(non-Javadoc)
	 * @see descent.ui.text.java.IInvocationContext#getCoveredNode()
	 */
	public ASTNode getCoveredNode() {
		NodeFinder finder= new NodeFinder(fOffset, fLength);
		getASTRoot().accept(finder);
		return finder.getCoveredNode();
	}

}
