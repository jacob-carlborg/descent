package mmrnmhrm.core.model.lang;

import java.util.ArrayList;

import melnorme.miscutil.Assert;
import melnorme.miscutil.log.Logg;
import melnorme.miscutil.tree.IElement;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;



/**
 * Common class for IDE language model elements.
 * Not handle-based nor cached, like JDT.
 * (XXX: Uses LangElementInfo as mixin only) 
 */
public abstract class LangElement implements ILangElement {
	
	protected ILangElement parent;
	protected boolean opened = false;
	
	public LangElement(ILangElement parent) {
		this.parent = parent;
	}
	
	public ILangElement getParent() {
		return parent;
	}
	
	public String toString() {
		return getElementName();
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
	public final ArrayList<?> getChildrenOfType(int type) {
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
	
	public final Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
	
	public void getElementInfo() throws CoreException {
		if(!opened) {
			opened = true;
			createElementInfo();
		}
	}
	
	/** Marks an element as needing update. (Lazy update) */
	public void updateElementLazily() throws CoreException {
		disposeElementInfo();
		opened = false; 
	}
	
	/** Updates the lang element according to the underlying filesystem data. */
	public void updateElementRecursive() throws CoreException {
		/*for(ILangElement child : getLangChildren()) {
			updateElemLazily();
		}*/
		disposeElementInfo();
		Logg.model.println("Update Element Recursive: " + this.getUnderlyingResource());
		createElementInfo();
		opened = true;
		for(ILangElement child : getLangChildren()) {
			child.updateElementRecursive();
		}
	}
	
	/** Gets the children which are Lang Elements. */
	public abstract ILangElement[] getLangChildren();
	
	/** Creates the element's info. */
	protected abstract void createElementInfo() throws CoreException;
	
	/** Disposes of the element's info. */
	protected abstract void disposeElementInfo() throws CoreException;


}
