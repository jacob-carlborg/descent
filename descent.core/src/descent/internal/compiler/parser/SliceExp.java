package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class SliceExp extends UnaExp {

	public Expression lwr;
	public Expression upr;
	public VarDeclaration lengthVar;

	public SliceExp(Loc loc, Expression e1, Expression lwr, Expression upr) {
		super(loc, TOK.TOKslice, e1);
		this.lwr = lwr;
		this.upr = upr;
		this.lengthVar = null;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression syntaxCopy() {
		Expression lwr = null;
		if (this.lwr != null) {
			lwr = this.lwr.syntaxCopy();
		}

		Expression upr = null;
		if (this.upr != null) {
			upr = this.upr.syntaxCopy();
		}

		return new SliceExp(loc, e1.syntaxCopy(), lwr, upr);
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		return super.semantic(sc, context);
		/*
		Expression e;
	    AggregateDeclaration ad;
	    //FuncDeclaration fd;
	    ScopeDsymbol sym;

	    if (type != null)
		return this;

	    super.semantic(sc, context);
	    e1 = resolveProperties(sc, e1, context);

	    e = this;

	    Type t = e1.type.toBasetype(context);
	    if (t.ty == Tpointer)
	    {
		if (lwr == null || upr == null)
		    error("need upper and lower bound to slice pointer");
	    }
	    else if (t.ty == Tarray)
	    {
	    }
	    else if (t.ty == Tsarray)
	    {
	    }
	    else if (t.ty == Tclass)
	    {
	        ad = ((TypeClass )t).sym;
	        // goto L1;
	    }
	    else if (t.ty == Tstruct)
	    {
	        ad = ((TypeStruct )t).sym;

	    L1:
		if (search_function(ad, Id.slice, context) != null)
	        {
	            // Rewrite as e1.slice(lwr, upr)
		    e = new DotIdExp(loc, e1, new IdentifierExp(loc, Id.slice));

		    if (lwr != null)
		    {
		    	Assert.isNotNull(upr);
			e = new CallExp(loc, e, lwr, upr);
		    }
		    else
		    {	
		    	Assert.isTrue(upr == null);
			e = new CallExp(loc, e);
		    }
		    e = e.semantic(sc, context);
		    return e;
	        }
		// goto Lerror;
	    }
	    else if (t.ty == Ttuple)
	    {
		if (lwr == null && upr == null)
		    return e1;
		if (lwr == null || upr == null)
		{   error("need upper and lower bound to slice tuple");
		    // goto Lerror;
		}
	    }
	    else {
		// goto Lerror;
	    }

	    if (t.ty == Tsarray || t.ty == Tarray || t.ty == Ttuple)
	    {
		sym = new ArrayScopeSymbol(this);
		sym.parent = sc.scopesym;
		sc = sc.push(sym);
	    }

	    if (lwr != null)
	    {	lwr = lwr.semantic(sc, context);
		lwr = resolveProperties(sc, lwr, context);
		lwr = lwr.implicitCastTo(sc, Type.tsize_t, context);
	    }
	    if (upr != null)
	    {	upr = upr.semantic(sc, context);
		upr = resolveProperties(sc, upr, context);
		upr = upr.implicitCastTo(sc, Type.tsize_t, context);
	    }

	    if (t.ty == Tsarray || t.ty == Tarray || t.ty == Ttuple)
		sc.pop();

	    if (t.ty == Ttuple)
	    {
		lwr = lwr.optimize(WANTvalue);
		upr = upr.optimize(WANTvalue);
		BigInteger i1 = lwr.toUInteger(context);
		BigInteger i2 = upr.toUInteger(context);

		int length;
		TupleExp te;
		TypeTuple tup;

		if (e1.op == TOKtuple)		// slicing an expression tuple
		{   te = (TupleExp )e1;
		    length = te.exps.size();
		}
		else if (e1.op == TOKtype)	// slicing a type tuple
		{   tup = (TypeTuple )t;
		    length = Argument.dim(tup.arguments, context);
		}
		else {
		    Assert.isTrue(false);
		}

		if (i1 <= i2 && i2 <= length)
		{   size_t j1 = (size_t) i1;
		    size_t j2 = (size_t) i2;

		    if (e1.op == TOKtuple)
		    {	Expressions exps = new Expressions;
			exps.setDim(j2 - j1);
			for (size_t i = 0; i < j2 - j1; i++)
			{   Expression e = (Expression )te.exps.data[j1 + i];
			    exps.data[i] = (void )e;
			}
			e = new TupleExp(loc, exps);
		    }
		    else
		    {	Arguments args = new Arguments;
			args.reserve(j2 - j1);
			for (size_t i = j1; i < j2; i++)
			{   Argument arg = Argument.getNth(tup.arguments, i);
			    args.push(arg);
			}
			e = new TypeExp(e1.loc, new TypeTuple(args));
		    }
		    e = e.semantic(sc);
		}
		else
		{
		    error("string slice [%ju .. %ju] is out of bounds", i1, i2);
		    e = e1;
		}
		return e;
	    }

	    type = t.next.arrayOf();
	    return e;

	Lerror:
	    char *s;
	    if (t.ty == Tvoid)
		s = e1.toChars();
	    else
		s = t.toChars();
	    error("%s cannot be sliced with []", s);
	    type = Type.terror;
	    return e;
	    */
	}

	@Override
	public int getNodeType() {
		return SLICE_EXP;
	}

}
