package descent.internal.compiler.parser.ast;

import melnorme.miscutil.tree.IElement;
import melnorme.miscutil.tree.IVisitable;

/**
 */
public interface IASTNode extends IElement, IVisitable<IASTVisitor> {
	int getStartPos();
	
	int getOffset();
	int getLength();
	int getEndPos();

	String toStringAsNode(boolean printRangeInfo);

	boolean hasNoSourceRangeInfo();

}
