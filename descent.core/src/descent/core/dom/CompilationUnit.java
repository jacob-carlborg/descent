package descent.core.dom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import descent.core.compiler.IProblem;

/**
 * Compilation unit AST node.
 * 
 * <pre>
 * CompilationUnit:
 *    [ ModuleDeclaration ]
 *    { Declaration }
 * </pre>
 */
public class CompilationUnit extends ASTNode {
	
	/**
	 * The "scriptLine" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor SCRIPT_LINE_PROPERTY =
		new ChildPropertyDescriptor(CompilationUnit.class, "scriptLine", ScriptLine.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "moduleDeclaration" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor MODULE_DECLARATION_PROPERTY =
		new ChildPropertyDescriptor(CompilationUnit.class, "moduleDeclaration", ModuleDeclaration.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "declarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor DECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(CompilationUnit.class, "declarations", Declaration.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(CompilationUnit.class, properyList);
		addProperty(SCRIPT_LINE_PROPERTY, properyList);
		addProperty(MODULE_DECLARATION_PROPERTY, properyList);
		addProperty(DECLARATIONS_PROPERTY, properyList);
		PROPERTY_DESCRIPTORS = reapPropertyList(properyList);
	}

	/**
	 * Returns a list of structural property descriptors for this node type.
	 * Clients must not modify the result.
	 * 
	 * @param apiLevel the API level; one of the
	 * <code>AST.JLS*</code> constants

	 * @return a list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor})
	 * @since 3.0
	 */
	public static List propertyDescriptors(int apiLevel) {
		return PROPERTY_DESCRIPTORS;
	}
	
	/**
	 * The scriptLine.
	 */
	private ScriptLine scriptLine;

	/**
	 * The moduleDeclaration.
	 */
	private ModuleDeclaration moduleDeclaration;
	
	/**
	 * The comment list (element type: <code>Comment</code>, 
	 * or <code>null</code> if none; initially <code>null</code>.
	 */
	private List optionalCommentList = null;

	/**
	 * The comment table, or <code>null</code> if none; initially
	 * <code>null</code>. This array is the storage underlying
	 * the <code>optionalCommentList</code> ArrayList.
	 */
	Comment[] optionalCommentTable = null;
	
	/**
	 * The pragma list (element type: <code>Pragma</code>, 
	 * or <code>null</code> if none; initially <code>null</code>.
	 */
	private List optionalPragmaList = null;

	/**
	 * The prgama table, or <code>null</code> if none; initially
	 * <code>null</code>. This array is the storage underlying
	 * the <code>optionalPragmaList</code> ArrayList.
	 */
	Pragma[] optionalPragmaTable = null;

	/**
	 * The declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList declarations =
		new ASTNode.NodeList(DECLARATIONS_PROPERTY);

	/**
	 * Creates a new unparented compilation unit node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	CompilationUnit(AST ast) {
		super(ast);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalStructuralPropertiesForType(int apiLevel) {
		return propertyDescriptors(apiLevel);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == SCRIPT_LINE_PROPERTY) {
			if (get) {
				return getScriptLine();
			} else {
				setScriptLine((ScriptLine) child);
				return null;
			}
		}
		if (property == MODULE_DECLARATION_PROPERTY) {
			if (get) {
				return getModuleDeclaration();
			} else {
				setModuleDeclaration((ModuleDeclaration) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalGetChildListProperty(ChildListPropertyDescriptor property) {
		if (property == DECLARATIONS_PROPERTY) {
			return declarations();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return COMPILATION_UNIT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		CompilationUnit result = new CompilationUnit(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setScriptLine((ScriptLine) ASTNode.copySubtree(target, getScriptLine()));
		result.setModuleDeclaration((ModuleDeclaration) ASTNode.copySubtree(target, getModuleDeclaration()));
		result.declarations.addAll(ASTNode.copySubtrees(target, declarations()));
		return result;
	}
	
	/**
	 * Returns a list of the comments encountered while parsing
	 * this compilation unit.
	 * <p>
	 * Since the D language allows comments to appear most anywhere
	 * in the source text, it is problematic to locate comments in relation
	 * to the structure of an AST. The one exception is doc comments 
	 * which, by convention, immediately precede declarations; 
	 * these comments are located in the AST
	 * by {@link  Declaration#getDDocs Declaration.getDDocs}.
	 * Other comments do not show up in the AST. The table of comments
	 * is provided for clients that need to find the source ranges of
	 * all comments in the original source string. It includes entries
	 * for comments of all kinds (line, block, plus and doc), arranged in order
	 * of increasing source position. 
	 * </p>
	 * <p>
	 * A note on visitors: The only comment nodes that will be visited when
	 * visiting a compilation unit are the doc comments parented by body
	 * declarations. To visit all comments in normal reading order, iterate
	 * over the comment table and call {@link ASTNode#accept(ASTVisitor) accept}
	 * on each element.
	 * </p>
	 * <p>
	 * Clients cannot modify the resulting list.
	 * </p>
	 * 
	 * @return an unmodifiable list of comments in increasing order of source
	 * start position, or <code>null</code> if comment information
	 * for this compilation unit is not available
	 * @see ASTParser
	 * @since 3.0
	 */
	public List getCommentList() {
		return this.optionalCommentList;
	}
	
