package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Equal;

import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.PREC.PREC_assign;
import static descent.internal.compiler.parser.TOK.TOKequal;

import static descent.internal.compiler.parser.TY.Taarray;
import static descent.internal.compiler.parser.TY.Tvoid;

// DMD 1.020
public class AssocArrayLiteralExp extends Expression {

	public Expressions keys;
	public Expressions values;

	public AssocArrayLiteralExp(Loc loc, Expressions keys,
			Expressions values) {
		super(loc, TOK.TOKassocarrayliteral);
		this.keys = keys;
		this.values = values;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, keys);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Type typeb = type.toBasetype(context);
		Type tb = t.toBasetype(context);
		if (tb.ty == Taarray && typeb.ty == Taarray
				&& tb.next.toBasetype(context).ty != Tvoid) {
			assert (keys.size() == values.size());
			for (int i = 0; i < keys.size(); i++) {
				Expression e = values.get(i);
				e = e.castTo(sc, tb.next, context);
				values.set(i, e);

				e = keys.get(i);
				e = e.castTo(sc, ((TypeAArray) tb).key, context);
				keys.set(i, e);
			}
			type = t;
			return this;
		}
		// L1:
		return super.castTo(sc, t, context);
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context)
	{
		int f = 0;
		
		for(int i = 0; i < keys.size(); i++)
		{
			Expression key = keys.get(i);
			Expression value = values.get(i);
			
			f |= key.checkSideEffect(2, context);
			f |= value.checkSideEffect(2, context);
		}
		if(flag == 0 && f == 0) {
			super.checkSideEffect(0, context);
		}
		return f;
	}

	@Override
	public int getNodeType() {
		return ASSOC_ARRAY_LITERAL_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		MATCH result = MATCHexact;

		Type typeb = type.toBasetype(context);
		Type tb = t.toBasetype(context);
		if (tb.ty == Taarray && typeb.ty == Taarray) {
			for (int i = 0; i < keys.size(); i++) {
				Expression e = keys.get(i);
				MATCH m = e.implicitConvTo(((TypeAArray) tb).key,
						context);
				if (m.ordinal() < result.ordinal()) {
					result = m; // remember worst match
				}
				if (result == MATCHnomatch) {
					break; // no need to check for worse
				}
				e = values.get(i);
				m = e.implicitConvTo(tb.next, context);
				if (m.ordinal() < result.ordinal()) {
					result = m; // remember worst match
				}
				if (result == MATCHnomatch) {
					break; // no need to check for worse
				}
			}
			return result;
		} else {
			return super.implicitConvTo(t, context);
		}
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context)
	{
		Expressions keysx = keys;
		Expressions valuesx = values;
		
		for(int i = 0; i < keys.size(); i++)
		{
			Expression ekey = keys.get(i);
			Expression evalue = values.get(i);
			Expression ex;
			
			ex = ekey.interpret(istate, context);
			if(ex == EXP_CANT_INTERPRET) {
				return EXP_CANT_INTERPRET; // goto Lerr;
			}
				
			/*
			 * If any changes, do Copy On Write
			 */
			if(ex != ekey)
			{
				if(keysx == keys) {
					keysx = new Expressions(keys);
				}
				keysx.set(i, ex);
			}
			
			ex = evalue.interpret(istate, context);
			if(ex == EXP_CANT_INTERPRET) {
				return EXP_CANT_INTERPRET; // goto Lerr;
			}
				
			/*
			 * If any changes, do Copy On Write
			 */
			if(ex != evalue)
			{
				if(valuesx == values) {
					valuesx = new Expressions(values);
				}
				valuesx.set(i, ex);
			}
		}
		if(keysx != keys) {
			expandTuples(keysx, context);
		}
		if(valuesx != values) {
			expandTuples(valuesx, context);
		}
		if(keysx.size() != valuesx.size()) {
			return EXP_CANT_INTERPRET; // goto Lerr;
		}
			
		/*
		 * Remove duplicate keys
		 */
		for(int i = 1; i < keysx.size(); i++)
		{
			Expression ekey = keysx.get(i - 1);
			
			for(int j = i; j < keysx.size(); j++)
			{
				Expression ekey2 = keysx.get(j);
				Expression ex = Equal.call(TOKequal, Type.tbool, ekey, ekey2,
						context);
				if(ex == EXP_CANT_INTERPRET) {
					return EXP_CANT_INTERPRET; // goto Lerr;
				}
				if(ex.isBool(true)) // if a match
				{
					// Remove ekey
					if(keysx == keys) {
						keysx = new Expressions(keys);
					}
					if(valuesx == values) {
						valuesx = new Expressions(values);
					}
					keysx.remove(i - 1);
					valuesx.remove(i - 1);
					i -= 1; // redo the i'th iteration
					break;
				}
			}
		}
		
		if(keysx != keys || valuesx != values)
		{
			AssocArrayLiteralExp ae;
			ae = new AssocArrayLiteralExp(loc, keysx, valuesx);
			ae.type = type;
			return ae;
		}
		return this;
	}

	@Override
	public boolean isBool(boolean result)
	{
		int dim = keys.size();
	    return result ? (dim != 0) : (dim == 0);
	}

	@Override
	public Expression optimize(int result, SemanticContext context)
	{
		assert (keys.size() == values.size());
		for(int i = 0; i < keys.size(); i++)
		{
			Expression e = keys.get(i);
			
			e = e.optimize(WANTvalue | (result & WANTinterpret), context);
			keys.set(i, e);
			
			e = values.get(i);
			e = e.optimize(WANTvalue | (result & WANTinterpret), context);
			values.set(i, e);
		}
		return this;
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context)
	{
		arrayExpressionScanForNestedRef(sc, keys, context);
	    arrayExpressionScanForNestedRef(sc, values, context);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		Expression e;
		Type tkey = null;
		Type tvalue = null;
		
		// Run semantic() on each element
		for(int i = 0; i < keys.size(); i++)
		{
			Expression key = keys.get(i);
			Expression value = values.get(i);
			
			key = key.semantic(sc, context);
			value = value.semantic(sc, context);
			
			keys.set(i, key);
			values.set(i, value);
		}
		expandTuples(keys, context);
		expandTuples(values, context);
		if(keys.size() != values.size())
		{
			error("number of keys is %u, must match number of values %u", keys
					.size(), values.size());
			keys.clear();
			values.clear();
		}
		for(int i = 0; i < keys.size(); i++)
		{
			Expression key = keys.get(i);
			Expression value = values.get(i);
			
			if(null == key.type) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolHasNoValue, 0, key.start, key.length, new String[] { key.toChars(context) }));
			}
			if(null == value.type) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolHasNoValue, 0, value.start, value.length, new String[] { value.toChars(context) }));
			}
			key = resolveProperties(sc, key, context);
			value = resolveProperties(sc, value, context);
			
			if(null == tkey) {
				tkey = key.type;
			} else {
				key = key.implicitCastTo(sc, tkey, context);
			}
			keys.set(i, key);
			
			if(null == tvalue) {
				tvalue = value.type;
			} else {
				value = value.implicitCastTo(sc, tvalue, context);
			}
			values.set(i, value);
		}
		
		if(null == tkey) {
			tkey = Type.tvoid;
		}
		if(null == tvalue) {
			tvalue = Type.tvoid;
		}
		type = new TypeAArray(tvalue, tkey);
		type = type.semantic(loc, sc, context);
		return this;
	}

	@Override
	public Expression syntaxCopy()
	{
		 return new AssocArrayLiteralExp(loc, 
				 arraySyntaxCopy(keys), arraySyntaxCopy(values));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context)
	{
		buf.writeByte('[');
		for(int i = 0; i < keys.size(); i++)
		{
			Expression key = keys.get(i);
			Expression value = values.get(i);
			
			if(i != 0) {
				buf.writeByte(',');
			}
			
			expToCBuffer(buf, hgs, key, PREC_assign, context);
			buf.writeByte(':');
			expToCBuffer(buf, hgs, value, PREC_assign, context);
		}
		buf.writeByte(']');
	}

	@Override
	public void toMangleBuffer(OutBuffer buf, SemanticContext context)
	{
		int dim = keys.size();
		buf.writestring("A" + dim);
		for(int i = 0; i < dim; i++)
		{
			Expression key = keys.get(i);
			Expression value = values.get(i);
			
			key.toMangleBuffer(buf, context);
			value.toMangleBuffer(buf, context);
		}
	}
	
	// PERHAPS int inlineCost(InlineCostState *ics);
	// PERHAPS Expression *doInline(InlineDoState *ids);
	// PERHAPS Expression *inlineScan(InlineScanState *iss);
}
