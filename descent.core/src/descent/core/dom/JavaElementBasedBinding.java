package descent.core.dom;

import descent.core.IJavaElement;
import descent.internal.compiler.parser.Dsymbol;

/**
 * A binding based on a java element.
 */
public abstract class JavaElementBasedBinding implements IBinding {
	
	protected final DefaultBindingResolver bindingResolver;
	protected IJavaElement element;
	protected final Dsymbol node;

	public JavaElementBasedBinding(DefaultBindingResolver resolver, Dsymbol node) {
		this.bindingResolver = resolver;
		this.node = node;
	}
	
	public final IJavaElement getJavaElement() {
		if (element == null) {
			element = node.getJavaElement();
			if (element == null) {
				element = bindingResolver.resolveBinarySearch(node);
			}
		}
		return element;
	}
	
	public final String getName() {
		return getJavaElement().getElementName();
	}
	
	public final long getModifiers() {
		return node.getFlags();
	}
	
	public final boolean isDeprecated() {
		return (getModifiers() & Modifier.DEPRECATED) != 0;
	}

}
