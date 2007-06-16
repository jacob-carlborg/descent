package mmrnmhrm.core.model.lang;

import util.ExceptionAdapter;
/**
 * Actual info for lang elements.
 * Not cached, used as mixin.
 * # Work in Progress. # 
 */
public abstract class LangElementInfo {

	/** Collection of elements of immediate children of this
	 * object. This is an empty array if this element has
	 * no children.
	 */
	protected ILangElement[] children;

	protected LangElementInfo() {
		this.children = newChildrenArray(0);
	}
	

	public ILangElement[] getChildren() {
		return this.children;
	}
	
	protected void setChildren(ILangElement[] children) {
		this.children = children;
	}
	
	/** Creates an array with a runtime type appropriate to store this 
	 * element's children. */
	protected abstract ILangElement[] newChildrenArray(int size);

	/** Adds a child to this element, if it doesn't exist already. */
	protected void addChild(ILangElement child) {
		int length = this.children.length;
		if (length == 0) {
			this.children = newChildrenArray(1);
			this.children[0] = child;
		} else {
			for (int i = 0; i < length; i++) {
				if (children[i].equals(child))
					return; // already included
			}
			System.arraycopy(this.children, 0,
					this.children = newChildrenArray(length + 1), 0, length);
			this.children[length] = child;
		}
	}


	/** Removes a child from this element, if it exists. */
	protected void removeChild(ILangElement child) {
		for (int i = 0, length = this.children.length; i < length; i++) {
			ILangElement element = this.children[i];
			if (element.equals(child)) {
				if (length == 1) {
					this.children = newChildrenArray(0);
				} else {
					ILangElement[] newChildren = newChildrenArray(length - 1);
					System.arraycopy(this.children, 0, newChildren, 0, i);
					if (i < length - 1)
						System.arraycopy(this.children, i + 1, newChildren, i,
								length - 1 - i);
					this.children = newChildren;
				}
				break;
			}
		}
	}
	

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}
}
