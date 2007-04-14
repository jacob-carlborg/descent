package util.tree;

public interface IVisitable<VISITOR extends TreeVisitor> {

	/** Accepts the given visitor on a visit of the current node. */
	void accept(VISITOR visitor);

}