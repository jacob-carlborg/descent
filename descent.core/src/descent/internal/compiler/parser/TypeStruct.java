package descent.internal.compiler.parser;

import java.util.List;

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

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context)
	{
		return merge(context);
	}

	@Override
	public int alignsize()
	{
		int sz;

	    sym.size();		// give error for forward references
	    sz = sym.alignsize;
	    if (sz > sym.structalign)
	    	sz = sym.structalign;
	    return sz;
	}

	@Override
	public boolean checkBoolean(SemanticContext context)
	{
		// TODO Auto-generated method stub
		return super.checkBoolean(context);
	}

	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context)
	{
		// TODO Auto-generated method stub
		return super.dotExp(sc, e, ident, context);
	}

	@Override
	public boolean hasPointers(SemanticContext context)
	{
		// TODO Auto-generated method stub
		return super.hasPointers(context);
	}

	@Override
	public boolean isZeroInit()
	{
		// TODO Auto-generated method stub
		return super.isZeroInit();
	}

	@Override
	public int memalign(int salign)
	{
		// TODO Auto-generated method stub
		return super.memalign(salign);
	}

	@Override
	public int size(Loc loc)
	{
		return sym.size(/* PERHAPS loc */);
	}

	@Override
	public Type syntaxCopy()
	{
		// TODO Auto-generated method stub
		return super.syntaxCopy();
	}

	@Override
	public void toCBuffer2(OutBuffer argbuf, Object object, HdrGenState hgs)
	{
		// TODO Auto-generated method stub
		super.toCBuffer2(argbuf, object, hgs);
	}

	@Override
	public void toDecoBuffer(OutBuffer buf)
	{
		// TODO Auto-generated method stub
		super.toDecoBuffer(buf);
	}

	@Override
	public Dsymbol toDsymbol(Scope sc, SemanticContext context)
	{
		return sym;
	}

	@Override
	public String toChars()
	{
		// TODO Auto-generated method stub
		return super.toChars();
	}
	
	public void toTypeInfoBuffer(OutBuffer buf)
	{
		// TODO Auto-generated method stub
	}
	
	/*
	dt_t[] toDt(dt_t[] pdt)
	{
		// TODO semantic
	}
	*/
	
	public MATCH deduceType(Scope sc, Type tparam, 
			List<TemplateParameter> parameters, 
			List<ASTDmdNode> dedtypes)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
    TypeInfoDeclaration getTypeInfoDeclaration()
    {
    	// TODO Auto-generated method stub
    	return null;
    }

}