	/**
	 * Sets the list of the comments encountered while parsing
	 * this compilation unit.
	 * 
	 * @param commentTable a list of comments in increasing order
	 * of source start position, or <code>null</code> if comment
	 * information for this compilation unit is not available
	 * @exception IllegalArgumentException if the comment table is
	 * not in increasing order of source position
	 * @see #getCommentList()
	 * @see ASTParser
	 * @since 3.0
	 */
	void setCommentTable(Comment[] commentTable) {
		// double check table to ensure that all comments have
		// source positions and are in strictly increasing order
		if (commentTable == null) {
			this.optionalCommentList = null;
			this.optionalCommentTable = null;
		} else {
			int nextAvailablePosition = 0;
			for (int i = 0; i < commentTable.length; i++) {
				Comment comment = commentTable[i];
				if (comment == null) {
					throw new IllegalArgumentException();
				}
				int start = comment.getStartPosition();
				int length = comment.getLength();
				if (start < 0 || length < 0 || start < nextAvailablePosition) {
					throw new IllegalArgumentException();
				}
				nextAvailablePosition = comment.getStartPosition() + comment.getLength();
			}
			this.optionalCommentTable = commentTable;
			List commentList = Arrays.asList(commentTable);
			// protect the list from further modification
			this.optionalCommentList = Collections.unmodifiableList(commentList);
		}
	}
	
	/**
	 * Returns a list of the pragmas encountered while parsing
	 * this compilation unit.
	 * <p>
	 * Since the D language allows pragmas to appear most anywhere
	 * in the source text, it is problematic to locate pragmas in relation
	 * to the structure of an AST. The table of pragmas
	 * is provided for clients that need to find the source ranges of
	 * all pragmas in the original source string. It includes entries
	 * for all pragmas, arranged in order
	 * of increasing source position. 
	 * </p>
	 * <p>
	 * A note on visitors: to visit all pragmas in normal reading order, iterate
	 * over the pragmas table and call {@link ASTNode#accept(ASTVisitor) accept}
	 * on each element.
	 * </p>
	 * <p>
	 * Clients cannot modify the resulting list.
	 * </p>
	 * 
	 * @return an unmodifiable list of pragmas in increasing order of source
	 * start position, or <code>null</code> if pragma information
	 * for this compilation unit is not available
	 * @see ASTParser
	 */
	public List<Pragma> getPragmaList() {
		return this.optionalPragmaList;
	}
	
