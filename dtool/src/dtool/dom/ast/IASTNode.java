package dtool.dom.ast;

import melnorme.miscutil.tree.ITreeNode;
import melnorme.miscutil.tree.IVisitable;
import descent.core.domX.IASTVisitor;

/**
 * A token/representation type.
 */
public interface IASTNode extends ITreeNode, IVisitable<IASTVisitor> {

}
