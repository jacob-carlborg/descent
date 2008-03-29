package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import descent.core.IType;
import descent.core.compiler.IProblem;
import static descent.internal.compiler.parser.PROT.PROTpackage;
import static descent.internal.compiler.parser.PROT.PROTpublic;

// DMD 1.020
public abstract class AggregateDeclaration extends ScopeDsymbol {

	public Type type;
	public PROT protection;
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
	public boolean com; // !=0 if this is a COM class
	public boolean isauto; // !=0 if this is an auto class
	public boolean isabstract; // !=0 if abstract class
	public Scope scope; // !=NULL means context to use
	public Type handle; // 'this' type

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
	
	private IType javaElement;

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
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.MemberIsNotAccessible, reference, new String[] { smember.toChars(context) }));
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
	public Type getType() {
		return type;
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
	public void inlineScan(SemanticContext context) {
		int i;

		if (members != null) {
			for (i = 0; i < members.size(); i++) {
				Dsymbol s = members.get(i);
				s.inlineScan(context);
			}
		}
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
		// if (toParent() == cd->toParent())
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
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolHasForwardReferences, this, new String[] { toChars(context) }));
		}
		if (members != null) {
			sc = sc.push(this);
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
			for (i = 0; i < members.size(); i++) {
				Dsymbol s = members.get(i);
				s.semantic3(sc, context);
			}
			sc.pop();
		}
	}

	@Override
	public int size(SemanticContext context) {
		if (null == members) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.UnknownSize, this));
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
	
}
