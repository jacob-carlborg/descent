package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

public class BaseClass extends ASTNode {
	
	public Modifier modifier;
	public Type type;
	public Type sourceType;
	public PROT protection;
	public ClassDeclaration base;
	public int offset;				// 'this' pointer offset
    public List<FuncDeclaration> vtbl;	// for interfaces: Array of FuncDeclaration's
										// making up the vtbl[]
	
	public List<BaseClass> baseInterfaces;		// if BaseClass is an interface, these
									// are a copy of the InterfaceDeclaration::interfaces
	
	public BaseClass(Type type, Modifier modifier, PROT protection) {
		this.type = type;
		this.sourceType = type;
		this.modifier = modifier;
		this.protection = protection;
	}
	
	public BaseClass(Type type, PROT protection) {
		this.type = type;
		this.protection = protection;
	}
	
	public void copyBaseInterfaces(List<BaseClass> vtblInterfaces) {
	    baseInterfaces = new ArrayList<BaseClass>(base.interfaces.size());

	    for (int i = 0; i < base.interfaces.size(); i++)
	    {
		BaseClass b = baseInterfaces.get(i);
		BaseClass b2 = base.interfaces.get(i);

		Assert.isTrue(b2.vtbl.size() == 0);// should not be filled yet
		/* TODO semantic
		memcpy(b, b2, sizeof(BaseClass));
		*/

		if (i == 0)				// single inheritance is i==0
		    vtblInterfaces.add(b);	// only need for M.I.
		b.copyBaseInterfaces(vtblInterfaces);
	    }
	}
	
	@Override
	public int getNodeType() {
		return BASE_CLASS;
	}

}
