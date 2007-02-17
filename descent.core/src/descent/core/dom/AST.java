/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.core.dom;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.StringTokenizer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import descent.core.JavaCore;
import descent.core.ToolFactory;
import descent.core.compiler.IScanner;


/**
 * Umbrella owner and abstract syntax tree node factory.
 * An <code>AST</code> instance serves as the common owner of any number of
 * AST nodes, and as the factory for creating new AST nodes owned by that 
 * instance.
 * <p>
 * Abstract syntax trees may be hand constructed by clients, using the
 * <code>new<i>TYPE</i></code> factory methods to create new nodes, and the
 * various <code>set<i>CHILD</i></code> methods 
 * (see {@link descen.core.dom.ASTNode ASTNode} and its subclasses)
 * to connect them together.
 * </p>
 * <p>
 * Each AST node belongs to a unique AST instance, called the owning AST.
 * The children of an AST node always have the same owner as their parent node.
 * If a node from one AST is to be added to a different AST, the subtree must
 * be cloned first to ensures that the added nodes have the correct owning AST.
 * </p>
 * <p>
 * There can be any number of AST nodes owned by a single AST instance that are
 * unparented. Each of these nodes is the root of a separate little tree of nodes.
 * The method <code>ASTNode.getRoot()</code> navigates from any node to the root
 * of the tree that it is contained in. Ordinarily, an AST instance has one main
 * tree (rooted at a <code>CompilationUnit</code>), with newly-created nodes appearing
 * as additional roots until they are parented somewhere under the main tree.
 * One can navigate from any node to its AST instance, but not conversely.
 * </p>
 * <p>
 * The class {@link ASTParser} parses a string
 * containing a Java source code and returns an abstract syntax tree
 * for it. The resulting nodes carry source ranges relating the node back to
 * the original source characters.
 * </p>
 * <p>
 * Compilation units created by <code>ASTParser</code> from a
 * source document can be serialized after arbitrary modifications
 * with minimal loss of original formatting. Here is an example:
 * <pre>
 * Document doc = new Document("import java.util.List;\nclass X {}\n");
 * ASTParser parser = ASTParser.newParser(AST.JLS3);
 * parser.setSource(doc.get().toCharArray());
 * CompilationUnit cu = (CompilationUnit) parser.createAST(null);
 * cu.recordModifications();
 * AST ast = cu.getAST();
 * ImportDeclaration id = ast.newImportDeclaration();
 * id.setName(ast.newName(new String[] {"java", "util", "Set"});
 * cu.imports().add(id); // add import declaration at end
 * TextEdit edits = cu.rewrite(document, null);
 * UndoEdit undo = edits.apply(document);
 * </pre>
 * See also {@link org.eclipse.jdt.core.dom.rewrite.ASTRewrite} for
 * an alternative way to describe and serialize changes to a
 * read-only AST.
 * </p>
 * <p>
 * Clients may create instances of this class using {@link #newAST(int)}, 
 * but this class is not intended to be subclassed.
 * </p>
 * 
 * @see ASTParser
 * @see ASTNode
 */
public final class AST {
	
	/**
	 * Constant for indicating the AST API that handles D1
	 * (D v1.0).
	 */
	public static final int D1 = 3;
	
	/**
	 * The event handler for this AST. 
	 * Initially an event handler that does not nothing.
	 */
	private NodeEventHandler eventHandler = new NodeEventHandler();
	
	/**
	 * Level of AST API supported by this AST.
	 */
	int apiLevel;
	
	/**
	 * Internal modification count; initially 0; increases monotonically
	 * <b>by one or more</b> as the AST is successively modified.
	 */
	private long modificationCount = 0;
	
	/**
	 * Internal original modification count; value is equals to <code>
	 * modificationCount</code> at the end of the parse (<code>ASTParser
	 * </code>). If this ast is not created with a parser then value is 0.
	 */
	private long originalModificationCount = 0;
	
	/**
	 * When disableEvents > 0, events are not reported and
	 * the modification count stays fixed.
	 * <p>
	 * This mechanism is used in lazy initialization of a node
	 * to prevent events from being reported for the modification
	 * of the node as well as for the creation of the missing child.
	 * </p>
	 */
	private int disableEvents = 0;
	
	/**
	 * Internal object unique to the AST instance. Readers must synchronize on
	 * this object when the modifying instance fields.
	 */
	private final Object internalASTLock = new Object();
	
	/**
	 * Java Scanner used to validate preconditions for the creation of specific nodes
	 * like CharacterLiteral, NumberLiteral, StringLiteral or SimpleName.
	 */
	IScanner scanner;
	
	/**
	 * Internal ast rewriter used to record ast modification when record mode is enabled.
	 */
	InternalASTRewrite rewriter;
	
	/**
	 * Default value of <code>flag<code> when a new node is created.
	 */
	private int defaultNodeFlag = 0;
	
	/**
	 * This flag is a workaround for the following problem:
	 * the parser build the AST nodes in a way that ASTNode.checkNewChild
	 * fails sometimes. This flag is set to true by ASTParser to
	 * bypass certain checks. After the parsing, the flag is set
	 * back to false.
	 */
	boolean internalParserMode = false;
	
	/**
	 * Creates a new Java abstract syntax tree
     * (AST) following the specified set of API rules. 
     * 
 	 * @param level the API level; one of the LEVEL constants
	 */
	private AST(int level) {
		if (level != AST.D1) {
			throw new IllegalArgumentException();
		}
		this.apiLevel = level;
		// initialize a scanner
		this.scanner = ToolFactory.createScanner(true, true, true, true, level);		
	}
	
	/**
	 * Creates a new Java abstract syntax tree
     * (AST) following the specified set of API rules. 
     * <p>
     * Clients should use this method specifing {@link #D1} as the
     * AST level in all cases, even when dealing with JDK 1.3 or 1.4..
     * </p>
     * 
 	 * @param level the API level; one of the LEVEL constants
	 * @return new AST instance following the specified set of API rules.
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the API level is not one of the LEVEL constants</li>
	 * </ul>
     * @since 3.0
	 */
	public static AST newAST(int level) {
		if (level != AST.D1) {
			throw new IllegalArgumentException();
		}
		return new AST(level);
	}
	
	/**
	 * Returns the modification count for this AST. The modification count
	 * is a non-negative value that increases (by 1 or perhaps by more) as
	 * this AST or its nodes are changed. The initial value is unspecified.
	 * <p>
	 * The following things count as modifying an AST:
	 * <ul>
	 * <li>creating a new node owned by this AST,</li>
	 * <li>adding a child to a node owned by this AST,</li>
	 * <li>removing a child from a node owned by this AST,</li>
	 * <li>setting a non-node attribute of a node owned by this AST.</li>
	 * </ul>
	 * </p>
	 * Operations which do not entail creating or modifying existing nodes
	 * do not increase the modification count.
	 * <p>
	 * N.B. This method may be called several times in the course
	 * of a single client operation. The only promise is that the modification
	 * count increases monotonically as the AST or its nodes change; there is 
	 * no promise that a modifying operation increases the count by exactly 1.
	 * </p>
	 * 
	 * @return the current value (non-negative) of the modification counter of
	 *    this AST
	 */
	public long modificationCount() {
		return this.modificationCount;
	}
	
	/**
	 * Return the API level supported by this AST.
	 * 
	 * @return level the API level; one of the <code>JLS*</code>LEVEL
     * declared on <code>AST</code>; assume this set is open-ended
	 */
	public int apiLevel() {
		return this.apiLevel;	
	}
	
