package descent.internal.compiler.parser;

// DMD 1.020
public abstract class Package extends ScopeDsymbol {
	
	public Package(Loc loc) {
		super(loc);
	}

	@Override
	public Package isPackage() {
		return this;
	}

}
