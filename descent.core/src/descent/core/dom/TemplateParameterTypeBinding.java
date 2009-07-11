package descent.core.dom;

import descent.core.IJavaElement;
import descent.core.Signature;
import descent.internal.compiler.parser.TypeIdentifier;

public class TemplateParameterTypeBinding extends AbstractBinding implements ITypeBinding {
	
	private final TypeIdentifier type;

	public TemplateParameterTypeBinding(TypeIdentifier type) {
		this.type = type;		
	}

	public IBinding getAliasedSymbol() {
		return null;
	}

	public ITypeBinding getAliasedType() {
		return null;
	}

	public ITypeBinding getComponentType() {
		return null;
	}

	public IVariableBinding[] getDeclaredFields() {
		return null;
	}

	public IMethodBinding[] getDeclaredMethods() {
		return null;
	}

	public int getDeclaredModifiers() {
		return 0;
	}

	public ITypeBinding[] getDeclaredTypes() {
		return null;
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
		return null;
	}

	public ITypeBinding getKeyType() {
		return null;
	}

	public int getLowerBound() {
		return 0;
	}

	public long getModifiers() {
		return 0;
	}

	public String getName() {
		return type.ident.toString();
	}

	public IPackageBinding getPackage() {
		return null;
	}

	public ITypeBinding[] getParametersTypes() {
		return null;
	}

	public String getQualifiedName() {
		return null;
	}

	public ITypeBinding getReturnType() {
		return null;
	}

	public ITypeBinding getSuperclass() {
		return null;
	}

	public ITypeBinding getTypedefedType() {
		return null;
	}

	public int getUpperBound() {
		return 0;
	}

	public ITypeBinding getValueType() {
		return null;
	}

	public boolean isAlias() {
		return false;
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
		return false;
	}

	public boolean isDelegate() {
		return false;
	}

	public boolean isDynamicArray() {
		return false;
	}

	public boolean isEnum() {
		return false;
	}

	public boolean isFromSource() {
		return false;
	}

	public boolean isFunction() {
		return false;
	}

	public boolean isInterface() {
		return false;
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
		return false;
	}

	public boolean isPointer() {
		return false;
	}

	public boolean isPrimitive() {
		return false;
	}

	public boolean isSlice() {
		return false;
	}

	public boolean isStaticArray() {
		return false;
	}

	public boolean isStruct() {
		return false;
	}

	public boolean isSubTypeCompatible(ITypeBinding type) {
		return false;
	}

	public boolean isTemplate() {
		return false;
	}

	public boolean isTemplateParameter() {
		return true;
	}

	public boolean isTemplated() {
		return false;
	}

	public boolean isTypedef() {
		return false;
	}

	public boolean isUnion() {
		return false;
	}
	
	public ITemplateParameterBinding[] getTypeParameters() {
		return NO_TEMPLATE_PARAMETERS;
	}

	public IJavaElement getJavaElement() {
		return null;
	}

	public String getKey() {
		return String.valueOf(Signature.C_IDENTIFIER) + type.ident.toString();
	}

	public int getKind() {
		return TYPE;
	}

	public boolean isDeprecated() {
		return false;
	}

	public boolean isSynthetic() {
		return false;
	}

}