	/**
	 * Indicates that this AST is about to be modified.
	 * <p>
	 * The following things count as modifying an AST:
	 * <ul>
	 * <li>creating a new node owned by this AST</li>
	 * <li>adding a child to a node owned by this AST</li>
	 * <li>removing a child from a node owned by this AST</li>
	 * <li>setting a non-node attribute of a node owned by this AST</li>.
	 * </ul>
	 * </p>
	 * <p>
	 * N.B. This method may be called several times in the course
	 * of a single client operation.
	 * </p> 
	 */
	void modifying() {
		// when this method is called during lazy init, events are disabled
		// and the modification count will not be increased
		if (this.disableEvents > 0) {
			return;
		}
		// increase the modification count
		this.modificationCount++;
	}
	
	/**
     * Disable events.
	 * This method is thread-safe for AST readers.
	 * 
	 * @see #reenableEvents()
     * @since 3.0
     */
	final void disableEvents() {
		synchronized (this.internalASTLock) {
			// guard against concurrent access by another reader
			this.disableEvents++;
		}
		// while disableEvents > 0 no events will be reported, and mod count will stay fixed
	}
	
	/**
     * Reenable events.
	 * This method is thread-safe for AST readers.
	 * 
	 * @see #disableEvents()
     * @since 3.0
     */
	final void reenableEvents() {
		synchronized (this.internalASTLock) {
			// guard against concurrent access by another reader
			this.disableEvents--;
		}
	}
	
	/**
	 * Reports that the given node is about to lose a child.
	 * 
	 * @param node the node about to be modified
	 * @param child the node about to be removed
	 * @param property the child or child list property descriptor
	 * @since 3.0
	 */
	void preRemoveChildEvent(ASTNode node, ASTNode child, StructuralPropertyDescriptor property) {
		// IMPORTANT: this method is called by readers during lazy init
		synchronized (this.internalASTLock) {
			// guard against concurrent access by a reader doing lazy init
			if (this.disableEvents > 0) {
				// doing lazy init OR already processing an event
				// System.out.println("[BOUNCE DEL]");
				return;
			} else {
				disableEvents();
			}
		}
		try {
			this.eventHandler.preRemoveChildEvent(node, child, property);
			// N.B. even if event handler blows up, the AST is not
			// corrupted since node has not been changed yet
		} finally {
			reenableEvents();
		}
	}
	
	/**
	 * Reports that the given node jsut lost a child.
	 * 
	 * @param node the node that was modified
	 * @param child the child node that was removed
	 * @param property the child or child list property descriptor
	 * @since 3.0
	 */
	void postRemoveChildEvent(ASTNode node, ASTNode child, StructuralPropertyDescriptor property) {
		// IMPORTANT: this method is called by readers during lazy init
		synchronized (this.internalASTLock) {
			// guard against concurrent access by a reader doing lazy init
			if (this.disableEvents > 0) {
				// doing lazy init OR already processing an event
				// System.out.println("[BOUNCE DEL]");
				return;
			} else {
				disableEvents();
			}
		}
		try {
			this.eventHandler.postRemoveChildEvent(node, child, property);
			// N.B. even if event handler blows up, the AST is not
			// corrupted since node has not been changed yet
		} finally {
			reenableEvents();
		}
	}
	
	/**
	 * Reports that the given node is about have a child replaced.
	 * 
	 * @param node the node about to be modified
	 * @param child the child node about to be removed
	 * @param newChild the replacement child
	 * @param property the child or child list property descriptor
	 * @since 3.0
	 */
	void preReplaceChildEvent(ASTNode node, ASTNode child, ASTNode newChild, StructuralPropertyDescriptor property) {
		// IMPORTANT: this method is called by readers during lazy init
		synchronized (this.internalASTLock) {
			// guard against concurrent access by a reader doing lazy init
			if (this.disableEvents > 0) {
				// doing lazy init OR already processing an event
				// System.out.println("[BOUNCE REP]");
				return;
			} else {
				disableEvents();
			}
		}
		try {
			this.eventHandler.preReplaceChildEvent(node, child, newChild, property);
			// N.B. even if event handler blows up, the AST is not
			// corrupted since node has not been changed yet
		} finally {
			reenableEvents();
		}
	}
	
	/**
	 * Reports that the given node has just had a child replaced.
	 * 
	 * @param node the node modified
	 * @param child the child removed
	 * @param newChild the replacement child
	 * @param property the child or child list property descriptor
	 * @since 3.0
	 */
	void postReplaceChildEvent(ASTNode node, ASTNode child, ASTNode newChild, StructuralPropertyDescriptor property) {
		// IMPORTANT: this method is called by readers during lazy init
		synchronized (this.internalASTLock) {
			// guard against concurrent access by a reader doing lazy init
			if (this.disableEvents > 0) {
				// doing lazy init OR already processing an event
				// System.out.println("[BOUNCE REP]");
				return;
			} else {
				disableEvents();
			}
		}
		try {
			this.eventHandler.postReplaceChildEvent(node, child, newChild, property);
			// N.B. even if event handler blows up, the AST is not
			// corrupted since node has not been changed yet
		} finally {
			reenableEvents();
		}
	}
	
	/**
	 * Reports that the given node is about to gain a child.
	 * 
	 * @param node the node that to be modified
	 * @param child the node that to be added as a child
	 * @param property the child or child list property descriptor
	 * @since 3.0
	 */
	void preAddChildEvent(ASTNode node, ASTNode child, StructuralPropertyDescriptor property) {
		// IMPORTANT: this method is called by readers during lazy init
		synchronized (this.internalASTLock) {
			// guard against concurrent access by a reader doing lazy init
			if (this.disableEvents > 0) {
				// doing lazy init OR already processing an event
				// System.out.println("[BOUNCE ADD]");
				return;
			} else {
				disableEvents();
			}
		}
		try {
			this.eventHandler.preAddChildEvent(node, child, property);
			// N.B. even if event handler blows up, the AST is not
			// corrupted since node has already been changed
		} finally {
			reenableEvents();
		}
	}
	
	/**
	 * Reports that the given node has just gained a child.
	 * 
	 * @param node the node that was modified
	 * @param child the node that was added as a child
	 * @param property the child or child list property descriptor
	 * @since 3.0
	 */
	void postAddChildEvent(ASTNode node, ASTNode child, StructuralPropertyDescriptor property) {
		// IMPORTANT: this method is called by readers during lazy init
		synchronized (this.internalASTLock) {
			// guard against concurrent access by a reader doing lazy init
			if (this.disableEvents > 0) {
				// doing lazy init OR already processing an event
				// System.out.println("[BOUNCE ADD]");
				return;
			} else {
				disableEvents();
			}
		}
		try {
			this.eventHandler.postAddChildEvent(node, child, property);
			// N.B. even if event handler blows up, the AST is not
			// corrupted since node has already been changed
		} finally {
			reenableEvents();
		}
	}
	
	/**
	 * Reports that the given node is about to change the value of a
	 * non-child property.
	 * 
	 * @param node the node to be modified
	 * @param property the property descriptor
	 */
	void preValueChangeEvent(ASTNode node, SimplePropertyDescriptor property) {
		// IMPORTANT: this method is called by readers during lazy init
		synchronized (this.internalASTLock) {
			// guard against concurrent access by a reader doing lazy init
			if (this.disableEvents > 0) {
				// doing lazy init OR already processing an event
				// System.out.println("[BOUNCE CHANGE]");
				return;
			} else {
				disableEvents();
			}
		}
		try {
			this.eventHandler.preValueChangeEvent(node, property);
			// N.B. even if event handler blows up, the AST is not
			// corrupted since node has already been changed
		} finally {
			reenableEvents();
		}
	}
	
