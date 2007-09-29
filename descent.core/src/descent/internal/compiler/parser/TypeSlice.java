package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.TY.*;

// DMD 1.020
public class TypeSlice extends Type {

	public Expression lwr;
	public Expression upr;

	public TypeSlice(Type next, Expression lwr, Expression upr) {
		super(TY.Tslice, next);
		this.lwr = lwr;
		this.upr = upr;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, lwr);
			TreeVisitor.acceptChildren(visitor, upr);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return TYPE_SLICE;
	}

	@Override
	public void resolve(Loc loc, Scope sc, Expression[] pe, Type[] pt,
			Dsymbol[] ps, SemanticContext context)
	{
		next.resolve(loc, sc, pe, pt, ps, context);
		if(null != pe[0])
		{ 
			// It's really a slice expression
			Expression e;
			e = new SliceExp(loc, pe[0], lwr, upr);
			pe[0] = e;
		}
		else if(null != ps[0])
		{
			Dsymbol s = ps[0];
			TupleDeclaration td = s.isTupleDeclaration();
			if(null != td)
			{
				/*
				 * It's a slice of a TupleDeclaration
				 */
				ScopeDsymbol sym = new ArrayScopeSymbol(td);
				sym.parent = sc.scopesym;
				sc = sc.push(sym);
				
				lwr = lwr.semantic(sc, context);
				lwr = lwr.optimize(WANTvalue, context);
				int i1 = lwr.toUInteger(context).intValue();
				
				upr = upr.semantic(sc, context);
				upr = upr.optimize(WANTvalue, context);
				int i2 = upr.toUInteger(context).intValue();
				
				sc = sc.pop();
				
				if(!(i1 <= i2 && i2 <= td.objects.size()))
				{
					error("slice [%ju..%ju] is out of range of [0..%u]",
							i1, i2, td.objects.size());
					super.resolve(loc, sc, pe, pt, ps, context); // goto Ldefault;
				}
				
				if(i1 == 0 && i2 == td.objects.size())
				{
					ps[0] = td;
					return;
				}
				
				/*
				 * Create a new TupleDeclaration which is a slice [i1..i2] out
				 * of the old one.
				 */
				Objects objects = new Objects();
				objects.setDim(i2 - i1);
				for(int i = 0; i < objects.size(); i++)
				{
					objects.add(td.objects.get(i1 + i));
				}
				
				TupleDeclaration tds = new TupleDeclaration(loc, td.ident,
						objects);
				ps[0] = tds;
			}
			else
				super.resolve(loc, sc, pe, pt, ps, context); // goto Ldefault;
		}
		else
		{
			// Ldefault:
			super.resolve(loc, sc, pe, pt, ps, context);
		}
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context)
	{
		// printf("TypeSlice.semantic() %s\n", toChars());
		next = next.semantic(loc, sc, context);
		// printf("next: %s\n", next.toChars());
		
		Type tbn = next.toBasetype(context);
		if(tbn.ty != Ttuple)
		{
			error(loc, "can only slice tuple types, not %s", tbn
					.toChars(context));
			return Type.terror;
		}
		TypeTuple tt = (TypeTuple) tbn;
		
		lwr = semanticLength(sc, tbn, lwr, context);
		lwr = lwr.optimize(WANTvalue, context);
		int i1 = lwr.toUInteger(context).intValue();
		
		upr = semanticLength(sc, tbn, upr, context);
		upr = upr.optimize(WANTvalue, context);
		int i2 = upr.toUInteger(context).intValue();
		
		if(!(i1 <= i2 && i2 <= tt.arguments.size()))
		{
			error("slice [%ju..%ju] is out of range of [0..%u]", i1, i2,
					tt.arguments.size());
			return Type.terror;
		}
		
		Arguments args = new Arguments(i2 - i1);
		for(int i = i1; i < i2; i++)
		{
			Argument arg = tt.arguments.get(i);
			args.add(arg);
		}
		
		return TypeTuple.newArguments(args);
	}

	@Override
	public Type syntaxCopy()
	{
		return new TypeSlice(next.syntaxCopy(), lwr.syntaxCopy(), upr.syntaxCopy());
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		OutBuffer buf2 = new OutBuffer();

		buf2.writestring("[");
		buf2.writestring(lwr.toChars(context));
		buf2.writestring(" .. ");
		buf2.writestring(upr.toChars(context));
		buf2.writestring("]");

		buf.prependstring(buf2.toChars());
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
		next.toCBuffer2(buf, null, hgs, context);
	}
	
}
