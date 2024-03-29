package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.VarDeclaration;

public abstract class AbstractBinding implements IBinding {
	
	protected final static IVariableBinding[] NO_VARIABLES = new IVariableBinding[0];
	protected final static IMethodBinding[] NO_METHODS = new IMethodBinding[0];
	protected final static ITypeBinding[] NO_TYPES = new ITypeBinding[0];
	protected final static ICompilationUnitBinding[] NO_UNITS = new ICompilationUnitBinding[0];
	protected final static ITemplateParameterBinding[] NO_TEMPLATE_PARAMETERS = new ITemplateParameterBinding[0];

	@Override
	public final boolean equals(Object obj) {
		return this == obj;
	}
	
	public final boolean isEqualTo(IBinding binding) {
		if (this == binding)
			return true;
		
		if (binding == null)
			return false;
		
		return getKey().equals(binding.getKey());
	}
	
	static IVariableBinding[] getDeclaredVariables(Dsymbol symbol, DefaultBindingResolver bindingResolver) {
		List<IVariableBinding> vars = new ArrayList<IVariableBinding>();
		addDeclaredVariables(symbol, vars, bindingResolver);
		return vars.toArray(new IVariableBinding[vars.size()]);
	}
	
	static IMethodBinding[] getDeclaredMethods(Dsymbol symbol, DefaultBindingResolver bindingResolver) {
		List<IMethodBinding> types = new ArrayList<IMethodBinding>();
		addDeclaredMethods(symbol, types, bindingResolver);
		return types.toArray(new IMethodBinding[types.size()]);
	}
	
	static ITypeBinding[] getDeclaredTypes(Dsymbol symbol, DefaultBindingResolver bindingResolver) {
		List<ITypeBinding> types = new ArrayList<ITypeBinding>();
		addDeclaredTypes(symbol, types, bindingResolver);
		return types.toArray(new ITypeBinding[types.size()]);
	}
	
	private static void addDeclaredVariables(Dsymbol symbol, List<IVariableBinding> vars, DefaultBindingResolver bindingResolver) {
		if (!(symbol instanceof ScopeDsymbol))
			return;
		
		ScopeDsymbol sd = (ScopeDsymbol) symbol;
		
		if (sd.symtab != null) {
			for(Object value : sd.symtab.values()) {
				if (value instanceof VarDeclaration || value instanceof EnumMember) {
					IBinding binding = bindingResolver.resolveDsymbol((Dsymbol) value, null);
					if (binding instanceof IVariableBinding) {
						vars.add((IVariableBinding) binding);
					}
				}
			}	
		}
		
		if (sd.members != null) {
			for(Dsymbol sym : sd.members) {
				if (sym instanceof TemplateMixin) {
					addDeclaredVariables((TemplateMixin) sym, vars, bindingResolver);
				}
			}
		}
	}
	
	private static void addDeclaredMethods(Dsymbol symbol, List<IMethodBinding> methods, DefaultBindingResolver bindingResolver) {
		if (!(symbol instanceof ScopeDsymbol))
			return;
		
		ScopeDsymbol sd = (ScopeDsymbol) symbol;
		
		if (sd.symtab != null) {
			for(Object value : sd.symtab.values()) {
				Dsymbol toResolve;
				if (value instanceof FuncDeclaration) {
					toResolve = (Dsymbol) value;
				} else if (value instanceof TemplateDeclaration) {
					TemplateDeclaration temp = (TemplateDeclaration) value;
					if (temp.wrapper && temp.members.get(0) instanceof FuncDeclaration) {
						toResolve = temp.members.get(0);
					} else {
						continue;
					}
				} else {
					continue;
				}
				
				IBinding binding = bindingResolver.resolveDsymbol(toResolve, null);
				if (binding != null && binding instanceof IMethodBinding) {
					methods.add((IMethodBinding) binding);
				}
			}	
		}
		
		if (sd.members != null) {
			for(Dsymbol sym : sd.members) {
				if (sym instanceof TemplateMixin) {
					addDeclaredMethods((TemplateMixin) sym, methods, bindingResolver);
				}
			}
		}
	}
	
	private static void addDeclaredTypes(Dsymbol symbol, List<ITypeBinding> types, DefaultBindingResolver bindingResolver) {
		if (!(symbol instanceof ScopeDsymbol))
			return;
		
		ScopeDsymbol sd = (ScopeDsymbol) symbol;
		
		if (sd.symtab != null) {
			for(Object value : sd.symtab.values()) {
				Dsymbol toResolve;
				if (value instanceof ClassDeclaration
						|| value instanceof StructDeclaration
						|| value instanceof AliasDeclaration
						|| value instanceof TypedefDeclaration
						|| value instanceof descent.internal.compiler.parser.EnumDeclaration) {
					toResolve = (Dsymbol) value;
				} else if (value instanceof TemplateDeclaration) {
					TemplateDeclaration temp = (TemplateDeclaration) value;
					if (temp.wrapper) {
						Dsymbol wrapped = temp.members.get(0);
						if (wrapped instanceof ClassDeclaration ||
							wrapped instanceof StructDeclaration) {
							toResolve = wrapped;
						} else {
							continue;
						}
					} else {
						toResolve = temp;
					}
				} else {
					continue;
				}
				
				IBinding binding = bindingResolver .resolveDsymbol(toResolve, null);
				if (binding instanceof ITypeBinding) {
					types.add((ITypeBinding) binding);
				}
			}	
		}
		
		if (sd.members != null) {
			for(Dsymbol sym : sd.members) {
				if (sym instanceof TemplateMixin) {
					addDeclaredTypes((TemplateMixin) sym, types, bindingResolver);
				}
			}
		}
	}
	
	static ITemplateParameterBinding[] getTypeParameters(Dsymbol node, DefaultBindingResolver bindingResolver) {
		TemplateDeclaration template;
		
		if (node.templated()) {
			if (node.parent instanceof TemplateDeclaration) {
				template = (TemplateDeclaration) node.parent;
			} else if (node.parent instanceof TemplateInstance) {
				template = ((TemplateInstance) node.parent).tempdecl;
			} else {
				return NO_TEMPLATE_PARAMETERS;
			}
		} else {
			if (node instanceof TemplateDeclaration) {
				template = (TemplateDeclaration) node;
			} else {
				return NO_TEMPLATE_PARAMETERS;
			}
		}
		
		if (template.parameters == null || template.parameters.isEmpty())
			return NO_TEMPLATE_PARAMETERS;
		
		ITemplateParameterBinding[] parameters = new ITemplateParameterBinding[template.parameters.size()];
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = new TemplateParameterBinding(template.parameters.get(i), bindingResolver);
		}
		return parameters;
	}
	
}
