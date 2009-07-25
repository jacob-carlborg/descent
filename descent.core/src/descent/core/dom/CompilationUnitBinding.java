package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.ScopeDsymbol;

public class CompilationUnitBinding extends AbstractBinding implements ICompilationUnitBinding {

	private IJavaElement element;
	private final Module module;
	private final String signature;
	private final DefaultBindingResolver bindingResolver;

	public CompilationUnitBinding(DefaultBindingResolver bindingResolver, Module module, String signature) {
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

	public ICompilationUnit getJavaElement() {
		if (element == null) {
			element = module.getJavaElement();
			if (element == null) {
				element = bindingResolver.getCompilationUnit(module);
			}
		}
		return (ICompilationUnit) element;
	}
	
	public ICompilationUnitBinding[] getPublicImports() {
		if (module.imports == null || module.imports.isEmpty())
			return NO_UNITS;
		
		List<ICompilationUnitBinding> imps = new ArrayList<ICompilationUnitBinding>();
		for (int i = 0; i < module.imports.size(); i++) {
			PROT prot = module.prots.get(i);
			if (prot == PROT.PROTpublic) {
				ScopeDsymbol maybeMod = module.imports.get(i);
				if (maybeMod instanceof Module) {
					imps.add(bindingResolver.resolveModule((Module) maybeMod, null));
				}
			}
		}
		
		return imps.toArray(new ICompilationUnitBinding[imps.size()]);
	}
	
	public IMethodBinding[] getDeclaredFunctions() {
		return getDeclaredMethods(module, bindingResolver);
	}
	
	public ITypeBinding[] getDeclaredTypes() {
		return getDeclaredTypes(module, bindingResolver);
	}
	
	public IVariableBinding[] getDeclaredVariables() {
		return getDeclaredVariables(module, bindingResolver);
	}

	public String getKey() {
		return signature;
	}

	public int getKind() {
		return COMPILATION_UNIT;
	}

	public long getModifiers() {
		return 0;
	}

	public boolean isDeprecated() {
		return false;
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

}
