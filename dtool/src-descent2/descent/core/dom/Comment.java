package descent.core.dom;


/**
 * Abstract base class for all AST nodes that represent comments.
 * There are exactly two kinds of comment: 
 * code comments ({@link CodeComment}),
 * d doc comments ({@link DDocComment}).
 * <p>
 * <pre>
 * Comment:
 *     CodeComment
 *     DDocComment
 * </pre>
 * </p>
 */
public abstract class Comment extends ASTNode {
	
	public static enum Kind {
		LINE_COMMENT,
		BLOCK_COMMENT,
		PLUS_COMMENT,
	}
	
	/**
	 * The kind of comment.
	 */
	Kind kind = Kind.LINE_COMMENT;
	
	/**
	 * Alternate root node, or <code>null</code> if none.
	 * Initially <code>null</code>.
	 */
	private ASTNode alternateRoot = null;
	

	


	/**
	 * Creates a new AST node for a comment owned by the given AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Comment(AST ast) {
		super(ast);
	}
	
	/**
	 * Returns whether this comment is a code comment
	 * (<code>CodeComment</code>).
	 * 
	 * @return <code>true</code> if this is a code comment, and 
	 *    <code>false</code> otherwise
	 */
	public final boolean isCodeComment() {
		return (this instanceof CodeComment);
	}
	
	/**
	 * Returns whether this comment is a d doc comment
	 * (<code>DDocComment</code>).
	 * 
	 * @return <code>true</code> if this is a d doc comment, and 
	 *    <code>false</code> otherwise
	 */
	public final boolean isDDocComment() {
		return (this instanceof DDocComment);
	}
	
	/**
	 * Returns the root AST node that this comment occurs
	 * within, or <code>null</code> if none (or not recorded).
	 * <p>
	 * Typically, the comment nodes created while parsing a compilation
	 * unit are not considered descendents of the normal AST
	 * root, namely an {@link CompilationUnit}. Instead, these
	 * comment nodes exist outside the normal AST and each is 
	 * a root in its own right. This optional property provides
	 * a well-known way to navigate from the comment to the
	 * compilation unit in such cases. Note that the alternate root
	 * property is not one of the comment node's children. It is simply a
	 * reference to a node.
	 * </p>
	 * 
	 * @return the alternate root node, or <code>null</code> 
	 * if none
	 * @see #setAlternateRoot(ASTNode)
	 */
	public final ASTNode getAlternateRoot() {
		return this.alternateRoot;
	}
	
	/**
	 * Returns the root AST node that this comment occurs
	 * within, or <code>null</code> if none (or not recorded).
	 * <p>
	 * </p>
	 * 
	 * @param root the alternate root node, or <code>null</code> 
	 * if none
	 * @see #getAlternateRoot()
	 */
	public final void setAlternateRoot(ASTNode root) {
		// alternate root is *not* considered a structural property
		// but we protect them nevertheless
		checkModifiable();
		this.alternateRoot = root;
	}
	
	/**
	 * Returns the kind of this comment.
	 * 
	 * @return the kind
	 */ 
	public Kind getKind() {
		return this.kind;
	}

	/**
	 * Sets the kind of this comment.
	 * 
	 * @param kind the kind
	 */ 
	public void setKind(Kind kind) {
		if (kind == null) {
			throw new IllegalArgumentException();
		}
		this.kind = kind;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 1 * 4;
	}

}
