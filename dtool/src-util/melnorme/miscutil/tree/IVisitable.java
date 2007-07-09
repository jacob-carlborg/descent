package melnorme.miscutil.tree;

public interface IVisitable<VISITOR> {

	/** Accepts the given visitor on a visit of the current node. */
	void accept(VISITOR visitor);

	//void acceptALT(Object visitor);

	
}