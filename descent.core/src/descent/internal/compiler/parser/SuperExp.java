package descent.internal.compiler.parser;

public class SuperExp extends ThisExp {
	
	@Override
	public int kind() {
		return SUPER_EXP;
	}

}
