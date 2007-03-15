package descent.internal.compiler.parser;

public class EnumDeclaration extends ScopeDsymbol {

	public Type memtype;

	public EnumDeclaration(IdentifierExp id, Type memtype) {
		super(id);
		this.memtype = memtype;		
	}
	
	@Override
	public int kind() {
		return ENUM_DECLARATION;
	}

}