	/**
	 * Reports that the given node has just changed the value of a
	 * non-child property.
	 * 
	 * @param node the node that was modified
	 * @param property the property descriptor
	 */
	void postValueChangeEvent(ASTNode node, SimplePropertyDescriptor property) {
		// IMPORTANT: this method is called by readers during lazy init
		synchronized (this.internalASTLock) {
			// guard against concurrent access by a reader doing lazy init
			if (this.disableEvents > 0) {
				// doing lazy init OR already processing an event
				// System.out.println("[BOUNCE CHANGE]");
				return;
			} else {
				disableEvents();
			}
		}
		try {
			this.eventHandler.postValueChangeEvent(node, property);
			// N.B. even if event handler blows up, the AST is not
			// corrupted since node has already been changed
		} finally {
			reenableEvents();
		}
	}
	
	/**
	 * Reports that the given node is about to be cloned.
	 * 
	 * @param node the node to be cloned
	 * @since 3.0
	 */
	void preCloneNodeEvent(ASTNode node) {
		synchronized (this.internalASTLock) {
			// guard against concurrent access by a reader doing lazy init
			if (this.disableEvents > 0) {
				// doing lazy init OR already processing an event
				// System.out.println("[BOUNCE CLONE]");
				return;
			} else {
				disableEvents();
			}
		}
		try {
			this.eventHandler.preCloneNodeEvent(node);
			// N.B. even if event handler blows up, the AST is not
			// corrupted since node has already been changed
		} finally {
			reenableEvents();
		}
	}
	
	/**
	 * Reports that the given node has just been cloned.
	 * 
	 * @param node the node that was cloned
	 * @param clone the clone of <code>node</code>
	 * @since 3.0
	 */
	void postCloneNodeEvent(ASTNode node, ASTNode clone) {
		synchronized (this.internalASTLock) {
			// guard against concurrent access by a reader doing lazy init
			if (this.disableEvents > 0) {
				// doing lazy init OR already processing an event
				// System.out.println("[BOUNCE CLONE]");
				return;
			} else {
				disableEvents();
			}
		}
		try {
			this.eventHandler.postCloneNodeEvent(node, clone);
			// N.B. even if event handler blows up, the AST is not
			// corrupted since node has already been changed
		} finally {
			reenableEvents();
		}
	}
	
	/**
	 * Returns the binding resolver for this AST.
	 * 
	 * @return the binding resolver for this AST
	 */
	/* TODO JDT binding
	BindingResolver getBindingResolver() {
		return this.resolver;
	}
	*/
	
	/**
	 * Returns the event handler for this AST.
	 * 
	 * @return the event handler for this AST
	 */
	NodeEventHandler getEventHandler() {
		return this.eventHandler;
	}

	/**
	 * Sets the event handler for this AST.
	 * 
	 * @param eventHandler the event handler for this AST
	 */
	void setEventHandler(NodeEventHandler eventHandler) {
		if (this.eventHandler == null) {
			throw new IllegalArgumentException();
		}
		this.eventHandler = eventHandler;
	}
	
	/**
	 * Returns default node flags of new nodes of this AST.
	 * 
	 * @return the default node flags of new nodes of this AST
	 */
	int getDefaultNodeFlag() {
		return this.defaultNodeFlag;
	}
	
	/**
	 * Sets default node flags of new nodes of this AST.
	 * 
	 * @param flag node flags of new nodes of this AST
	 * @since 3.0
	 */
	void setDefaultNodeFlag(int flag) {
		this.defaultNodeFlag = flag;
	}
	
	/**
	 * Set <code>originalModificationCount</code> to the current modification count
	 */
	void setOriginalModificationCount(long count) {
		this.originalModificationCount = count;
	}

	/**
	 * new Class[] {AST.class}
	 */
	private static final Class[] AST_CLASS = new Class[] {AST.class};

	/**
	 * new Object[] {this}
	 */
	private final Object[] THIS_AST= new Object[] {this};
	
	/**
	 * Enables the recording of changes to the given compilation
	 * unit and its descendents. The compilation unit must have
	 * been created by <code>ASTParser</code> and still be in
	 * its original state. Once recording is on,
	 * arbitrary changes to the subtree rooted at the compilation
	 * unit are recorded internally. Once the modification has
	 * been completed, call <code>rewrite</code> to get an object
	 * representing the corresponding edits to the original 
	 * source code string.
	 *
	 * @exception IllegalArgumentException if this compilation unit is
	 * marked as unmodifiable, or if this compilation unit has already 
	 * been tampered with, or if recording has already been enabled,
	 * or if <code>root</code> is not owned by this AST
	 * @see CompilationUnit#recordModifications()
	 * @since 3.0
	 */
	void recordModifications(CompilationUnit root) {
		if(this.modificationCount != this.originalModificationCount) {
			throw new IllegalArgumentException("AST is already modified"); //$NON-NLS-1$
		} else if(this.rewriter  != null) {
			throw new IllegalArgumentException("AST modifications are already recorded"); //$NON-NLS-1$
		} else if((root.getFlags() & ASTNode.PROTECT) != 0) {
			throw new IllegalArgumentException("Root node is unmodifiable"); //$NON-NLS-1$
		} else if(root.getAST() != this) {
			throw new IllegalArgumentException("Root node is not owned by this ast"); //$NON-NLS-1$
		}
		
		this.rewriter = new InternalASTRewrite(root);
		this.setEventHandler(this.rewriter);
	}
	
	/**
	 * Converts all modifications recorded into an object
	 * representing the corresponding text edits to the
	 * given document containing the original source
	 * code for the compilation unit that gave rise to
	 * this AST.
	 * 
	 * @param document original document containing source code
	 * for the compilation unit
	 * @param options the table of formatter options
	 * (key type: <code>String</code>; value type: <code>String</code>);
	 * or <code>null</code> to use the standard global options
	 * {@link JavaCore#getOptions() JavaCore.getOptions()}.
	 * @return text edit object describing the changes to the
	 * document corresponding to the recorded AST modifications
	 * @exception IllegalArgumentException if the document passed is
	 * <code>null</code> or does not correspond to this AST
	 * @exception IllegalStateException if <code>recordModifications</code>
	 * was not called to enable recording
	 * @see CompilationUnit#rewrite(IDocument, Map)
	 * @since 3.0
	 */
	TextEdit rewrite(IDocument document, Map options) {
		if (document == null) {
			throw new IllegalArgumentException();
		}
		if (this.rewriter  == null) {
			throw new IllegalStateException("Modifications record is not enabled"); //$NON-NLS-1$
		}
		return this.rewriter.rewriteAST(document, options);
	}
	
	/**
	 * Creates an unparented node of the given node class
	 * (non-abstract subclass of {@link ASTNode}). 
	 * 
	 * @param nodeClass AST node class
	 * @return a new unparented node owned by this AST
	 * @exception IllegalArgumentException if <code>nodeClass</code> is 
	 * <code>null</code> or is not a concrete node type class
	 */
	public ASTNode createInstance(Class nodeClass) {
		if (nodeClass == null) {
			throw new IllegalArgumentException();
		}
		try {
			// invoke constructor with signature Foo(AST)
			Constructor c = nodeClass.getDeclaredConstructor(AST_CLASS);
			Object result = c.newInstance(this.THIS_AST);
			return (ASTNode) result;
		} catch (NoSuchMethodException e) {
			// all AST node classes have a Foo(AST) constructor
			// therefore nodeClass is not legit
			throw new IllegalArgumentException();
		} catch (InstantiationException e) {
			// all concrete AST node classes can be instantiated
			// therefore nodeClass is not legit
			throw new IllegalArgumentException();
		} catch (IllegalAccessException e) {
			// all AST node classes have an accessible Foo(AST) constructor
			// therefore nodeClass is not legit
			throw new IllegalArgumentException();
		} catch (InvocationTargetException e) {
			// concrete AST node classes do not die in the constructor
			// therefore nodeClass is not legit
			throw new IllegalArgumentException();
		}		
	}
	
