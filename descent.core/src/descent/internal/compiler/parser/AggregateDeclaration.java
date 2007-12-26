package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;

// DMD 1.020
public abstract class AggregateDeclaration extends ScopeDsymbol implements IAggregateDeclaration {

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
	public IInvariantDeclaration inv; // invariant
	public NewDeclaration aggNew; // allocator
	public DeleteDeclaration aggDelete; // deallocator

	public List<IVarDeclaration> fields;
	
	// Back end
    Symbol stag;		// tag symbol for debug data
    Symbol sinit;

	public AggregateDeclaration(Loc loc, IdentifierExp id) {
		super(id);
		this.loc = loc;
		fields = new ArrayList<IVarDeclaration>(0);
	}

	// The "reference" is not in DMD. It holds the source range of the node
	// that needs the access check, so that we can point errors in the correct place
	public void accessCheck(Scope sc, IDsymbol smember, SemanticContext context, INode reference) {
		SemanticMixin.accessCheck(this, sc, smember, context, reference);
	}

	public void addField(Scope sc, IVarDeclaration v, SemanticContext context) {
		int memsize; // size of member
		int memalignsize; // size of member for alignment purposes
		int xalign; // alignment boundaries

		// Check for forward referenced types which will fail the size() call
		Type t = v.type().toBasetype(context);
		if (t.ty == TY.Tstruct /* && isStructDeclaration() */) {
			TypeStruct ts = (TypeStruct) t;

			if (ts.sym.sizeok() != 1) {
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

		v.storage_class(STC.STCfield);
		if (fields == null) {
			fields = new ArrayList<IVarDeclaration>();
		}
		fields.add(v);
	}

	public void alignmember(int salign, int size, int[] poffset) {
		SemanticMixin.alignmember(this, salign, size, poffset);
	}

	public PROT getAccess(IDsymbol smember) {
		return PROT.PROTpublic;
	}

	@Override
	public Type getType() {
		return type;
	}

	/***************************************************************************
	 * Determine if smember has access to private members of this declaration.
	 */

	public boolean hasPrivateAccess(IDsymbol smember) {
		return SemanticMixin.hasPrivateAccess(this, smember);
	}

	@Override
	public void inlineScan(SemanticContext context) {
		int i;

		if (members != null) {
			for (i = 0; i < members.size(); i++) {
				IDsymbol s = members.get(i);
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

	public boolean isFriendOf(IAggregateDeclaration cd) {
		return SemanticMixin.isFriendOf(this, cd);
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
				IDsymbol s = members.get(i);
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
				IDsymbol s = members.get(i);
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
	
	public IInvariantDeclaration inv() {
		return inv;
	}
	
	public void inv(IInvariantDeclaration inv) {
		this.inv = inv;
	}
	
	public int sizeok() {
		return sizeok;
	}
	
	public void sizeok(int sizeok) {
		this.sizeok = sizeok;
	}
	
	public Type type() {
		return type;
	}

	public Type handle() {
		return handle;
	}
	
	public List<IVarDeclaration> fields() {
		return fields;
	}
	
	public int structsize() {
		return structsize;
	}
	
	public void structsize(int structsize) {
		this.structsize = structsize;
	}
	
	public int alignsize() {
		return alignsize;
	}
	
	public void alignsize(int alignsize) {
		this.alignsize = alignsize;
	}
	
	public int hasUnions() {
		return hasUnions;
	}
	
	public void hasUnions(int hasUnions) {
		this.hasUnions = hasUnions;
	}
	
	public int storage_class() {
		return storage_class;
	}
	
	public INewDeclaration aggNew() {
		return aggNew;
	}
	
	public IDeleteDeclaration aggDelete() {
		return aggDelete;
	}
	
	public Scope scope() {
		return scope;
	}
	
	public int structalign() {
		return structalign;
	}
	
}
