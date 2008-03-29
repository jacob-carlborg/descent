package descent.core.dom;

import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.Dsymbol;

/**
 * A binding based on a java element.
 */
public abstract class JavaElementBasedBinding implements IBinding {
	
	protected final DefaultBindingResolver bindingResolver;
	private IJavaElement element;
	protected final Dsymbol node;

	public JavaElementBasedBinding(DefaultBindingResolver resolver, IJavaElement element, Dsymbol node) {
		this.bindingResolver = resolver;
		this.element = element;
		this.node = node;
	}
	
	public final IJavaElement getJavaElement() {
		if (element == null) {
			element = bindingResolver.getJavaElement(node);
		}
		return element;
	}
	
	public final String getName() {
		return getJavaElement().getElementName();
	}
	
	public final long getModifiers() {
		if (node != null) {
			return node.getFlags();
		}
		
		if (getJavaElement() instanceof IMember) {
			try {
				return ((IMember) getJavaElement()).getFlags();
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
