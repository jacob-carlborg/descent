package dtool.dom.ast;

import util.tree.IElement;
import util.tree.IVisitable;
import descent.core.domX.IASTVisitor;

/**
 * A token/representation type.
 */
public interface IASTNode extends IElement, IVisitable<IASTVisitor> {

}
