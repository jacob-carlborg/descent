package descent.internal.compiler.parser;

// This is not part of DMD: it's an expression to signal an error
public class ErrorExp extends IntegerExp {

	public ErrorExp() {
		super(Loc.ZERO, Id.ZERO, 0, Type.tint32);
	}

}
