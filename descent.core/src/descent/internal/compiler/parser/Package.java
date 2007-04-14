package descent.internal.compiler.parser;

public abstract class Package extends ScopeDsymbol {
	
	public Package(Loc loc) {
		super(loc);
	}

	@Override
	public Package isPackage() {
		return this;
	}

}
