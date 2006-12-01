package descent.internal.core.dom;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.IElement;

public abstract class ASTNode implements IElement {
	
	public final static IElement[] NO_ELEMENTS = new IElement[0];
	public final static IDeclaration[] NO_DECLARATIONS = new IDeclaration[0];
	
	public String comments;
	
	/**
	 * Owning AST.
     * <p>
     * N.B. This ia a private field, but declared as package-visible
     * for more efficient access from inner classes.
     * </p>
	 */
	final AST ast;
	
	/**
	 * Parent AST node, or <code>null</code> if this node is a root.
	 * Initially <code>null</code>.
	 */
	private ASTNode parent = null;
	
	/**
	 * An unmodifiable empty map (used to implement <code>properties()</code>).
	 */
	private static final Map<Object, Object> UNMODIFIABLE_EMPTY_MAP
		= Collections.unmodifiableMap(new HashMap<Object, Object>(1));
	
	/**
	 * Primary field used in representing node properties efficiently.
	 * If <code>null</code>, this node has no properties.
	 * If a <code>String</code>, this is the name of this node's sole property,
	 * and <code>property2</code> contains its value.
	 * If a <code>HashMap</code>, this is the table of property name-value
	 * mappings; <code>property2</code>, if non-null is its unmodifiable
	 * equivalent.
	 * Initially <code>null</code>.
	 * 
	 * @see #property2
	 */
	private Object property1 = null;
	
	/**
	 * Auxillary field used in representing node properties efficiently.
	 * 
	 * @see #property1
	 */
	private Object property2 = null;
	
	/**
	 * A character index into the original source string, 
	 * or <code>-1</code> if no source position information is available
	 * for this node; <code>-1</code> by default.
	 */
	public int startPosition = -1;
	
	/**
	 * A character length, or <code>0</code> if no source position
	 * information is recorded for this node; <code>0</code> by default.
	 */
	public int length = 0;
	
	/**
	 * Flag constant (bit mask, value 1) indicating that there is something
	 * not quite right with this AST node.
	 * <p>
	 * The standard parser (<code>ASTParser</code>) sets this
	 * flag on a node to indicate a syntax error detected in the vicinity.
	 * </p>
	 */
	public static final int MALFORMED = 1;

	/**
	 * Flag constant (bit mask, value 2) indicating that this is a node
	 * that was created by the parser (as opposed to one created by another
	 * party).
	 * <p>
	 * The standard parser (<code>ASTParser</code>) sets this
	 * flag on the nodes it creates.
	 * </p>
	 * @since 3.0
	 */
	public static final int ORIGINAL = 2;

	/**
	 * Flag constant (bit mask, value 4) indicating that this node
	 * is unmodifiable. When a node is marked unmodifiable, the
	 * following operations result in a runtime exception:
	 * <ul>
	 * <li>Change a simple property of this node.</li>
	 * <li>Add or remove a child node from this node.</li>
	 * <li>Parent (or reparent) this node.</li>
	 * </ul>
	 * <p>
	 * The standard parser (<code>ASTParser</code>) does not set
	 * this flag on the nodes it creates. However, clients may set
	 * this flag on a node to prevent further modification of the
	 * its structural properties.
	 * </p>
	 * @since 3.0
	 */
	public static final int PROTECT = 4;

	/**
	 * Flag constant (bit mask, value 8) indicating that this node
	 * or a part of this node is recovered from source that contains
	 * a syntax error detected in the vicinity.
	 * <p>
	 * The standard parser (<code>ASTParser</code>) sets this
	 * flag on a node to indicate a recovered node.
	 * </p>
	 * @since 3.2
	 */
	public static final int RECOVERED = 8;
	
	/**
	 * int containing the node type in the top 16 bits and
	 * flags in the bottom 16 bits; none set by default.
     * <p>
     * N.B. This is a private field, but declared as package-visible
     * for more efficient access from inner classes.
     * </p>
	 * 
	 * @see #MALFORMED
	 */
	int typeAndFlags = 0;
	
	/**
	 * Property of parent in which this node is a child, or <code>null</code>
	 * if this node is a root. Initially <code>null</code>.
	 * 
	 * @see #getLocationInParent
	 * @since 3.0
	 */
	private StructuralPropertyDescriptor location = null;
	
	/** Internal convenience constant indicating that there is definite risk of cycles.
	 * @since 3.0
	 */ 
	static final boolean CYCLE_RISK = true;
	
	/** Internal convenience constant indicating that there is no risk of cycles.
	 * @since 3.0
	 */ 
	static final boolean NO_CYCLE_RISK = false;
	
	/** Internal convenience constant indicating that a structural property is mandatory.
	 * @since 3.0
	 */ 
	static final boolean MANDATORY = true;
	
	/** Internal convenience constant indicating that a structural property is optional.
	 * @since 3.0
	 */ 
	static final boolean OPTIONAL = false;
	
	/**
	 * A specialized implementation of a list of ASTNodes. The
	 * implementation is based on an ArrayList.
	 */ 
	class NodeList extends AbstractList {
		
		/**
		 * The underlying list in which the nodes of this list are
		 * stored (element type: <code>ASTNode</code>).
		 * <p>
		 * Be stingy on storage - assume that list will be empty.
		 * </p>
		 * <p>
		 * This field declared default visibility (rather than private)
		 * so that accesses from <code>NodeList.Cursor</code> do not require
		 * a synthetic accessor method.
		 * </p>
		 */
		ArrayList store = new ArrayList(0);
		
		/**
		 * The property descriptor for this list.
		 */
		ChildListPropertyDescriptor propertyDescriptor;
		
		/**
		 * A cursor for iterating over the elements of the list.
		 * Does not lose its position if the list is changed during
		 * the iteration.
		 */
		class Cursor implements Iterator {
			/**
			 * The position of the cursor between elements. If the value
			 * is N, then the cursor sits between the element at positions
			 * N-1 and N. Initially just before the first element of the
			 * list.
			 */
			private int position = 0;
			
			/* (non-Javadoc)
			 * Method declared on <code>Iterator</code>.
			 */
			public boolean hasNext() {
				return this.position < NodeList.this.store.size();
			}
			
			/* (non-Javadoc)
			 * Method declared on <code>Iterator</code>.
			 */
			public Object next() {
				Object result = NodeList.this.store.get(this.position);
				this.position++;
				return result;
		    }
			
			/* (non-Javadoc)
			 * Method declared on <code>Iterator</code>.
			 */
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
			/**
			 * Adjusts this cursor to accomodate an add/remove at the given
			 * index.
			 * 
			 * @param index the position at which the element was added
			 *    or removed
			 * @param delta +1 for add, and -1 for remove
			 */
			void update(int index, int delta) {
				if (this.position > index) {
					// the cursor has passed the added or removed element
					this.position += delta;
				}
			}
		}

		/**
		 * A list of currently active cursors (element type:
		 * <code>Cursor</code>), or <code>null</code> if there are no
		 * active cursors.
		 * <p>
		 * It is important for storage considerations to maintain the
		 * null-means-empty invariant; otherwise, every NodeList instance
		 * will waste a lot of space. A cursor is needed only for the duration
		 * of a visit to the child nodes. Under normal circumstances, only a 
		 * single cursor is needed; multiple cursors are only required if there
		 * are multiple visits going on at the same time.
		 * </p>
		 */
		private List cursors = null;

