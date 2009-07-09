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
		// TODO for local variables this returns the enclosing function,
		// which is wrong!
		if (element == null) {
			element = node.getJavaElement();
			if (element == null) {
				element = bindingResolver.resolveBinarySearch(node);
			}
		}
		return element;
	}
	
	public final String getName() {
		return new String(node.ident.ident);
	}
	
	public final long getModifiers() {
		return node.getFlags();
	}
	
	public final boolean isDeprecated() {
		return (getModifiers() & Modifier.DEPRECATED) != 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IBinding)) {
			return false;
		}
			
		IBinding other = (IBinding) obj;
		return getKey().equals(other.getKey());
	}

}
