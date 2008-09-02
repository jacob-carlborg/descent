package descent.internal.compiler.parser;

import descent.core.IJavaElement;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TY.Tenum;


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
	public Expression defaultInit(Loc loc, SemanticContext context) {
		// Initialize to first member of enum
		Expression e;
		if (context.isD2()) {
			e = (Expression) sym.defaultval;
		} else {
			e = new IntegerExp(loc, (integer_t) sym.defaultval, this);
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
	    		context.acceptProblem(Problem.newSemanticTypeError(IProblem.ForwardReferenceOfSymbolDotSymbol, e, ident, new String[] { toChars(context), ident.toChars() }));
	    	}
	        return new IntegerExp(Loc.ZERO, 0, this);
	    }

		s = sym.symtab.lookup(ident);
		
		// Descent: for binding resolution
		ident.resolvedSymbol = s;
		
		if (null == s) {
			return getProperty(e.loc, ident, context);
		}
		m = s.isEnumMember();
		em = m.value().copy();
		em.loc = e.loc;
		return em;
	}

	@Override
	public int getNodeType() {
		return TYPE_ENUM;
	}

	@Override
	public Expression getProperty(Loc loc, char[] ident, int lineNumber, int start, int length,
			SemanticContext context) {
		Expression e;

		if (equals(ident, Id.max)) {
			if (null == sym.symtab) {
				// goto Lfwd;
				return getProperty_Lfwd(ident, context);
			}
			e = new IntegerExp(Loc.ZERO, (integer_t) sym.maxval, this);
		} else if (equals(ident, Id.min)) {
			if (null == sym.symtab) {
				// goto Lfwd;
				return getProperty_Lfwd(ident, context);
			}
			e = new IntegerExp(Loc.ZERO, (integer_t) sym.minval, this);
		} else if (equals(ident, Id.init)) {
			if (null == sym.symtab) {
				// goto Lfwd;
				return getProperty_Lfwd(ident, context);
			}
			e = defaultInit(loc, context);
		} else {
			if (null == sym.memtype) {
				// goto Lfwd;
				return getProperty_Lfwd(ident, context);
			}
			e = sym.memtype.getProperty(loc, ident, lineNumber, start, length, context);
		}
		return e;
	}

	private Expression getProperty_Lfwd(char[] ident, SemanticContext context) {
		if (context.acceptsErrors()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ForwardReferenceOfSymbolDotSymbol, this,
					new String[] { toChars(context),
							new String(ident) }));
		}
		return new IntegerExp(Loc.ZERO, 0, this);
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
	public boolean isZeroInit(SemanticContext context) {
		if (context.isD2()) {
			return (((Expression)sym.defaultval)).equals(new IntegerExp(0), context);
		} else {
			return (sym.defaultval.equals(0));
		}
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		sym.semantic(sc, context);
		return merge(context);
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		if (null == sym.memtype) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.EnumIsForwardReference, this, new String[] { sym.toChars(context) }));
			}
			return 4;
		}
		return sym.memtype.size(loc, context);
	}

	@Override
	public Type toBasetype(SemanticContext context) {
		if (sym.memtype == null) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.EnumIsForwardReference, sym));
			}
			return tint32;
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
	public void toDecoBuffer(OutBuffer buf, SemanticContext context) {
		String name = sym.mangle(context);
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
	public String getSignature0() {
		return sym.getSignature();
	}
	
	@Override
	protected void appendSignature0(StringBuilder sb) {
		sb.append(getSignature());
	}

}
