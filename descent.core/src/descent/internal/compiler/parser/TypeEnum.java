package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.TY.Tenum;
import descent.core.IJavaElement;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class TypeEnum extends Type {

	public EnumDeclaration sym;

	public TypeEnum(EnumDeclaration sym) {
		super(TY.Tenum, null);
		this.sym = sym;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public int alignsize(SemanticContext context) {
		if (null == sym.memtype) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.EnumIsForwardReference, this, new String[] { sym.toChars(context) }));
			}
			return 4;
		}
		return sym.memtype.alignsize(context);
	}

	@Override
	public MATCH deduceType(Scope sc, Type tparam,
			TemplateParameters parameters, Objects dedtypes,
			SemanticContext context) {
		// Extra check
		if (tparam != null && tparam.ty == Tenum) {
			TypeEnum tp = (TypeEnum) tparam;

			if (sym != tp.sym) {
				return MATCHnomatch;
			}
		}
		return super.deduceType(sc, tparam, parameters, dedtypes, context);
	}

	@Override
	public Expression defaultInit(char[] filename, int lineNumber, SemanticContext context) {
		// Initialize to first member of enum
		Expression e;
		if (context.isD2()) {
			e = (Expression) sym.defaultval;
		} else {
			e = new IntegerExp(filename, lineNumber, (integer_t) sym.defaultval, this);
		}
		return e;
	}

	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		EnumMember m;
		Dsymbol s;
		Expression em;
		
	    if (null == sym.symtab) {
	    	// goto Lfwd;
	    	if (context.acceptsErrors()) {
	    		context.acceptProblem(Problem.newSemanticTypeError(IProblem.ForwardReferenceOfEnumSymbolDotSymbol, e, ident, new String[] { toChars(context), ident.toChars() }));
	    	}
	        return new IntegerExp(null, 0, 0, Type.terror);
	    }

		s = sym.symtab.lookup(ident);
		
		// Descent: for binding resolution
		ident.setResolvedSymbol(s, context);
		
		if (null == s) {
			if (equals(ident, Id.max) || equals(ident, Id.min)
					|| equals(ident, Id.init) || equals(ident, Id.stringof)
					|| null == sym.memtype) {
				return getProperty(e.filename, e.lineNumber,  ident, context);
			}
			return sym.memtype.dotExp(sc, e, ident, context);
		}
		m = s.isEnumMember();
		em = m.value().copy();
		em.filename = e.filename;
		em.lineNumber = e.lineNumber;
		return em;
	}

	@Override
	public int getNodeType() {
		return TYPE_ENUM;
	}

	@Override
	public Expression getProperty(char[] filename, int lineNumber, char[] ident, int start, int length,
			SemanticContext context) {
		Expression e;

		if (equals(ident, Id.max)) {
			if (null == sym.symtab) {
				// goto Lfwd;
				return getProperty_Lfwd(ident, context);
			}
			e = new IntegerExp(null, 0, (integer_t) sym.maxval, this);
		} else if (equals(ident, Id.min)) {
			if (null == sym.symtab) {
				// goto Lfwd;
				return getProperty_Lfwd(ident, context);
			}
			e = new IntegerExp(null, 0, (integer_t) sym.minval, this);
		} else if (equals(ident, Id.init)) {
			if (null == sym.symtab) {
				// goto Lfwd;
				return getProperty_Lfwd(ident, context);
			}
			e = defaultInit(filename, lineNumber, context);
		}
	    else if (equals(ident, Id.stringof)) {
			String s = toChars(context);
			e = new StringExp(filename, lineNumber, s.toCharArray(), s.length(), 'c');
			Scope sc = new Scope();
			e = e.semantic(sc, context);
		} else {
			if (null == sym.memtype) {
				// goto Lfwd;
				return getProperty_Lfwd(ident, context);
			}
			e = sym.memtype.getProperty(filename, lineNumber, ident, start, length, context);
		}
		return e;
	}

	private Expression getProperty_Lfwd(char[] ident, SemanticContext context) {
		if (context.acceptsErrors()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ForwardReferenceOfEnumSymbolDotSymbol, this,
					new String[] { toChars(context),
							new String(ident) }));
		}
		return new IntegerExp(null, 0, 0, this);
	}

	@Override
	public TypeInfoDeclaration getTypeInfoDeclaration(SemanticContext context) {
		return new TypeInfoEnumDeclaration(this, context);
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		return toBasetype(context).hasPointers(context);
	}

	@Override
	public MATCH implicitConvTo(Type to, SemanticContext context) {
		MATCH m;

		if (this.equals(to)) {
			m = MATCHexact; // exact match
		} else if (sym.memtype.implicitConvTo(to, context) != MATCHnomatch) {
			m = MATCHconvert; // match with conversions
		} else {
			m = MATCHnomatch; // no match
		}
		return m;
	}

	@Override
	public boolean isfloating() {
		return false;
	}

	@Override
	public boolean isintegral() {
		return true;
	}

	@Override
	public boolean isscalar(SemanticContext context) {
		return true;
	}

	@Override
	public boolean isunsigned() {
		return sym.memtype.isunsigned();
	}

	@Override
	public boolean isZeroInit(char[] filename, int lineNumber, SemanticContext context) {
		if (context.isD2()) {
			return (((Expression)sym.defaultval)).equals(new IntegerExp(0), context);
		} else {
			return (sym.defaultval.equals(0));
		}
	}

	@Override
	public Type semantic(char[] filename, int lineNumber, Scope sc, SemanticContext context) {
		sym.semantic(sc, context);
		return merge(context);
	}

	@Override
	public int size(char[] filename, int lineNumber, SemanticContext context) {
		if (null == sym.memtype) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.EnumIsForwardReference, this, new String[] { sym.toChars(context) }));
			}
			return 4;
		}
		return sym.memtype.size(filename, lineNumber, context);
	}

	@Override
	public Type toBasetype(SemanticContext context) {
		if (sym.memtype == null) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.EnumIsForwardReference, sym));
			}
			return terror;
		}
		return sym.memtype.toBasetype(context);
	}

	@Override
	public void toCBuffer2(OutBuffer buf, HdrGenState hgs, int mod, SemanticContext context) {
	    if (mod != this.mod) {
			toCBuffer3(buf, hgs, mod, context);
			return;
		}
		buf.writestring(sym.toChars(context));
	}

	@Override
	public String toChars(SemanticContext context) {
		return sym.toChars(context);
	}

	@Override
	public void toDecoBuffer(OutBuffer buf, int flag, SemanticContext context) {
		String name = sym.mangle(context);
		super.toDecoBuffer(buf, flag, context);
		buf.writestring(ty.mangleChar);
		buf.writestring(name);
	}

	@Override
	public Dsymbol toDsymbol(Scope sc, SemanticContext context) {
		return sym;
	}
	
	@Override
	public IJavaElement getJavaElement() {
		return sym.getJavaElement();
	}
	
	@Override
	protected void appendSignature0(StringBuilder sb, int options) {
		sym.appendSignature(sb, options);
	}
	
	@Override
	public char[] identRep() {
		return sym.ident == null ? null : sym.ident.ident;
	}

}
