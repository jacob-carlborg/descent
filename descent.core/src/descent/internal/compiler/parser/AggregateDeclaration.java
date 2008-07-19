package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.PROT.PROTpackage;
import static descent.internal.compiler.parser.PROT.PROTpublic;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Tstruct;

import java.util.ArrayList;
import java.util.List;

import descent.core.IType;
import descent.core.compiler.IProblem;

public abstract class AggregateDeclaration extends ScopeDsymbol {

	public Type type;
	public PROT protection;
	public Type handle; // 'this' type
	public int storage_class;
	public boolean isdeprecated;
	public int structsize; // size of struct
	public int alignsize; // size of struct for alignment purposes
	public int structalign; // struct member alignment in effect
	public int hasUnions; // set if aggregate has overlapping fields
	public int sizeok; // set when structsize contains valid data
	// 0: no size
	// 1: size is correct
	// 2: cannot determine size; fwd referenced
	public boolean com; // !=0 if this is a COM class (meaning it derives from IUnknown)
	public boolean isauto; // !=0 if this is an auto class
	public boolean isabstract; // !=0 if abstract class
	public Scope scope; // !=NULL means context to use
    public FuncDeclarations dtors;	// Array of destructors
    public FuncDeclaration dtor;	// aggregate destructor

	// Special member functions
	public InvariantDeclaration inv; // invariant
	public NewDeclaration aggNew; // allocator
	public DeleteDeclaration aggDelete; // deallocator

	public List<VarDeclaration> fields;
	
	// Back end
    Symbol stag;		// tag symbol for debug data
    Symbol sinit;
	
	// Wether this aggregate is actually a templated aggregate 
	public boolean templated;
	
	protected IType javaElement;

	public AggregateDeclaration(Loc loc, IdentifierExp id) {
		super(id);
		this.loc = loc;
		fields = new ArrayList<VarDeclaration>(0);
	}

