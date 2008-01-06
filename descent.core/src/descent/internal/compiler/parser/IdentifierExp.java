package descent.internal.compiler.parser;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class IdentifierExp extends Expression {
	
	public final static IdentifierExp EMPTY = new IdentifierExp(Id.empty);
	
	public static int count;
	public static char[] generateId(char[] id) {
		StringBuilder s = new StringBuilder();
		s.append(id);
		s.append(count);
		count++;
		if (count < 0) {
			count = 0;
		}
		return s.toString().toCharArray();
	}

	public char[] ident;
	
	/*
	 * Once the semantic pass is done, the evaluated expression is kept in
	 * this variable. Only for compile-time function evaluation. 
	 */
	public Expression evaluatedExpression;

	public IdentifierExp(Loc loc) {
		super(loc, TOK.TOKidentifier);
	}
	
	public IdentifierExp(char[] ident) {
		this(Loc.ZERO, ident);
	}

	public IdentifierExp(Loc loc, char[] ident) {
		this(loc);
		this.ident = ident;
	}

	public IdentifierExp(Loc loc, IdentifierExp ident) {
		this(loc);
		this.ident = ident.ident;
		this.start = ident.start;
		this.length = ident.length;
	}

	public IdentifierExp(Loc loc, Token token) {
		this(loc);
		this.ident = token.sourceString;
		this.start = token.ptr;
		this.length = token.sourceLen;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public DYNCAST dyncast() {
		return DYNCAST.DYNCAST_IDENTIFIER;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof char[]) {
			char[] c = (char[]) o;
			return CharOperation.equals(ident, c);
		}
		
		if (!(o instanceof IdentifierExp)) {
			return false;
		}

		IdentifierExp i = (IdentifierExp) o;
		return equals(this, i);
	}

	@Override
	public int getNodeType() {
		return IDENTIFIER_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		IDsymbol s;
		IDsymbol[] scopesym = { null };

		s = sc.search(loc, this, scopesym, context);
		
		// Descent: for binding resolution
		resolvedSymbol = s;
		
		if (s != null) {
			Expression e;
			WithScopeSymbol withsym;

			// See if it was a with class
			withsym = scopesym[0].isWithScopeSymbol();
			if (withsym != null) {
				s = s.toAlias(context);

				// Same as wthis.ident
				if (s.needThis() || s.isTemplateDeclaration() != null) {
					e = new VarExp(loc, withsym.withstate.wthis);
					e = new DotIdExp(loc, e, this);
				} else {
					Type t = withsym.withstate.wthis.type;
					if (t.ty == TY.Tpointer) {
						t = t.next;
					}
					e = new TypeDotIdExp(loc, t, this);
				}
			} else {
				if (s.parent() == null
						&& scopesym[0].isArrayScopeSymbol() != null) { // Kludge
					// to
					// run
					// semantic()
					// here
					// because
					// ArrayScopeSymbol::search() doesn't have access to sc.
					s.semantic(sc, context);
				}
				// Look to see if f is really a function template
				IFuncDeclaration f = s.isFuncDeclaration();
				if (f != null && f.parent() != null) {
					TemplateInstance ti = f.parent().isTemplateInstance();

					if (ti != null
							&& ti.isTemplateMixin() == null
							&& (equals(ti.name, f.ident()) || 
									equals(ti.toAlias(context).ident(), f.ident()))
							&& ti.tempdecl != null
							&& ti.tempdecl.onemember() != null) {
						ITemplateDeclaration tempdecl = ti.tempdecl;
						if (tempdecl.overroot() != null) { // if not start of
							// overloaded list of
							// TemplateDeclaration's
							tempdecl = tempdecl.overroot(); // then get the
							// start
						}
						e = new TemplateExp(loc, tempdecl);
						e = e.semantic(sc, context);
						return e;
					}
				}
				e = new DsymbolExp(loc, s);
				e.start = start;
				e.length = length;
			}
			
			// Descent: for binding resolution
			return resolvedExpression = e.semantic(sc, context);
		}
		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.UndefinedIdentifier, this,
				new String[] { new String(ident) }));
		type = Type.terror;
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (hgs.hdrgen) {
			buf.writestring(toHChars2());
		} else {
			buf.writestring(ident);
		}
	}
	
	private final static char[] notThis = { '~', 't', 'h', 'i', 's' };
	private final static char[] invariant = { 'i', 'n', 'v', 'a', 'r', 'i', 'a', 'n', 't' };
	private final static char[] unittest = { 'u', 'n', 'i', 't', 't', 'e', 's', 't' };
	private final static char[] staticThis = { 's', 't', 'a', 't', 'i', 'c', ' ', 't', 'h', 'i', 's' };
	private final static char[] staticNotThis = { 's', 't', 'a', 't', 'i', 'c', ' ', '~', 't', 'h', 'i', 's' };
	private final static char[] dollar = { '$' };
	private final static char[] with = { 'w', 'i', 't', 'h' };
	private final static char[] result = { 'r', 'e', 's', 'u', 'l', 't' };
	private final static char[] Return = { 'r', 'e', 't', 'u', 'r', 'n' };

	public char[] toHChars2() {
		char[] p = null;

	    if (equals(ident, Id.ctor)) {
			p = Id.This;
		} else if (equals(ident, Id.dtor)) {
			p = notThis;
		} else if (equals(ident, Id.classInvariant)) {
			p = invariant;
		} else if (equals(ident, Id.unitTest)) {
			p = unittest;
		} else if (equals(ident, Id.staticCtor)) {
			p = staticThis;
		} else if (equals(ident, Id.staticDtor)) {
			p = staticNotThis;
		} else if (equals(ident, Id.dollar)) {
			p = dollar;
		} else if (equals(ident, Id.withSym)) {
			p = with;
		} else if (equals(ident, Id.result)) {
			p = result;
		} else if (equals(ident, Id.returnLabel)) {
			p = Return;
		} else {
			p = ident;
		}

	    return p;
	}

	@Override
	public char[] toCharArray() {
		return ident;
	}
	
	public String toChars() {
		return new String(ident).intern();
	}

	@Override
	public String toChars(SemanticContext context) {
		return toChars();
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		return this;
	}

	@Override
	public String toString() {
		if (ident == null) {
			return "null";
		} else {
			return new String(ident);
		}
	}
	
	@Override
	public void setResolvedSymbol(IDsymbol symbol) {
		resolvedSymbol = symbol;
	}
	
	@Override
	public void setEvaluatedExpression(Expression exp) {
		this.evaluatedExpression = exp;
	}
	
	@Override
	public void setResolvedExpression(Expression exp) {
		if (exp instanceof CallExp) {
			this.resolvedExpression = ((CallExp) exp).e1;
		} else {
			this.resolvedExpression = exp;
		}
	}
	
	@Override
	public IDsymbol getResolvedSymbol() {
		return resolvedSymbol;
	}

}
