package dtool.dom.ast;

import descent.core.domX.IASTVisitor;
import util.tree.IElement;
import util.tree.IVisitable;

/**
 * A token/representation type.
 */
public interface IASTNode extends IElement, IVisitable<IASTVisitor> {

}
