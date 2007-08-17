package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeClass extends Type {
	
	public ClassDeclaration sym;
	
	public TypeClass(ClassDeclaration sym) {
		super(TY.Tclass, null);
		this.sym = sym;
	}
	
	public void accept0(IASTVisitor visitor) {
		Assert.fail("Accept0 on fake class");
	}
	
	@Override
	public boolean isauto() {
		return sym.isauto;
	}
	
	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
	    e = new NullExp(Loc.ZERO);
	    e.type = this;
	    return e;
	}
	
	@Override
	public int getNodeType() {
		return TYPE_CLASS;
	}

}
