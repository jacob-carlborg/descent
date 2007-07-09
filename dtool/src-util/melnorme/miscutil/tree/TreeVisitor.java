package melnorme.miscutil.tree;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import melnorme.miscutil.ExceptionAdapter;

import melnorme.miscutil.tree.ITreeNode;
import melnorme.miscutil.tree.IVisitable;
import melnorme.miscutil.tree.TreeNode;
import melnorme.miscutil.tree.TreeVisitor;

/** Abstract visitor for a heterogenous tree with some utility methods. */
public abstract class TreeVisitor {
	
	
	protected boolean visitingAsSuper = false;
	
	/** Utility method that visits an element as if it were of it's base class. */
	public <T> boolean visitAsSuperType(ITreeNode element, Class<T> elemclass)  {
		Class<? super T> elemsuper = elemclass.getSuperclass();
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
	

	
	/** Accepts the visitor on node. If node is null, nothing happens. */
	public void acceptElement(IVisitable<TreeVisitor> node) {
		if(node != null)
			node.accept(this);
	}
	
	/** Accepts the visitor on node. If node is null, nothing happens. */
	public void acceptMany(IVisitable<TreeVisitor> node) {
		if (node != null) {
			node.accept(this);
		}
	}
	
	/** Accepts the visitor on the children. If children is null, nothing
	 * happens. */
	public void acceptMany(List<? extends IVisitable<TreeVisitor>> nodes) {
		if (nodes == null)
			return;
		
		for(int i = 0; i < nodes.size(); i++) {
			this.acceptElement(nodes.get(i));
		}
	}

	/** Accepts the visitor on the children. If children is null, nothing
	 * happens.	*/
	public void acceptMany(IVisitable<TreeVisitor>[] nodes) {
		if (nodes == null)
			return;
		
		for(int i = 0; i < nodes.length; i++) {
			this.acceptElement(nodes[i]);
		}
	}
	
	/** Accepts the visitor on child. If child is null, nothing happens. */
	//@SuppressWarnings("unchecked")
	public static <T> void acceptChild(T visitor, IVisitable<T> child) {
		if (child != null) {
			child.accept(visitor);
		}
	}

	/** Same as {@link #acceptChild(CommonASTVisitor, TreeNode) } */
	public static <T> void acceptChildren(T visitor, IVisitable<T> child) {
		TreeVisitor.acceptChild(visitor, child);
	}

	/** Accepts the visitor on the children. If children is null, nothing
	 * happens.	*/
	public static <T> void acceptChildren(T visitor, IVisitable<T>[] children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.length; i++) {
			TreeVisitor.acceptChild(visitor, children[i]);
		}
	}

	/** Accepts the visitor on the children. If children is null, nothing
	 * happens. */
	public static <T> void acceptChildren(T visitor, List<? extends IVisitable<T>> children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.size(); i++) {
			TreeVisitor.acceptChild(visitor, children.get(i));
		}
	}



}