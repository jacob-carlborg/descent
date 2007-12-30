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
	
	public final IJavaElement getJavaElement() {
		return element;
	}
	
	public final String getName() {
		return element.getElementName();
	}
	
	public final long getModifiers() {
		if (element instanceof IMember) {
			try {
				return ((IMember) element).getFlags();
			} catch (JavaModelException e) {
				return 0;
			}
		}
		return 0;
	}
	
	public final boolean isDeprecated() {
		return (getModifiers() & Modifier.DEPRECATED) != 0;
	}

}
