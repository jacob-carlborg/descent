package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.TY.Ttypedef;
import descent.core.IJavaElement;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class TypeTypedef extends Type {

	public TypedefDeclaration sym;

	public TypeTypedef(TypedefDeclaration sym) {
		super(TY.Ttypedef, null);
		this.sym = sym;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public int alignsize(SemanticContext context) {
		return sym.basetype.alignsize(context);
	}

	@Override
	public boolean checkBoolean(SemanticContext context) {
		return sym.basetype.checkBoolean(context);
	}

	@Override
	public MATCH deduceType(Scope sc, Type tparam,
			TemplateParameters parameters, Objects dedtypes,
			SemanticContext context) {
		// Extra check
		if (tparam != null && tparam.ty == Ttypedef) {
			TypeTypedef tp = (TypeTypedef) tparam;

			if (sym != tp.sym) {
				return MATCHnomatch;
			}
		}
		return super.deduceType(sc, tparam, parameters, dedtypes, context);
	}

	@Override
	public Expression defaultInit(char[] filename, int lineNumber, SemanticContext context) {
		Expression e;
		Type bt;

		if (sym.init != null) {
			return sym.init.toExpression(context);
		}
		bt = sym.basetype;
		e = bt.defaultInit(filename, lineNumber, context);
		e.type = this;
		while (bt.ty == TY.Tsarray) {
			e.type = bt.next;
			bt = bt.next.toBasetype(context);
		}
		return e;
	}

	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		if (equals(ident, Id.init)) {
			return super.dotExp(sc, e, ident, context);
		}
		return sym.basetype.dotExp(sc, e, ident, context);
	}

	@Override
	public int getNodeType() {
		return TYPE_TYPEDEF;
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		return toBasetype(context).hasPointers(context);
	}

	@Override
	public MATCH implicitConvTo(Type to, SemanticContext context) {
		MATCH m;

		//printf("TypeTypedef::implicitConvTo()\n");
		if (this.equals(to)) {
			m = MATCHexact; // exact match
		} else if (sym.basetype.implicitConvTo(to, context) != MATCHnomatch) {
			m = MATCHconvert; // match with conversions
		} else {
			m = MATCHnomatch; // no match
		}
		return m;
	}

	@Override
	public boolean isbit() {
		return sym.basetype.isbit();
	}

	@Override
	public boolean iscomplex() {
		return sym.basetype.iscomplex();
	}

	@Override
	public boolean isfloating() {
		return sym.basetype.isfloating();
	}

	@Override
	public boolean isimaginary() {
		return sym.basetype.isimaginary();
	}

	@Override
	public boolean isintegral() {
		return sym.basetype.isintegral();
	}

	@Override
	public boolean isreal() {
		return sym.basetype.isreal();
	}

	@Override
	public boolean isscalar(SemanticContext context) {
		return sym.basetype.isscalar(context);
	}

	@Override
	public boolean isunsigned() {
		return sym.basetype.isunsigned();
	}

	@Override
	public boolean isZeroInit(char[] filename, int lineNumber, SemanticContext context) {
		if (sym.init != null) {
			if (sym.init.isVoidInitializer() != null) {
				return true; // initialize voids to 0
			}
			Expression e = sym.init.toExpression(context);
			if (e != null && e.isBool(false)) {
				return true;
			}
			return false; // assume not
		}
		if (sym.inuse != 0) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.CircularDefinition, this, new String[] { toChars(context) }));
			}
			sym.basetype = Type.terror;
		}
		sym.inuse = 1;
		boolean result = sym.basetype.isZeroInit(filename, lineNumber, context);
		sym.inuse = 0;
		return result;
	}

	@Override
	public Type semantic(char[] filename, int lineNumber, Scope sc, SemanticContext context) {
		sym.semantic(sc, context);
		return merge(context);
	}

	@Override
	public int size(char[] filename, int lineNumber, SemanticContext context) {
		return sym.basetype.size(filename, lineNumber, context);
	}

	@Override
	public Type syntaxCopy(SemanticContext context) {
		return this;
	}

	@Override
	public Type toBasetype(SemanticContext context) {
		if (sym.inuse != 0) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.CircularDefinition, this, new String[] { toChars(context) }));
			}
			sym.basetype = Type.terror;
			return Type.terror;
		}
		sym.inuse = 1;
		Type t = sym.basetype.toBasetype(context);
		sym.inuse = 0;
		return t;
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
	public Expression getProperty(char[] filename, int lineNumber, char[] ident, int start, int length, SemanticContext context) {
		if (CharOperation.equals(ident, Id.init)) {
			return super.getProperty(filename, lineNumber, ident, start, length,
					context);
		}
		return sym.basetype.getProperty(filename, lineNumber, ident, start, length,
				context);
	}
	
	@Override
	public IJavaElement getJavaElement() {
		return sym.getJavaElement();
	}
	
	@Override
	protected void appendSignature0(StringBuilder sb, int options) {
		sym.appendSignature(sb, options);
	}

}
