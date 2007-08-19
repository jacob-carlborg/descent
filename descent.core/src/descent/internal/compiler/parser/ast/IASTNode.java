package descent.internal.compiler.parser.ast;

import melnorme.miscutil.tree.IElement;
import melnorme.miscutil.tree.IVisitable;

/**
 * A token/representation type.
 */
public interface IASTNode extends IElement, IVisitable<IASTVisitor> {

}