	/**
	 * Sets the list of the pragmas encountered while parsing
	 * this compilation unit.
	 * 
	 * @param pragmaTable a list of pragmas in increasing order
	 * of source start position, or <code>null</code> if pragma
	 * information for this compilation unit is not available
	 * @exception IllegalArgumentException if the pragma table is
	 * not in increasing order of source position
	 * @see #getPragmaList()
	 * @see ASTParser
	 */
	void setPragmaTable(Pragma[] pragmaTable) {
		// double check table to ensure that all comments have
		// source positions and are in strictly increasing order
		if (pragmaTable == null) {
			this.optionalPragmaList = null;
			this.optionalPragmaTable = null;
		} else {
			int nextAvailablePosition = 0;
			for (int i = 0; i < pragmaTable.length; i++) {
				Pragma pragma = pragmaTable[i];
				if (pragma == null) {
					throw new IllegalArgumentException();
				}
				int start = pragma.getStartPosition();
				int length = pragma.getLength();
				if (start < 0 || length < 0 || start < nextAvailablePosition) {
					throw new IllegalArgumentException();
				}
				nextAvailablePosition = pragma.getStartPosition() + pragma.getLength();
			}
			this.optionalPragmaTable = pragmaTable;
			List pragmaList = Arrays.asList(pragmaTable);
			// protect the list from further modification
			this.optionalPragmaList = Collections.unmodifiableList(pragmaList);
		}
	}
	
	/**
	 * Returns the extended source length of the given node. Unlike
	 * {@link ASTNode#getStartPosition()} and {@link ASTNode#getLength()},
	 * the extended source range may include comments and whitespace
	 * immediately before or after the normal source range for the node.
	 * 
	 * @param node the node
	 * @return a (possibly 0) length, or <code>0</code>
	 *    if no source position information is recorded for this node
	 * @see #getExtendedStartPosition(ASTNode)
	 */
	// TODO: implement
	public int getExtendedLength(ASTNode node) {
		return node.getLength();
	}

	/**
	 * Returns the extended start position of the given node. Unlike
	 * {@link ASTNode#getStartPosition()} and {@link ASTNode#getLength()},
	 * the extended source range may include comments and whitespace
	 * immediately before or after the normal source range for the node.
	 * 
	 * @param node the node
	 * @return the 0-based character index, or <code>-1</code>
	 *    if no source position information is recorded for this node
	 * @see #getExtendedLength(ASTNode)
	 */
	// TODO: implement
	public int getExtendedStartPosition(ASTNode node) {
		return node.getStartPosition();
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final boolean subtreeMatch0(ASTMatcher matcher, Object other) {
		// dispatch to correct overloaded match method
		return matcher.match(this, other);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	void accept0(ASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if (visitChildren) {
			// visit children in normal left to right reading order
			acceptChild(visitor, getScriptLine());
			acceptChild(visitor, getModuleDeclaration());
			acceptChildren(visitor, declarations);
		}
		visitor.endVisit(this);
	}
	
	/**
	 * Returns the script line of this compilation unit.
	 * 
	 * @return the script line
	 */ 
	public ScriptLine getScriptLine() {
		return this.scriptLine;
	}

	/**
	 * Sets the script line of this compilation unit.
	 * 
	 * @param scriptLine the script line
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setScriptLine(ScriptLine scriptLine) {
		ASTNode oldChild = this.scriptLine;
		preReplaceChild(oldChild, scriptLine, SCRIPT_LINE_PROPERTY);
		this.scriptLine = scriptLine;
		postReplaceChild(oldChild, scriptLine, SCRIPT_LINE_PROPERTY);
	}

	/**
	 * Returns the module declaration of this compilation unit.
	 * 
	 * @return the module declaration
	 */ 
	public ModuleDeclaration getModuleDeclaration() {
		return this.moduleDeclaration;
	}

	/**
	 * Sets the module declaration of this compilation unit.
	 * 
	 * @param moduleDeclaration the module declaration
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		ASTNode oldChild = this.moduleDeclaration;
		preReplaceChild(oldChild, moduleDeclaration, MODULE_DECLARATION_PROPERTY);
		this.moduleDeclaration = moduleDeclaration;
		postReplaceChild(oldChild, moduleDeclaration, MODULE_DECLARATION_PROPERTY);
	}

	/**
	 * Returns the live ordered list of declarations for this
	 * compilation unit.
	 * 
	 * @return the live list of compilation unit
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> declarations() {
		return this.declarations;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 3 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.scriptLine == null ? 0 : getScriptLine().treeSize())
			+ (this.moduleDeclaration == null ? 0 : getModuleDeclaration().treeSize())
			+ (this.declarations.listSize())
	;
	}
	
	public List<IProblem> problems;
	
	public IProblem[] getProblems() {
		if (problems == null) return new IProblem[0];
		return problems.toArray(new IProblem[0]);
	}

}
