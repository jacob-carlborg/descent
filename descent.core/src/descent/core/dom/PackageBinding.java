package descent.core.dom;

import descent.core.ICompilationUnit;
import descent.internal.compiler.parser.IModule;

public class PackageBinding implements IPackageBinding {

	private ICompilationUnit element;
	private final IModule node;
	private final String signature;
	private final DefaultBindingResolver bindingResolver;

	public PackageBinding(DefaultBindingResolver bindingResolver, ICompilationUnit element, IModule node, String signature) {
		this.bindingResolver = bindingResolver;
		this.element = element;
		this.node = node;
		this.signature = signature;
	}

	public String getName() {
		if (node != null && node.getFullyQualifiedName() != null) {
			return node.getFullyQualifiedName();
		}
		return getJavaElement().getFullyQualifiedName();
	}

	public String[] getNameComponents() {
		return getName().split("\\.");
	}

	public boolean isUnnamed() {
		// TODO Auto-generated method stub
		return false;
	}

	public ICompilationUnit getJavaElement() {
		if (element == null) {
			element = (ICompilationUnit) bindingResolver.getJavaElement(node);
		}
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
