package descent.internal.compiler.parser.ast;

import melnorme.miscutil.tree.ITreeNode;
import melnorme.miscutil.tree.IVisitable;

/**
 * A token/representation type.
 */
public interface IASTNode extends ITreeNode, IVisitable<IASTVisitor> {

}
