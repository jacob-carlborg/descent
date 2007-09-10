package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.LINK.LINKd;
import static descent.internal.compiler.parser.TOK.TOKdelegate;

import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tstruct;

// DMD 1.020
public class StructInitializer extends Initializer {

	public Identifiers field;
	public Initializers value;

	public List<VarDeclaration> vars; // parallel array of VarDeclaration *'s
	public AggregateDeclaration ad; // which aggregate this is for

	public StructInitializer(Loc loc) {
		super(loc);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, field);
			TreeVisitor.acceptChildren(visitor, value);
		}
		visitor.endVisit(this);
	}

	public void addInit(IdentifierExp field, Initializer value) {
		if (this.field == null) {
			this.field = new Identifiers();
			this.value = new Initializers();
		}
		this.field.add(field);
		this.value.add(value);
	}

	@Override
	public int getNodeType() {
		return STRUCT_INITIALIZER;
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
			for (i = 0; i < field.size(); i++) {
				IdentifierExp id = field.get(i);
				Initializer val = value.get(i);
				Dsymbol s;
				VarDeclaration v;

				if (id == null) {
					if (fieldi >= ad.fields.size()) {
						error(loc, "too many initializers for %s", ad
								.toChars(context));
						continue;
					} else {
						s = ad.fields.get(fieldi);
					}
				} else {
					//s = ad.symtab.lookup(id);
					s = ad.search(loc, id, 0, context);
					if (null == s) {
						error("'%s' is not a member of '%s'", id.toChars(), t
								.toChars(context));
						continue;
					}

					// Find out which field index it is
					for (fieldi = 0; true; fieldi++) {
						if (fieldi >= ad.fields.size()) {
							s
									.error("is not a per-instance initializable field");
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
					vars.set(i, v);
				} else {
					error(loc, "%s is not a field of %s", id != null ? id
							.toChars() : s.toChars(context), ad
							.toChars(context));
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
			error(loc, "a struct is not a valid initializer for a %s", t
					.toChars(context));
			errors = 1;
		}
		if (errors != 0) {
			field.clear();
			value.clear();
			vars.clear();
		}
		return this;
	}

	@Override
	public Initializer syntaxCopy() {
		StructInitializer ai = new StructInitializer(loc);

		if (field.size() != value.size()) {
			throw new IllegalStateException("assert(field.dim == value.dim);");
		}

		ai.field = new Identifiers(field.size());
		ai.value = new Initializers(value.size());
		for (int i = 0; i < field.size(); i++) {
			ai.field.set(i, field.get(i));

			Initializer init = value.get(i);
			init = init.syntaxCopy();
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
		return null;
	}

}
