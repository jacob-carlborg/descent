package util.tree;

public interface ITreeVisitor<NODE extends TreeNode> {

	/** Generic visit start. */
	void preVisit(NODE elem);

	/** Generic visit end. */
	void postVisit(NODE elem);

}