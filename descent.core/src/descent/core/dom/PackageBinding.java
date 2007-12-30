package descent.core.dom;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;

public class PackageBinding implements IPackageBinding {

	private final DefaultBindingResolver bindingResolver;
	private final ICompilationUnit element;
	private final String signature;

	public PackageBinding(DefaultBindingResolver bindingResolver, ICompilationUnit element, String signature) {
		this.bindingResolver = bindingResolver;
		this.element = element;
		this.signature = signature;
	}

	public String getName() {
		return element.getFullyQualifiedName();
	}

	public String[] getNameComponents() {
		return element.getFullyQualifiedName().split("\\.");
	}

	public boolean isUnnamed() {
		// TODO Auto-generated method stub
		return false;
	}

	public IJavaElement getJavaElement() {
		return element;
	}

	public String getKey() {
		return signature;
	}

	public int getKind() {
		return PACKAGE;
	}

	public long getModifiers() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isDeprecated() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEqualTo(IBinding binding) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

}
