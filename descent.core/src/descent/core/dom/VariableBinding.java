package descent.core.dom;

import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.VarDeclaration;

public class VariableBinding extends JavaElementBasedBinding implements IVariableBinding {
	
	private final String signature;	

	public VariableBinding(DefaultBindingResolver bindingResolver, Dsymbol node, String signature) {
		super(bindingResolver, node);
		this.signature = signature;
	}

	public Object getConstantValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public IBinding getDeclaringSymbol() {
		return bindingResolver.resolveDsymbol(node.parent);
	}

	public ITypeBinding getType() {
		if (node instanceof VarDeclaration) {
			VarDeclaration var = (VarDeclaration) node;
			return bindingResolver.resolveType(var.type);
		} else if (node instanceof EnumMember) {
			EnumMember em = (EnumMember) node;
			if (em.sourceType != null) {
				return bindingResolver.resolveType(em.type);
			} else {
				if (!(em.parent instanceof EnumDeclaration)) {
					return null;
				}
				EnumDeclaration parent = (EnumDeclaration) em.parent;
				return bindingResolver.resolveType(parent.type);
			}
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
		return node instanceof EnumMember;
	}

	public boolean isVariable() {
		return node instanceof VarDeclaration;
	}
	
	public boolean isLocal() {
		return node.effectiveParent() instanceof FuncDeclaration;
	}

	public boolean isParameter() {
		if (node instanceof VarDeclaration) {
			return ((VarDeclaration) node).isParameter();
		}
		return false;
	}

	public String getKey() {
		return signature;
	}

	public int getKind() {
		return VARIABLE;
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

}
