package descent.internal.compiler.lookup;

import java.util.List;

import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.BaseClass;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.ClassInfoDeclaration;
import descent.internal.compiler.parser.SemanticMixin;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.FuncDeclarations;
import descent.internal.compiler.parser.IClassDeclaration;
import descent.internal.compiler.parser.ICtorDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IInterfaceDeclaration;
import descent.internal.compiler.parser.IVarDeclaration;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeClass;
import descent.internal.core.util.Util;

public class RClassDeclaration extends RAggregateDeclaration implements
		IClassDeclaration {
	
	private TypeClass type;
	private IClassDeclaration baseClass;
	private ICtorDeclaration ctor;
	private BaseClasses interfaces;

	public RClassDeclaration(IType element, SemanticContext context) {
		super(element, context);
	}

	public IClassDeclaration baseClass() {
		if (baseClass == null) {
			IType t = (IType) element;
			String sig;
			try {
				sig = t.getSuperclassTypeSignature();
				if (sig == null) { // May be the case of Object
					return null;
				}
				Type supertype = getTypeFromSignature(sig);
				if (supertype instanceof TypeClass) {
					TypeClass tc = (TypeClass) supertype;
					baseClass = tc.sym;
				}
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
		return baseClass;
	}

	public BaseClasses baseclasses() {
		// TODO Auto-generated method stub
		return null;
	}

	public ICtorDeclaration ctor() {
		if (ctor == null) {
			for(IDsymbol s : members()) {
				ctor = s.isCtorDeclaration();
				if (ctor != null) {
					break;
				}
			}
		}
		return ctor;
	}

	public void defaultCtor(CtorDeclaration defaultCtor) {
		// TODO Auto-generated method stub

	}

	public FuncDeclarations dtors() {
		// TODO Auto-generated method stub
		return null;
	}

	public void dtors(FuncDeclarations dtors) {
		// TODO Auto-generated method stub

	}

	public BaseClasses interfaces() {
		if (interfaces == null) {
			interfaces = new BaseClasses();
			
			IType type = (IType) element;
			try {
				String[] supersignatures = type.getSuperInterfaceTypeSignatures();
				for(String sig : supersignatures) {
					Type t = getTypeFromSignature(sig);
					if (t instanceof TypeClass) {
						TypeClass tc = (TypeClass) t;
						
						// TODO protection
						BaseClass baseClass = new BaseClass(tc, PROT.PROTpublic);
						baseClass.base = tc.sym;
						
						// TODO this may kill performance if the hierarchy is too deep,
						// see if we need to hide baseInterfaces with a method
						IInterfaceDeclaration inter = tc.sym.isInterfaceDeclaration();
						if (inter != null) {
							baseClass.baseInterfaces = inter.interfaces();
						}
						
						interfaces.add(baseClass);
					}
				}
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
		return interfaces;
	}

	public boolean isBaseOf(IClassDeclaration cd, int[] poffset,
			SemanticContext context) {
		if (poffset != null) {
			poffset[0] = 0;
		}
		while (cd != null) {
			IClassDeclaration base = cd.baseClass();
			if (SemanticMixin.equals(this, base)) {
				return true;
			}
			cd = base;
		}
		return false;
	}

	public boolean isNested() {
		// TODO Auto-generated method stub
		return false;
	}

	public void isabstract(boolean isabstract) {
		// TODO Auto-generated method stub

	}

	public List vtbl() {
		// TODO Auto-generated method stub
		return null;
	}

	public List vtblFinal() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IClassDeclaration isClassDeclaration() {
		return this;
	}
	
	@Override
	public Type type() {
		if (type == null) {
			type = new TypeClass(this);
			if (type != null) {
				type.deco = "C" + getTypeDeco();
			}
		}
		return type;
	}

	@Override
	public Type getType() {
		return type();
	}
	
	public IVarDeclaration vthis() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ClassInfoDeclaration vclassinfo() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void vclassinfo(ClassInfoDeclaration vclassinfo) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isCOMclass() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isauto() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isAbstract() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public PROT getAccess(IDsymbol smember) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int vtblOffset() {
		return 1;
	}

}
