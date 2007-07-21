package mmrnmhrm.core.model.lang;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.Platform;

import melnorme.miscutil.Assert;
import melnorme.miscutil.tree.IElement;



/**
 * Common class for IDE language model elements.
 * Not handle-based nor cached, like JDT.
 * (XXX: Uses LangElementInfo as mixin only) 
 */
public abstract class LangElement extends LangElementInfo implements ILangElement {
	
	protected ILangElement parent;
	protected boolean opened = false;
	
	public LangElement(ILangElement parent) {
		this.parent = parent;
	}
	
	public ILangElement getParent() {
		return parent;
	}
	
	public boolean hasChildren() {
		return getChildren().length > 0;
	}

	
	/** Returns true if this handle represents the same Lang element
	 * as the given handle. By default, two handles represent the same
	 * element if they are identical or if they represent the same type
	 * of element, have equal names, parents, and occurrence counts.
	 * 
	 * XXX: occurrence counts ?
	 */
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
	
		// Only lang model root's parent is null
		if (this.parent == null) 
			return (this == obj); // model root is a singleton

		Assert.isNotNull(this.parent);

		if(!(obj instanceof LangElement))
			return false;

		LangElement other = (LangElement) obj;		
		return getElementName().equals(other.getElementName()) &&
				this.parent.equals(other.parent);
	}
	
	/** Returns the hash code for this Lang element. By default,
	 * the hash code for an element is a combination of its name
	 * and parent's hash code. Elements with other requirements must
	 * override this method. 
	 */
	public int hashCode() {
		if (this.parent == null) 
			return super.hashCode();
		return melnorme.miscutil.Misc.combineHashCodes(getElementName().hashCode(), this.parent.hashCode());
	}
	
	/** Returns a collection of (immediate) children of this node of the
	 * specified element type.
	 */
	public ArrayList<?> getChildrenOfType(int type) {
		IElement[] children = getChildren();
		int size = children.length;
		ArrayList<ILangElement> list = new ArrayList<ILangElement>(size);
		for (int i = 0; i < size; ++i) {
			LangElement elt = (LangElement)children[i];
			if (elt.getElementType() == type) {
				list.add(elt);
			}
		}
		return list;
	}
	
	
	
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
