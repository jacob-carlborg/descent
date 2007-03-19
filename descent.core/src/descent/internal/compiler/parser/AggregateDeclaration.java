package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateDeclaration extends ScopeDsymbol {
	
	public Type type;
	public int storage_class;
	public boolean isdeprecated;
	public int structsize;	// size of struct
	public int alignsize;		// size of struct for alignment purposes
	public int structalign;	// struct member alignment in effect
	public int sizeok;		// set when structsize contains valid data
							// 0: no size
							// 1: size is correct
							// 2: cannot determine size; fwd referenced
	public boolean com;				// !=0 if this is a COM class
	public boolean isauto;				// !=0 if this is an auto class
	public boolean isabstract;			// !=0 if abstract class
	public Scope scope;		// !=NULL means context to use
	public Type handle;		// 'this' type
	public NewDeclaration aggNew;		// allocator
	public DeleteDeclaration aggDelete;	// deallocator
	public List<VarDeclaration> fields;
	
	public AggregateDeclaration(IdentifierExp id) {
		super(id);
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public AggregateDeclaration isAggregateDeclaration() {
		return this;
	}
	
	public void addField(Scope sc, VarDeclaration v, SemanticContext context) {
		int memsize;		// size of member
		int memalignsize;	// size of member for alignment purposes
		int xalign;		// alignment boundaries

	    //printf("AggregateDeclaration::addField('%s') %s\n", v->toChars(), toChars());

	    // Check for forward referenced types which will fail the size() call
	    Type t = v.type.toBasetype(context);
	    if (t.ty == TY.Tstruct /*&& isStructDeclaration()*/)
	    {	TypeStruct ts = (TypeStruct) t;

		if (ts.sym.sizeok != 1)
		{
		    sizeok = 2;		// cannot finish; flag as forward referenced
		    return;
		}
	    }
	    if (t.ty == TY.Tident)
	    {
		sizeok = 2;		// cannot finish; flag as forward referenced
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
	    //printf("\talignsize = %d\n", alignsize);

	    v.storage_class |= STC.STCfield;
	    //printf(" addField '%s' to '%s' at offset %d, size = %d\n", v.toChars(), toChars(), v.offset, memsize);
	    if (fields == null) {
	    	fields = new ArrayList<VarDeclaration>();
	    }
	    fields.add(v);
	}
	
	private void alignmember(int xalign, int memalignsize, int[] offset) {
		// TODO semantic
	}

	@Override
	public boolean isDeprecated() {
		return isdeprecated;
	}

}
