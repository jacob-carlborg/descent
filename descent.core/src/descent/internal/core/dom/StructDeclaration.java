package descent.internal.core.dom;



public class StructDeclaration extends AggregateDeclaration {

	public StructDeclaration(Loc loc, Identifier id) {
		super(id, null);
		
		type = new TypeStruct(this);
	}

	public int getAggregateDeclarationType() {
		return STRUCT_DECLARATION;
	}

}
