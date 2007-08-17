package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeStruct extends Type {
	
	public StructDeclaration sym;

	public TypeStruct(StructDeclaration sym) {
		super(TY.Tstruct, null);
		this.sym = sym;
	}
	
	public void accept0(IASTVisitor visitor) {
		Assert.fail("Accept0 on fake class");
	}
	
	@Override
	public Expression defaultInit(SemanticContext context) {
		return super.defaultInit(context);
		/* TODO semantic
		Symbol s;
	    Declaration d;

	    s = sym.toInitializer();
	    d = new SymbolDeclaration(sym.loc, s, sym);
	    assert(d);
	    d.type = this;
	    return new VarExp(sym.loc, d);
	    */
	}

	@Override
	public int getNodeType() {
		return TYPE_STRUCT;
	}

}
