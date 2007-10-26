package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 2.003
public class TraitsExp extends Expression {

	public IdentifierExp ident;
	public Objects args;

	public TraitsExp(Loc loc, IdentifierExp ident, Objects args) {
		super(loc, TOK.TOKtraits);
		this.ident = ident;
		this.args = args;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}


	@Override
	public int getNodeType() {
		return TRAITS_EXP;
	}
	
		@Override
	public Expression semantic(final Scope sc,
			final SemanticContext context)
	{
		
		/* TODO semantic
		TemplateInstance.semanticTiargs(loc, sc, args);
		*/
		//int dim = null != args ? args.size() : 0;
	    char[] ident = this.ident.ident;
		
	    if (equals(ident, Id.isArithmetic))
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
	    
	    else if (equals(ident, Id.isFloating))
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
	    
	    else if (equals(ident, Id.isIntegral))
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
	    
	    else if (equals(ident, Id.isScalar))
	    {
	    	//ISTYPE(t.isscalar())
	    	return isType(new ISTYPE_Conditional()
	    	{
				public boolean check(Type t)
				{
					return t.isscalar(context);
				}
	    	});
	    }
	    
	    else if (equals(ident, Id.isUnsigned))
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
	    
	    else if (equals(ident, Id.isAssociativeArray))
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
	    
	    else if (equals(ident, Id.isStaticArray))
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
	    
	    else if (equals(ident, Id.isAbstractClass))
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
	    
	    else if (equals(ident, Id.isFinalClass))
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
	    
	    else if (equals(ident, Id.isAbstractFunction))
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
	    
	    else if (equals(ident, Id.isVirtualFunction))
	    {
	    	//ISDSYMBOL((f = s.isFuncDeclaration()) != NULL && f.isVirtual())
	    	return isDSymbol(new ISDSYMBOL_Conditional()
	    	{
				public boolean check(Dsymbol s)
				{
					FuncDeclaration f = s.isFuncDeclaration();
					return null != f && f.isVirtual(context);
				}
	    	});
	    }
	    
	    else if (equals(ident, Id.isFinalFunction))
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
	    
	    else if (equals(ident, Id.hasMember) ||
	    		equals(ident, Id.getMember) ||
	    		equals(ident, Id.getVirtualFunctions))
	    {
	    	int dim = null != args ? args.size() : 0;
			if(dim != 2)
			{
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.WrongNumberOfArguments, 0, start,
						length, new String[] { String.valueOf(dim) }));
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			ASTDmdNode o = args.get(0);
			Expression e = isExpression((ASTDmdNode) args.get(1));
			if(null == e)
			{ 
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.StringExpectedAsSecondArgument, 0, start,
						length));
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			e = e.optimize(WANTvalue | WANTinterpret, context);
			if(e.op != TOK.TOKstring)
			{
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.StringExpectedAsSecondArgument, 0, start,
						length));
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			StringExp se = (StringExp) e;
			se = se.toUTF8(sc, context);
			if (se.sz != 1)
			{
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.StringMustBeChars, 0, start,
						length));
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			char[] id = null; /* TODO semantic Lexer.idPool((char *)se.string); */
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
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.InvalidFirstArgument, 0, start,
						length));
				return new IntegerExp(loc, 0, Type.tbool);
			}
	
			if(equals(ident, Id.hasMember))
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
			else if(equals(ident, Id.getMember))
			{
			    e = e.semantic(sc, context);
			    return e;
			}
			else if(equals(ident, Id.getVirtualFunctions))
			{
			    e = e.semantic(sc, context);
	
			    /* Create tuple of virtual function overloads of e
			     */
			    //e.dump(0);
			    Expressions exps = new Expressions();
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
			    overloadApply(f, fpvirtuals, &p);
			    */
	
			    TupleExp tup = new TupleExp(loc, exps);
			    return tup.semantic(sc, context);
			}
			else
			    assert(false);
	    }
	    
	    else if (equals(ident, Id.classInstanceSize))
	    {
	    	int dim = null != args ? args.size() : 0;
			if (dim != 1)
			{
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.WrongNumberOfArguments, 0, start,
						length, new String[] { String.valueOf(dim) }));
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			ASTDmdNode o = args.get(0);
			Dsymbol s = getDsymbol(o, context);
			if(null == s)
			{
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.FirstArgumentIsNotAClass, 0, start,
						length));
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			ClassDeclaration cd = s.isClassDeclaration();
			if (null == cd)
			{
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.FirstArgumentIsNotAClass, 0, start,
						length));
			    return new IntegerExp(loc, 0, Type.tbool);
			}
			return new IntegerExp(loc, cd.structsize, Type.tsize_t);
	    }
	    
	    else if (equals(ident, Id.allMembers)
	    		|| equals(ident, Id.derivedMembers))
	    {
	    	int dim = null != args ? args.size() : 0;
			if (dim != 1)
			{
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.WrongNumberOfArguments, 0, start,
						length, new String[] { String.valueOf(dim) }));
				return new IntegerExp(loc, 0, Type.tbool);
			}
			
			ASTDmdNode o = args.get(0);
			Dsymbol s = getDsymbol(o, context);
			if (null == s)
			{
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.ArgumentHasNoMembers, 0, start,
						length));
			    return new IntegerExp(loc, 0, Type.tbool);
			}
			
			ScopeDsymbol sd = s.isScopeDsymbol();
			if(null == sd)
			{
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.KindSymbolHasNoMembers, 0, start,
						length, new String[] { s.kind(), s.toChars(context) }));
			    return new IntegerExp(loc, 0, Type.tbool);
			}
			
			Expressions exps = new Expressions();
			Louter: while(true)
			{
			    Linner: for(int i = 0; i < sd.members.size(); i++)
			    {
					Dsymbol sm = (Dsymbol) sd.members.get(i);
					if(null != sm.ident)
					{
					    char[] str = sm.ident.ident;
		
					    /* Skip if already present in exps[]
					     */
					    for(Expression exp : exps)
					    {
					    	StringExp se2 = (StringExp) exp;
					    	if(equals(str, se2.string))
					    		continue Linner;
					    }
					    
					    StringExp se = new StringExp(loc, str, str.length);
					    exps.add(se);
					}
			    }
			    ClassDeclaration cd = sd.isClassDeclaration();
			    if (null != cd &&
			    		null != cd.baseClass &&
			    		equals(ident, Id.allMembers))
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
	    	context.acceptProblem(Problem.newSemanticTypeError(
	    			IProblem.UnrecongnizedTrait, 0, this.ident.start, this.ident.length, new String[] { new String(ident) }));
	    	return new IntegerExp(loc, 0, Type.tbool);
	    }
	    
	    assert(false);
	    return null;
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
