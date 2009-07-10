package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.VarDeclaration;

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
		if (module.symtab == null)
			return NO_UNITS;
		
		List<ICompilationUnitBinding> imps = new ArrayList<ICompilationUnitBinding>();
		for(Object value : module.symtab.values()) {
			if (value instanceof Import) {
				Import imp = (Import) value;
				if (imp.ispublic) {
					while(imp != null) {
						IBinding binding = bindingResolver.resolveModule(imp.mod);
						if (binding instanceof ICompilationUnitBinding) {
							imps.add((ICompilationUnitBinding) binding);
						}
						imp = imp.next;
					}
				}
			}
		}
		
		return imps.toArray(new ICompilationUnitBinding[imps.size()]);
	}
	
	public IMethodBinding[] getDeclaredFunctions() {
		if (module.symtab == null)
			return NO_METHODS;
		
		List<IMethodBinding> func = new ArrayList<IMethodBinding>();
		for(Object value : module.symtab.values()) {
			if (value instanceof FuncDeclaration) {
				IBinding binding = bindingResolver.resolveDsymbol((Dsymbol)value);
				if (binding instanceof IMethodBinding) {
					func.add((IMethodBinding) binding);
				}
			}
		}
		
		return func.toArray(new IMethodBinding[func.size()]);
	}
	
	public ITypeBinding[] getDeclaredTypes() {
		if (module.symtab == null)
			return NO_TYPES;
		
		List<ITypeBinding> types = new ArrayList<ITypeBinding>();
		for(Object value : module.symtab.values()) {
			if (value instanceof ClassDeclaration ||
				value instanceof StructDeclaration ||
				value instanceof TemplateDeclaration ||
				value instanceof AliasDeclaration ||
				value instanceof TypedefDeclaration ||
				value instanceof descent.internal.compiler.parser.EnumDeclaration) {
				IBinding binding = bindingResolver.resolveDsymbol((Dsymbol)value);
				if (binding instanceof ITypeBinding) {
					types.add((ITypeBinding) binding);
				}
			}
		}
		
		return types.toArray(new ITypeBinding[types.size()]);
		
	}
	
	public IVariableBinding[] getDeclaredVariables() {
		if (module.symtab == null)
			return NO_VARIABLES;
		
		List<IVariableBinding> vars = new ArrayList<IVariableBinding>();
		for(Object value : module.symtab.values()) {
			if (value instanceof VarDeclaration) {
				IBinding binding = bindingResolver.resolveDsymbol((VarDeclaration)value);
				if (binding instanceof IVariableBinding) {
					vars.add((IVariableBinding) binding);
				}
			}
		}
		
		return vars.toArray(new IVariableBinding[vars.size()]);
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
