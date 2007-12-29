package descent.core.dom;

import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.JavaModelException;

/**
 * A binding based on a java element.
 */
public abstract class JavaElementBasedBinding implements IBinding {
	
	protected final IJavaElement element;

	public JavaElementBasedBinding(IJavaElement element) {
		this.element = element;		
	}
	
	public IJavaElement getJavaElement() {
		return element;
	}
	
	public String getName() {
		return element.getElementName();
	}
	
	public int getModifiers() {
		if (element instanceof IMember) {
			try {
				return ((IMember) element).getFlags();
			} catch (JavaModelException e) {
				return 0;
			}
		}
		return 0;
	}
	
	public boolean isDeprecated() {
		return (getModifiers() & Modifier.DEPRECATED) != 0;
	}

}
