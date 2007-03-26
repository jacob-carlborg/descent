package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;
import static descent.internal.compiler.parser.PROT.*;

public abstract class AggregateDeclaration extends ScopeDsymbol {

	public Type type;
	PROT protection;
	public int storage_class;
	public boolean isdeprecated;
	public int structsize; // size of struct
	public int alignsize; // size of struct for alignment purposes
	public int structalign; // struct member alignment in effect
	public int sizeok; 	// set when structsize contains valid data
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

	public AggregateDeclaration(IdentifierExp id) {
		super(id);
		fields = new ArrayList<VarDeclaration>(0);
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public AggregateDeclaration isAggregateDeclaration() {
		return this;
	}

	public PROT getAccess(Dsymbol smember) {
		return PROT.PROTpublic;
	}
	
	/*******************************
	 * Do access check for member of this class, this class being the
	 * type of the 'this' pointer used to access smember.
	 */

	public void accessCheck(Scope sc, Dsymbol smember) {
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

		if (smemberparent == this) {
			PROT access2 = smember.prot();

			result = access2.level >= PROTpublic.level || hasPrivateAccess(f)
					|| isFriendOf(cdscope)
					|| (access2 == PROTpackage && hasPackageAccess(sc, this));
		} else if ((access = this.getAccess(smember)).level >= PROTpublic.level) {
			result = true;
		} else if (access == PROTpackage && hasPackageAccess(sc, this)) {
			result = true;
		} else {
			result = accessCheckX(smember, f, this, cdscope);
		}
		if (!result) {
			error("member %s is not accessible", smember.toChars());
		}
	}

	/***************************************************************************
	 * Determine if this is the same or friend of cd.
	 */

	public boolean isFriendOf(AggregateDeclaration cd) {
		if (this == cd) {
			return true;
		}

		// Friends if both are in the same module
		// if (toParent() == cd->toParent())
		if (cd != null && getModule() == cd.getModule()) {
			return true;
		}

		return false;
	}
	
	/***************************************************************************
	 * Determine if smember has access to private members of this declaration.
	 */

	public boolean hasPrivateAccess(Dsymbol smember) {
		if (smember != null) {
			AggregateDeclaration cd = null;
			Dsymbol smemberparent = smember.toParent();
			if (smemberparent != null)
				cd = smemberparent.isAggregateDeclaration();

			if (this == cd) { // smember is a member of this class
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
			if (cd == null && toParent() == smember.toParent()) {
				return true;
			}
			if (cd == null && getModule() == smember.getModule()) {
				return true;
			}
		}
		return false;
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

		memsize = v.type.size();
		memalignsize = v.type.alignsize();
		xalign = v.type.memalign(sc.structalign);

		int[] sc_offset_pointer = { sc.offset };
		alignmember(xalign, memalignsize, sc_offset_pointer);
		sc.offset = sc_offset_pointer[0];

		v.offset = sc.offset;
		sc.offset += memsize;
		if (sc.offset > structsize)
			structsize = sc.offset;
		if (sc.structalign < memalignsize)
			memalignsize = sc.structalign;
		if (alignsize < memalignsize)
			alignsize = memalignsize;
		// printf("\talignsize = %d\n", alignsize);

		v.storage_class |= STC.STCfield;
		// printf(" addField '%s' to '%s' at offset %d, size = %d\n",
		// v.toChars(), toChars(), v.offset, memsize);
		if (fields == null) {
			fields = new ArrayList<VarDeclaration>();
		}
		fields.add(v);
	}

	public void alignmember(int xalign, int memalignsize, int[] offset) {
		// TODO semantic
	}

	@Override
	public boolean isDeprecated() {
		return isdeprecated;
	}

}
