package util.tree;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import util.ExceptionAdapter;

/** Abstract visitor for a heterogenous tree with some utility methods. */
public abstract class TreeVisitor<NODE extends TreeNode> implements ITreeVisitor<NODE> {
	

	public void preVisit(NODE elem) {
		// Default implementation: do nothing
	}

	public void postVisit(NODE elem) {
		// Default implementation: do nothing
	}

	
	protected boolean visitingAsSuper = false;
	
	/** Utility method that visits an element as if it were of it's base class. */
	public boolean visitAsSuperType(NODE element, Class elemclass)  {
		Class elemsuper = elemclass.getSuperclass();
		Method method;
		try {
			method = this.getClass().getMethod("visit", new Class[]{elemsuper});
			visitingAsSuper = true;
			boolean result = (Boolean) method.invoke(this, element);
			visitingAsSuper = false;
			return result;
		} catch (InvocationTargetException ite) {
			Throwable e = (Throwable)ite.getTargetException();
			throw ExceptionAdapter.unchecked(e);
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		} 
	}
	

	
	@SuppressWarnings("unchecked")
	/** Accepts the visitor on node. If node is null, nothing happens. */
	public void acceptElement(NODE node) {
		if(node != null)
			node.accept(this);
	}
	
	/** Accepts the visitor on node. If node is null, nothing happens. */
	@SuppressWarnings("unchecked")
	public void acceptMany(NODE node) {
		if (node != null) {
			node.accept(this);
		}
	}
	
	/** Accepts the visitor on the children. If children is null, nothing
	 * happens. */
	public void acceptMany(List<? extends NODE> nodes) {
		if (nodes == null)
			return;
		
		for(int i = 0; i < nodes.size(); i++) {
			this.acceptElement(nodes.get(i));
		}
	}

	/** Accepts the visitor on the children. If children is null, nothing
	 * happens.	*/
	public void acceptMany(NODE[] nodes) {
		if (nodes == null)
			return;
		
		for(int i = 0; i < nodes.length; i++) {
			this.acceptElement(nodes[i]);
		}
	}
	
	/** Accepts the visitor on child. If child is null, nothing happens. */
	@SuppressWarnings("unchecked")
	public static void acceptChild(ITreeVisitor visitor, IVisitable child) {
		if (child != null) {
			child.accept(visitor);
		}
	}

	/** Same as {@link #acceptChild(CommonASTVisitor, TreeNode) } */
	public static void acceptChildren(ITreeVisitor visitor, IVisitable child) {
		TreeVisitor.acceptChild(visitor, child);
	}

	/** Accepts the visitor on the children. If children is null, nothing
	 * happens.	*/
	public static void acceptChildren(ITreeVisitor visitor, IVisitable[] children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.length; i++) {
			TreeVisitor.acceptChild(visitor, children[i]);
		}
	}

	/** Accepts the visitor on the children. If children is null, nothing
	 * happens. */
	public static void acceptChildren(ITreeVisitor visitor, List<? extends ITreeNode> children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.size(); i++) {
			TreeVisitor.acceptChild(visitor, children.get(i));
		}
	}



}