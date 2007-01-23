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
	 * The comment mapper, or <code>null</code> if none; 
	 * initially <code>null</code>.
	 * @since 3.0
	 */
	private DefaultCommentMapper commentMapper = null;
	
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
	 * Line end table. If <code>lineEndTable[i] == p</code> then the
	 * line number <code>i+1</code> ends at character position 
	 * <code>p</code>. Except for the last line, the positions are that
	 * of the last character of the line delimiter. 
	 * For example, the source string <code>A\nB\nC</code> has
	 * line end table {1, 3} (if \n is one character).
	 */
	private int[] lineEndTable = new int[0];

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
	
	/**
	 * Return the index in the whole comments list {@link #getCommentList() }
	 * of the first leading comments associated with the given node. 
	 * 
	 * @param node the node
	 * @return 0-based index of first leading comment or -1 if node has no associated
	 * 	comment before its start position.
	 * @since 3.2
	 */
	public int firstLeadingCommentIndex(ASTNode node) {
		if (node == null) {
			throw new IllegalArgumentException();
		}
		if (this.commentMapper == null || node.getAST() != getAST()) {
			return -1;
		}
		return this.commentMapper.firstLeadingCommentIndex(node);
	}

	/**
	 * Return the index in the whole comments list {@link #getCommentList() }
	 * of the last trailing comments associated with the given node. 
	 * 
	 * @param node the node
	 * @return 0-based index of last trailing comment or -1 if node has no
	 * 	associated comment after its end position.
	 * @since 3.2
	 */
	public int lastTrailingCommentIndex(ASTNode node) {
		if (node == null) {
			throw new IllegalArgumentException();
		}
		if (this.commentMapper == null || node.getAST() != getAST()) {
			return -1;
		}
		return this.commentMapper.lastTrailingCommentIndex(node);
	}
	
	/**
	 * Initializes the internal comment mapper with the given
	 * scanner.
	 * 
	 * @param scanner the scanner
	 * @since 3.0
	 */
	void initCommentMapper(PublicScanner scanner) {
		this.commentMapper = new DefaultCommentMapper(this.optionalCommentTable);
		this.commentMapper.initialize(this, scanner);
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
	 * Returns the column number corresponding to the given source character
	 * position in the original source string. Column number are zero-based. 
	 * Return <code>-1</code> if it is beyond the valid range or <code>-2</code>
	 * if the column number information is unknown.
	 * 
	 * @param position a 0-based character position, possibly
	 *   negative or out of range
	 * @return the 0-based column number, or <code>-1</code> if the character
	 *    position does not correspond to a source line in the original
	 *    source file or <code>-2</code> if column number information is unknown for this
	 *    compilation unit
	 * @see ASTParser
	 */
	public int getColumnNumber(final int position) {
		if (this.lineEndTable == null) return -2;
		final int line = getLineNumber(position);
		if (line == -1) {
			return -1;
		}
		if (line == 1) {
			if (position >= getStartPosition() + getLength()) return -1;
			return position;
		}
		// length is different from 0
		int length = this.lineEndTable.length;
		// -1 to for one-based to zero-based conversion.
		// -1, again, to get previous line.
		final int previousLineOffset = this.lineEndTable[line - 2];
		 // previousLineOffset + 1 is the first character of the current line
		final int offsetForLine = previousLineOffset + 1;
		final int currentLineEnd = line == length + 1 ? getStartPosition() + getLength() - 1 :	this.lineEndTable[line - 1];
		if (offsetForLine > currentLineEnd) {
			return -1;
		} else {
			return position - offsetForLine;
		}
	}
	
	/**
	 * Given a line number and column number, returns the corresponding 
	 * position in the original source string.
	 * Returns -2 if no line number information is available for this
	 * compilation unit. 
	 * Returns the total size of the source string if <code>line</code>
	 * is greater than the actual number lines in the unit.
	 * Returns -1 if <code>column</code> is less than 0,  
	 * or the position of the last character of the line if <code>column</code>
	 * is beyond the legal range, or the given line number is less than one. 
	 * 
	 * @param line the one-based line number
	 * @param column the zero-based column number
	 * @return the 0-based character position in the source string; 
	 * <code>-2</code> if line/column number information is not known 
	 * for this compilation unit or <code>-1</code> the inputs are not valid
	 */
	 public int getPosition(int line, int column) {
		if (this.lineEndTable == null) return -2;
		if (line < 1 || column < 0) return -1;
		int length;
		if ((length = this.lineEndTable.length) == 0) {
			if (line != 1) return -1;
			return column >= getStartPosition() + getLength() ? -1 : column;
		}
		if (line == 1) {
			final int endOfLine = this.lineEndTable[0];
			return column > endOfLine ? -1 : column;			
		} else if( line > length + 1 ) {
			// greater than the number of lines in the source string.
			return -1;
		}		
		// -1 to for one-based to zero-based conversion.
		// -1, again, to get previous line.
		final int previousLineOffset = this.lineEndTable[line - 2];
		 // previousLineOffset + 1 is the first character of the current line
		final int offsetForLine = previousLineOffset + 1;
		final int currentLineEnd = line == length + 1 ? getStartPosition() + getLength() - 1 : this.lineEndTable[line-1];
		if ((offsetForLine + column) > currentLineEnd) {  
			return -1;
		} else {  
			return offsetForLine + column;
		}
	}
	 
	 /**
	 * Returns the line number corresponding to the given source character
	 * position in the original source string. The initial line of the 
	 * compilation unit is numbered 1, and each line extends through the
	 * last character of the end-of-line delimiter. The very last line extends
	 * through the end of the source string and has no line delimiter.
	 * For example, the source string <code>class A\n{\n}</code> has 3 lines
	 * corresponding to inclusive character ranges [0,7], [8,9], and [10,10].
	 * Returns -1 for a character position that does not correspond to any
	 * source line, or -2 if no line number information is available for this
	 * compilation unit.
	 * 
	 * @param position a 0-based character position, possibly
	 *   negative or out of range
	 * @return the 1-based line number, or <code>-1</code> if the character
	 *    position does not correspond to a source line in the original
	 *    source file or <code>-2</code> if line number information is not known for this
	 *    compilation unit
	 * @see ASTParser
	 * @since 3.2
	 */
	public int getLineNumber(int position) {
		if (this.lineEndTable == null) return -2;
		int length;
		if ((length = this.lineEndTable.length) == 0) {
			if (position >= getStartPosition() + getLength()) {
				return -1;
			}
			return 1;
		}
		int low = 0;
		if (position < 0) {
			// position illegal 
			return -1;
		}
		if (position <= this.lineEndTable[low]) {
			// before the first line delimiter
			return 1;
		}
		// assert position > lineEndTable[low+1]  && low == 0
		int hi = length - 1;
		if (position > this.lineEndTable[hi]) {
			// position beyond the last line separator
			if (position >= getStartPosition() + getLength()) {
				// this is beyond the end of the source length
				return -1;
			} else {
				return length + 1;
			}
		}
		// assert lineEndTable[low]  < position <= lineEndTable[hi]
		// && low == 0 && hi == length - 1 && low < hi
		
		// binary search line end table
		while (true) {
			// invariant lineEndTable[low] < position <= lineEndTable[hi]
			// && 0 <= low < hi <= length - 1
			// reducing measure hi - low
			if (low + 1 == hi) {
				// assert lineEndTable[low] < position <= lineEndTable[low+1]
				// position is on line low+1 (line number is low+2)
				return low + 2;
			}
			// assert hi - low >= 2, so average is truly in between
			int mid = (low + hi) / 2;
			// assert 0 <= low < mid < hi <= length - 1
			if (position <= this.lineEndTable[mid]) {
				// assert lineEndTable[low] < position <= lineEndTable[mid]
				// && 0 <= low < mid < hi <= length - 1
				hi = mid;
			} else {
				// position > lineEndTable[mid]
				// assert lineEndTable[mid] < position <= lineEndTable[hi]
				// && 0 <= low < mid < hi <= length - 1
				low = mid;
			}
			// in both cases, invariant reachieved with reduced measure
		}
	}
	
	/**
	 * Sets the line end table for this compilation unit.
	 * If <code>lineEndTable[i] == p</code> then line number <code>i+1</code> 
	 * ends at character position <code>p</code>. Except for the last line, the 
	 * positions are that of (the last character of) the line delimiter.
	 * For example, the source string <code>A\nB\nC</code> has
	 * line end table {1, 3, 4}.
	 * 
	 * @param lineEndTable the line end table
	 */
	void setLineEndTable(int[] lineEndTable) {
		if (lineEndTable == null) {
			throw new NullPointerException();
		}
		// alternate root is *not* considered a structural property
		// but we protect them nevertheless
		checkModifiable();
		this.lineEndTable = lineEndTable;
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
	 * @since 3.0
	 */
	public int getExtendedLength(ASTNode node) {
		if (node == null) {
			throw new IllegalArgumentException();
		}
		if (this.commentMapper == null || node.getAST() != getAST()) {
			// fall back: use best info available
			return node.getLength();
		} else {
			return this.commentMapper.getExtendedLength(node);
		}
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
	 * @since 3.0
	 */
	public int getExtendedStartPosition(ASTNode node) {
		if (node == null) {
			throw new IllegalArgumentException();
		}
		if (this.commentMapper == null || node.getAST() != getAST()) {
			// fall back: use best info available
			return node.getStartPosition();
		} else {
			return this.commentMapper.getExtendedStartPosition(node);
		}
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
