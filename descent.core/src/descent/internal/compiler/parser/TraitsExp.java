package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class TraitsExp extends Expression {

	public IdentifierExp ident;
	public List<ASTNode> args;

	public TraitsExp(Loc loc, IdentifierExp ident, List<ASTNode> args) {
		super(loc, TOK.TOKtraits);
		this.ident = ident;
		this.args = args;
	}
	
	@Override
	public Expression semantic(final Scope sc,
			final SemanticContext context)
	{
		
		/* TODO semantic TemplateInstance.semanticTiargs(loc, sc, args); */
		//int dim = null != args ? args.size() : 0;
	    String ident = this.ident.ident;
		
	    if (ident.equals(Id.isArithmetic.string))
	    {
	    	//ISTYPE(t.isintegral() || t.isfloating())
	    	return isType(new ISTYPE_Conditional()
	    	{
				public boolean check(Type t)
				{
					return t.isintegral() || t.isfloating();
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.isFloating.string))
	    {
	    	//ISTYPE(t.isfloating())
	    	return isType(new ISTYPE_Conditional()
	    	{
				public boolean check(Type t)
				{
					return t.isfloating();
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.isIntegral.string))
	    {
	    	//ISTYPE(t.isintegral())
	    	return isType(new ISTYPE_Conditional()
	    	{
				public boolean check(Type t)
				{
					return t.isintegral();
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.isScalar.string))
	    {
	    	//ISTYPE(t.isscalar())
	    	return isType(new ISTYPE_Conditional()
	    	{
				public boolean check(Type t)
				{
					return t.isscalar();
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.isUnsigned.string))
	    {
	    	//ISTYPE(t.isunsigned())
	    	return isType(new ISTYPE_Conditional()
	    	{
				public boolean check(Type t)
				{
					return t.isunsigned();
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.isAssociativeArray.string))
	    {
	    	//ISTYPE(t.toBasetype().ty == Taarray)
	    	return isType(new ISTYPE_Conditional()
	    	{
				public boolean check(Type t)
				{
					return t.toBasetype(context).ty == TY.Taarray;
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.isStaticArray.string))
	    {
	    	//ISTYPE(t.toBasetype().ty == Tsarray)
	    	return isType(new ISTYPE_Conditional()
	    	{
				public boolean check(Type t)
				{
					return t.toBasetype(context).ty == TY.Tsarray;
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.isAbstractClass.string))
	    {
	    	//ISTYPE(t.toBasetype().ty == Tclass && ((TypeClass *)t.toBasetype()).sym.isAbstract())
	    	return isType(new ISTYPE_Conditional()
	    	{
				public boolean check(Type t)
				{
					return t.toBasetype(context).ty == TY.Tclass && 
							((TypeClass) t.toBasetype(context)).sym.isAbstract();
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.isFinalClass.string))
	    {
			//ISTYPE(t.toBasetype().ty == Tclass && ((TypeClass *)t.toBasetype()).sym.storage_class & STCfinal)
	    	return isType(new ISTYPE_Conditional()
	    	{
				public boolean check(Type t)
				{
					return t.toBasetype(context).ty == TY.Tclass && 
							((((TypeClass) t.toBasetype(context)).sym.storage_class
							& STC.STCfinal) != 0);
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.isAbstractFunction.string))
	    {
	    	//ISDSYMBOL((f = s.isFuncDeclaration()) != NULL && f.isAbstract())
	    	return isDSymbol(new ISDSYMBOL_Conditional()
	    	{
				public boolean check(Dsymbol s)
				{
					FuncDeclaration f = s.isFuncDeclaration();
					return null != f && f.isAbstract();
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.isVirtualFunction.string))
	    {
	    	//ISDSYMBOL((f = s.isFuncDeclaration()) != NULL && f.isVirtual())
	    	return isDSymbol(new ISDSYMBOL_Conditional()
	    	{
				public boolean check(Dsymbol s)
				{
					FuncDeclaration f = s.isFuncDeclaration();
					return null != f && f.isVirtual();
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.isFinalFunction.string))
	    {
	    	//ISDSYMBOL((f = s.isFuncDeclaration()) != NULL && f.isFinal())
	    	return isDSymbol(new ISDSYMBOL_Conditional()
	    	{
				public boolean check(Dsymbol s)
				{
					FuncDeclaration f = s.isFuncDeclaration();
					return null != f && f.isFinal();
				}
	    	});
	    }
	    
	    else if (ident.equals(Id.hasMember.string) ||
		     ident.equals(Id.getMember.string) ||
		     ident.equals(Id.getVirtualFunctions.string))
	    {
	    	int dim = null != args ? args.size() : 0;
			if(dim != 2)
			{
				error("wrong number of arguments %d", dim);
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			ASTNode o = args.get(0);
			Expression e = null; /* TODO semantic isExpression((Object) args.get(1)); */
			if(null == e)
			{ 
				error("string expected as second argument");
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			e = e.optimize(WANTvalue | WANTinterpret);
			if(e.op != TOK.TOKstring)
			{
				error("string expected as second argument");
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			StringExp se = (StringExp) e;
			se = se.toUTF8(sc);
			if (se.sz != 1)
			{
				error("string must be chars");
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			Identifier id = null; /* TODO semantic Lexer.idPool((char *)se.string); */
			Type t = null; /* TODO semantic isType(o); */
			e = null; /* TODO semantic isExpression(o); */
			Dsymbol s = null; /* TODO semantic isDsymbol(o); */
			if(null != t)
			    e = new TypeDotIdExp(loc, t, new IdentifierExp(Loc.ZERO, id));
			else if(null != e)
			    e = new DotIdExp(loc, e, new IdentifierExp(Loc.ZERO, id));
			else if (null != s)
			{
				e = new DsymbolExp(loc, s);
			    e = new DotIdExp(loc, e, new IdentifierExp(Loc.ZERO, id));
			}
			else
			{
				error("invalid first argument");
				return new IntegerExp(loc, 0, Type.tbool);
			}
	
			if(ident.equals(Id.hasMember.string))
			{
				/* Take any errors as meaning it wasn't found
			     */
			    int errors = context.global.errors;
			    context.global.gag++;
			    e = e.semantic(sc, context);
			    context.global.gag--;
			    if(errors != context.global.errors)
			    {
			    	if (context.global.gag == 0)
				    	context.global.errors = errors;
			    	return new IntegerExp(loc, 0, Type.tbool);
			    }
			    else
			    	return new IntegerExp(loc, 1, Type.tbool);
			}
			else if(ident.equals(Id.getMember))
			{
			    e = e.semantic(sc, context);
			    return e;
			}
			else if(ident.equals(Id.getVirtualFunctions.string))
			{
			    e = e.semantic(sc, context);
	
			    /* Create tuple of virtual function overloads of e
			     */
			    //e.dump(0);
			    List<Expression> exps = new ArrayList<Expression>();
			    FuncDeclaration f;
			    if (e.op == TOK.TOKvar)
			    {
			    	VarExp ve = (VarExp) e;
					f = ve.var.isFuncDeclaration();
			    }
			    else if (e.op == TOK.TOKdotvar)
			    {
			    	DotVarExp dve = (DotVarExp) e;
					f = dve.var.isFuncDeclaration();
			    }
			    else
			    {
			    	f = null;
			    }
			    
			    /* TODO semantic
				Pvirtuals p;
			    p.exps = exps;
			    p.e1 = e;
			    overloadApply(f, fpvirtuals, &p); */
	
			    TupleExp tup = new TupleExp(loc, exps);
			    return tup.semantic(sc, context);
			}
			else
			    assert(false);
	    }
	    
	    else if (ident.equals(Id.classInstanceSize.string))
	    {
	    	int dim = null != args ? args.size() : 0;
			if (dim != 1)
			{
				error("wrong number of arguments %d", dim);
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			ASTNode o = args.get(0);
			Dsymbol s = null; /* TODO semantic getDsymbol(o); */
			if(null == s)
			{
				error("first argument is not a class");
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			ClassDeclaration cd = s.isClassDeclaration();
			if (null == cd)
			{
			    error("first argument is not a class");
			    return new IntegerExp(loc, 0, Type.tbool);
			}
			return new IntegerExp(loc, cd.structsize, Type.tsize_t);
	    }
	    
	    else if (ident.equals(Id.allMembers.string)
	    		|| ident.equals(Id.derivedMembers.string))
	    {
	    	int dim = null != args ? args.size() : 0;
			if (dim != 1)
			{
				error("wrong number of arguments %d", dim);
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			ASTNode o = args.get(0);
			Dsymbol s = null;/* TODO semantic getDsymbol(o); */
			if (null == s)
			{
			    error("argument has no members");
			    return new IntegerExp(loc, 0, Type.tbool);
			}
			
			ScopeDsymbol sd = s.isScopeDsymbol();
			if(null == sd)
			{
			    error("%s %s has no members", s.kind(), s.toChars());
			    return new IntegerExp(loc, 0, Type.tbool);
			}
			
			List<StringExp> exps = new ArrayList<StringExp>();
			Louter: while(true)
			{
			    Linner: for(int i = 0; i < sd.members.size(); i++)
			    {
					Dsymbol sm = (Dsymbol) sd.members.get(i);
					if(null != sm.ident)
					{
					    String str = sm.ident.toChars();
		
					    /* Skip if already present in exps[]
					     */
					    for(StringExp se2 : exps)
					    {
					    	if(str.equals(se2.string))
					    		continue Linner;
					    }
					    
					    StringExp se = new StringExp(loc, str);
					    exps.add(se);
					}
			    }
			    ClassDeclaration cd = sd.isClassDeclaration();
			    if (null != cd &&
			    		null != cd.baseClass &&
			    		ident.equals(Id.allMembers.string))
			    	sd = cd.baseClass;	// do again with base class
			    else
			    	break Louter;
			}
			
			Expression e = new ArrayLiteralExp(loc, exps);
			e = e.semantic(sc, context);
			return e;
	    }
	    
	    else
	    {
	    	error("unrecognized trait %s", ident);
	    	return new IntegerExp(loc, 0, Type.tbool);
	    }
	    
	    assert(false);
	    return null;
	}

	@Override
	public int getNodeType() {
		return TRAITS_EXP;
	}
	
	/*
	 * #define ISTYPE(cond) \
	for (size_t i = 0; i < dim; i++)	\
	{   Type *t = getType((Object *)args.data[i]);	\
	    if (!t)				\
		goto Lfalse;			\
	    if (!(cond))			\
		goto Lfalse;			\
	}					\
	if (!dim)				\
	    goto Lfalse;			\
	goto Ltrue;
	 */
	private static interface ISTYPE_Conditional
	{
		public boolean check(Type t);
	}
	
	private IntegerExp isType(ISTYPE_Conditional cond)
	{
		int dim = null != args ? args.size() : 0;
		for(int i = 0; i < dim; i++)
		{
			Type t = null; /* TODO semantic getType((Object)args.get(i)); */
		    if(null == t)
		    	return new IntegerExp(loc, 0, Type.tbool);
		    if(!cond.check(t))
		    	return new IntegerExp(loc, 0, Type.tbool);
		}
		
		if(0 == dim)
			return new IntegerExp(loc, 0, Type.tbool);
		
		return new IntegerExp(loc, 1, Type.tbool);
	}
	
	/*
	 * #define ISDSYMBOL(cond) \
	for (size_t i = 0; i < dim; i++)	\
	{   Dsymbol *s = getDsymbol((Object *)args.data[i]);	\
	    if (!s)				\
		goto Lfalse;			\
	    if (!(cond))			\
		goto Lfalse;			\
	}					\
	if (!dim)				\
	    goto Lfalse;			\
	goto Ltrue;
	 */
	private static interface ISDSYMBOL_Conditional
	{
		public boolean check(Dsymbol s);
	}
	
	private IntegerExp isDSymbol(ISDSYMBOL_Conditional cond)
	{
		int dim = null != args ? args.size() : 0;
		for (int i = 0; i < dim; i++)
		{
			Dsymbol s = null; /* TODO semantic getDsymbol((Object) args.data[i]); */
		    if(null == s)
		    	return new IntegerExp(loc, 0, Type.tbool);
		    if(!cond.check(s))
		    	return new IntegerExp(loc, 0, Type.tbool);
		}
		if(0 == dim)
			return new IntegerExp(loc, 0, Type.tbool);
		
		return new IntegerExp(loc, 1, Type.tbool);
	}
	
}
