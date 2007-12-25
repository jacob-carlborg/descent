package descent.internal.compiler.parser;

public interface IInterfaceDeclaration extends IClassDeclaration {
	
	boolean isBaseOf(BaseClass bc, int[] poffset);

}
