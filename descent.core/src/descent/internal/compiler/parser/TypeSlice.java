package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.TY.*;

// DMD 1.020
public class TypeSlice extends Type {

	public Expression lwr, sourceLwr;
	public Expression upr, sourceUpr;

	public TypeSlice(Type next, Expression lwr, Expression upr) {
		super(TY.Tslice, next);
		this.lwr = this.sourceLwr = lwr;
		this.upr = this.sourceUpr = upr;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceNext);
			TreeVisitor.acceptChildren(visitor, sourceLwr);
			TreeVisitor.acceptChildren(visitor, sourceUpr);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return TYPE_SLICE;
	}

	@Override
	public void resolve(Loc loc, Scope sc, Expression[] pe, Type[] pt,
			IDsymbol[] ps, SemanticContext context)
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
			IDsymbol s = ps[0];
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
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.SliceIsOutOfRange, this, new String[] { String.valueOf(i1), String.valueOf(i2), String.valueOf(td.objects.size()) }));
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
					objects.set(i, td.objects.get(i1 + i));
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
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CanOnlySliceTupleTypes, this, new String[] { tbn.toChars(context) }));
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
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.SliceIsOutOfRange, this, new String[] { String.valueOf(i1), String.valueOf(i2), String.valueOf(tt.arguments.size()) }));
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
	public Type syntaxCopy(SemanticContext context)
	{
		return new TypeSlice(next.syntaxCopy(context), lwr.syntaxCopy(context), upr.syntaxCopy(context));
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
	
	@Override
	public String getSignature0() {
		StringBuilder sb = new StringBuilder();
		appendSignature0(sb);
		return sb.toString();
	}
	
	@Override
	protected void appendSignature0(StringBuilder sb) {
		sb.append(ISignatureConstants.SLICE);
		
		char[] expc = new ASTNodeEncoder().encodeExpression(lwr);
		sb.append(expc.length);
		sb.append(ISignatureConstants.TYPEOF);
		sb.append(expc);
		
		expc = new ASTNodeEncoder().encodeExpression(upr);
		sb.append(expc.length);
		sb.append(ISignatureConstants.TYPEOF);
		sb.append(expc);
	}
	
}
