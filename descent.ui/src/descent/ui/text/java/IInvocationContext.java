package descent.ui.text.java;

import descent.core.ICompilationUnit;

import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnit;

/**
 * Context information for quick fix and quick assist processors.
 * <p>
 * Note: this interface is not intended to be implemented.
 * </p>
 *
 * @since 3.0
 */
public interface IInvocationContext {

	/**
	 * @return Returns the current compilation unit.
	 */
	ICompilationUnit getCompilationUnit();

	/**
	 * @return Returns the offset of the current selection
	 */
	int getSelectionOffset();

	/**
	 * @return Returns the length of the current selection
	 */
	int getSelectionLength();

	/**
	 * Returns an AST of the compilation unit, possibly only a partial AST focused on the selection
	 * offset (see {@link descent.core.dom.ASTParser#setFocalPosition(int)}).
	 * The returned AST is shared and therefore protected and cannot be modified.
	 * The client must check the AST API level and do nothing if they are given an AST
	 * they can't handle. (see {@link descent.core.dom.AST#apiLevel()}).
	 * @return Returns the root of the AST corresponding to the current compilation unit.
	 */
	CompilationUnit getASTRoot();

	/**
	 * Convenience method to evaluate the AST node covering the current selection.
	 * @return Returns the node that covers the location of the problem
	 */
	ASTNode getCoveringNode();

	/**
	 * Convenience method to evaluate the AST node that is covered by the current selection.
	 * @return Returns the node that is covered by the location of the problem
	 */
	ASTNode getCoveredNode();

}
