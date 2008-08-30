package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.lookup.LazyStructDeclaration;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.LINK.LINKd;
import static descent.internal.compiler.parser.TOK.TOKdelegate;

import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tstruct;


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
			
			// Descent: remove lazy initalization
			ts.sym = ts.sym.unlazy(CharOperation.NO_CHAR, context);
			
			ad = ts.sym;
			
			if (ad instanceof LazyStructDeclaration) {
				ts.sym =  ((LazyStructDeclaration) ad).unlazy(CharOperation.NO_CHAR, context);
				ad = ts.sym;
			}
			
			for (i = 0; i < size(field); i++) {
				IdentifierExp id = field.get(i);
				Initializer val = value.get(i);
				Dsymbol s;
				VarDeclaration v;

				if (id == null) {
					if (fieldi >= ad.fields.size()) {
						if (context.acceptsErrors()) {
							context.acceptProblem(Problem.newSemanticTypeError(IProblem.TooManyInitializers, this, new String[] { ad.toChars(context) }));
						}
						field.remove(i);
						i--;
						continue;
					} else {
						s = ad.fields.get(fieldi);
					}
				} else {
					//s = ad.symtab.lookup(id);
					s = ad.search(loc, id, 0, context);
					if (null == s) {
						if (context.acceptsErrors()) {
							context.acceptProblem(Problem.newSemanticTypeError(
									IProblem.SymbolIsNotAMemberOf, id, new String[] { id.toChars(), t.toChars(context) }));
						}
						continue;
					}

					// Find out which field index it is
					for (fieldi = 0; true; fieldi++) {
						if (fieldi >= ad.fields.size()) {
							if (context.acceptsErrors()) {
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
					if (context.acceptsErrors()) {
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
			if (context.acceptsErrors()) {
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
	
//	@Override
//	public void toDt(SemanticContext context) {
//		Array dts = new Array();
//	    int i;
//	    int j;
//	    dt_t *dt;
//	    dt_t *d;
//	    dt_t **pdtend;
//	    int offset;
//
//	    dts.setDim(size(ad.fields));
//	    dts.zero();
//
//	    for (i = 0; i < size(vars); i++)
//	    {
//		VarDeclaration v = (VarDeclaration) vars.get(i);
//		Initializer val = (Initializer) value.get(i);
//
//		//printf("vars[%d] = %s\n", i, v.toChars());
//
//		for (j = 0; true; j++)
//		{
//		    if ((VarDeclaration) ad.fields.get(j) == v)
//		    {
//			if (dts.get(j) != null)
//			    error(loc, "field %s of %s already initialized", v.toChars(), ad.toChars());
//			dts.set(j, val.toDt(context));
//			break;
//		    }
//		}
//	    }
//
//	    dt = null;
//	    pdtend = &dt;
//	    offset = 0;
//	    for (j = 0; j < size(dts); j++)
//	    {
//		VarDeclaration v = (VarDeclaration) ad.fields.get(j);
//
//		d = (dt_t *)dts.data[j];
//		if (!d)
//		{   // An instance specific initializer was not provided.
//		    // Look to see if there's a default initializer from the
//		    // struct definition
//		    VarDeclaration *v = (VarDeclaration *)ad.fields.data[j];
//
//		    if (v.init)
//		    {
//			d = v.init.toDt();
//		    }
//		    else if (v.offset >= offset)
//		    {
//			int k;
//			int offset2 = v.offset + v.type.size();
//			// Make sure this field does not overlap any explicitly
//			// initialized field.
//			for (k = j + 1; 1; k++)
//			{
//			    if (k == dts.dim)		// didn't find any overlap
//			    {
//				v.type.toDt(&d);
//				break;
//			    }
//			    VarDeclaration *v2 = (VarDeclaration *)ad.fields.data[k];
//
//			    if (v2.offset < offset2 && dts.data[k])
//				break;			// overlap
//			}
//		    }
//		}
//		if (d)
//		{
//		    if (v.offset < offset)
//			error(loc, "duplicate union initialization for %s", v.toChars());
//		    else
//		    {	int sz = dt_size(d);
//			int vsz = v.type.size();
//			int voffset = v.offset;
//			assert(sz <= vsz);
//
//			int dim = 1;
//			for (Type *vt = v.type.toBasetype();
//			     vt.ty == Tsarray;
//			     vt = vt.next.toBasetype())
//			{   TypeSArray *tsa = (TypeSArray *)vt;
//			    dim *= tsa.dim.toInteger();
//			}
//
//			for (size_t i = 0; i < dim; i++)
//			{
//			    if (offset < voffset)
//				pdtend = dtnzeros(pdtend, voffset - offset);
//			    if (!d)
//			    {
//				if (v.init)
//				    d = v.init.toDt();
//				else
//				    v.type.toDt(&d);
//			    }
//			    pdtend = dtcat(pdtend, d);
//			    d = NULL;
//			    offset = voffset + sz;
//			    voffset += vsz / dim;
//			    if (sz == vsz)
//				break;
//			}
//		    }
//		}
//	    }
//	    if (offset < ad.structsize)
//		dtnzeros(pdtend, ad.structsize - offset);
//
//	    return dt;
//	}

}
