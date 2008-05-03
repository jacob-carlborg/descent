package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.LINK.LINKd;
import static descent.internal.compiler.parser.TOK.TOKdelegate;

import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tstruct;

// DMD 1.020
public class StructInitializer extends Initializer {

	public Identifiers field, sourceField;
	public Initializers value, sourceValue;

	public Array<VarDeclaration> vars; // parallel array of VarDeclaration *'s
	public AggregateDeclaration ad; // which aggregate this is for

	public StructInitializer(Loc loc) {
		super(loc);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceField);
			TreeVisitor.acceptChildren(visitor, sourceValue);
		}
		visitor.endVisit(this);
	}

	public void addInit(IdentifierExp field, Initializer value) {
		if (this.field == null) {
			this.field = new Identifiers();
			this.value = new Initializers();
			this.sourceField = new Identifiers();
			this.sourceValue = new Initializers();
		}
		this.field.add(field);
		this.value.add(value);
		this.sourceField.add(field);
		this.sourceValue.add(value);
	}

	@Override
	public int getNodeType() {
		return STRUCT_INITIALIZER;
	}
	
	@Override
	public StructInitializer isStructInitializer() {
		return this;
	}

	@Override
	public Initializer semantic(Scope sc, Type t, SemanticContext context) {
		TypeStruct ts;
		int errors = 0;

		t = t.toBasetype(context);
		if (t.ty == Tstruct) {
			int i;
			int fieldi = 0;

			ts = (TypeStruct) t;
			ad = ts.sym;
			
			// Descent: lazy initialization
			ad.consumeRest();
			
			for (i = 0; i < size(field); i++) {
				IdentifierExp id = field.get(i);
				Initializer val = value.get(i);
				Dsymbol s;
				VarDeclaration v;

				if (id == null) {
					if (fieldi >= ad.fields.size()) {
						if (context.acceptsProblems()) {
							context.acceptProblem(Problem.newSemanticTypeError(IProblem.TooManyInitializers, this, new String[] { ad.toChars(context) }));
						}
						continue;
					} else {
						s = ad.fields.get(fieldi);
					}
				} else {
					//s = ad.symtab.lookup(id);
					s = ad.search(loc, id, 0, context);
					if (null == s) {
						if (context.acceptsProblems()) {
							context.acceptProblem(Problem.newSemanticTypeError(
									IProblem.SymbolIsNotAMemberOf, id, new String[] { id.toChars(), t.toChars(context) }));
						}
						continue;
					}

					// Find out which field index it is
					for (fieldi = 0; true; fieldi++) {
						if (fieldi >= ad.fields.size()) {
							if (context.acceptsProblems()) {
								context.acceptProblem(Problem.newSemanticTypeError(
										IProblem.SymbolIsNotAPreInstanceInitializableField, s, new String[] { s.toChars(context) }));
							}
							break;
						}
						if (s == ad.fields.get(fieldi)) {
							break;
						}
					}
				}
				if (s != null && (v = s.isVarDeclaration()) != null) {
					val = val.semantic(sc, v.type, context);
					value.set(i, val);
					if (vars == null) {
						vars = new Array<VarDeclaration>();
					}
					vars.set(i, v);
				} else {
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.SymbolIsNotAFieldOfSymbol, this, new String[] { id != null ? id
										.toChars() : s.toChars(context), ad
										.toChars(context) }));
					}
					errors = 1;
				}
				fieldi++;
			}
		} else if (t.ty == Tdelegate && value.size() == 0) { //  Rewrite as empty delegate literal { }
			Arguments arguments = new Arguments();
			Type tf = new TypeFunction(arguments, null, 0, LINKd);
			FuncLiteralDeclaration fd = new FuncLiteralDeclaration(loc, tf,
					TOKdelegate, null);
			fd.fbody = new CompoundStatement(loc, new Statements());
			// fd.endloc = loc; // this was removed from DMD (in case a bug exists in Descent)
			Expression e = new FuncExp(loc, fd);
			ExpInitializer ie = new ExpInitializer(loc, e);
			return ie.semantic(sc, t, context);
		} else {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.AStructIsNotAValidInitializerFor, this, new String[] { t.toChars(context) }));
			}
			errors = 1;
		}
		if (errors != 0) {
			if (field != null) {
				field.clear();
			}
			if (value != null) {
				value.clear();
			}
			if (vars != null) {
				vars.clear();
			}
		}
		return this;
	}

	@Override
	public Initializer syntaxCopy(SemanticContext context) {
		StructInitializer ai = new StructInitializer(loc);

		if (field.size() != value.size()) {
			throw new IllegalStateException("assert(field.dim == value.dim);");
		}

		ai.field = new Identifiers(field.size());
		ai.value = new Initializers(value.size());
		for (int i = 0; i < field.size(); i++) {
			ai.field.set(i, field.get(i));

			Initializer init = value.get(i);
			init = init.syntaxCopy(context);
			ai.value.set(i, init);
		}
		return ai;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writebyte('{');
		for (int i = 0; i < field.size(); i++) {
			if (i > 0) {
				buf.writebyte(',');
			}
			IdentifierExp id = field.get(i);
			if (id != null) {
				buf.writestring(id.toChars());
				buf.writebyte(':');
			}
			Initializer iz = value.get(i);
			if (iz != null) {
				iz.toCBuffer(buf, hgs, context);
			}
		}
		buf.writebyte('}');
	}

	@Override
	public Expression toExpression(SemanticContext context) {
		Expression e;

		if (null == ad) { // if fwd referenced
			return null;
		}
		StructDeclaration sd = ad.isStructDeclaration();
		if (null == sd) {
			return null;
		}
		Expressions elements = new Expressions();
		for (int i = 0; i < value.size(); i++) {
			if (field.get(i) != null) {
				// goto Lno;
				return null;
			}
			Initializer iz = (Initializer) value.get(i);
			if (null == iz) {
				// goto Lno;
				return null;
			}
			Expression ex = iz.toExpression(context);
			if (null == ex) {
				// goto Lno;
				return null;
			}
			elements.add(ex);
		}
		e = new StructLiteralExp(loc, sd, elements);
		e.type = sd.type;
		return e;
	}

}