	// The "reference" is not in DMD. It holds the source range of the node
	// that needs the access check, so that we can point errors in the correct place
	public void accessCheck(Scope sc, Dsymbol smember, SemanticContext context, ASTDmdNode reference) {
		boolean result;

		FuncDeclaration f = sc.func;
		AggregateDeclaration cdscope = sc.getStructClassScope();
		PROT access;

		Dsymbol smemberparent = smember.toParent();
		if (smemberparent == null
				|| smemberparent.isAggregateDeclaration() == null) {
			return; // then it is accessible
		}

		// BUG: should enable this check
		// assert(smember.parent.isBaseOf(this, NULL));

		// TODO don't do reference comparison
		if (smemberparent instanceof AggregateDeclaration && SemanticMixin.equals((AggregateDeclaration) smemberparent, this)) {
			PROT access2 = smember.prot();

			result = access2.level >= PROTpublic.level || this.hasPrivateAccess(f)
					|| this.isFriendOf(cdscope)
					|| (access2 == PROTpackage && ASTDmdNode.hasPackageAccess(sc, this));
		} else if ((access = this.getAccess(smember)).level >= PROTpublic.level) {
			result = true;
		} else if (access == PROTpackage && ASTDmdNode.hasPackageAccess(sc, this)) {
			result = true;
		} else {
			result = ASTDmdNode.accessCheckX(smember, f, this, cdscope);
		}
		if (!result) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.MemberIsNotAccessible, reference, smember.toChars(context)));
			}
		}
	}

	public void addField(Scope sc, VarDeclaration v, SemanticContext context) {
		int memsize; // size of member
		int memalignsize; // size of member for alignment purposes
		int xalign; // alignment boundaries

		// Check for forward referenced types which will fail the size() call
		Type t = v.type.toBasetype(context);
		if (t.ty == TY.Tstruct /* && isStructDeclaration() */) {
			TypeStruct ts = (TypeStruct) t;
			
			if (context.isD2()) {
				if (ts.sym == this) {
					if (context.acceptsErrors()) {
						context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.CannotHaveFieldWithSameStructType, v));
					}
				}
			}

			if (ts.sym.sizeok != 1) {
				sizeok = 2; // cannot finish; flag as forward referenced
				return;
			}
		}
		if (t.ty == TY.Tident) {
			sizeok = 2; // cannot finish; flag as forward referenced
			return;
		}

		memsize = v.type().size(loc, context);
		memalignsize = v.type().alignsize(context);
		xalign = v.type().memalign(sc.structalign, context);

		int[] sc_offset_pointer = { sc.offset };
		alignmember(xalign, memalignsize, sc_offset_pointer);
		sc.offset = sc_offset_pointer[0];

		v.offset(sc.offset);
		sc.offset += memsize;
		if (sc.offset > structsize) {
			structsize = sc.offset;
		}
		if (sc.structalign < memalignsize) {
			memalignsize = sc.structalign;
		}
		if (alignsize < memalignsize) {
			alignsize = memalignsize;
		}

		v.storage_class = STC.STCfield;
		if (fields == null) {
			fields = new ArrayList<VarDeclaration>();
		}
		fields.add(v);
	}

	public void alignmember(int salign, int size, int[] poffset) {
		if (salign > 1) {
			//int sa;

			switch (size) {
			case 1:
				break;
			case 2:
				//case_2:
				poffset[0] = (poffset[0] + 1) & ~1; // align to word
				break;
			case 3:
			case 4:
				if (salign == 2) {
					// goto case_2;
					poffset[0] = (poffset[0] + 1) & ~1; // align to word
				}
				poffset[0] = (poffset[0] + 3) & ~3; // align to dword
				break;
			default:
				poffset[0] = (poffset[0] + salign - 1) & ~(salign - 1);
				break;
			}
		}
	}

	public PROT getAccess(Dsymbol smember) {
		return PROT.PROTpublic;
	}

	@Override
	public Type getType(SemanticContext context) {
		return type;
	}
	
	/*****************************************
	 * Create inclusive destructor for struct/class by aggregating
	 * all the destructors in dtors[] with the destructors for
	 * all the members.
	 * Note the close similarity with StructDeclaration::buildPostBlit(),
	 * and the ordering changes (runs backward instead of forwards).
	 */

	public FuncDeclaration buildDtor(Scope sc, SemanticContext context) {
		Expression e = null;

		for (int i = 0; i < size(fields); i++) {
			Dsymbol s = (Dsymbol) fields.get(i);
			VarDeclaration v = s.isVarDeclaration();
			Type tv = v.type.toBasetype(context);
			int dim = 1;
			while (tv.ty == Tsarray) {
//				TypeSArray ta = (TypeSArray) tv;
				dim *= ((TypeSArray) tv).dim.toInteger(context).intValue();
				tv = tv.nextOf().toBasetype(context);
			}
			if (tv.ty == Tstruct) {
				TypeStruct ts = (TypeStruct) tv;
				StructDeclaration sd = ts.sym;
				if (sd.dtor != null) {
					Expression ex;

					// this.v
					ex = new ThisExp(Loc.ZERO);
					ex = new DotVarExp(Loc.ZERO, ex, v, 0);

					if (dim == 1) { // this.v.dtor()
						ex = new DotVarExp(Loc.ZERO, ex, sd.dtor, 0);
						ex = new CallExp(Loc.ZERO, ex);
					} else {
						// Typeinfo.destroy(cast(void*)&this.v);
						Expression ea = new AddrExp(Loc.ZERO, ex);
						ea = new CastExp(Loc.ZERO, ea, Type.tvoid
								.pointerTo(context));

						Expression et = v.type.getTypeInfo(sc, context);
						et = new DotIdExp(Loc.ZERO, et, new IdentifierExp(Id.destroy));

						ex = new CallExp(Loc.ZERO, et, ea);
					}
					e = Expression.combine(ex, e); // combine in reverse order
				}
			}
		}

		/*
		 * Build our own "destructor" which executes e
		 */
		if (e != null) {
			DtorDeclaration dd = new DtorDeclaration(Loc.ZERO, new IdentifierExp(Id.__fieldDtor));
			dd.fbody = new ExpStatement(Loc.ZERO, e);
			if (dtors == null) {
				dtors = new FuncDeclarations();
			}
			dtors.add(0, dd);

			if (members == null) {
				members = new Dsymbols();
			}
			members.add(dd);
			dd.semantic(sc, context);
		}

		switch (size(dtors)) {
		case 0:
			return null;

		case 1:
			return (FuncDeclaration) dtors.get(0);

		default:
			e = null;
			for (int i = 0; i < size(dtors); i++) {
				FuncDeclaration fd = (FuncDeclaration) dtors.get(i);
				Expression ex = new ThisExp(Loc.ZERO);
				ex = new DotVarExp(Loc.ZERO, ex, fd, 0);
				ex = new CallExp(Loc.ZERO, ex);
				e = Expression.combine(ex, e);
			}
			DtorDeclaration dd = new DtorDeclaration(Loc.ZERO, new IdentifierExp(Id.__aggrDtor));
			dd.fbody = new ExpStatement(Loc.ZERO, e);
			if (members == null) {
				members = new Dsymbols();
			}
			members.add(dd);
			dd.semantic(sc, context);
			return dd;
		}
	}

	/***************************************************************************
	 * Determine if smember has access to private members of this declaration.
	 */

	public boolean hasPrivateAccess(Dsymbol smember) {
		if (smember != null) {
			AggregateDeclaration cd = null;
			Dsymbol smemberparent = smember.toParent();
			if (smemberparent != null) {
				cd = smemberparent.isAggregateDeclaration();
			}

			if (SemanticMixin.equals(this, cd)) { // smember is a member of this class
				return true; // so we get private access
			}

			// If both are members of the same module, grant access
			while (true) {
				Dsymbol sp = smember.toParent();
				if (sp.isFuncDeclaration() != null
						&& smember.isFuncDeclaration() != null) {
					smember = sp;
				} else {
					break;
				}
			}
			// TODO check reference comparison
			if (cd == null && this.toParent() == smember.toParent()) {
				return true;
			}
			if (cd == null && this.getModule() == smember.getModule()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public AggregateDeclaration isAggregateDeclaration() {
		return this;
	}

	@Override
	public boolean isDeprecated() {
		return isdeprecated;
	}

	/***************************************************************************
	 * Determine if this is the same or friend of cd.
	 */

	public boolean isFriendOf(AggregateDeclaration cd) {
		if (SemanticMixin.equals(this, cd)) {
			return true;
		}

		// Friends if both are in the same module
		// if (toParent() == cd.toParent())
		// TODO check reference comparison
		if (cd != null && this.getModule() == cd.getModule()) {
			return true;
		}

		return false;
	}

	@Override
	public PROT prot() {
		return protection;
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		if (scope != null) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolHasForwardReferences, this, toChars(context)));
			}
		}
		if (members != null) {
			sc = sc.push(this);
			
			semantic2Scope(sc);
			
			for (int i = 0; i < members.size(); i++) {
				Dsymbol s = members.get(i);
				s.semantic2(sc, context);
			}
			sc.pop();
		}
	}

	@Override
	public void semantic3(Scope sc, SemanticContext context) {
		int i;

		if (members != null) {
			sc = sc.push(this);
			
			semantic3Scope(sc);
			
			for (i = 0; i < members.size(); i++) {
				Dsymbol s = members.get(i);
				s.semantic3(sc, context);
			}
			sc.pop();
		}
	}
	
	protected void semanticScope(Scope sc) {
		
	}
	
	protected void semantic2Scope(Scope sc) {
		
	}
	
	protected void semantic3Scope(Scope sc) {
		
	}

	@Override
	public int size(SemanticContext context) {
		if (null == members) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.UnknownSize, this));
			}
		}
		if (sizeok != 1) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.NoSizeYetForForwardReference, this));
		}
		return structsize;
	}

	public Symbol toInitializer() {
		// TODO semantic back-end
		if (null == sinit) {
			sinit = new Symbol();
		}
		return sinit;
	}
	
	@Override
	public int getLineNumber() {
		return loc.linnum;
	}
	
	@Override
	public boolean templated() {
		return templated;
	}
	
	public void setJavaElement(IType javaElement) {
		this.javaElement = javaElement;
	}
	
	@Override
	public IType getJavaElement() {
		return javaElement;
	}
	
	@Override
	public PROT getProtection() {
		return protection;
	}
	
	@Override
	public int getStorageClass() {
		return storage_class;
	}
	
	@Override
	public Type type() {
		return type;
	}
	
	@Override
	public AggregateDeclaration unlazy(char[] prefix, SemanticContext context) {
		return this;
	}
	
}
