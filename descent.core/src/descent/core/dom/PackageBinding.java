package descent.core.dom;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.internal.compiler.parser.Module;

public class PackageBinding implements IPackageBinding {

	private IJavaElement element;
	private final Module module;
	private final String signature;
	private final DefaultBindingResolver bindingResolver;

	public PackageBinding(DefaultBindingResolver bindingResolver, Module module, String signature) {
		this.bindingResolver = bindingResolver;
		this.module = module;
		this.signature = signature;
	}

	public String getName() {
		return module.getFullyQualifiedName();
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
			element = module.getJavaElement();
			if (element == null) {
				element = bindingResolver.getCompilationUnit(module);
			}
		}
		return (ICompilationUnit) element;
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
