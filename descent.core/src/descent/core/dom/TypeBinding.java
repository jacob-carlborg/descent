package descent.core.dom;

import descent.core.IType;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.UnionDeclaration;

public class TypeBinding extends JavaElementBasedBinding implements ITypeBinding {
	
	private final String key;
	
	public TypeBinding(DefaultBindingResolver resolver, Dsymbol node, String key) {
		super(resolver, node);
		this.key = key;
	}
	
	public ITypeBinding getComponentType() {
		return null;
	}
	
	public ITypeBinding getAliasedType() {
		if (!(node instanceof AliasDeclaration))
			return null;
		
		AliasDeclaration alias = (AliasDeclaration) node;
		if (alias.aliassym == null) {
			return bindingResolver.resolveType(alias.type, null);
		}
		return null;
	}
	
	public IBinding getAliasedSymbol() {
		if (!(node instanceof AliasDeclaration))
			return null;
		
		AliasDeclaration alias = (AliasDeclaration) node;
		if (alias.aliassym != null) {
			return bindingResolver.resolveDsymbol(alias.aliassym, null);
		}
		return null;
	}
	
	public ITypeBinding getTypedefedType() {
		if (!(node instanceof TypedefDeclaration))
			return null;
		
		TypedefDeclaration typedef = (TypedefDeclaration) node;
		if (typedef.basetype != null) {
			return bindingResolver.resolveType(typedef.basetype, null);
		}
		return null;
	}
	
	public IVariableBinding[] getDeclaredFields() {
		return getDeclaredVariables(node, bindingResolver);
	}
	
	public IMethodBinding[] getDeclaredMethods() {
		return getDeclaredMethods(node, bindingResolver);
	}
	
	public int getDeclaredModifiers() {
		return 0;
	}
	
	public ITypeBinding[] getDeclaredTypes()  {
		return getDeclaredTypes(node, bindingResolver);
	}
	
	public IMethodBinding getDeclaringMethod() {
		return null;
	}
	
	public ITypeBinding getDeclaringType() {
		return null;
	}
	
	public int getDimension() {
		return 0;
	}
	
	public ITypeBinding[] getInterfaces() {
		if (!(node instanceof ClassDeclaration))
			return NO_TYPES;
		
		ClassDeclaration c = (ClassDeclaration) node;
		if (c.interfaces == null || c.interfaces.isEmpty()) {
			return NO_TYPES;
		}
		
		ITypeBinding[] types = new ITypeBinding[c.interfaces.size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = (ITypeBinding) bindingResolver.resolveDsymbol(c.interfaces.get(i).base, null);
		}
		return types;
	}
	
	public String getKey() {
		return key;
	}

	public ITypeBinding getKeyType() {
		return null;
	}

	public int getKind() {
		return TYPE;
	}

	public IPackageBinding getPackage() {
		return null;
	}

	public ITypeBinding[] getParametersTypes() {
		return PrimitiveTypeBinding.NO_TYPES;
	}

	public String getQualifiedName() {
		return ((IType) getJavaElement()).getFullyQualifiedName();
	}

	public ITypeBinding getReturnType() {
		return null;
	}

	public ITypeBinding getSuperclass() {
		if (node instanceof ClassDeclaration) {
			ClassDeclaration c = (ClassDeclaration) node;
			return (ITypeBinding) bindingResolver.resolveDsymbol(c.baseClass, null);
		} else if (node instanceof EnumDeclaration) {
			EnumDeclaration e = (EnumDeclaration) node;
			return bindingResolver.resolveType(e.memtype, null);
		} else {
			return null;
		}
	}

	public ITypeBinding getValueType() {
		return null;
	}

	public boolean isAnonymous() {
		return false;
	}

	public boolean isAssignmentCompatible(ITypeBinding variableType) {
		return false;
	}

	public boolean isAssociativeArray() {
		return false;
	}

	public boolean isCastCompatible(ITypeBinding type) {
		return false;
	}

	public boolean isClass() {
		return node instanceof ClassDeclaration 
			&& !(node instanceof InterfaceDeclaration);
	}

	public boolean isDelegate() {
		return false;
	}

	public boolean isDynamicArray() {
		return false;
	}

	public boolean isEnum() {
		return node instanceof EnumDeclaration;
	}
	
	public boolean isFromSource() {
		return true;
	}
	
	public boolean isFunction() {
		return false;
	}

	public boolean isGenericType() {
		return false;
	}

	public boolean isInterface() {
		return node instanceof InterfaceDeclaration;
	}

	public boolean isLocal() {
		return false;
	}

	public boolean isMember() {
		return false;
	}

	public boolean isNested() {
		return false;
	}

	public boolean isNullType() {
		return false;
	}

	public boolean isParameterizedType() {
		return node instanceof TemplateDeclaration || node.templated();
	}

	public boolean isPointer() {
		return false;
	}

	public boolean isPrimitive() {
		return false;
	}

	public boolean isStaticArray() {
		return false;
	}

	public boolean isStruct() {
		return node instanceof StructDeclaration &&
			!(node instanceof UnionDeclaration);
	}

	public boolean isSubTypeCompatible(ITypeBinding type) {
		return false;
	}

	public boolean isSynthetic() {
		return false;
	}
	
	public boolean isTemplate() {
		return node instanceof TemplateDeclaration;
	}

	public boolean isTopLevel() {
		return false;
	}

	public boolean isUnion() {
		return node instanceof UnionDeclaration;
	}
	
	public boolean isAlias() {
		return node instanceof AliasDeclaration;
	}
	
	public boolean isTypedef() {
		return node instanceof TypedefDeclaration;
	}
	
	public boolean isTemplateParameter() {
		return false;
	}
	
	public ITemplateParameterBinding[] getTypeParameters() {
		return getTypeParameters(node, bindingResolver);
	}
	
	@Override
	public String toString() {
		return getJavaElement().toString();
	}

	public int getLowerBound() {
		return 0;
	}

	public int getUpperBound() {
		return 0;
	}

	public boolean isSlice() {
		return false;
	}

}
