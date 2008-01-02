package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.PREC.PREC_assign;
import static descent.internal.compiler.parser.TOK.TOKstring;
import static descent.internal.compiler.parser.Constfold.ArrayLength;

// DMD 1.020
public class SliceExp extends UnaExp {

	public Expression lwr, sourceLwr;
	public Expression upr, sourceUpr;
	public VarDeclaration lengthVar;

	public SliceExp(Loc loc, Expression e1, Expression lwr, Expression upr) {
		super(loc, TOK.TOKslice, e1);
		this.lwr = this.sourceLwr = lwr;
		this.upr = this.sourceUpr = upr;
		this.lengthVar = null;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceE1);
			TreeVisitor.acceptChildren(visitor, sourceLwr);
			TreeVisitor.acceptChildren(visitor, sourceUpr);
		}
		visitor.endVisit(this);
	}

	@Override
	public void checkEscape(SemanticContext context)
	{
		e1.checkEscape(context);
	}

	@Override
	public int getNodeType() {
		return SLICE_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context)
	{
		Expression e;
		Expression e1;
		Expression lwr;
		Expression upr;
		
		e1 = this.e1.interpret(istate, context);
		if(e1 == EXP_CANT_INTERPRET)
			return EXP_CANT_INTERPRET; // goto Lcant;
		if(null == this.lwr)
		{
			e = e1.castTo(null, type, context);
			return e.interpret(istate, context);
		}
		
		/* Set the $ variable
		 */
		e = ArrayLength.call(Type.tsize_t, e1, context);
		if(e == EXP_CANT_INTERPRET)
			return EXP_CANT_INTERPRET; // goto Lcant;
		if(null != lengthVar)
			lengthVar.value = e;
		
		/* Evaluate lower and upper bounds of slice
		 */
		lwr = this.lwr.interpret(istate, context);
		if(lwr == EXP_CANT_INTERPRET)
			return EXP_CANT_INTERPRET; // goto Lcant;
		upr = this.upr.interpret(istate, context);
		if(upr == EXP_CANT_INTERPRET)
			return EXP_CANT_INTERPRET; // goto Lcant;
			
		return Constfold.Slice(type, e1, lwr, upr, context);
		
		//Lcant:
		//return EXP_CANT_INTERPRET;
	}

	@Override
	public Expression modifiableLvalue(Scope sc, Expression e,
			SemanticContext context)
	{
		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.SliceExpressionIsNotAModifiableLvalue, this, new String[] { toChars(context) }));
	    return this;
	}

	@Override
	public Expression optimize(int result, SemanticContext context)
	{
		Expression e;
		
		e = this;
		e1 = e1.optimize(WANTvalue | (result & WANTinterpret), context);
		if(null == lwr)
		{
			if(e1.op == TOKstring)
			{ // Convert slice of string literal into dynamic array
				Type t = e1.type.toBasetype(context);
				if(null != t.next)
					e = e1.castTo(null, t.next.arrayOf(context), context);
			}
			return e;
		}
		if((result & WANTinterpret) > 0)
			e1 = fromConstInitializer(e1, context);
		lwr = lwr.optimize(WANTvalue | (result & WANTinterpret), context);
		upr = upr.optimize(WANTvalue | (result & WANTinterpret), context);
		e = Constfold.Slice(type, e1, lwr, upr, context);
		if(e == EXP_CANT_INTERPRET)
			e = this;
		return e;
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context)
	{
		e1.scanForNestedRef(sc, context);
		
		if(null != lengthVar)
		{ //printf("lengthVar\n");
			lengthVar.parent = sc.parent;
		}
		
		if(null != lwr)
			lwr.scanForNestedRef(sc, context);
		if(null != upr)
			upr.scanForNestedRef(sc, context);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {

		Expression e;
		IAggregateDeclaration ad;
		ScopeDsymbol sym;

		if (null != type)
			return this;

		super.semantic(sc, context);
		e1 = resolveProperties(sc, e1, context);

		e = this;
		Type t = e1.type.toBasetype(context);

		if (t.ty == TY.Tpointer) {
			if (null == lwr || null == upr) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.NeedUpperAndLowerBoundToSlicePointer, this));
			}
		} else if (t.ty == TY.Tarray || t.ty == TY.Tsarray) {
		} else if (t.ty == TY.Tclass || t.ty == TY.Tstruct) {
			if (t.ty == TY.Tclass)
				ad = ((TypeClass) t).sym;
			else
				ad = ((TypeStruct) t).sym;

			if (null != search_function(ad, Id.slice, context)) {
				// Rewrite as e1.slice(lwr, upr)
				e = new DotIdExp(loc, e1, new IdentifierExp(Loc.ZERO, Id.slice));
				if (null != lwr) {
					assert (null != upr);
					e = new CallExp(loc, e, lwr, upr);
				} else {
					assert (null == upr);
					e = new CallExp(loc, e);
				}

				e = e.semantic(sc, context);
				return e;
			} else {
				return Lerror(t, e, sc, context);
			}
		} else if (t.ty == TY.Ttuple) {
			if (null == lwr && null == upr) {
				return e1;
			} else if (null == lwr || null == upr) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.NeedUpperAndLowerBoundToSliceTuple, this));
				return Lerror(t, e, sc, context);
			}
		} else {
			return Lerror(t, e, sc, context);
		}

		if (t.ty == TY.Tsarray || t.ty == TY.Tarray || t.ty == TY.Ttuple) {
			sym = new ArrayScopeSymbol(this);
			sym.loc = loc;
			sym.parent = sc.scopesym;
			sc = sc.push(sym);
		}

		if (null != lwr) {
			lwr = lwr.semantic(sc, context);
			lwr = resolveProperties(sc, lwr, context);
			lwr = lwr.implicitCastTo(sc, Type.tsize_t, context);
		}
		if (null != upr) {
			upr = upr.semantic(sc, context);
			upr = resolveProperties(sc, upr, context);
			upr = upr.implicitCastTo(sc, Type.tsize_t, context);
		}

		if (t.ty == TY.Tsarray || t.ty == TY.Tarray || t.ty == TY.Ttuple)
			sc.pop();

		if (t.ty == TY.Ttuple) {
			lwr = lwr.optimize(WANTvalue, context);
			upr = upr.optimize(WANTvalue, context);
			int i1 = (int) lwr.toUInteger(context).longValue();
			int i2 = (int) upr.toUInteger(context).longValue();

			int length = 0;
			TupleExp te = null;
			TypeTuple tup = null;

			if (e1.op == TOK.TOKtuple) // slicing an expression tuple
			{
				te = (TupleExp) e1;
				length = te.exps.size();
			} else if (e1.op == TOK.TOKtype) // slicing a type tuple
			{
				tup = (TypeTuple) t;
				length = tup.arguments.size();
			} else {
				assert (false);
			}

			if (i1 <= i2 && i2 <= length) {
				//int j1 = (size_t) i1;
				//int j2 = (size_t) i2;

				if (e1.op == TOK.TOKtuple) {
					Expressions exps = new Expressions();
					exps.setDim(i2 - i1);
					for (int i = 0; i < (i2 - i1); i++) {
						Expression tmp = (Expression) te.exps.get(i1 + i);
						exps.set(i, tmp);
					}
					e = new TupleExp(loc, exps);
				} else {
					Arguments args = new Arguments(i2 - i1);
					for (int i = i1; i < i2; i++) {
						Argument arg = tup.arguments.get(i);
						args.add(arg);
					}
					e = new TypeExp(e1.loc, TypeTuple.newArguments(args));
				}
				e = e.semantic(sc, context);
			} else {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.StringSliceIsOutOfBounds, this, new String[] { String.valueOf(i1), String.valueOf(i2) }));
				e = e1;
			}
			return e;
		} else {
			type = t.next.arrayOf(context);
			return e;
		}
	}

	@Override
	public Expression syntaxCopy(SemanticContext context) {
		Expression lwr = null;
		if (this.lwr != null) {
			lwr = this.lwr.syntaxCopy(context);
		}

		Expression upr = null;
		if (this.upr != null) {
			upr = this.upr.syntaxCopy(context);
		}

		return new SliceExp(loc, e1.syntaxCopy(context), lwr, upr);
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context)
	{
		expToCBuffer(buf, hgs, e1, op.precedence, context);
	    buf.writeByte('[');
	    if (null != upr || null != lwr)
	    {
		if (null != lwr)
		    expToCBuffer(buf, hgs, lwr, PREC_assign, context);
		else
		    buf.writeByte('0');
		buf.writestring("..");
		if (null != upr)
		    expToCBuffer(buf, hgs, upr, PREC_assign, context);
		else
		    buf.writestring("length");		// BUG: should be array.length
	    }
	    buf.writeByte(']');
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context)
	{
		return this;
	}

	// Lerror:
	private Expression Lerror(Type t, Expression e, Scope sc,
			SemanticContext context) {
		String s;
		if (t.ty == TY.Tvoid)
			s = e1.toChars(context);
		else
			s = t.toChars(context);

		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.SymbolCannotBeSlicedWithBrackets, this, new String[] { s }));
		type = Type.terror;
		return e;
	}
	
	//PERHAPS void dump(int indent);
    //PERHAPS int inlineCost(InlineCostState *ics);
    //PERHAPS Expression *doInline(InlineDoState *ids);
    //PERHAPS Expression *inlineScan(InlineScanState *iss);

}