		/**
		 * Creates a new empty list of nodes owned by this node.
		 * This node will be the common parent of all nodes added to 
		 * this list.
		 * 
		 * @param property the property descriptor
		 * @since 3.0
		 */
		NodeList(ChildListPropertyDescriptor property) {
			super();
			this.propertyDescriptor = property;
		}
	
		/* (non-javadoc)
		 * @see java.util.AbstractCollection#size()
		 */
		public int size() {
			return this.store.size();
		}
	
		/* (non-javadoc)
		 * @see AbstractList#get(int)
		 */
		public Object get(int index) {
			return this.store.get(index);
		}
	
		/* (non-javadoc)
		 * @see List#set(int, java.lang.Object)
		 */
		public Object set(int index, Object element) {
		    if (element == null) {
		        throw new IllegalArgumentException();
		    }
			if ((ASTNode.this.typeAndFlags & PROTECT) != 0) {
				// this node is protected => cannot gain or lose children
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			// delink old child from parent, and link new child to parent
			ASTNode newChild = (ASTNode) element;
			ASTNode oldChild = (ASTNode) this.store.get(index);
			if (oldChild == newChild) {
				return oldChild;
			}
			if ((oldChild.typeAndFlags & PROTECT) != 0) {
				// old child is protected => cannot be unparented
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			ASTNode.checkNewChild(ASTNode.this, newChild, this.propertyDescriptor.cycleRisk, this.propertyDescriptor.elementType);
			ASTNode.this.ast.preReplaceChildEvent(ASTNode.this, oldChild, newChild, this.propertyDescriptor);
			
			Object result = this.store.set(index, newChild);
			// n.b. setParent will call ast.modifying()
			oldChild.setParent(null, null);
			newChild.setParent(ASTNode.this, this.propertyDescriptor);
			ASTNode.this.ast.postReplaceChildEvent(ASTNode.this, oldChild, newChild, this.propertyDescriptor);
			return result;
		}
		
		/* (non-javadoc)
		 * @see List#add(int, java.lang.Object)
		 */
		public void add(int index, Object element) {
		    if (element == null) {
		        throw new IllegalArgumentException();
		    }
			if ((ASTNode.this.typeAndFlags & PROTECT) != 0) {
				// this node is protected => cannot gain or lose children
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			// link new child to parent
			ASTNode newChild = (ASTNode) element;
			ASTNode.checkNewChild(ASTNode.this, newChild, this.propertyDescriptor.cycleRisk, this.propertyDescriptor.elementType);
			ASTNode.this.ast.preAddChildEvent(ASTNode.this, newChild, this.propertyDescriptor);
			
			
			this.store.add(index, element);
			updateCursors(index, +1);
			// n.b. setParent will call ast.modifying()
			newChild.setParent(ASTNode.this, this.propertyDescriptor);
			ASTNode.this.ast.postAddChildEvent(ASTNode.this, newChild, this.propertyDescriptor);
		}
		
		/* (non-javadoc)
		 * @see List#remove(int)
		 */
		public Object remove(int index) {
			if ((ASTNode.this.typeAndFlags & PROTECT) != 0) {
				// this node is protected => cannot gain or lose children
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			// delink old child from parent
			ASTNode oldChild = (ASTNode) this.store.get(index);
			if ((oldChild.typeAndFlags & PROTECT) != 0) {
				// old child is protected => cannot be unparented
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			
			ASTNode.this.ast.preRemoveChildEvent(ASTNode.this, oldChild, this.propertyDescriptor);
			// n.b. setParent will call ast.modifying()
			oldChild.setParent(null, null);
			Object result = this.store.remove(index);
			updateCursors(index, -1);
			ASTNode.this.ast.postRemoveChildEvent(ASTNode.this, oldChild, this.propertyDescriptor);
			return result;

		}
		
		/**
		 * Allocate a cursor to use for a visit. The client must call
		 * <code>releaseCursor</code> when done.
		 * <p>
		 * This method is internally synchronized on this NodeList.
		 * It is thread-safe to create a cursor.
		 * </p>
		 * 
		 * @return a new cursor positioned before the first element 
		 *    of the list
		 */
		Cursor newCursor() {
			synchronized (this) {
				// serialize cursor management on this NodeList
				if (this.cursors == null) {
					// convert null to empty list
					this.cursors = new ArrayList(1);
				}
				Cursor result = new Cursor();
				this.cursors.add(result);
				return result;
			}
		}
		
		/**
		 * Releases the given cursor at the end of a visit.
		 * <p>
		 * This method is internally synchronized on this NodeList.
		 * It is thread-safe to release a cursor.
		 * </p>
		 * 
		 * @param cursor the cursor
		 */
		void releaseCursor(Cursor cursor) {
			synchronized (this) {
				// serialize cursor management on this NodeList
				this.cursors.remove(cursor);
				if (this.cursors.isEmpty()) {
					// important: convert empty list back to null
					// otherwise the node will hang on to needless junk
					this.cursors = null;
				}
			}
		}

		/**
		 * Adjusts all cursors to accomodate an add/remove at the given
		 * index.
		 * <p>
		 * This method is only used when the list is being modified.
		 * The AST is not thread-safe if any of the clients are modifying it.
		 * </p>
		 * 
		 * @param index the position at which the element was added
		 *    or removed
		 * @param delta +1 for add, and -1 for remove
		 */
		private void updateCursors(int index, int delta) {
			if (this.cursors == null) {
				// there are no cursors to worry about
				return;
			}
			for (Iterator it = this.cursors.iterator(); it.hasNext(); ) {
				Cursor c = (Cursor) it.next();
				c.update(index, delta);
			}
		}
		
		/**
		 * Returns an estimate of the memory footprint of this node list 
		 * instance in bytes.
	     * <ul>
	     * <li>1 object header for the NodeList instance</li>
	     * <li>5 4-byte fields of the NodeList instance</li>
	     * <li>0 for cursors since null unless walk in progress</li>
	     * <li>1 object header for the ArrayList instance</li>
	     * <li>2 4-byte fields of the ArrayList instance</li>
	     * <li>1 object header for an Object[] instance</li>
	     * <li>4 bytes in array for each element</li>
	     * </ul>
	 	 * 
		 * @return the size of this node list in bytes
		 */
		int memSize() {
			int result = HEADERS + 5 * 4;
			result += HEADERS + 2 * 4;
			result += HEADERS + 4 * size();
			return result;
		}

		/**
		 * Returns an estimate of the memory footprint in bytes of this node
		 * list and all its subtrees.
		 * 
		 * @return the size of this list of subtrees in bytes
		 */
		int listSize() {
			int result = memSize();
			for (Iterator it = iterator(); it.hasNext(); ) {
				ASTNode child = (ASTNode) it.next();
				result += child.treeSize();
			}
			return result;
		}
	}
	
	// TODO Descent remove
	ASTNode() {
		this.ast = null;
	}
	
	/**
	 * Creates a new AST node owned by the given AST. Once established,
	 * the relationship between an AST node and its owning AST does not change
	 * over the lifetime of the node. The new node has no parent node,
	 * and no properties.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses my be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ASTNode(AST ast) {
		if (ast == null) {
			throw new IllegalArgumentException();
		}
		
		this.ast = ast;
		setNodeType(getNodeType0());
		setFlags(ast.getDefaultNodeFlag());
		// setFlags calls modifying();
	}
	
	/**
	 * Returns this node's AST.
	 * <p>
	 * Note that the relationship between an AST node and its owing AST does
	 * not change over the lifetime of a node.
	 * </p>
	 * 
	 * @return the AST that owns this node
	 */ 
	public final AST getAST() {
		return this.ast;
	}
	
	/**
	 * Returns this node's parent node, or <code>null</code> if this is the
	 * root node.
	 * <p>
	 * Note that the relationship between an AST node and its parent node
	 * may change over the lifetime of a node.
	 * </p>
	 * 
	 * @return the parent of this node, or <code>null</code> if none
	 */ 
	public final ASTNode getParent() {
		return this.parent;
	}
	
	/**
	 * Returns the location of this node within its parent,
	 * or <code>null</code> if this is a root node.
	 * <p>
	 * <pre>
	 * ASTNode node = ...;
	 * ASTNode parent = node.getParent();
	 * StructuralPropertyDescriptor location = node.getLocationInParent();
	 * assert (parent != null) == (location != null);
	 * if ((location != null) && location.isChildProperty())
	 *    assert parent.getStructuralProperty(location) == node;
	 * if ((location != null) && location.isChildListProperty())
	 *    assert ((List) parent.getStructuralProperty(location)).contains(node);
	 * </pre>
	 * </p>
	 * <p>
	 * Note that the relationship between an AST node and its parent node
	 * may change over the lifetime of a node.
	 * </p>
	 * 
	 * @return the location of this node in its parent, 
	 * or <code>null</code> if this node has no parent
	 * @since 3.0
	 */ 
	public final StructuralPropertyDescriptor getLocationInParent() {
		return this.location;
	}
	
	/**
	 * Returns the root node at or above this node; returns this node if 
	 * it is a root.
	 * 
	 * @return the root node at or above this node
	 */ 
	public final ASTNode getRoot() {
		ASTNode candidate = this;
		while (true) {
			ASTNode p = candidate.getParent();
			if (p == null) {
				// candidate has no parent - that's the guy
				return candidate;
			}
			candidate = p;
		}
	}
	
	/**
	 * Returns the value of the given structural property for this node. The value
	 * returned depends on the kind of property:
	 * <ul>
	 * <li>{@link SimplePropertyDescriptor} - the value of the given simple property,
	 * or <code>null</code> if none; primitive values are "boxed"</li>
	 * <li>{@link ChildPropertyDescriptor} - the child node (type <code>ASTNode</code>),
	 * or <code>null</code> if none</li>
	 * <li>{@link ChildListPropertyDescriptor} - the list (element type: {@link ASTNode})</li>
	 * </ul>
	 * 
	 * @param property the property
	 * @return the value, or <code>null</code> if none
	 * @exception RuntimeException if this node does not have the given property
	 * @since 3.0
	 */
	public final Object getStructuralProperty(StructuralPropertyDescriptor property) {
		if (property instanceof SimplePropertyDescriptor) {
			SimplePropertyDescriptor p = (SimplePropertyDescriptor) property;
			if (p.getValueType() == int.class) {
				int result = internalGetSetIntProperty(p, true, 0);
				return new Integer(result);
			} else if (p.getValueType() == boolean.class) {
				boolean result = internalGetSetBooleanProperty(p, true, false);
				return Boolean.valueOf(result);
			} else {
				return internalGetSetObjectProperty(p, true, null);
			}
		}
		if (property instanceof ChildPropertyDescriptor) {
			return internalGetSetChildProperty((ChildPropertyDescriptor) property, true, null);
		}
		if (property instanceof ChildListPropertyDescriptor) {
			return internalGetChildListProperty((ChildListPropertyDescriptor) property);
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * Sets the value of the given structural property for this node. The value
	 * passed depends on the kind of property:
	 * <ul>
	 * <li>{@link SimplePropertyDescriptor} - the new value of the given simple property,
	 * or <code>null</code> if none; primitive values are "boxed"</li>
	 * <li>{@link ChildPropertyDescriptor} - the new child node (type <code>ASTNode</code>),
	 * or <code>null</code> if none</li>
	 * <li>{@link ChildListPropertyDescriptor} - not allowed</li>
	 * </ul>
	 * 
	 * @param property the property
	 * @param value the property value
	 * @exception RuntimeException if this node does not have the
	 * given property, or if the given property cannot be set
	 * @since 3.0
	 */
	public final void setStructuralProperty(StructuralPropertyDescriptor property, Object value) {
		if (property instanceof SimplePropertyDescriptor) {
			SimplePropertyDescriptor p = (SimplePropertyDescriptor) property;
			if (p.getValueType() == int.class) {
				int arg = ((Integer) value).intValue();
				internalGetSetIntProperty(p, false, arg);
				return;
			} else if (p.getValueType() == boolean.class) {
				boolean arg = ((Boolean) value).booleanValue();
				internalGetSetBooleanProperty(p, false, arg);
				return;
			} else {
				if (value == null && p.isMandatory()) {
					throw new IllegalArgumentException();
				}
				internalGetSetObjectProperty(p, false, value);
				return;
			}
		}
		if (property instanceof ChildPropertyDescriptor) {
			ChildPropertyDescriptor p = (ChildPropertyDescriptor) property;
			ASTNode child = (ASTNode) value;
			if (child == null && p.isMandatory()) {
				throw new IllegalArgumentException();
			}
			internalGetSetChildProperty(p, false, child);
			return;
		}
		if (property instanceof ChildListPropertyDescriptor) {
			throw new IllegalArgumentException("Cannot set the list of child list property");  //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the value of the given int-valued property for this node.
	 * The default implementation of this method throws an exception explaining
	 * that this node does not have such a property. This method should be
	 * extended in subclasses that have at leasy one simple property whose value
	 * type is int.
	 * 
	 * @param property the property
	 * @param get <code>true</code> for a get operation, and 
	 * <code>false</code> for a set operation
	 * @param value the new property value; ignored for get operations
	 * @return the value; always returns
	 * <code>0</code> for set operations
	 * @exception RuntimeException if this node does not have the 
	 * given property, or if the given value cannot be set as specified
	 * @since 3.0
	 */
	int internalGetSetIntProperty(SimplePropertyDescriptor property, boolean get, int value) {
		throw new RuntimeException("Node does not have this property");  //$NON-NLS-1$
	}
	
	/**
	 * Sets the value of the given boolean-valued property for this node.
	 * The default implementation of this method throws an exception explaining
	 * that this node does not have such a property. This method should be
	 * extended in subclasses that have at leasy one simple property whose value
	 * type is boolean.
	 * 
	 * @param property the property
	 * @param get <code>true</code> for a get operation, and 
	 * <code>false</code> for a set operation
	 * @param value the new property value; ignored for get operations
	 * @return the value; always returns
	 * <code>false</code> for set operations
	 * @exception RuntimeException if this node does not have the 
	 * given property, or if the given value cannot be set as specified
	 * @since 3.0
	 */
	boolean internalGetSetBooleanProperty(SimplePropertyDescriptor property, boolean get, boolean value) {
		throw new RuntimeException("Node does not have this property");  //$NON-NLS-1$
	}
	
	/**
	 * Sets the value of the given property for this node.
	 * The default implementation of this method throws an exception explaining
	 * that this node does not have such a property. This method should be
	 * extended in subclasses that have at leasy one simple property whose value
	 * type is a reference type.
	 * 
	 * @param property the property
	 * @param get <code>true</code> for a get operation, and 
	 * <code>false</code> for a set operation
	 * @param value the new property value, or <code>null</code> if none;
	 * ignored for get operations
	 * @return the value, or <code>null</code> if none; always returns
	 * <code>null</code> for set operations
	 * @exception RuntimeException if this node does not have the 
	 * given property, or if the given value cannot be set as specified
	 * @since 3.0
	 */
	Object internalGetSetObjectProperty(SimplePropertyDescriptor property, boolean get, Object value) {
		throw new RuntimeException("Node does not have this property");  //$NON-NLS-1$
	}

	/**
	 * Sets the child value of the given property for this node.
	 * The default implementation of this method throws an exception explaining
	 * that this node does not have such a property. This method should be
	 * extended in subclasses that have at leasy one child property.
	 * 
	 * @param property the property
	 * @param get <code>true</code> for a get operation, and 
	 * <code>false</code> for a set operation
	 * @param child the new child value, or <code>null</code> if none;
	 * always <code>null</code> for get operations
	 * @return the child, or <code>null</code> if none; always returns
	 * <code>null</code> for set operations
	 * @exception RuntimeException if this node does not have the
	 * given property, or if the given child cannot be set as specified
	 * @since 3.0
	 */
	ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		throw new RuntimeException("Node does not have this property");  //$NON-NLS-1$
	}
	
	/**
	 * Returns the list value of the given property for this node.
	 * The default implementation of this method throws an exception explaining
	 * that this noed does not have such a property. This method should be
	 * extended in subclasses that have at leasy one child list property.
	 * 
	 * @param property the property
	 * @return the list (element type: {@link ASTNode})
	 * @exception RuntimeException if the given node does not have the
	 * given property
	 * @since 3.0
	 */
	List internalGetChildListProperty(ChildListPropertyDescriptor property) {
		throw new RuntimeException("Node does not have this property");  //$NON-NLS-1$
	}
	
	/**
	 * Returns a list of structural property descriptors for nodes of the
	 * same type as this node. Clients must not modify the result.
	 * <p>
	 * Note that property descriptors are a meta-level mechanism
	 * for manipulating ASTNodes in a generic way. They are
	 * unrelated to <code>get/setProperty</code>.
	 * </p>
	 * 
	 * @return a list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor})
	 * @since 3.0
	 */
	public final List structuralPropertiesForType() {
		return internalStructuralPropertiesForType(this.ast.apiLevel);
	}
	
	/**
	 * Returns a list of property descriptors for this node type.
	 * Clients must not modify the result. This abstract method
	 * must be implemented in each concrete AST node type.
	 * <p>
	 * N.B. This method is package-private, so that the implementations
	 * of this method in each of the concrete AST node types do not
	 * clutter up the API doc.
	 * </p>
	 * 
	 * @param apiLevel the API level; one of the <code>AST.JLS*</code> constants
	 * @return a list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor})
	 * @since 3.0
	 * 
	 * TODO make it abstract
	 */
	List internalStructuralPropertiesForType(int apiLevel) { return null; }
	
	/**
	 * Internal helper method that starts the building a list of
	 * property descriptors for the given node type.
	 * 
	 * @param nodeClass the class for a concrete node type
	 * @param propertyList empty list
	 */
	static void createPropertyList(Class nodeClass, List propertyList) {
		// stuff nodeClass at head of list for future ref
		propertyList.add(nodeClass);
	}
	
	/**
	 * Internal helper method that adding a property descriptor.
	 * 
	 * @param property the structural property descriptor
	 * @param propertyList list beginning with the AST node class
	 * followed by accumulated structural property descriptors
	 */
	static void addProperty(StructuralPropertyDescriptor property, List propertyList) {
		Class nodeClass = (Class) propertyList.get(0);
		if (property.getNodeClass() != nodeClass) {
			// easily made cut-and-paste mistake
			throw new RuntimeException("Structural property descriptor has wrong node class!");  //$NON-NLS-1$
		}
		propertyList.add(property);
	}
	
	/**
	 * Internal helper method that completes the building of
	 * a node type's structural property descriptor list.
	 * 
	 * @param propertyList list beginning with the AST node class
	 * followed by accumulated structural property descriptors
	 * @return unmodifiable list of structural property descriptors
	 * (element type: <code>StructuralPropertyDescriptor</code>)
	 */
	static List reapPropertyList(List<StructuralPropertyDescriptor> propertyList) {
		propertyList.remove(0); // remove nodeClass
		// compact
		ArrayList a = new ArrayList(propertyList.size());
		a.addAll(propertyList); 
		return Collections.unmodifiableList(a);
	}
	
	/**
     * Checks that this AST operation is not used when
     * building JLS2 level ASTs.

     * @exception UnsupportedOperationException
	 * @since 3.0
     */
	final void unsupportedIn2() {
	  if (this.ast.apiLevel == AST.JLS2_INTERNAL) {
	  	throw new UnsupportedOperationException("Operation not supported in JLS2 AST"); //$NON-NLS-1$
	  }
	}

	/**
     * Checks that this AST operation is only used when
     * building JLS2 level ASTs.

     * @exception UnsupportedOperationException
	 * @since 3.0
     */
	final void supportedOnlyIn2() {
	  if (this.ast.apiLevel != AST.JLS2_INTERNAL) {
	  	throw new UnsupportedOperationException("Operation only supported in JLS2 AST"); //$NON-NLS-1$
	  }
	}
	
	/**
	 * Sets or clears this node's parent node and location.
	 * <p>
	 * Note that this method is package-private. The pointer from a node
	 * to its parent is set implicitly as a side effect of inserting or
	 * removing the node as a child of another node. This method calls
	 * <code>ast.modifying()</code>.
	 * </p>
	 * 
	 * @param parent the new parent of this node, or <code>null</code> if none
	 * @param property the location of this node in its parent, 
	 * or <code>null</code> if <code>parent</code> is <code>null</code>
	 * @see #getLocationInParent
	 * @see #getParent
	 * @since 3.0
	 */ 
	final void setParent(ASTNode parent, StructuralPropertyDescriptor property) {
		this.ast.modifying();
		this.parent = parent;
		this.location = property;
	}
	
	/**
	 * Removes this node from its parent. Has no effect if this node
	 * is unparented. If this node appears as an element of a child list
	 * property of its parent, then this node is removed from the
	 * list using <code>List.remove</code>.
	 * If this node appears as the value of a child property of its
	 * parent, then this node is detached from its parent 
	 * by passing <code>null</code> to the appropriate setter method;
	 * this operation fails if this node is in a mandatory property.
	 * 
	 * @since 3.0
	 */ 
	public final void delete() {
		StructuralPropertyDescriptor p = getLocationInParent();
		if (p == null) {
			// node is unparented
			return;
		}
		if (p.isChildProperty()) {
			getParent().setStructuralProperty(this.location, null);
			return;
		}
		if (p.isChildListProperty()) {
			List l = (List) getParent().getStructuralProperty(this.location);
			l.remove(this);
		}
	}
	
	/**
	 * Checks whether the given new child node is a node 
	 * in a different AST from its parent-to-be, whether it is
	 * already has a parent, whether adding it to its
	 * parent-to-be would create a cycle, and whether the child is of
	 * the right type. The parent-to-be is the enclosing instance.
	 * 
	 * @param node the parent-to-be node
	 * @param newChild the new child of the parent
	 * @param cycleCheck <code>true</code> if cycles are possible and need 
	 *   to be checked, <code>false</code> if cycles are impossible and do 
	 *   not need to be checked
	 * @param nodeType a type constraint on child nodes, or <code>null</code>
	 *   if no special check is required
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the child is null</li>
	 * <li>the node belongs to a different AST</li>
	 * <li>the child has the incorrect node type</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	static void checkNewChild(ASTNode node, ASTNode newChild,
			boolean cycleCheck, Class nodeType) {
		if (newChild.ast != node.ast) {
			// new child is from a different AST
			throw new IllegalArgumentException();
		}		
		if (newChild.getParent() != null) {
			// new child currently has a different parent
			throw new IllegalArgumentException();
		}
		if (cycleCheck && newChild == node.getRoot()) {
			// inserting new child would create a cycle
			throw new IllegalArgumentException();
		}
		Class childClass = newChild.getClass();
		if (nodeType != null && !nodeType.isAssignableFrom(childClass)) {
			// new child is not of the right type
			throw new ClassCastException();
		}
		if ((newChild.typeAndFlags & PROTECT) != 0) {
			// new child node is protected => cannot be parented
			throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
		}
	}
	
	/**
     * Prelude portion of the "3 step program" for replacing the
	 * old child of this node with another node.
     * Here is the code pattern found in all AST node subclasses:
     * <pre>
     * ASTNode oldChild = this.foo;
     * preReplaceChild(oldChild, newFoo, FOO_PROPERTY);
     * this.foo = newFoo;
     * postReplaceChild(oldChild, newFoo, FOO_PROPERTY);
     * </pre>
     * The first part (preReplaceChild) does all the precondition checks,
     * reports pre-delete events, and changes parent links.
	 * The old child is delinked from its parent (making it a root node),
	 * and the new child node is linked to its parent. The new child node
	 * must be a root node in the same AST as its new parent, and must not
	 * be an ancestor of this node. All three nodes must be
     * modifiable (not PROTECTED). The replace operation must fail
     * atomically; so it is crucial that all precondition checks
     * be done before any linking and delinking happens.
     * The final part (postReplaceChild )reports post-add events.
	 * <p>
	 * This method calls <code>ast.modifying()</code> for the nodes affected.
	 * </p>
	 * 
	 * @param oldChild the old child of this node, or <code>null</code> if
	 *   there was no old child to replace
	 * @param newChild the new child of this node, or <code>null</code> if
	 *   there is no replacement child
	 * @param property the property descriptor of this node describing
     * the relationship between node and child
	 * @exception RuntimeException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * <li>any of the nodes involved are unmodifiable</li>
	 * </ul>
	 * @since 3.0
	 */ 
	final void preReplaceChild(ASTNode oldChild, ASTNode newChild, ChildPropertyDescriptor property) {
		if ((this.typeAndFlags & PROTECT) != 0) {
			// this node is protected => cannot gain or lose children
			throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
		}
		if (newChild != null) {
			checkNewChild(this, newChild, property.cycleRisk, null);
		}
		// delink old child from parent
		if (oldChild != null) {
			if ((oldChild.typeAndFlags & PROTECT) != 0) {
				// old child node is protected => cannot be unparented
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			if (newChild != null) {
				this.ast.preReplaceChildEvent(this, oldChild, newChild, property);
			} else {
				this.ast.preRemoveChildEvent(this, oldChild, property);
			}
			oldChild.setParent(null, null);
		} else {
			if(newChild != null) {
				this.ast.preAddChildEvent(this, newChild, property);
			}
		}
		// link new child to parent
		if (newChild != null) {
			newChild.setParent(this, property);
			// cannot notify postAddChildEvent until parent is linked to child too
		}
	}

	/**
     * Postlude portion of the "3 step program" for replacing the
	 * old child of this node with another node.
     * See {@link #preReplaceChild(ASTNode, ASTNode, ChildPropertyDescriptor)}
     * for details.
	 * @since 3.0
	 */ 
	final void postReplaceChild(ASTNode oldChild, ASTNode newChild, ChildPropertyDescriptor property) {
		// link new child to parent
		if (newChild != null) {
			if (oldChild != null) {
				this.ast.postReplaceChildEvent(this, oldChild, newChild, property);
			} else {
				this.ast.postAddChildEvent(this, newChild, property);
			}
		} else {
			this.ast.postRemoveChildEvent(this, oldChild, property);
		}
	}
	
	/**
     * Prelude portion of the "3 step program" for changing the
	 * value of a simple property of this node.
     * Here is the code pattern found in all AST node subclasses:
     * <pre>
     * preValueChange(FOO_PROPERTY);
     * this.foo = newFoo;
     * postValueChange(FOO_PROPERTY);
     * </pre>
     * The first part (preValueChange) does the precondition check
     * to make sure the node is modifiable (not PROTECTED).
     * The change operation must fail atomically; so it is crucial
     * that the precondition checks are done before the field is
     * hammered. The final part (postValueChange)reports post-change
     * events.
	 * <p>
	 * This method calls <code>ast.modifying()</code> for the node affected.
	 * </p>
	 * 
	 * @param property the property descriptor of this node 
	 * @exception RuntimeException if:
	 * <ul>
	 * <li>this node is unmodifiable</li>
	 * </ul>
	 * @since 3.0
	 */ 
	final void preValueChange(SimplePropertyDescriptor property) {
		if ((this.typeAndFlags & PROTECT) != 0) {
			// this node is protected => cannot change valure of properties
			throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
		}
		this.ast.preValueChangeEvent(this, property);
		this.ast.modifying();
	}

	/**
     * Postlude portion of the "3 step program" for replacing the
	 * old child of this node with another node.
     * See {@link #preValueChange(SimplePropertyDescriptor)} for details.
	 * @since 3.0
	 */ 
	final void postValueChange(SimplePropertyDescriptor property) {
		this.ast.postValueChangeEvent(this, property);
	}
	
	/**
     * Ensures that this node is modifiable (that is, not marked PROTECTED).
     * If successful, calls ast.modifying().
     * @exception RuntimeException is not modifiable
     */
	final void checkModifiable() {
		if ((this.typeAndFlags & PROTECT) != 0) {
			throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
		}
		this.ast.modifying();
	}
	
	/**
     * Begin lazy initialization of this node.
     * Here is the code pattern found in all AST
     * node subclasses:
     * <pre>
     * if (this.foo == null) {
	 *    // lazy init must be thread-safe for readers
     *    synchronized (this) {
     *       if (this.foo == null) {
     *          preLazyInit();
     *          this.foo = ...; // code to create new node
     *          postLazyInit(this.foo, FOO_PROPERTY);
     *       }
     *    }
     * }
     * </pre>
     * @since 3.0
     */
	final void preLazyInit() {
		// IMPORTANT: this method is called by readers
		// ASTNode.this is locked at this point
		this.ast.disableEvents();
		// will turn events back on in postLasyInit
	}
	
	/**
     * End lazy initialization of this node.
     * 
	 * @param newChild the new child of this node, or <code>null</code> if
	 *   there is no replacement child
	 * @param property the property descriptor of this node describing
     * the relationship between node and child
     * @since 3.0
     */
	final void postLazyInit(ASTNode newChild, ChildPropertyDescriptor property) {
		// IMPORTANT: this method is called by readers
		// ASTNode.this is locked at this point
		// newChild is brand new (so no chance of concurrent access)
		newChild.setParent(this, property);
		// turn events back on (they were turned off in corresponding preLazyInit)
		this.ast.reenableEvents();
	}

	/**
	 * Returns the named property of this node, or <code>null</code> if none.
	 * 
	 * @param propertyName the property name
	 * @return the property value, or <code>null</code> if none
	 * @see #setProperty(String,Object)
	 */
	public final Object getProperty(String propertyName) {
		if (propertyName == null) {
			throw new IllegalArgumentException();
		}
		if (this.property1 == null) {
			// node has no properties at all
			return null;
		}
		if (this.property1 instanceof String) {
			// node has only a single property
			if (propertyName.equals(this.property1)) {
				return this.property2;
			} else {
				return null;
			}
		}
		// otherwise node has table of properties
		Map m = (Map) this.property1;
		return m.get(propertyName);
	}
	
	/**
	 * Sets the named property of this node to the given value,
	 * or to <code>null</code> to clear it.
	 * <p>
	 * Clients should employ property names that are sufficiently unique
	 * to avoid inadvertent conflicts with other clients that might also be
	 * setting properties on the same node.
	 * </p>
	 * <p>
	 * Note that modifying a property is not considered a modification to the 
	 * AST itself. This is to allow clients to decorate existing nodes with 
	 * their own properties without jeopardizing certain things (like the 
	 * validity of bindings), which rely on the underlying tree remaining static.
	 * </p>
	 * 
	 * @param propertyName the property name
	 * @param data the new property value, or <code>null</code> if none
	 * @see #getProperty(String)
	 */
	public final void setProperty(String propertyName, Object data) {
		if (propertyName == null) {
			throw new IllegalArgumentException();
		}
		// N.B. DO NOT CALL ast.modifying();

		if (this.property1 == null) {
			// node has no properties at all
			if (data == null) {
				// we already know this
				return;
			}
			// node gets its fist property
			this.property1 = propertyName;
			this.property2 = data;
			return;
		}

		if (this.property1 instanceof String) {
			// node has only a single property
			if (propertyName.equals(this.property1)) {
				// we're in luck
				this.property2 = data;
				if (data == null) {
					// just deleted last property
					this.property1 = null;
					this.property2 = null;
				}
				return;
			}
			if (data == null) {
				// we already know this
				return;
			}
			// node already has one property - getting its second
			// convert to more flexible representation
			HashMap m = new HashMap(2);
			m.put(this.property1, this.property2);
			m.put(propertyName, data);
			this.property1 = m;
			this.property2 = null;
			return;
		}
			
		// node has two or more properties
		HashMap m = (HashMap) this.property1;
		if (data == null) {
			m.remove(propertyName);
			// check for just one property left
			if (m.size() == 1) {
				// convert to more efficient representation
				Map.Entry[] entries = (Map.Entry[]) m.entrySet().toArray(new Map.Entry[1]);
				this.property1 = entries[0].getKey();
				this.property2 = entries[0].getValue();
			}
			return;
		} else {
			m.put(propertyName, data);
			// still has two or more properties
			return;
		}
	}
	
	/**
	 * Returns an unmodifiable table of the properties of this node with 
	 * non-<code>null</code> values.
	 * 
	 * @return the table of property values keyed by property name
	 *   (key type: <code>String</code>; value type: <code>Object</code>)
	 */
	public final Map properties() {
		if (this.property1 == null) {
			// node has no properties at all
			return UNMODIFIABLE_EMPTY_MAP;
		} 
		if (this.property1 instanceof String) {
			// node has a single property
			return Collections.singletonMap(this.property1, this.property2);
		}
		
		// node has two or more properties
		if (this.property2 == null) {
			this.property2 = Collections.unmodifiableMap((Map) this.property1);
		}
		// property2 is unmodifiable wrapper for map in property1
		return (Map) this.property2;
	}
	
	/**
	 * Returns the flags associated with this node.
	 * <p>
	 * No flags are associated with newly created nodes.
	 * </p>
	 * <p>
	 * The flags are the bitwise-or of individual flags.
	 * The following flags are currently defined:
	 * <ul>
	 * <li>{@link #MALFORMED} - indicates node is syntactically 
	 *   malformed</li>
	 * <li>{@link #ORIGINAL} - indicates original node
	 * created by ASTParser</li>
	 * <li>{@link #PROTECT} - indicates node is protected
	 * from further modification</li>
	 * <li>{@link #RECOVERED} - indicates node or a part of this node
	 *  is recovered from source that contains a syntax error</li>
	 * </ul>
	 * Other bit positions are reserved for future use.
	 * </p>
	 * 
	 * @return the bitwise-or of individual flags
	 * @see #setFlags(int)
	 */
	public final int getFlags() {
		return this.typeAndFlags & 0xFFFF;
	}
	
	/**
	 * Sets the flags associated with this node to the given value.
	 * <p>
	 * The flags are the bitwise-or of individual flags.
	 * The following flags are currently defined:
	 * <ul>
	 * <li>{@link #MALFORMED} - indicates node is syntactically 
	 *   malformed</li>
	 * <li>{@link #ORIGINAL} - indicates original node
	 * created by ASTParser</li>
	 * <li>{@link #PROTECT} - indicates node is protected
	 * from further modification</li>
	 * <li>{@link #RECOVERED} - indicates node or a part of this node
	 *  is recovered from source that contains a syntax error</li>
	 * </ul>
	 * Other bit positions are reserved for future use.
	 * </p>
	 * <p>
	 * Note that the flags are <em>not</em> considered a structural
	 * property of the node, and can be changed even if the
	 * node is marked as protected.
	 * </p>
	 * 
	 * @param flags the bitwise-or of individual flags
	 * @see #getFlags()
	 */
	public final void setFlags(int flags) {
		this.ast.modifying();
		int old = this.typeAndFlags & 0xFFFF0000;
		this.typeAndFlags = old | (flags & 0xFFFF);
	}
	
	/**
	 * Returns an integer value identifying the type of this concrete AST node.
	 * The values are small positive integers, suitable for use in switch statements.
	 * <p>
	 * For each concrete node type there is a unique node type constant (name
	 * and value). The unique node type constant for a concrete node type such as 
	 * <code>CastExpression</code> is <code>ASTNode.CAST_EXPRESSION</code>.
	 * </p>
	 * 
	 * @return one of the node type constants
	 */
	public final int getNodeType() {
		return this.typeAndFlags >>> 16;
	}
	
	/**
	 * Sets the integer value identifying the type of this concrete AST node.
	 * The values are small positive integers, suitable for use in switch statements.
	 * 
	 * @param nodeType one of the node type constants
	 */
	private void setNodeType(int nodeType) {
		int old = this.typeAndFlags & 0xFFFF0000;
		this.typeAndFlags = old | (nodeType << 16);
	}
	
	/**
	 * Returns an integer value identifying the type of this concrete AST node.
	 * <p>
	 * This internal method is implemented in each of the
	 * concrete node subclasses.
	 * </p>
	 * 
	 * @return one of the node type constants
	 * 
	 * TODO make it abstract
	 */
	int getNodeType0() { return 0; }
	
	/**
	 * The <code>ASTNode</code> implementation of this <code>Object</code>
	 * method uses object identity (==). Use <code>subtreeMatch</code> to
	 * compare two subtrees for equality.
	 * 
	 * @param obj {@inheritDoc}
	 * @return {@inheritDoc}
	 * @see #subtreeMatch(ASTMatcher matcher, Object other)
	 */
	public final boolean equals(Object obj) {
		return this == obj; // equivalent to Object.equals
	}
	
	/**
	 * Returns whether the subtree rooted at the given node matches the
	 * given other object as decided by the given matcher.
	 * 
	 * @param matcher the matcher
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 * <code>false</code> if they do not match
	 */
	public final boolean subtreeMatch(ASTMatcher matcher, Object other) {
		return subtreeMatch0(matcher, other);
	}
	
	/**
	 * Returns whether the subtree rooted at the given node matches the
	 * given other object as decided by the given matcher.
	 * <p>
	 * This internal method is implemented in each of the
	 * concrete node subclasses.
	 * </p>
	 * 
	 * @param matcher the matcher
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 * <code>false</code> if they do not match
	 * 
	 * TODO make it abstract
	 */
	boolean subtreeMatch0(ASTMatcher matcher, Object other) { return false; }
	
	/**
	 * Returns a deep copy of the subtree of AST nodes rooted at the
	 * given node. The resulting nodes are owned by the given AST,
	 * which may be different from the ASTs of the given node. 
	 * Even if the given node has a parent, the result node will be unparented.
	 * <p>
	 * Source range information on the original nodes is automatically copied to the new
	 * nodes. Client properties (<code>properties</code>) are not carried over.
	 * </p>
	 * <p>
	 * The node's <code>AST</code> and the target <code>AST</code> must support
     * the same API level.
	 * </p>
	 * 
	 * @param target the AST that is to own the nodes in the result
	 * @param node the node to copy, or <code>null</code> if none
	 * @return the copied node, or <code>null</code> if <code>node</code>
	 *    is <code>null</code>
	 */
	public static ASTNode copySubtree(AST target, ASTNode node) {
		if (node == null) {
			return null;
		}
		if (target == null) {
			throw new IllegalArgumentException();
		}
		if (target.apiLevel() != node.getAST().apiLevel()) {
			throw new UnsupportedOperationException();
		}
		ASTNode newNode = node.clone(target);
		return newNode;
	}
	
	/**
	 * Returns a deep copy of the subtrees of AST nodes rooted at the
	 * given list of nodes. The resulting nodes are owned by the given AST,
	 * which may be different from the ASTs of the nodes in the list. 
	 * Even if the nodes in the list have parents, the nodes in the result
	 * will be unparented.
	 * <p>
	 * Source range information on the original nodes is automatically copied to the new
	 * nodes. Client properties (<code>properties</code>) are not carried over.
	 * </p>
	 * 
	 * @param target the AST that is to own the nodes in the result
	 * @param nodes the list of nodes to copy
	 *    (element type: <code>ASTNode</code>)
	 * @return the list of copied subtrees
	 *    (element type: <code>ASTNode</code>)
	 */
	public static List copySubtrees(AST target, List nodes) {
		List result = new ArrayList(nodes.size());
		for (Iterator it = nodes.iterator(); it.hasNext(); ) {
			ASTNode oldNode = (ASTNode) it.next();
			ASTNode newNode = oldNode.clone(target);
			result.add(newNode);
		}
		return result;
	}
	
	/**
	 * Returns a deep copy of the subtree of AST nodes rooted at this node.
	 * The resulting nodes are owned by the given AST, which may be different
	 * from the AST of this node. Even if this node has a parent, the 
	 * result node will be unparented.
	 * <p>
	 * This method reports pre- and post-clone events, and dispatches
	 * to <code>clone0(AST)</code> which is reimplemented in node subclasses.
	 * </p>
	 * 
	 * @param target the AST that is to own the nodes in the result
	 * @return the root node of the copies subtree
	 */
	final ASTNode clone(AST target) {
		this.ast.preCloneNodeEvent(this);
		ASTNode c = this.clone0(target);
		this.ast.postCloneNodeEvent(this, c);
		return c;
	}
	
	/**
	 * Returns a deep copy of the subtree of AST nodes rooted at this node.
	 * The resulting nodes are owned by the given AST, which may be different
	 * from the AST of this node. Even if this node has a parent, the 
	 * result node will be unparented.
	 * <p>
	 * This method must be implemented in subclasses.
	 * </p>
	 * <p>
	 * This method does not report pre- and post-clone events.
	 * All callers should instead call <code>clone(AST)</code>
	 * to ensure that pre- and post-clone events are reported.
	 * </p>
	 * <p>
	 * N.B. This method is package-private, so that the implementations
	 * of this method in each of the concrete AST node types do not
	 * clutter up the API doc. 
	 * </p>
	 * 
	 * @param target the AST that is to own the nodes in the result
	 * @return the root node of the copies subtree
	 * 
	 * TODO make it abstract
	 */
	ASTNode clone0(AST target) { return null; }
	
	public int modifiers;
	
	public int getModifiers() {
		return modifiers;
	}
	
	public void addComment(String string, int blockCommentStart) {
		comments = string;
		if (blockCommentStart != -1) {
			this.length += this.startPosition - blockCommentStart; 
			this.startPosition = blockCommentStart;
		}
	}
	
	public String getComments() {
		return comments;
	}
	
	/**
	 * Accepts the given visitor on a visit of the current node.
	 * 
	 * @param visitor the visitor object
	 * @exception IllegalArgumentException if the visitor is null
	 */
	public final void accept(ASTVisitor visitor) {
		if (visitor == null) {
			throw new IllegalArgumentException();
		}
		// begin with the generic pre-visit
		visitor.preVisit(this);
		// dynamic dispatch to internal method for type-specific visit/endVisit
		accept0(visitor);
		// end with the generic post-visit
		visitor.postVisit(this);
	}
	
	/**
	 * Accepts the given visitor on a type-specific visit of the current node.
	 * This method must be implemented in all concrete AST node types.
	 * <p>
	 * General template for implementation on each concrete IElement class:
	 * <pre>
	 * <code>
	 * boolean visitChildren = visitor.visit(this);
	 * if (visitChildren) {
	 *    // visit children in normal left to right reading order
	 *    acceptChild(visitor, getProperty1());
	 *    acceptChildren(visitor, rawListProperty);
	 *    acceptChild(visitor, getProperty2());
	 * }
	 * visitor.endVisit(this);
	 * </code>
	 * </pre>
	 * Note that the caller (<code>accept</code>) take cares of invoking
	 * <code>visitor.preVisit(this)</code> and <code>visitor.postVisit(this)</code>.
	 * </p>
	 * 
	 * @param visitor the visitor object
	 */
	abstract void accept0(ASTVisitor visitor);
	
	/**
	 * Accepts the visitor on the child. If child is null,
	 * nothing happens.
	 */
	protected void acceptChild(ASTVisitor visitor, IElement child) {
		if (child == null)
			return;
		
		child.accept(visitor);
	}
	
	/**
	 * Accepts the given visitor on a visit of the given live list of
	 * child nodes. 
	 * <p>
	 * This method must be used by the concrete implementations of
	 * <code>accept</code> to traverse list-values properties; it
	 * encapsulates the proper handling of on-the-fly changes to the list.
	 * </p>
	 * 
	 * @param visitor the visitor object
	 * @param children the child AST node to dispatch too, or <code>null</code>
	 *    if none
	 */
	final void acceptChildren(ASTVisitor visitor, ASTNode.NodeList children) {
		// use a cursor to keep track of where we are up to
		// (the list may be changing under foot)
		NodeList.Cursor cursor = children.newCursor();
		try {
			while (cursor.hasNext()) {
				ASTNode child = (ASTNode) cursor.next();
				child.accept(visitor);
			}
		} finally {
			children.releaseCursor(cursor);
		}
	}
	
	/**
	 * Returns the character index into the original source file indicating
	 * where the source fragment corresponding to this node begins.
	 * <p>
	 * The parser supplies useful well-defined source ranges to the nodes it creates.
	 * See {@link ASTParser#setKind(int)} for details
	 * on precisely where source ranges begin and end.
	 * </p>
	 * 
	 * @return the 0-based character index, or <code>-1</code>
	 *    if no source position information is recorded for this node
	 * @see #getLength()
	 * @see ASTParser
	 */
	public int getStartPosition() {
		return startPosition;
	}
	
	/**
	 * Returns the length in characters of the original source file indicating
	 * where the source fragment corresponding to this node ends.
	 * <p>
	 * The parser supplies useful well-defined source ranges to the nodes it creates.
	 * See {@link ASTParser#setKind(int)} methods for details
	 * on precisely where source ranges begin and end.
	 * </p>
	 * 
	 * @return a (possibly 0) length, or <code>0</code>
	 *    if no source position information is recorded for this node
	 * @see #getStartPosition()
	 * @see ASTParser
	 */
	public final int getLength() {
		return this.length;
	}
	
	/**
	 * Sets the source range of the original source file where the source
	 * fragment corresponding to this node was found.
	 * <p>
	 * See {@link ASTParser#setKind(int)} for details
	 * on precisely where source ranges are supposed to begin and end.
	 * </p>
	 * 
	 * @param startPosition a 0-based character index, 
	 *    or <code>-1</code> if no source position information is 
	 *    available for this node
	 * @param length a (possibly 0) length, 
	 *    or <code>0</code> if no source position information is recorded 
	 *    for this node
	 * @see #getStartPosition()
	 * @see #getLength()
	 * @see ASTParser
	 */
	public final void setSourceRange(int startPosition, int length) {
		if (startPosition >= 0 && length < 0) {
			throw new IllegalArgumentException();
		}
		if (startPosition < 0 && length != 0) {
			throw new IllegalArgumentException();
		}
		// source positions are not considered a structural property
		// but we protect them nevertheless
		checkModifiable();
		this.startPosition = startPosition;
		this.length = length;
	}
	
	/**
	 * Estimate of size of an object header in bytes.
	 */
	static final int HEADERS = 12;
	
	/**
	 * Approximate base size of an AST node instance in bytes, 
	 * including object header and instance fields.
	 * That is, HEADERS + (# instance vars in ASTNode)*4.
	 */
	static final int BASE_NODE_SIZE = HEADERS + 7 * 4;
	
	/**
	 * Returns an estimate of the memory footprint, in bytes,
	 * of the given string.
	 * 
	 * @param string the string to measure, or <code>null</code>
	 * @return the size of this string object in bytes, or
	 *   0 if the string is <code>null</code>
     * @since 3.0
	 */
	static int stringSize(String string) {
		int size = 0;
		if (string != null) {
			// Strings usually have 4 instance fields, one of which is a char[]
			size += HEADERS + 4 * 4;
			// char[] has 2 bytes per character
			size += HEADERS + 2 * string.length();
		}
		return size;
	}
	
	/**
	 * Returns an estimate of the memory footprint in bytes of the entire 
	 * subtree rooted at this node.
	 * 
	 * @return the size of this subtree in bytes
	 */
	public final int subtreeBytes() {
		return treeSize();
	}
	
	/**
	 * Returns an estimate of the memory footprint in bytes of the entire 
	 * subtree rooted at this node.
	 * <p>
	 * N.B. This method is package-private, so that the implementations
	 * of this method in each of the concrete AST node types do not
	 * clutter up the API doc.
	 * </p>
	 * 
	 * @return the size of this subtree in bytes
	 * 
	 * TODO make it abstract
	 */
	int treeSize() { return 0; }
	
	/**
	 * Returns an estimate of the memory footprint of this node in bytes.
	 * The estimate does not include the space occupied by child nodes.
	 * 
	 * @return the size of this node in bytes
	 * 
	 * TODO make it abstract
	 */
	int memSize() { return 0; }
	
	/**
	 * Accepts the visitor on the children. If children is null,
	 * nothing happens.
	 * TODO Descent remove
	 */
	protected void acceptChildren(ASTVisitor visitor, IElement[] children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.length; i++) {
			if (children[i] instanceof IElement) {
				acceptChild(visitor, children[i]);
			}
		}
	}
	
	/**
	 * Accepts the visitor on the children. If children is null,
	 * nothing happens.
	 * TODO Descent remove
	 */
	protected void acceptChildren(ASTVisitor visitor, List<? extends IElement> children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.size(); i++) {
			if (children.get(i) instanceof IElement) {
				acceptChild(visitor, children.get(i));
			}
		}
	}

}
