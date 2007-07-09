package melnorme.miscutil.tree;

public interface ITreeVisitor<NODE extends ITreeNode> {

	/** Generic visit start. */
	void preVisit(NODE elem);

	/** Generic visit end. */
	void postVisit(NODE elem);

}