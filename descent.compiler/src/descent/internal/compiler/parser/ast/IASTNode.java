package descent.internal.compiler.parser.ast;

import melnorme.utilbox.tree.IElement;
import melnorme.utilbox.tree.IVisitable;

/**
 */
public interface IASTNode extends IElement, IVisitable<IASTVisitor> {
	int getStartPos();
	
	int getOffset();
	int getLength();
	int getEndPos();

	String toStringAsNode(boolean printRangeInfo);

	boolean hasNoSourceRangeInfo();
	
	@Override
	public IASTNode[] getChildren(); // Redefined to refine the type of children
	
}
