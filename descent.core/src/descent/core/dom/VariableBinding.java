package descent.core.dom;

import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.ILocalVariable;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.core.util.Util;

public class VariableBinding extends JavaElementBasedBinding implements IVariableBinding {
	
	private final String signature;
	private final boolean isParameter;
	

	public VariableBinding(DefaultBindingResolver bindingResolver, IJavaElement element, Dsymbol node, boolean isParameter, String signature) {
		super(bindingResolver, element, node);
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

	public IBinding getType() {
		String signature;
		try {
			if (getJavaElement() instanceof IField) {
				signature = ((IField) getJavaElement()).getTypeSignature();
			} else if (getJavaElement() instanceof ILocalVariable) {
				signature = ((ILocalVariable) getJavaElement()).getTypeSignature();
			} else {
				return null;
			}
			return bindingResolver.resolveBinding(signature);
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
			return getJavaElement() instanceof IField && ((IField) getJavaElement()).isEnumConstant();
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
	}

	public boolean isVariable() {
		if (node != null) {
			return node instanceof VarDeclaration;
		}
		
		try {
			if (getJavaElement() instanceof IField) {
				return ((IField) getJavaElement()).isVariable();
			} else if (getJavaElement() instanceof ILocalVariable) {
				return ((ILocalVariable) getJavaElement()).isVariable();
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
	}
	
	public boolean isAlias() {
		if (node != null) {
			return node instanceof descent.internal.compiler.parser.AliasDeclaration;
		}
		
		try {
			if (getJavaElement() instanceof IField) {
				return ((IField) getJavaElement()).isAlias();
			} else if (getJavaElement() instanceof ILocalVariable) {
				return ((ILocalVariable) getJavaElement()).isAlias();
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
	}
	
	public boolean isLocal() {
		if (node != null) {
			return node.effectiveParent() instanceof FuncDeclaration;
		}
		
		return getJavaElement() instanceof ILocalVariable &&
			!isParameter();
	}
	
	public boolean isTypedef() {
		if (node != null) {
			return node instanceof descent.internal.compiler.parser.TypedefDeclaration;
		}
		
		try {
			if (getJavaElement() instanceof IField) {
				return ((IField) getJavaElement()).isTypedef();
			} else if (getJavaElement() instanceof ILocalVariable) {
				return ((ILocalVariable) getJavaElement()).isTypedef();
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
