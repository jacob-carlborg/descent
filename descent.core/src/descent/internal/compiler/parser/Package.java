package descent.internal.compiler.parser;

// DMD 1.020
public abstract class Package extends ScopeDsymbol {

	public Package(IdentifierExp ident) {
		super(ident);
	}

	@Override
	public Package isPackage() {
		return this;
	}

	@Override
	public String kind() {
		return "package";
	}
}
