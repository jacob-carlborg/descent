package dtool.dom.ast;

import util.tree.ITreeNode;
import util.tree.IVisitable;
import descent.core.domX.IASTVisitor;

/**
 * A token/representation type.
 */
public interface IASTNode extends ITreeNode, IVisitable<IASTVisitor> {

}
