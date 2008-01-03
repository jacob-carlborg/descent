package descent.core.dom;

import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.ILocalVariable;
import descent.core.JavaModelException;
import descent.internal.core.util.Util;

public class VariableBinding extends JavaElementBasedBinding implements IVariableBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final String signature;
	private final boolean isParameter;

	public VariableBinding(DefaultBindingResolver bindingResolver, IJavaElement element, boolean isParameter, String signature) {
		super(element);
		this.bindingResolver = bindingResolver;
		this.isParameter = isParameter;
		this.signature = signature;
	}

	public Object getConstantValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getDeclaringClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public IMethodBinding getDeclaringMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getType() {
		String signature;
		try {
			if (element instanceof IField) {
				signature = ((IField) element).getTypeSignature();
			} else if (element instanceof ILocalVariable) {
				signature = ((ILocalVariable) element).getTypeSignature();
			} else {
				throw new IllegalStateException("element must be IField or ILocalVariable");
			}
			return (ITypeBinding) bindingResolver.resolveBinding(signature);
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return null;
	}

	public IVariableBinding getVariableDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getVariableId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEnumConstant() {
		try {
			return element instanceof IField && ((IField) element).isEnumConstant();
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
	}

	public boolean isVariable() {
		try {
			if (element instanceof IField) {
				return ((IField) element).isVariable();
			} else {
				return ((ILocalVariable) element).isVariable();
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
	}
	
	public boolean isAlias() {
		try {
			if (element instanceof IField) {
				return ((IField) element).isAlias();
			} else {
				return ((ILocalVariable) element).isAlias();
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
	}
	
	public boolean isLocal() {
		return element instanceof ILocalVariable &&
			!isParameter();
	}
	
	public boolean isTypedef() {
		try {
			if (element instanceof IField) {
				return ((IField) element).isTypedef();
			} else {
				return ((ILocalVariable) element).isTypedef();
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
	}

	public boolean isParameter() {
		return isParameter;
	}

	public String getKey() {
		return signature;
	}

	public int getKind() {
		return VARIABLE;
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
