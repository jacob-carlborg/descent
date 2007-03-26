package descent.internal.compiler.parser;

public abstract class Package extends ScopeDsymbol {
	
	@Override
	public Package isPackage() {
		return this;
	}

}
