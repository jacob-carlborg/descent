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
 * See also {@link descent.core.dom.rewrite.ASTRewrite} for
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
	public static final int D1 = 1;
	
	/**
	 * Constant for indicating the AST API that handles D1.x
	 * , where x > 0. This is the "in-development" version of D.
	 */
	public static final int D2 = 2;
	
	/**
	 * Constant for indicating the AST API that handles the latest version of D.
	 * Clients should not use this constant.
	 */
	public static final int LATEST = D2;
	

	
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
	 * Default value of <code>flag<code> when a new node is created.
	 */
	private int defaultNodeFlag = 0;
	

	/**
	 * Creates a new Java abstract syntax tree
     * (AST) following the specified set of API rules. 
     * 
 	 * @param level the API level; one of the LEVEL constants
	 */
	private AST(int level) {
		if (level != AST.D1 && level != AST.D2) {
			throw new IllegalArgumentException();
		}
		this.apiLevel = level;
		// initialize a scanner
	}
	
	/**
	 * Creates a new Java abstract syntax tree
     * (AST) following the specified set of API rules. 
     * <p>
     * Clients should use this method specifing {@link #LATEST} as the
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
		if (level != AST.D1 && level != AST.D2) {
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
	 * Creates an unparented code comment node owned by this AST.
	 * 
	 * @return the new unparented code comment node
	 */
	public CodeComment newCodeComment() {
		CodeComment node = new CodeComment(this);
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
	 * Creates an unparented pragma node owned by this AST.
	 * 
	 * @return the new unparented pragma node
	 */
	public Pragma newPragma() {
		Pragma node = new Pragma(this);
		return node;
	}



}
