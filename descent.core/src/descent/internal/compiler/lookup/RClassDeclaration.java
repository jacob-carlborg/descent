package descent.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;

import descent.core.Flags;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.BaseClass;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.ClassInfoDeclaration;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.FuncDeclarations;
import descent.internal.compiler.parser.IClassDeclaration;
import descent.internal.compiler.parser.ICtorDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IFuncDeclaration;
import descent.internal.compiler.parser.IInterfaceDeclaration;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IVarDeclaration;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.SemanticMixin;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeClass;
import descent.internal.core.util.Util;

public class RClassDeclaration extends RAggregateDeclaration implements
		IClassDeclaration {
	
	private Type type;
	private IClassDeclaration baseClass;
	private ICtorDeclaration ctor;
	private BaseClasses interfaces;
	private BaseClasses baseclasses;
	
	private boolean vtblReady;
	private List vtbl;
	private List vtblFinal;
	
	private ClassInfoDeclaration vclassinfo;

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
				Type supertype = getTypeFromSignature(sig, true);
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
		if (baseclasses == null) {
			baseclasses = new BaseClasses();
			IClassDeclaration baseClass = baseClass();
			if (baseClass != null) {
				BaseClass bc = getBaseClass(baseClass.type());
				if (bc != null) {
					baseclasses.add(bc);
				}
			}
			baseclasses.addAll(interfaces());
		}
		return baseclasses;
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
					Type t = getTypeFromSignature(sig, true);
					BaseClass bc = getBaseClass(t);
					if (bc != null) {
						interfaces.add(bc);
					}
				}
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
		return interfaces;
	}
	
	private BaseClass getBaseClass(Type t) {
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

			return baseClass;
		} else {
			return null;
		}
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
	
	@Override
	public IDsymbol search(Loc loc, char[] ident, int flags, SemanticContext context) {
		IDsymbol result = super.search(loc, ident, flags, context);
		if (result == null) {
			for(BaseClass bc : baseclasses()) {
				result = bc.base.search(loc, ident, flags, context);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	public List vtbl() {
		buildVtbls();
		return vtbl;
	}

	public List vtblFinal() {
		buildVtbls();
		return vtblFinal;
	}
	
	private void buildVtbls() {
		if (vtblReady) {
			return;
		}
		
		vtbl = new ArrayList();
		vtblFinal = new ArrayList();
		
		for(IDsymbol s : members()) {
			IFuncDeclaration f = s.isFuncDeclaration();
			if (f == null || !f.isVirtual(context)) {
				continue;
			}
			
			if (f.isFinal()) {
				vtblFinal.add(f);
			} else {
				vtbl.add(f);
			}
		}
		
		IClassDeclaration baseClass = baseClass();
		if (baseClass != null) {
			vtbl.addAll(baseClass.vtbl());
			vtblFinal.addAll(baseClass.vtblFinal());
		}
		
		vtblReady = true;
	}
	
	@Override
	public IClassDeclaration isClassDeclaration() {
		return this;
	}
	
	@Override
	public Type handle() {
		return type();
	}
	
	@Override
	public String mangle(SemanticContext context) {
		return SemanticMixin.mangle(this, context);
	}
	
	@Override
	public Type type() {
		if (type == null) {
			type = new TypeClass(this);
			merge(type);
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
		return vclassinfo;
	}
	
	public void vclassinfo(ClassInfoDeclaration vclassinfo) {
		this.vclassinfo = vclassinfo;
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
		try {
			return (((IType) element).getFlags() & Flags.AccAbstract) != 0;
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}
	
	@Override
	public PROT getAccess(IDsymbol smember) {
		return SemanticMixin.getAccess(this, smember);
	}
	
	public int vtblOffset() {
		return 1;
	}
	
	public char getSignaturePrefix() {
		return ISignatureConstants.CLASS;
	}
	
	@Override
	public String kind() {
		return "class";
	}

}