	/**
	 * Creates an unparented node of the given node type.
	 * This convenience method is equivalent to:
	 * <pre>
	 * createInstance(ASTNode.nodeClassForType(nodeType))
	 * </pre>
	 * 
	 * @param nodeType AST node type, one of the node type
	 * constants declared on {@link ASTNode}
	 * @return a new unparented node owned by this AST
	 * @exception IllegalArgumentException if <code>nodeType</code> is 
	 * not a legal AST node type
	 */
	public ASTNode createInstance(int nodeType) {
		// nodeClassForType throws IllegalArgumentException if nodeType is bogus
		Class nodeClass = ASTNode.nodeClassForType(nodeType);
		return createInstance(nodeClass);
	}
	
	// ============================ new methods ================================
	
	/**
	 * Creates an unparented aggregate declaration node owned by this AST.
	 * 
	 * @return the new unparented aggregate declaration node
	 */
	public AggregateDeclaration newAggregateDeclaration() {
		AggregateDeclaration node = new AggregateDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented alias declaration node owned by this AST, with the given fragment.
	 * 
	 * @return the new unparented alias declaration node
	 */
	public AliasDeclaration newAliasDeclaration(AliasDeclarationFragment fragment) {
		if (fragment == null) {
			throw new IllegalArgumentException();
		}
		
		AliasDeclaration node = new AliasDeclaration(this);
		node.fragments().add(fragment);
		return node;
	}

	/**
	 * Creates an unparented alias declaration fragment node owned by this AST, with the
	 * given name.
	 * 
	 * @return the new unparented alias declaration fragment node
	 */
	public AliasDeclarationFragment newAliasDeclarationFragment(SimpleName name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		
		AliasDeclarationFragment node = new AliasDeclarationFragment(this);
		node.setName(name);
		return node;
	}

	/**
	 * Creates an unparented alias template parameter node owned by this AST.
	 * 
	 * @return the new unparented alias template parameter node
	 */
	public AliasTemplateParameter newAliasTemplateParameter() {
		AliasTemplateParameter node = new AliasTemplateParameter(this);
		return node;
	}

	/**
	 * Creates an unparented align declaration node owned by this AST.
	 * 
	 * @return the new unparented align declaration node
	 */
	public AlignDeclaration newAlignDeclaration() {
		AlignDeclaration node = new AlignDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented argument node owned by this AST.
	 * 
	 * @return the new unparented argument node
	 */
	public Argument newArgument() {
		Argument node = new Argument(this);
		return node;
	}

	/**
	 * Creates an unparented array access node owned by this AST.
	 * 
	 * @return the new unparented array access node
	 */
	public ArrayAccess newArrayAccess() {
		ArrayAccess node = new ArrayAccess(this);
		return node;
	}

	/**
	 * Creates an unparented array initializer node owned by this AST.
	 * 
	 * @return the new unparented array initializer node
	 */
	public ArrayInitializer newArrayInitializer() {
		ArrayInitializer node = new ArrayInitializer(this);
		return node;
	}

	/**
	 * Creates an unparented array initializer fragment node owned by this AST.
	 * 
	 * @return the new unparented array initializer fragment node
	 */
	public ArrayInitializerFragment newArrayInitializerFragment() {
		ArrayInitializerFragment node = new ArrayInitializerFragment(this);
		return node;
	}

	/**
	 * Creates an unparented array literal node owned by this AST.
	 * 
	 * @return the new unparented array literal node
	 */
	public ArrayLiteral newArrayLiteral() {
		ArrayLiteral node = new ArrayLiteral(this);
		return node;
	}
	
	/**
	 * Creates an unparented asm block node owned by this AST.
	 * 
	 * @return the new unparented asm block node
	 */
	public AsmBlock newAsmBlock() {
		AsmBlock node = new AsmBlock(this);
		return node;
	}

	/**
	 * Creates an unparented asm statement node owned by this AST.
	 * 
	 * @return the new unparented asm statement node
	 */
	public AsmStatement newAsmStatement() {
		AsmStatement node = new AsmStatement(this);
		return node;
	}

	/**
	 * Creates an unparented asm token node owned by this AST.
	 * 
	 * @return the new unparented asm token node
	 */
	public AsmToken newAsmToken(String token) {
		AsmToken node = new AsmToken(this);
		node.setToken(token);
		return node;
	}

	/**
	 * Creates an unparented assert expression node owned by this AST.
	 * 
	 * @return the new unparented assert expression node
	 */
	public AssertExpression newAssertExpression() {
		AssertExpression node = new AssertExpression(this);
		return node;
	}

	/**
	 * Creates an unparented assignment node owned by this AST.
	 * 
	 * @return the new unparented assignment node
	 */
	public Assignment newAssignment() {
		Assignment node = new Assignment(this);
		return node;
	}

	/**
	 * Creates an unparented associative array type node owned by this AST.
	 * 
	 * @return the new unparented associative array type node
	 */
	public AssociativeArrayType newAssociativeArrayType() {
		AssociativeArrayType node = new AssociativeArrayType(this);
		return node;
	}

	/**
	 * Creates an unparented base class node owned by this AST.
	 * 
	 * @return the new unparented base class node
	 */
	public BaseClass newBaseClass() {
		BaseClass node = new BaseClass(this);
		return node;
	}

	/**
	 * Creates an unparented block node owned by this AST.
	 * 
	 * @return the new unparented block node
	 */
	public Block newBlock() {
		Block node = new Block(this);
		return node;
	}

	/**
	 * Creates an unparented boolean literal node owned by this AST.
	 * 
	 * @return the new unparented boolean literal node
	 */
	public BooleanLiteral newBooleanLiteral() {
		BooleanLiteral node = new BooleanLiteral(this);
		return node;
	}

	/**
	 * Creates an unparented break statement node owned by this AST.
	 * 
	 * @return the new unparented break statement node
	 */
	public BreakStatement newBreakStatement() {
		BreakStatement node = new BreakStatement(this);
		return node;
	}

	/**
	 * Creates an unparented call expression node owned by this AST.
	 * 
	 * @return the new unparented call expression node
	 */
	public CallExpression newCallExpression() {
		CallExpression node = new CallExpression(this);
		return node;
	}

	/**
	 * Creates an unparented cast expression node owned by this AST.
	 * 
	 * @return the new unparented cast expression node
	 */
	public CastExpression newCastExpression() {
		CastExpression node = new CastExpression(this);
		return node;
	}

	/**
	 * Creates an unparented catch clause node owned by this AST.
	 * 
	 * @return the new unparented catch clause node
	 */
	public CatchClause newCatchClause() {
		CatchClause node = new CatchClause(this);
		return node;
	}

	/**
	 * Creates an unparented character literal node owned by this AST.
	 * 
	 * @return the new unparented character literal node
	 */
	public CharacterLiteral newCharacterLiteral() {
		CharacterLiteral node = new CharacterLiteral(this);
		return node;
	}

	/**
	 * Creates an unparented code comment node owned by this AST.
	 * 
	 * @return the new unparented code comment node
	 */
	public CodeComment newCodeComment() {
		CodeComment node = new CodeComment(this);
		return node;
	}

	/**
	 * Creates an unparented compilation unit node owned by this AST.
	 * 
	 * @return the new unparented compilation unit node
	 */
	public CompilationUnit newCompilationUnit() {
		CompilationUnit node = new CompilationUnit(this);
		return node;
	}

	/**
	 * Creates an unparented conditional expression node owned by this AST.
	 * 
	 * @return the new unparented conditional expression node
	 */
	public ConditionalExpression newConditionalExpression() {
		ConditionalExpression node = new ConditionalExpression(this);
		return node;
	}
	
	/**
	 * Creates an unparented constructor declaration node owned by this AST.
	 * 
	 * @return the new unparented constructor declaration node
	 */
	public ConstructorDeclaration newConstructorDeclaration() {
		ConstructorDeclaration node = new ConstructorDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented continue statement node owned by this AST.
	 * 
	 * @return the new unparented continue statement node
	 */
	public ContinueStatement newContinueStatement() {
		ContinueStatement node = new ContinueStatement(this);
		return node;
	}

	/**
	 * Creates an unparented d doc comment node owned by this AST.
	 * 
	 * @return the new unparented d doc comment node
	 */
	public DDocComment newDDocComment(String text) {
		DDocComment node = new DDocComment(this);
		node.setText(text);
		return node;
	}

	/**
	 * Creates an unparented debug assignment node owned by this AST.
	 * 
	 * @return the new unparented debug assignment node
	 */
	public DebugAssignment newDebugAssignment() {
		DebugAssignment node = new DebugAssignment(this);
		return node;
	}

	/**
	 * Creates an unparented debug declaration node owned by this AST.
	 * 
	 * @return the new unparented debug declaration node
	 */
	public DebugDeclaration newDebugDeclaration() {
		DebugDeclaration node = new DebugDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented debug statement node owned by this AST.
	 * 
	 * @return the new unparented debug statement node
	 */
	public DebugStatement newDebugStatement() {
		DebugStatement node = new DebugStatement(this);
		return node;
	}

	/**
	 * Creates an unparented declaration statement node owned by this AST.
	 * 
	 * @return the new unparented declaration statement node
	 */
	public DeclarationStatement newDeclarationStatement() {
		DeclarationStatement node = new DeclarationStatement(this);
		return node;
	}

	/**
	 * Creates an unparented default statement node owned by this AST.
	 * 
	 * @return the new unparented default statement node
	 */
	public DefaultStatement newDefaultStatement() {
		DefaultStatement node = new DefaultStatement(this);
		return node;
	}

	/**
	 * Creates an unparented delegate type node owned by this AST.
	 * 
	 * @return the new unparented delegate type node
	 */
	public DelegateType newDelegateType() {
		DelegateType node = new DelegateType(this);
		return node;
	}

	/**
	 * Creates an unparented delete expression node owned by this AST.
	 * 
	 * @return the new unparented delete expression node
	 */
	public DeleteExpression newDeleteExpression() {
		DeleteExpression node = new DeleteExpression(this);
		return node;
	}

	/**
	 * Creates an unparented dollar literal node owned by this AST.
	 * 
	 * @return the new unparented dollar literal node
	 */
	public DollarLiteral newDollarLiteral() {
		DollarLiteral node = new DollarLiteral(this);
		return node;
	}

	/**
	 * Creates an unparented do statement node owned by this AST.
	 * 
	 * @return the new unparented do statement node
	 */
	public DoStatement newDoStatement() {
		DoStatement node = new DoStatement(this);
		return node;
	}

	/**
	 * Creates an unparented dot identifier expression node owned by this AST.
	 * 
	 * @return the new unparented dot identifier expression node
	 */
	public DotIdentifierExpression newDotIdentifierExpression() {
		DotIdentifierExpression node = new DotIdentifierExpression(this);
		return node;
	}

	/**
	 * Creates an unparented dot template type expression node owned by this AST.
	 * 
	 * @return the new unparented dot template type expression node
	 */
	public DotTemplateTypeExpression newDotTemplateTypeExpression() {
		DotTemplateTypeExpression node = new DotTemplateTypeExpression(this);
		return node;
	}

	/**
	 * Creates an unparented dynamic array type node owned by this AST.
	 * 
	 * @return the new unparented dynamic array type node
	 */
	public DynamicArrayType newDynamicArrayType() {
		DynamicArrayType node = new DynamicArrayType(this);
		return node;
	}

	/**
	 * Creates an unparented enum declaration node owned by this AST.
	 * 
	 * @return the new unparented enum declaration node
	 */
	public EnumDeclaration newEnumDeclaration() {
		EnumDeclaration node = new EnumDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented enum member node owned by this AST.
	 * 
	 * @return the new unparented enum member node
	 */
	public EnumMember newEnumMember() {
		EnumMember node = new EnumMember(this);
		return node;
	}

	/**
	 * Creates an unparented expression initializer node owned by this AST, with the
	 * given expression.
	 * 
	 * @return the new unparented expression initializer node
	 */
	public ExpressionInitializer newExpressionInitializer(Expression expression) {
		if (expression == null) {
			throw new IllegalArgumentException();
		}
		
		ExpressionInitializer node = new ExpressionInitializer(this);
		node.setExpression(expression);
		return node;
	}

	/**
	 * Creates an unparented expression statement node owned by this AST, with the
	 * given expression.
	 * 
	 * @return the new unparented expression statement node
	 */
	public ExpressionStatement newExpressionStatement(Expression expression) {
		if (expression == null) {
			throw new IllegalArgumentException();
		}
		
		ExpressionStatement node = new ExpressionStatement(this);
		node.setExpression(expression);
		return node;
	}

	/**
	 * Creates an unparented extern declaration node owned by this AST.
	 * 
	 * @return the new unparented extern declaration node
	 */
	public ExternDeclaration newExternDeclaration() {
		ExternDeclaration node = new ExternDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented foreach statement node owned by this AST.
	 * 
	 * @return the new unparented foreach statement node
	 */
	public ForeachStatement newForeachStatement() {
		ForeachStatement node = new ForeachStatement(this);
		return node;
	}

	/**
	 * Creates an unparented for statement node owned by this AST.
	 * 
	 * @return the new unparented for statement node
	 */
	public ForStatement newForStatement() {
		ForStatement node = new ForStatement(this);
		return node;
	}

	/**
	 * Creates an unparented function declaration node owned by this AST.
	 * 
	 * @return the new unparented function declaration node
	 */
	public FunctionDeclaration newFunctionDeclaration() {
		FunctionDeclaration node = new FunctionDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented function literal declaration expression node owned by this AST.
	 * 
	 * @return the new unparented function literal declaration expression node
	 */
	public FunctionLiteralDeclarationExpression newFunctionLiteralDeclarationExpression() {
		FunctionLiteralDeclarationExpression node = new FunctionLiteralDeclarationExpression(this);
		return node;
	}

	/**
	 * Creates an unparented goto case statement node owned by this AST.
	 * 
	 * @return the new unparented goto case statement node
	 */
	public GotoCaseStatement newGotoCaseStatement() {
		GotoCaseStatement node = new GotoCaseStatement(this);
		return node;
	}

	/**
	 * Creates an unparented goto default statement node owned by this AST.
	 * 
	 * @return the new unparented goto default statement node
	 */
	public GotoDefaultStatement newGotoDefaultStatement() {
		GotoDefaultStatement node = new GotoDefaultStatement(this);
		return node;
	}

	/**
	 * Creates an unparented goto statement node owned by this AST.
	 * 
	 * @return the new unparented goto statement node
	 */
	public GotoStatement newGotoStatement() {
		GotoStatement node = new GotoStatement(this);
		return node;
	}

	/**
	 * Creates an unparented if statement node owned by this AST.
	 * 
	 * @return the new unparented if statement node
	 */
	public IfStatement newIfStatement() {
		IfStatement node = new IfStatement(this);
		return node;
	}

	/**
	 * Creates an unparented iftype declaration node owned by this AST.
	 * 
	 * @return the new unparented iftype declaration node
	 */
	public IftypeDeclaration newIftypeDeclaration() {
		IftypeDeclaration node = new IftypeDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented iftype statement node owned by this AST.
	 * 
	 * @return the new unparented iftype statement node
	 */
	public IftypeStatement newIftypeStatement() {
		IftypeStatement node = new IftypeStatement(this);
		return node;
	}

	/**
	 * Creates an unparented import node owned by this AST.
	 * 
	 * @return the new unparented import node
	 */
	public Import newImport() {
		Import node = new Import(this);
		return node;
	}

	/**
	 * Creates an unparented import declaration node owned by this AST.
	 * 
	 * @return the new unparented import declaration node
	 */
	public ImportDeclaration newImportDeclaration() {
		ImportDeclaration node = new ImportDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented infix expression node owned by this AST.
	 * 
	 * @return the new unparented infix expression node
	 */
	public InfixExpression newInfixExpression() {
		InfixExpression node = new InfixExpression(this);
		return node;
	}

	/**
	 * Creates an unparented invariant declaration node owned by this AST.
	 * 
	 * @return the new unparented invariant declaration node
	 */
	public InvariantDeclaration newInvariantDeclaration() {
		InvariantDeclaration node = new InvariantDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented is type expression node owned by this AST.
	 * 
	 * @return the new unparented is type expression node
	 */
	public IsTypeExpression newIsTypeExpression() {
		IsTypeExpression node = new IsTypeExpression(this);
		return node;
	}

	/**
	 * Creates an unparented is type specialization expression node owned by this AST.
	 * 
	 * @return the new unparented is type specialization expression node
	 */
	public IsTypeSpecializationExpression newIsTypeSpecializationExpression() {
		IsTypeSpecializationExpression node = new IsTypeSpecializationExpression(this);
		return node;
	}

	/**
	 * Creates an unparented labeled statement node owned by this AST.
	 * 
	 * @return the new unparented labeled statement node
	 */
	public LabeledStatement newLabeledStatement() {
		LabeledStatement node = new LabeledStatement(this);
		return node;
	}

	/**
	 * Creates an unparented mixin declaration node owned by this AST.
	 * 
	 * @return the new unparented mixin declaration node
	 */
	public MixinDeclaration newMixinDeclaration() {
		MixinDeclaration node = new MixinDeclaration(this);
		return node;
	}

	/**
	 * Creates and returns a new unparented modifier node for the given
	 * modifier.
	 * 
	 * @param keyword one of the modifier keyword constants
	 * @return a new unparented modifier node
	 * @exception IllegalArgumentException if the primitive type code is invalid
	 * @exception UnsupportedOperationException if this operation is used in
	 * a JLS2 AST
	 * @since 3.1
	 */
	public Modifier newModifier(Modifier.ModifierKeyword keyword) {
		Modifier result = new Modifier(this);
		result.setModifierKeyword(keyword);
		return result;
	}

	/**
	 * Creates an unparented modifier declaration node owned by this AST.
	 * 
	 * @return the new unparented modifier declaration node
	 */
	public ModifierDeclaration newModifierDeclaration() {
		ModifierDeclaration node = new ModifierDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented module declaration node owned by this AST.
	 * 
	 * @return the new unparented module declaration node
	 */
	public ModuleDeclaration newModuleDeclaration() {
		ModuleDeclaration node = new ModuleDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented new anonymous class expression node owned by this AST.
	 * 
	 * @return the new unparented new anonymous class expression node
	 */
	public NewAnonymousClassExpression newNewAnonymousClassExpression() {
		NewAnonymousClassExpression node = new NewAnonymousClassExpression(this);
		return node;
	}

	/**
	 * Creates an unparented new expression node owned by this AST.
	 * 
	 * @return the new unparented new expression node
	 */
	public NewExpression newNewExpression() {
		NewExpression node = new NewExpression(this);
		return node;
	}

	/**
	 * Creates an unparented null literal node owned by this AST.
	 * 
	 * @return the new unparented null literal node
	 */
	public NullLiteral newNullLiteral() {
		NullLiteral node = new NullLiteral(this);
		return node;
	}

	/**
	 * Creates and returns a new unparented number literal node.
	 * 
	 * @param literal the token for the numeric literal as it would 
	 *    appear in Java source code
	 * @return a new unparented number literal node
	 * @exception IllegalArgumentException if the literal is null
	 */
	public NumberLiteral newNumberLiteral(String literal) {
		if (literal == null) {
			throw new IllegalArgumentException();
		}
		NumberLiteral result = new NumberLiteral(this);
		result.setToken(literal);
		return result;
	}

	/**
	 * Creates an unparented parenthesized expression node owned by this AST.
	 * 
	 * @return the new unparented parenthesized expression node
	 */
	public ParenthesizedExpression newParenthesizedExpression() {
		ParenthesizedExpression node = new ParenthesizedExpression(this);
		return node;
	}

	/**
	 * Creates an unparented pointer type node owned by this AST.
	 * 
	 * @return the new unparented pointer type node
	 */
	public PointerType newPointerType() {
		PointerType node = new PointerType(this);
		return node;
	}

	/**
	 * Creates an unparented postfix expression node owned by this AST.
	 * 
	 * @return the new unparented postfix expression node
	 */
	public PostfixExpression newPostfixExpression() {
		PostfixExpression node = new PostfixExpression(this);
		return node;
	}

	/**
	 * Creates an unparented pragma node owned by this AST.
	 * 
	 * @return the new unparented pragma node
	 */
	public Pragma newPragma() {
		Pragma node = new Pragma(this);
		return node;
	}

	/**
	 * Creates an unparented pragma declaration node owned by this AST.
	 * 
	 * @return the new unparented pragma declaration node
	 */
	public PragmaDeclaration newPragmaDeclaration() {
		PragmaDeclaration node = new PragmaDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented pragma statement node owned by this AST.
	 * 
	 * @return the new unparented pragma statement node
	 */
	public PragmaStatement newPragmaStatement() {
		PragmaStatement node = new PragmaStatement(this);
		return node;
	}

	/**
	 * Creates an unparented prefix expression node owned by this AST.
	 * 
	 * @return the new unparented prefix expression node
	 */
	public PrefixExpression newPrefixExpression() {
		PrefixExpression node = new PrefixExpression(this);
		return node;
	}

	/**
	 * Creates and returns a new unparented primitive type node with the given
	 * type code.
	 * 
	 * @param typeCode one of the primitive type code constants declared in 
	 *    <code>PrimitiveType</code>
	 * @return a new unparented primitive type node
	 * @exception IllegalArgumentException if the primitive type code is invalid
	 */
	public PrimitiveType newPrimitiveType(PrimitiveType.Code typeCode) {
		PrimitiveType result = new PrimitiveType(this);
		result.setPrimitiveTypeCode(typeCode);
		return result;
	}

	/**
	 * Creates and returns a new unparented qualified name node for the given 
	 * qualifier and simple name child node.
	 * 
	 * @param qualifier the qualifier name node
	 * @param name the simple name being qualified
	 * @return a new unparented qualified name node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * </ul>
	 */
	public QualifiedName newQualifiedName(
		Name qualifier,
		SimpleName name) {
		QualifiedName result = new QualifiedName(this);
		result.setQualifier(qualifier);
		result.setName(name);
		return result;
		
	}
	
	/**
	 * Creates and returns a new unparented name node for the given name 
	 * segments. Returns a simple name if there is only one name segment, and
	 * a qualified name if there are multiple name segments. Each of the name
	 * segments should be legal Java identifiers (this constraint may or may 
	 * not be enforced), and there must be at least one name segment.
	 * 
	 * @param identifiers a list of 1 or more name segments, each of which
	 *    is a legal Java identifier
	 * @return a new unparented name node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the identifier is invalid</li>
	 * <li>the list of identifiers is empty</li>
	 * </ul>
	 */
	public Name newName(String[] identifiers) {
		// update internalSetName(String[] if changed
		int count = identifiers.length;
		if (count == 0) {
			throw new IllegalArgumentException();
		}
		Name result = newSimpleName(identifiers[0]);
		for (int i = 1; i < count; i++) {
			SimpleName name = newSimpleName(identifiers[i]);
			result = newQualifiedName(result, name);
		}
		return result;
	}
	
	/**
	 * Creates and returns a new unparented name node for the given name.
	 * The name string must consist of 1 or more name segments separated 
	 * by single dots '.'. Returns a {@link QualifiedName} if the name has
	 * dots, and a {@link SimpleName} otherwise. Each of the name
	 * segments should be legal Java identifiers (this constraint may or may 
	 * not be enforced), and there must be at least one name segment.
	 * The string must not contains white space, '&lt;', '&gt;',
	 * '[', ']', or other any other characters that are not
	 * part of the Java identifiers or separating '.'s.
	 * 
	 * @param qualifiedName string consisting of 1 or more name segments,
	 * each of which is a legal Java identifier, separated  by single dots '.'
	 * @return a new unparented name node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the string is empty</li>
	 * <li>the string begins or ends in a '.'</li>
	 * <li>the string has adjacent '.'s</li>
	 * <li>the segments between the '.'s are not valid Java identifiers</li>
	 * </ul>
	 * @since 3.1
	 */
	public Name newName(String qualifiedName) {
		StringTokenizer t = new StringTokenizer(qualifiedName, ".", true); //$NON-NLS-1$
		Name result = null;
		// balance is # of name tokens - # of period tokens seen so far
		// initially 0; finally 1; should never drop < 0 or > 1
		int balance = 0;
		while(t.hasMoreTokens()) {
			String s = t.nextToken();
			if (s.indexOf('.') >= 0) {
				// this is a delimiter
				if (s.length() > 1) {
					// too many dots in a row
					throw new IllegalArgumentException();
				}
				balance--;
				if (balance < 0) {
					throw new IllegalArgumentException();
				}
			} else {
				// this is an identifier segment
				balance++;
				SimpleName name = newSimpleName(s);
				if (result == null) {
					result = name;
				} else {
					result = newQualifiedName(result, name);
				}
			}
		}
		if (balance != 1) {
			throw new IllegalArgumentException();
		}
		return result;
	}

	/**
	 * Creates an unparented qualified type node owned by this AST.
	 * 
	 * @return the new unparented qualified type node
	 */
	public QualifiedType newQualifiedType() {
		QualifiedType node = new QualifiedType(this);
		return node;
	}

	/**
	 * Creates an unparented return statement node owned by this AST.
	 * 
	 * @return the new unparented return statement node
	 */
	public ReturnStatement newReturnStatement() {
		ReturnStatement node = new ReturnStatement(this);
		return node;
	}

	/**
	 * Creates an unparented scope statement node owned by this AST.
	 * 
	 * @return the new unparented scope statement node
	 */
	public ScopeStatement newScopeStatement() {
		ScopeStatement node = new ScopeStatement(this);
		return node;
	}

	/**
	 * Creates an unparented selective import node owned by this AST.
	 * 
	 * @return the new unparented selective import node
	 */
	public SelectiveImport newSelectiveImport() {
		SelectiveImport node = new SelectiveImport(this);
		return node;
	}

	/**
	 * Creates and returns a new unparented simple name node for the given
	 * identifier. The identifier should be a legal Java identifier, but not
	 * a keyword, boolean literal ("true", "false") or null literal ("null").
	 * 
	 * @param identifier the identifier
	 * @return a new unparented simple name node
	 * @exception IllegalArgumentException if the identifier is invalid
	 */
	public SimpleName newSimpleName(String identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException();
		}
		SimpleName result = new SimpleName(this);
		result.setIdentifier(identifier);
		return result;
	}

	/**
	 * Creates and returns a new unparented simple type node with the given
	 * type name.
	 * <p>
	 * This method can be used to convert a name (<code>Name</code>) into a
	 * type (<code>Type</code>) by wrapping it.
	 * </p>
	 * 
	 * @param typeName the name of the class or interface
	 * @return a new unparented simple type node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * </ul>
	 */
	public SimpleType newSimpleType(SimpleName typeName) {
		SimpleType result = new SimpleType(this);
		result.setName(typeName);
		return result;
	}

	/**
	 * Creates an unparented slice expression node owned by this AST.
	 * 
	 * @return the new unparented slice expression node
	 */
	public SliceExpression newSliceExpression() {
		SliceExpression node = new SliceExpression(this);
		return node;
	}

	/**
	 * Creates an unparented slice type node owned by this AST.
	 * 
	 * @return the new unparented slice type node
	 */
	public SliceType newSliceType() {
		SliceType node = new SliceType(this);
		return node;
	}

	/**
	 * Creates an unparented static array type node owned by this AST.
	 * 
	 * @return the new unparented static array type node
	 */
	public StaticArrayType newStaticArrayType() {
		StaticArrayType node = new StaticArrayType(this);
		return node;
	}

	/**
	 * Creates an unparented static assert node owned by this AST.
	 * 
	 * @return the new unparented static assert node
	 */
	public StaticAssert newStaticAssert() {
		StaticAssert node = new StaticAssert(this);
		return node;
	}

	/**
	 * Creates an unparented static assert statement node owned by this AST.
	 * 
	 * @return the new unparented static assert statement node
	 */
	public StaticAssertStatement newStaticAssertStatement() {
		StaticAssertStatement node = new StaticAssertStatement(this);
		return node;
	}

	/**
	 * Creates an unparented static if declaration node owned by this AST.
	 * 
	 * @return the new unparented static if declaration node
	 */
	public StaticIfDeclaration newStaticIfDeclaration() {
		StaticIfDeclaration node = new StaticIfDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented static if statement node owned by this AST.
	 * 
	 * @return the new unparented static if statement node
	 */
	public StaticIfStatement newStaticIfStatement() {
		StaticIfStatement node = new StaticIfStatement(this);
		return node;
	}

	/**
	 * Creates an unparented string literal node owned by this AST.
	 * 
	 * @return the new unparented string literal node
	 */
	public StringLiteral newStringLiteral() {
		StringLiteral node = new StringLiteral(this);
		return node;
	}

	/**
	 * Creates an unparented strings expression node owned by this AST.
	 * 
	 * @return the new unparented strings expression node
	 */
	public StringsExpression newStringsExpression() {
		StringsExpression node = new StringsExpression(this);
		return node;
	}

	/**
	 * Creates an unparented struct initializer node owned by this AST.
	 * 
	 * @return the new unparented struct initializer node
	 */
	public StructInitializer newStructInitializer() {
		StructInitializer node = new StructInitializer(this);
		return node;
	}

	/**
	 * Creates an unparented struct initializer fragment node owned by this AST.
	 * 
	 * @return the new unparented struct initializer fragment node
	 */
	public StructInitializerFragment newStructInitializerFragment() {
		StructInitializerFragment node = new StructInitializerFragment(this);
		return node;
	}

	/**
	 * Creates an unparented super literal node owned by this AST.
	 * 
	 * @return the new unparented super literal node
	 */
	public SuperLiteral newSuperLiteral() {
		SuperLiteral node = new SuperLiteral(this);
		return node;
	}

	/**
	 * Creates an unparented switch case node owned by this AST.
	 * 
	 * @return the new unparented switch case node
	 */
	public SwitchCase newSwitchCase() {
		SwitchCase node = new SwitchCase(this);
		return node;
	}

	/**
	 * Creates an unparented switch statement node owned by this AST.
	 * 
	 * @return the new unparented switch statement node
	 */
	public SwitchStatement newSwitchStatement() {
		SwitchStatement node = new SwitchStatement(this);
		return node;
	}

	/**
	 * Creates an unparented synchronized statement node owned by this AST.
	 * 
	 * @return the new unparented synchronized statement node
	 */
	public SynchronizedStatement newSynchronizedStatement() {
		SynchronizedStatement node = new SynchronizedStatement(this);
		return node;
	}

	/**
	 * Creates an unparented template declaration node owned by this AST.
	 * 
	 * @return the new unparented template declaration node
	 */
	public TemplateDeclaration newTemplateDeclaration() {
		TemplateDeclaration node = new TemplateDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented template type node owned by this AST.
	 * 
	 * @return the new unparented template type node
	 */
	public TemplateType newTemplateType() {
		TemplateType node = new TemplateType(this);
		return node;
	}

	/**
	 * Creates an unparented this literal node owned by this AST.
	 * 
	 * @return the new unparented this literal node
	 */
	public ThisLiteral newThisLiteral() {
		ThisLiteral node = new ThisLiteral(this);
		return node;
	}

	/**
	 * Creates an unparented throw statement node owned by this AST.
	 * 
	 * @return the new unparented throw statement node
	 */
	public ThrowStatement newThrowStatement() {
		ThrowStatement node = new ThrowStatement(this);
		return node;
	}

	/**
	 * Creates an unparented try statement node owned by this AST.
	 * 
	 * @return the new unparented try statement node
	 */
	public TryStatement newTryStatement() {
		TryStatement node = new TryStatement(this);
		return node;
	}

	/**
	 * Creates an unparented tuple template parameter node owned by this AST.
	 * 
	 * @return the new unparented tuple template parameter node
	 */
	public TupleTemplateParameter newTupleTemplateParameter() {
		TupleTemplateParameter node = new TupleTemplateParameter(this);
		return node;
	}

	/**
	 * Creates an unparented typedef declaration node owned by this AST, with the given
	 * fragment.
	 * 
	 * @return the new unparented typedef declaration node
	 */
	public TypedefDeclaration newTypedefDeclaration(TypedefDeclarationFragment fragment) {
		if (fragment == null) {
			throw new IllegalArgumentException();
		}
		
		TypedefDeclaration node = new TypedefDeclaration(this);
		node.fragments().add(fragment);
		return node;
	}

	/**
	 * Creates an unparented typedef declaration fragment node owned by this AST.
	 * 
	 * @return the new unparented typedef declaration fragment node
	 */
	public TypedefDeclarationFragment newTypedefDeclarationFragment() {
		TypedefDeclarationFragment node = new TypedefDeclarationFragment(this);
		return node;
	}

	/**
	 * Creates an unparented type dot identifier expression node owned by this AST.
	 * 
	 * @return the new unparented type dot identifier expression node
	 */
	public TypeDotIdentifierExpression newTypeDotIdentifierExpression() {
		TypeDotIdentifierExpression node = new TypeDotIdentifierExpression(this);
		return node;
	}

	/**
	 * Creates an unparented type expression node owned by this AST.
	 * 
	 * @return the new unparented type expression node
	 */
	public TypeExpression newTypeExpression() {
		TypeExpression node = new TypeExpression(this);
		return node;
	}

	/**
	 * Creates an unparented typeid expression node owned by this AST.
	 * 
	 * @return the new unparented typeid expression node
	 */
	public TypeidExpression newTypeidExpression() {
		TypeidExpression node = new TypeidExpression(this);
		return node;
	}

	/**
	 * Creates an unparented typeof type node owned by this AST.
	 * 
	 * @return the new unparented typeof type node
	 */
	public TypeofType newTypeofType() {
		TypeofType node = new TypeofType(this);
		return node;
	}

	/**
	 * Creates an unparented type template parameter node owned by this AST.
	 * 
	 * @return the new unparented type template parameter node
	 */
	public TypeTemplateParameter newTypeTemplateParameter() {
		TypeTemplateParameter node = new TypeTemplateParameter(this);
		return node;
	}

	/**
	 * Creates an unparented unit test declaration node owned by this AST.
	 * 
	 * @return the new unparented unit test declaration node
	 */
	public UnitTestDeclaration newUnitTestDeclaration() {
		UnitTestDeclaration node = new UnitTestDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented value template parameter node owned by this AST.
	 * 
	 * @return the new unparented value template parameter node
	 */
	public ValueTemplateParameter newValueTemplateParameter() {
		ValueTemplateParameter node = new ValueTemplateParameter(this);
		return node;
	}

	/**
	 * Creates an unparented variable declaration node owned by this AST, with the
	 * given fragment.
	 * 
	 * @return the new unparented variable declaration node
	 */
	public VariableDeclaration newVariableDeclaration(VariableDeclarationFragment fragment) {
		if (fragment == null) {
			throw new IllegalArgumentException();
		}
		
		VariableDeclaration node = new VariableDeclaration(this);
		node.fragments().add(fragment);
		return node;
	}
	
	/**
	 * Creates an unparented variable declaration fragment node owned by this AST.
	 * 
	 * @return the new unparented variable declaration fragment node
	 */
	public VariableDeclarationFragment newVariableDeclarationFragment() {
		VariableDeclarationFragment node = new VariableDeclarationFragment(this);
		return node;
	}

	/**
	 * Creates an unparented variable declaration fragment node owned by this AST, with the
	 * given name.
	 * 
	 * @return the new unparented variable declaration fragment node
	 */
	public VariableDeclarationFragment newVariableDeclarationFragment(SimpleName name) {
		VariableDeclarationFragment node = new VariableDeclarationFragment(this);
		node.setName(name);
		return node;
	}

	/**
	 * Creates an unparented version node owned by this AST, with the given value.
	 * 
	 * @return the new unparented version node
	 */
	public Version newVersion(String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		
		Version node = new Version(this);
		node.setValue(value);
		return node;
	}

	/**
	 * Creates an unparented version assignment node owned by this AST.
	 * 
	 * @return the new unparented version assignment node
	 */
	public VersionAssignment newVersionAssignment() {
		VersionAssignment node = new VersionAssignment(this);
		return node;
	}

	/**
	 * Creates an unparented version declaration node owned by this AST.
	 * 
	 * @return the new unparented version declaration node
	 */
	public VersionDeclaration newVersionDeclaration() {
		VersionDeclaration node = new VersionDeclaration(this);
		return node;
	}

	/**
	 * Creates an unparented version statement node owned by this AST.
	 * 
	 * @return the new unparented version statement node
	 */
	public VersionStatement newVersionStatement() {
		VersionStatement node = new VersionStatement(this);
		return node;
	}

	/**
	 * Creates an unparented void initializer node owned by this AST.
	 * 
	 * @return the new unparented void initializer node
	 */
	public VoidInitializer newVoidInitializer() {
		VoidInitializer node = new VoidInitializer(this);
		return node;
	}

	/**
	 * Creates an unparented volatile statement node owned by this AST.
	 * 
	 * @return the new unparented volatile statement node
	 */
	public VolatileStatement newVolatileStatement() {
		VolatileStatement node = new VolatileStatement(this);
		return node;
	}

	/**
	 * Creates an unparented while statement node owned by this AST.
	 * 
	 * @return the new unparented while statement node
	 */
	public WhileStatement newWhileStatement() {
		WhileStatement node = new WhileStatement(this);
		return node;
	}

	/**
	 * Creates an unparented with statement node owned by this AST.
	 * 
	 * @return the new unparented with statement node
	 */
	public WithStatement newWithStatement() {
		WithStatement node = new WithStatement(this);
		return node;
	}

}
